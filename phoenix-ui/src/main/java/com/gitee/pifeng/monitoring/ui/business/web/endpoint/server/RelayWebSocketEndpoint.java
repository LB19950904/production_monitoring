package com.gitee.pifeng.monitoring.ui.business.web.endpoint.server;

import com.gitee.pifeng.monitoring.common.web.core.websocket.WebSocketRelayHelper;
import com.gitee.pifeng.monitoring.common.web.util.WebSocketUtils;
import com.gitee.pifeng.monitoring.plug.core.ConfigLoader;
import com.gitee.pifeng.monitoring.ui.business.web.endpoint.client.UpstreamClientEndpoint;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * WebSocket 中继代理服务端点
 * </p>
 * 本类作为 WebSocket 中继代理的核心服务端，主要职责如下：<br>
 * 1. 监听下游客户端的 WebSocket 连接请求，并在连接建立时为每个下游连接创建对应的上游连接；<br>
 * 2. 维护下游 {@link Session} 与上游 {@link Session} 之间的双向配对关系，确保消息能够正确路由；<br>
 * 3. 实现双向消息透传功能：将下游消息转发到上游，将上游消息转发到下游；<br>
 * 4. 管理连接生命周期，处理连接异常和资源清理。
 * <p>
 * 工作机制：<br>
 * 1. 下游客户端连接到本端点时，通过 {@link UpstreamClientEndpoint} 同步建立到上游服务器的连接；<br>
 * 2. 建立下游 {@link Session} 与上游 {@link Session} 的双向映射关系（{@link #DOWNSTREAM_TO_UPSTREAM} 和 {@link #UPSTREAM_TO_DOWNSTREAM}）；<br>
 * 3. {@link Session} 存储在 {@link #SESSION_CACHE} 中，使用 Guava Cache 实现自动过期和容量限制。<br>
 * <p>
 * 使用方式：<br>
 * 1. 本类通过 {@link ServerEndpoint} 注解注册为 WebSocket 端点，监听路径为 "/ws/relay/{subPath}"；<br>
 * 2. 下游客户端通过 WebSocket 协议连接到该端点，subPath 参数用于构造上游服务地址；<br>
 * 3. 上游服务地址由 baseUrl 和 subPath 拼接而成。
 * <p>
 * 注意事项：<br>
 * 1. 最大支持 {@link #CACHE_MAXIMUM_SIZE} 个并发连接；<br>
 * 2. 连接空闲超过 {@link #MAX_IDLE_TIMEOUT} 毫秒会自动关闭；<br>
 * 3. 单个消息最大支持 {@link #MAX_TEXT_MESSAGE_BUFFER_SIZE} 字节。
 *
 * @author 皮锋
 * @custom.date 2026/1/30 11:39
 */
@Slf4j
@ServerEndpoint("/websocket/relay/{subPath}")
public class RelayWebSocketEndpoint {

    /**
     * 上游服务基础地址
     * 格式：{@code WebSocket协议://主机:端口/路径}
     */
    private static final String UPSTREAM_BASE_URL = ConfigLoader.getMonitoringProperties().getComm().getWebsocket().getUrl();

    // ==================== 会话存储配置 ====================

    /**
     * 下游到上游的会话配对映射表
     * 维护下游 Session ID 到上游 Session ID 的映射关系
     */
    private static final ConcurrentMap<String, String> DOWNSTREAM_TO_UPSTREAM = new ConcurrentHashMap<>();

    /**
     * 上游到下游的会话配对映射表
     * 维护上游 Session ID 到下游 Session ID 的反向映射关系
     */
    private static final ConcurrentMap<String, String> UPSTREAM_TO_DOWNSTREAM = new ConcurrentHashMap<>();

    /**
     * Session 最大缓存数
     */
    private static final int CACHE_MAXIMUM_SIZE = 1000;

    /**
     * Session 对象缓存
     * 存储所有活跃的 WebSocket Session 对象，提供自动过期和 LRU 淘汰机制
     */
    private static final Cache<String, Session> SESSION_CACHE = CacheBuilder.newBuilder()
            // 最大缓存数
            .maximumSize(CACHE_MAXIMUM_SIZE)
            // 缓存过期时间
            .expireAfterAccess(30, TimeUnit.MINUTES)
            // 添加移除监听器
            .removalListener((RemovalListener<String, Session>) notification -> {
                String sessionId = notification.getKey();
                Session session = notification.getValue();
                // 安全关闭Session
                if (session != null && session.isOpen()) {
                    // 清理下游映射
                    String upstreamSessionId = DOWNSTREAM_TO_UPSTREAM.remove(sessionId);
                    if (upstreamSessionId != null) {
                        log.info("WebSocket 会话清理，下游Session[{}] -> 上游Session[{}]！", sessionId, upstreamSessionId);
                    }
                    // 清理上游映射
                    String downstreamSessionId = UPSTREAM_TO_DOWNSTREAM.remove(sessionId);
                    if (downstreamSessionId != null) {
                        log.info("WebSocket 会话清理，上游Session[{}] -> 下游Session[{}]！", sessionId, downstreamSessionId);
                    }
                    WebSocketUtils.safeClose(session, CloseReason.CloseCodes.GOING_AWAY, "连接空闲超时");
                }
            }).build();

    // ==================== 连接控制配置 ====================

    /**
     * 连接空闲超时时间（10 分钟）
     * 设置 WebSocket 连接的最大空闲时间，超过此时间无消息交互则自动关闭
     */
    private static final long MAX_IDLE_TIMEOUT = 10 * 60 * 1000L;

    /**
     * 消息缓冲区大小（10 MB）
     * 设置单条 WebSocket 消息的最大缓冲区容量
     */
    private static final int MAX_TEXT_MESSAGE_BUFFER_SIZE = 10 * 1024 * 1024;

    /*
     * 静态初始化块
     * 在类加载时初始化静态资源，执行一次性设置
     */
    static {
        log.info("WebSocket中继代理初始化完成，上游地址：{}", UPSTREAM_BASE_URL);
    }

    // ==================== WebSocket事件处理器 ====================

    /**
     * <p>
     * 处理下游客户端连接建立事件，本方法是 WebSocket 连接的入口点，当下游客户端连接到本中继服务时被调用
     * </p>
     * 主要执行以下操作：<br>
     * 1. 检查连接数限制，拒绝超过上限的连接；<br>
     * 2. 配置下游 {@link Session} 参数（空闲超时、缓冲区大小）；<br>
     * 3. 构建上游服务 URL，通过 {@link UpstreamClientEndpoint} 同步连接上游 WebSocket 服务；<br>
     * 4. 配置上游 {@link Session} 参数（空闲超时、缓冲区大小）；<br>
     * 5. 建立下游 {@link Session} 与上游 {@link Session} 的配对关系；<br>
     * 6. 统一处理各种异常情况，确保资源正确释放。
     *
     * @param downstreamSession 下游客户端建立的 WebSocket 会话对象
     * @param subPath           URL 路径中的动态参数，用于构造特定的上游服务地址
     * @author 皮锋
     * @custom.date 2026/1/30 11:53
     */
    @OnOpen
    public void onOpen(Session downstreamSession, @PathParam("subPath") String subPath) {
        String downstreamSessionId = downstreamSession.getId();
        try {
            // 判断 WebSocket 连接数是否已达到或超过指定的最大限制
            if (WebSocketRelayHelper.isConnectionLimitReached(SESSION_CACHE, CACHE_MAXIMUM_SIZE)) {
                log.warn("WebSocket连接数已达上限 {}个，拒绝新连接！", CACHE_MAXIMUM_SIZE);
                WebSocketUtils.safeClose(downstreamSession, CloseReason.CloseCodes.TRY_AGAIN_LATER, "服务器连接数已达上限");
                return;
            }
            // 用户自定义属性
            Map<String, Object> userProperties = Maps.newHashMap();
            userProperties.put("subPath", subPath);
            // 配置下游 Session 的连接参数
            WebSocketUtils.configureSession(downstreamSession, MAX_IDLE_TIMEOUT, MAX_TEXT_MESSAGE_BUFFER_SIZE, MAX_TEXT_MESSAGE_BUFFER_SIZE, userProperties);
            // 构建完整的上游 URL
            String upstreamUrl = WebSocketUtils.buildUrl(UPSTREAM_BASE_URL + "/websocket/relay", subPath, downstreamSession);
            log.info("WebSocket正在连接上游，URL：{}", upstreamUrl);
            // 获取 WebSocket 容器，用于发起客户端连接
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            // 连接上游服务
            UpstreamClientEndpoint clientEndpoint = new UpstreamClientEndpoint(DOWNSTREAM_TO_UPSTREAM, UPSTREAM_TO_DOWNSTREAM, SESSION_CACHE);
            Session upstreamSession = container.connectToServer(clientEndpoint, URI.create(upstreamUrl));
            if (upstreamSession == null) {
                log.error("WebSocket连接上游服务器失败，下游Session[{}]！", downstreamSessionId);
                WebSocketUtils.safeClose(downstreamSession, CloseReason.CloseCodes.CANNOT_ACCEPT, "无法连接到上游服务");
                return;
            }
            // 配置上游 Session 参数
            WebSocketUtils.configureSession(upstreamSession, MAX_IDLE_TIMEOUT, MAX_TEXT_MESSAGE_BUFFER_SIZE, MAX_TEXT_MESSAGE_BUFFER_SIZE, userProperties);
            String upstreamSessionId = upstreamSession.getId();
            // 建立双向配对关系
            WebSocketRelayHelper.establishPairing(SESSION_CACHE, DOWNSTREAM_TO_UPSTREAM, UPSTREAM_TO_DOWNSTREAM, downstreamSessionId, upstreamSessionId, downstreamSession, upstreamSession);
            log.info("WebSocket中继通道建立成功，下游Session[{}] -> 上游Session[{}]，下游活跃连接数：{}，上游活跃连接数：{}",
                    // 日志内容
                    downstreamSessionId, upstreamSessionId, DOWNSTREAM_TO_UPSTREAM.size(), UPSTREAM_TO_DOWNSTREAM.size());
        } catch (Exception e) {
            log.error("WebSocket中继通道建立失败，下游Session[{}]！", downstreamSessionId);
            WebSocketUtils.safeClose(downstreamSession, CloseReason.CloseCodes.CANNOT_ACCEPT, "无法连接到上游服务：" + e.getMessage());
        }
    }

    /**
     * <p>
     * 处理从下游客户端收到的文本消息，当下游客户端发送消息到中继服务时，本方法被调用
     * </p>
     * 主要执行以下操作：<br>
     * 1. 查找该下游 {@link Session} 配对的上游 {@link Session}；<br>
     * 2. 检查上游 {@link Session} 是否仍然可用；<br>
     * 3. 将消息异步转发到上游 {@link Session}；<br>
     * 4. 处理转发失败情况，清理失效的配对关系。
     *
     * @param message           下游客户端发送的文本消息内容
     * @param downstreamSession 发送消息的下游 WebSocket 会话对象
     * @author 皮锋
     * @custom.date 2026/1/30 12:49
     */
    @OnMessage
    public void onMessage(String message, Session downstreamSession) {
        String downstreamSessionId = downstreamSession.getId();
        // 查找配对的上游Session ID
        String upstreamSessionId = DOWNSTREAM_TO_UPSTREAM.get(downstreamSessionId);
        if (upstreamSessionId == null) {
            log.warn("WebSocket无配对上游，下游Session[{}]！", downstreamSessionId);
            return;
        }
        // 从缓存获取上游Session对象
        Session upstreamSession = SESSION_CACHE.getIfPresent(upstreamSessionId);
        if (upstreamSession == null || !upstreamSession.isOpen()) {
            String msg = String.format("WebSocket上游连接失效，上游Session[%s]已关闭！", upstreamSessionId);
            log.warn(msg);
            WebSocketRelayHelper.cleanupPair(SESSION_CACHE, DOWNSTREAM_TO_UPSTREAM, UPSTREAM_TO_DOWNSTREAM, downstreamSessionId, upstreamSessionId, CloseReason.CloseCodes.UNEXPECTED_CONDITION, msg);
            return;
        }
        // 异步转发消息到上游
        upstreamSession.getAsyncRemote().sendText(message, sendResult -> {
            if (sendResult.isOK()) {
                if (log.isDebugEnabled()) {
                    log.debug("WebSocket消息转发成功，下游Session[{}] -> 上游Session[{}]，消息长度：{}", downstreamSessionId, upstreamSessionId, message.length());
                }
            } else {
                String expMsg = sendResult.getException().getMessage();
                String msg = String.format("WebSocket消息转发失败，下游Session[%s] -> 上游Session[%s]，异常：%s", downstreamSessionId, upstreamSessionId, expMsg);
                log.error(msg);
                WebSocketRelayHelper.cleanupPair(SESSION_CACHE, DOWNSTREAM_TO_UPSTREAM, UPSTREAM_TO_DOWNSTREAM, downstreamSessionId, upstreamSessionId, CloseReason.CloseCodes.UNEXPECTED_CONDITION, msg);
            }
        });
    }

    /**
     * <p>
     * 处理下游连接关闭事件，当下游客户端主动关闭连接或连接异常断开时，本方法被调用
     * </p>
     * 主要执行以下操作：<br>
     * 1. 查找并清理与该下游 {@link Session} 配对的上下游映射关系；<br>
     * 2. 通过 {@link WebSocketRelayHelper#cleanupPair} 安全关闭配对的上下游 {@link Session}；<br>
     * 3. 清理缓存中的相关资源。
     *
     * @param downstreamSession 关闭的下游 WebSocket 会话对象
     * @param reason            连接关闭的原因
     * @author 皮锋
     * @custom.date 2026/1/30 13:11
     */
    @OnClose
    public void onClose(Session downstreamSession, CloseReason reason) {
        String downstreamSessionId = downstreamSession.getId();
        // 查找配对的上游 Session 并清理配对关系
        String upstreamSessionId = DOWNSTREAM_TO_UPSTREAM.get(downstreamSessionId);
        if (upstreamSessionId != null) {
            // 清理一对会话的配对关系和资源
            WebSocketRelayHelper.cleanupPair(SESSION_CACHE, DOWNSTREAM_TO_UPSTREAM, UPSTREAM_TO_DOWNSTREAM, downstreamSessionId, upstreamSessionId, reason.getCloseCode(), reason.getReasonPhrase());
        } else {
            // 清理单个会话资源，会触发这个类中缓存监听器，从而移除对应的会话配对映射表
            WebSocketRelayHelper.cleanupSession(SESSION_CACHE, downstreamSessionId, reason.getCloseCode(), reason.getReasonPhrase());
        }
        log.info("WebSocket下Session[{}]资源清理完成，下游活跃连接数：{}，上游活跃连接数：{}", downstreamSessionId, DOWNSTREAM_TO_UPSTREAM.size(), UPSTREAM_TO_DOWNSTREAM.size());
    }

    /**
     * <p>
     * 处理下游连接异常事件，当下游连接发生异常时，本方法被调用
     * </p>
     * 主要执行以下操作：<br>
     * 1.记录异常日志；<br>
     * 2.构造统一的关闭原因；<br>
     * 3.调用 onClose() 方法执行资源清理。
     *
     * @param downstreamSession 发生异常的下游 WebSocket 会话对象
     * @param throwable         具体的异常对象
     * @author 皮锋
     * @custom.date 2026/1/30 13:13
     */
    @OnError
    public void onError(Session downstreamSession, Throwable throwable) {
        String downstreamSessionId = downstreamSession.getId();
        log.error("WebSocket下游连接异常，下游Session[{}]，异常原因：{}", downstreamSessionId, throwable.getMessage());
        // 构造关闭原因，复用 onClose 逻辑
        CloseReason reason = new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "下游连接异常");
        this.onClose(downstreamSession, reason);
    }

}