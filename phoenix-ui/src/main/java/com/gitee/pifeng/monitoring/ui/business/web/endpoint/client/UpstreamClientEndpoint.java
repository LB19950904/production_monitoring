package com.gitee.pifeng.monitoring.ui.business.web.endpoint.client;

import com.gitee.pifeng.monitoring.common.web.core.websocket.WebSocketRelayHelper;
import com.gitee.pifeng.monitoring.ui.business.web.endpoint.server.RelayWebSocketEndpoint;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.*;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * 用于连接上游 WebSocket 服务的客户端端点，由主服务端点 {@link RelayWebSocketEndpoint} 创建
 * </p>
 * 主要职责：<br>
 * 1. 连接到指定的上游 WebSocket 服务；<br>
 * 2. 接收上游服务发送的消息；<br>
 * 3. 将消息转发给对应的下游客户端；<br>
 * 4. 处理上游连接的关闭和异常事件；<br>
 * 5. 维护与主服务端点的状态同步。<br>
 * 设计特点：<br>
 * - 轻量级设计，只处理消息转发和事件响应；<br>
 * - 线程安全，所有共享资源都通过参数传递；<br>
 * - 自动清理失效的配对关系，确保系统状态一致。
 *
 * @author 皮锋
 * @custom.date 2026/1/30 16:46
 */
@Slf4j
@ClientEndpoint
public class UpstreamClientEndpoint {

    /**
     * 下游到上游的会话配对映射表
     */
    private final ConcurrentMap<String, String> downstreamToUpstream;

    /**
     * 上游到下游的会话配对映射表
     */
    private final ConcurrentMap<String, String> upstreamToDownstream;

    /**
     * Session对象缓存
     */
    private final Cache<String, Session> sessionCache;

    /**
     * <p>
     * 构造函数，初始化客户端端点，接收主服务端点传递的共享资源
     * </p>
     *
     * @param downstreamToUpstream 下游到上游的配对映射表
     * @param upstreamToDownstream 上游到下游的配对映射表
     * @param sessionCache         Session对象缓存
     * @author 皮锋
     * @custom.date 2026/1/30 16:51
     */
    public UpstreamClientEndpoint(ConcurrentMap<String, String> downstreamToUpstream,
                                  ConcurrentMap<String, String> upstreamToDownstream,
                                  Cache<String, Session> sessionCache) {
        this.downstreamToUpstream = downstreamToUpstream;
        this.upstreamToDownstream = upstreamToDownstream;
        this.sessionCache = sessionCache;
        log.info("连接上游 WebSocket 服务的客户端端点创建完成！");
    }

