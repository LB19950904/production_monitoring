package com.gitee.pifeng.monitoring.server.business.server.websocket.handler.impl;

import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.common.exception.WebSocketException;
import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketServer;
import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketSimpleChannelInboundHandler;
import com.gitee.pifeng.monitoring.common.netty.core.server.info.WebSocketClientInfo;
import com.gitee.pifeng.monitoring.common.util.MsgPayloadUtils;
import com.gitee.pifeng.monitoring.plug.constant.WebSocketBusinessTypeConstants;
import com.gitee.pifeng.monitoring.plug.constant.WebSocketCloseReasonEnums;
import com.gitee.pifeng.monitoring.plug.core.wsclient.WebsocketClientIdGenerator;
import com.gitee.pifeng.monitoring.server.business.server.websocket.dispatcher.WebSocketMessageDispatcher;
import com.gitee.pifeng.monitoring.server.business.server.websocket.handler.IWebSocketBusinessHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.gitee.pifeng.monitoring.plug.constant.WebSocketPkgPayloadWhitelistConstants.UPSTREAM_ALLOWED_CLASS_NAMES;

/**
 * <p>
 * 监控 WebSocket 数据帧处理器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/2/24 15:49
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "ws.server.enable", havingValue = "true")
public class MonitoringFrameHandler implements IWebSocketBusinessHandler {

    /**
     * WebSocket 消息分发器
     */
    @Autowired
    private WebSocketMessageDispatcher dispatcher;

    /**
     * WebSocket 服务端
     */
    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public String businessType() {
        return WebSocketBusinessTypeConstants.MONITORING;
    }

    /**
     * <p>
     * 处理 WebSocket 握手完成后的业务逻辑
     * </p>
     * 在此方法中可执行认证、会话绑定、初始化资源、向客户端发送欢迎消息等操作，若业务不支持当前连接，应主动关闭 {@link Channel}
     *
     * @param handler       WebSocket 入站处理器上下文
     * @param ctx           Netty Channel 上下文，用于发送消息或关闭连接
     * @param handshake     握手完成事件，包含原始 HTTP 请求信息
     * @param uriComponents 解析后的 URI 组件（含路径、查询参数等）
     * @author 皮锋
     * @custom.date 2026/2/8 11:48
     */
    @Override
    public void handle(WebSocketSimpleChannelInboundHandler handler, ChannelHandlerContext ctx, HandshakeComplete handshake, UriComponents uriComponents) {
        String uri = handshake.requestUri();
        MultiValueMap<String, String> parameters = uriComponents.getQueryParams();
        // 客户端端点
        String endpoint = parameters.getFirst("endpoint");
        // 客户端实例唯一ID
        String instanceId = parameters.getFirst("instanceId");
        if (StringUtils.isBlank(endpoint)) {
            log.warn("Monitoring WebSocket 连接缺少 endpoint 参数，URI: {}", uri);
            // 可选：主动关闭连接
            ctx.writeAndFlush(new CloseWebSocketFrame(1008, "Monitoring WebSocket 连接缺少 endpoint 参数！"));
            // 依赖 WebSocketServerProtocolHandler 自动关闭连接
            // .addListener(ChannelFutureListener.CLOSE);
            return;
        }
        if (StringUtils.isBlank(instanceId)) {
            log.warn("Monitoring WebSocket 连接缺少 instanceId 参数，URI: {}", uri);
            // 可选：主动关闭连接
            ctx.writeAndFlush(new CloseWebSocketFrame(1008, "Monitoring WebSocket 连接缺少 instanceId 参数！"));
            // 依赖 WebSocketServerProtocolHandler 自动关闭连接
            // .addListener(ChannelFutureListener.CLOSE);
            return;
        }
        // 构造全局唯一的客户端标识
        final String websocketClientId = WebsocketClientIdGenerator.generate(endpoint, instanceId);
        // 检查是否已有相同标识的客户端在线（防止重复注册）
        Optional<WebSocketClientInfo> socketClientInfo = this.webSocketServer.findClient(websocketClientId);
        if (socketClientInfo.isPresent()) {
            WebSocketClientInfo oldWebSocketClientInfo = socketClientInfo.get();
            String host = oldWebSocketClientInfo.getHost();
            int port = oldWebSocketClientInfo.getPort();
            log.warn("检测到重复 Monitoring WebSocket 连接，客户端：{}，地址：{}:{}！即将关闭旧的连接，替换为新的连接！", websocketClientId, host, port);
            ChannelHandlerContext oldCtx = oldWebSocketClientInfo.getChannelHandlerContext();
            // 立即从服务端移除旧客户端信息，防止并发问题
            this.webSocketServer.removeClient(websocketClientId);
            // 4000 是我应用层自定义的异常码
            oldCtx.writeAndFlush(new CloseWebSocketFrame(WebSocketCloseReasonEnums.DUPLICATE_CONNECTION.getCode(), WebSocketCloseReasonEnums.DUPLICATE_CONNECTION.getReason()))
                    .addListener(future -> {
                        if (future.isSuccess()) {
                            log.info("旧的 Monitoring WebSocket 客户端[{}]连接关闭指令发送成功！", websocketClientId);
                        } else {
                            log.warn("旧的 Monitoring WebSocket 客户端[{}]连接关闭指令发送失败！", websocketClientId, future.cause());
                        }
                    });
            // 用原子标志确保 doRegister 只被执行一次：
            final AtomicBoolean registered = new AtomicBoolean(false);
            // 超时保护：防止旧连接僵死导致新连接永远无法注册
            ctx.channel().eventLoop().schedule(() -> {
                if (registered.compareAndSet(false, true)) {
                    log.warn("等待旧的 Monitoring WebSocket 客户端[{}]连接关闭超时，开始强制注册新连接！", websocketClientId);
                    this.doRegister(ctx, handshake, websocketClientId);
                }
            }, 5, TimeUnit.SECONDS);
            // 优先路径：旧连接正常关闭后立即注册
            oldCtx.channel().closeFuture().addListener(future -> {
                if (registered.compareAndSet(false, true)) {
                    log.info("旧的 Monitoring WebSocket 客户端[{}]连接已关闭，现在开始注册新连接！", websocketClientId);
                    this.doRegister(ctx, handshake, websocketClientId);
                }
            });
            return;
        }
        // 没有重复，直接注册
        this.doRegister(ctx, handshake, websocketClientId);
    }

    /**
     * <p>
     * 注册 Monitoring WebSocket 客户端
     * </p>
     *
     * @param ctx               Netty Channel 上下文
     * @param handshake         握手完成事件，包含原始 HTTP 请求信息
     * @param websocketClientId 全局唯一的客户端标识
     * @author 皮锋
     * @custom.date 2026/3/17 15:29
     */
    private void doRegister(ChannelHandlerContext ctx, HandshakeComplete handshake, String websocketClientId) {
        // 创建新的客户端信息对象
        WebSocketClientInfo webSocketClient = new WebSocketClientInfo(ctx, handshake, null);
        // 将客户端注册到 WebSocket 服务端（以 websocketClientId 为键）
        this.webSocketServer.addClient(websocketClientId, webSocketClient);
        // 注册连接关闭监听器：当客户端断开时，自动从服务端移除该客户端
        ctx.channel().closeFuture().addListener(future -> {
            log.info("Monitoring WebSocket 客户端连接已关闭，正在清理客户端：{}", websocketClientId);
            this.webSocketServer.removeClient(websocketClientId);
        });
        log.info("Monitoring WebSocket 客户端注册成功：{}，地址：{}:{}，URI：{}", websocketClientId, webSocketClient.getHost(), webSocketClient.getPort(), handshake.requestUri());
    }

    /**
     * <p>
     * 向指定监控客户端发送消息
     * </p>
     *
     * @param websocketClientId 客户端唯一标识（格式：phoenix_{endpoint}_{instanceId}）
     * @param requestPackage    待发送的 WebSocket 请求包，不可为 {@code null}
     * @author 皮锋
     * @custom.date 2026/2/25 15:08
     */
    public void sendMsgToClient(String websocketClientId, WebSocketPackage requestPackage) {
        // 根据客户端 ID 和请求包，校验并获取有效的 ChannelHandlerContext
        ChannelHandlerContext ctx = this.validateAndResolveContext(websocketClientId, requestPackage);
        if (ctx == null) {
            return;
        }
        String requestPackageJson = requestPackage.toJsonString();
        // 打印发送的数据包
        if (log.isDebugEnabled()) {
            log.debug("发送数据包：{}", requestPackageJson);
        }
        // 将 明文JSON字符串 转换成 密文JSON字符串
        String encryptStr = MsgPayloadUtils.encryptPayload(requestPackageJson);
        ctx.writeAndFlush(new TextWebSocketFrame(encryptStr)).addListener(future -> {
            if (!future.isSuccess()) {
                log.warn("向 Monitoring WebSocket 客户端[{}]发送消息失败！", websocketClientId, future.cause());
            }
        });
    }

    /**
     * <p>
     * 向指定监控客户端同步发送消息，并等待发送操作完成或超时
     * </p>
     * 该方法会阻塞当前线程，直到消息成功写入 {@link Channel}、发送失败或达到指定超时时间，
     * 若客户端不存在、连接已断开或发送过程中发生异常，将抛出 {@link WebSocketException}
     *
     * @param websocketClientId 客户端唯一标识（格式：phoenix_{endpoint}_{instanceId}）
     * @param requestPackage    待发送的 WebSocket 请求包，不可为 {@code null}
     * @param timeout           超时时间数值，必须大于 0
     * @param unit              超时时间单位，不可为 {@code null}
     * @throws WebSocketException 当客户端不存在、连接已关闭、发送失败或操作超时时抛出
     * @author 皮锋
     * @custom.date 2026/2/27 16:16
     */
    public void sendMsgToClientSync(String websocketClientId, WebSocketPackage requestPackage, long timeout, TimeUnit unit) {
        // 根据客户端 ID 和请求包，校验并获取有效的 ChannelHandlerContext
        ChannelHandlerContext ctx = this.validateAndResolveContext(websocketClientId, requestPackage);
        if (ctx == null) {
            throw new WebSocketException("无法向 Monitoring WebSocket 客户端[" + websocketClientId + "]发送消息：客户端不存在或连接已断开！");
        }
        String requestPackageJson = requestPackage.toJsonString();
        // 打印发送的数据包
        if (log.isDebugEnabled()) {
            log.debug("发送数据包：{}", requestPackageJson);
        }
        // 将 明文JSON字符串 转换成 密文JSON字符串
        String encryptStr = MsgPayloadUtils.encryptPayload(requestPackageJson);
        ChannelFuture future = ctx.writeAndFlush(new TextWebSocketFrame(encryptStr));
        try {
            // 最多等待多久的时间
            boolean completed = future.await(timeout, unit);
            if (!completed) {
                // 超时：操作未在指定时间内完成
                throw new WebSocketException("向 Monitoring WebSocket 客户端[" + websocketClientId + "]发送消息超时（超过 " + unit.toMillis(timeout) + "ms）！");
            }
            if (!future.isSuccess()) {
                Throwable cause = future.cause();
                throw new WebSocketException("向 Monitoring WebSocket 客户端[" + websocketClientId + "]发送消息失败：" + (cause != null ? cause.getMessage() : "未知错误"), cause);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new WebSocketException("向 Monitoring WebSocket 客户端[" + websocketClientId + "]发送消息操作被中断！", e);
        }
    }

    /**
     * <p>
     * 根据客户端 ID 和请求包，校验并获取有效的 {@link ChannelHandlerContext}，若校验失败，会记录警告日志并返回 {@code null}；调用方应根据返回值决定是否继续发送
     * </p>
     *
     * @param websocketClientId 客户端唯一标识（格式：phoenix_{endpoint}_{instanceId}）
     * @param requestPackage    待发送的 WebSocket 请求包
     * @return 有效的 {@link ChannelHandlerContext}，若校验失败则返回 {@code null}
     * @author 皮锋
     * @custom.date 2026/2/27 15:51
     */
    private ChannelHandlerContext validateAndResolveContext(String websocketClientId, WebSocketPackage requestPackage) {
        if (StringUtils.isBlank(websocketClientId)) {
            log.warn("发送消息失败：websocketClientId 为空！");
            return null;
        }
        if (requestPackage == null) {
            log.warn("发送消息失败：requestPackage 为空！");
            return null;
        }
        Optional<WebSocketClientInfo> clientOpt = this.webSocketServer.findClient(websocketClientId);
        if (!clientOpt.isPresent()) {
            log.warn("未找到 Monitoring WebSocket 客户端：{}", websocketClientId);
            return null;
        }
        WebSocketClientInfo client = clientOpt.get();
        ChannelHandlerContext ctx = client.getChannelHandlerContext();
        // 检查 channel 是否仍活跃（避免向已关闭的连接写数据）
        if (ctx == null || !ctx.channel().isActive()) {
            log.warn("Monitoring WebSocket 客户端[{}]的 Channel 已不活跃，无法发送消息！", websocketClientId);
            // 清理僵尸连接
            this.webSocketServer.removeClient(websocketClientId);
            return null;
        }
        return ctx;
    }

    /**
     * <p>
     * 处理客户端发送的 WebSocket 文本消息
     * </p>
     *
     * @param ctx   Channel 上下文，可用于发送响应、关闭连接等操作
     * @param frame 接收到的 WebSocket 文本帧
     * @author 皮锋
     * @custom.date 2026/3/2 15:30
     */
    @Override
    @SneakyThrows
    public void onMessageReceived(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        // 判断非空 和 判断当前帧 是否是“分片传输”中的非最后一片
        if (frame == null || !frame.isFinalFragment()) {
            return;
        }
        String message = frame.text();
        if (StringUtils.isBlank(message)) {
            return;
        }
        // 将原始 “密文 WebSocket 消息JSON字符串” 解析并转换为 “WebSocketPackage 数据包”
        WebSocketPackage pkg = WebSocketPackage.convert(message, UPSTREAM_ALLOWED_CLASS_NAMES);
        // 委托给 WebSocket 消息分发器
        this.dispatcher.dispatch(ctx, pkg);
    }

}