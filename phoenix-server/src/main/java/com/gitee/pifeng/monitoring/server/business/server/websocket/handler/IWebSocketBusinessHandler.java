package com.gitee.pifeng.monitoring.server.business.server.websocket.handler;

import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketSimpleChannelInboundHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.springframework.web.util.UriComponents;

/**
 * <p>
 * WebSocket 业务处理器接口
 * </p>
 * 每个具体的 WebSocket 业务（如 Arthas 诊断、指标上报等）应实现此接口，并通过 {@code @Component} 注册为 Spring Bean。
 *
 * @author 皮锋
 * @custom.date 2026/2/8 01:07
 */
public interface IWebSocketBusinessHandler {

    /**
     * <p>
     * 返回该处理器支持的业务类型标识
     * </p>
     *
     * @return 业务类型字符串，建议使用小写字母
     * @author 皮锋
     * @custom.date 2026/2/8 11:45
     */
    String businessType();

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
    void handle(WebSocketSimpleChannelInboundHandler handler,
                ChannelHandlerContext ctx,
                WebSocketServerProtocolHandler.HandshakeComplete handshake,
                UriComponents uriComponents);

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
    default void onMessageReceived(ChannelHandlerContext ctx, TextWebSocketFrame frame) {
        // 默认不处理，子类可 override
    }

}