    /**
     * <p>
     * 处理从上游服务端收到的文本消息，当上游游服务端发送消息到中继服务时，本方法被调用
     * </p>
     * 主要执行以下操作：<br>
     * 1. 根据上游 Session ID 查找配对的下游 Session ID；<br>
     * 2. 从缓存中获取下游 {@link Session} 对象；<br>
     * 3. 检查下游 {@link Session} 是否可用；<br>
     * 4. 将消息异步转发到下游 {@link Session}；<br>
     * 5. 处理转发失败情况，清理失效的配对关系。
     *
     * @param message         上游服务发送的文本消息
     * @param upstreamSession 发送消息的上游 WebSocket 会话
     * @author 皮锋
     * @custom.date 2026/1/30 16:53
     */
    @OnMessage
    public void onMessage(String message, Session upstreamSession) {
        String upstreamSessionId = upstreamSession.getId();
        // 从上游到下游映射表中查找配对的下游 Session ID
        String downstreamSessionId = this.upstreamToDownstream.get(upstreamSessionId);
        if (downstreamSessionId == null) {
            log.warn("WebSocket无配对下游，上游Session[{}]！", upstreamSessionId);
            return;
        }
        // 从缓存获取下游 Session 对象
        Session downstreamSession = this.sessionCache.getIfPresent(downstreamSessionId);
        if (downstreamSession == null || !downstreamSession.isOpen()) {
            String msg = String.format("WebSocket下游连接失效，下游Session[%s]！", downstreamSessionId);
            log.warn(msg);
            // 清理一对会话的配对关系和资源
            WebSocketRelayHelper.cleanupPair(this.sessionCache, this.downstreamToUpstream, this.upstreamToDownstream, downstreamSessionId, upstreamSessionId, CloseReason.CloseCodes.UNEXPECTED_CONDITION, msg);
            return;
        }
        // 异步转发消息到下游
        downstreamSession.getAsyncRemote().sendText(message, sendResult -> {
            if (sendResult.isOK()) {
                if (log.isDebugEnabled()) {
                    log.debug("WebSocket消息转发成功，上游Session[{}] -> 下游Session[{}]，消息长度：{}，消息：{}", upstreamSessionId, downstreamSessionId, message.length(), message);
                }
            } else {
                String expMsg = sendResult.getException().getMessage();
                String msg = String.format("WebSocket消息转发失败，上游Session[%s] -> 下游Session[%s]，异常：%s", upstreamSessionId, downstreamSessionId, expMsg);
                log.error(msg);
                // 清理一对会话的配对关系和资源
                WebSocketRelayHelper.cleanupPair(this.sessionCache, this.downstreamToUpstream, this.upstreamToDownstream, downstreamSessionId, upstreamSessionId, CloseReason.CloseCodes.UNEXPECTED_CONDITION, msg);
            }
        });
    }

    /**
     * <p>
     * 处理上游连接关闭事件，当上游 WebSocket 连接关闭时，此方法被调用
     * </p>
     * 主要执行以下操作：<br>
     * 1. 查找配对的下游 Session ID；<br>
     * 2. 清理配对关系和相关资源；<br>
     * 3. 记录连接关闭日志。
     *
     * @param upstreamSession 关闭的上游 WebSocket 会话
     * @param reason          连接关闭的原因
     * @author 皮锋
     * @custom.date 2026/1/30 17:00
     */
    @OnClose
    public void onClose(Session upstreamSession, CloseReason reason) {
        String upstreamSessionId = upstreamSession.getId();
        // 查找配对的下游 Session 并清理
        String downstreamSessionId = this.upstreamToDownstream.get(upstreamSessionId);
        if (downstreamSessionId != null) {
            // 清理一对会话的配对关系和资源
            WebSocketRelayHelper.cleanupPair(this.sessionCache, this.downstreamToUpstream, this.upstreamToDownstream, downstreamSessionId, upstreamSessionId, reason.getCloseCode(), reason.getReasonPhrase());
        } else {
            // 清理单个会话资源
            WebSocketRelayHelper.cleanupSession(this.sessionCache, upstreamSessionId, reason.getCloseCode(), reason.getReasonPhrase());
        }
        log.info("WebSocket上游Session[{}]资源清理完成，上游活跃连接数：{}，下游活跃连接数：{}", upstreamSessionId, this.upstreamToDownstream.size(), this.downstreamToUpstream.size());
    }

    /**
     * <p>
     * 处理上游连接异常事件，当上游连接发生异常时，此方法被调用
     * </p>
     * 主要执行以下操作：<br>
     * 1. 记录异常日志；<br>
     * 2. 构造统一的关闭原因；<br>
     * 3. 调用 onClose() 方法执行清理。
     *
     * @param upstreamSession 发生异常的上游 WebSocket 会话
     * @param throwable       具体的异常对象
     * @author 皮锋
     * @custom.date 2026/1/30 17:07
     */
    @OnError
    public void onError(Session upstreamSession, Throwable throwable) {
        String upstreamSessionId = upstreamSession.getId();
        log.error("WebSocket上游连接异常，上游Session[{}]，异常原因：{}", upstreamSessionId, throwable.getMessage());
        CloseReason reason = new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "上游连接异常");
        this.onClose(upstreamSession, reason);
    }

}