package com.gitee.pifeng.monitoring.common.netty.core.server;

import com.gitee.pifeng.monitoring.common.netty.inf.IWebSocketFrameHandler;
import com.gitee.pifeng.monitoring.common.web.core.Invoker;
import com.gitee.pifeng.monitoring.common.web.core.InvokerHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * WebSocket 简单通道入站消息处理器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/4/3 17:31
 */
@Slf4j
public class WebSocketSimpleChannelInboundHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    /**
     * <p>
     * 当 {@link ChannelHandlerContext#fireUserEventTriggered(Object)} 方法被调用时被调用，因为一个 POJO 被传经了 {@link ChannelPipeline}
     * </p>
     *
     * @param ctx {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *            ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *            每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *            ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param evt 事件
     * @author 皮锋
     * @custom.date 2023/3/29 17:07
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE && ctx.channel().isActive()) {
                // 5分钟内未收到任何数据（包括 Ping、Pong、业务消息）
                log.warn("WebSocket 客户端 {} 超过5分钟未通信，关闭连接！", ctx.channel().remoteAddress());
                // 主动关闭连接
                ctx.writeAndFlush(new CloseWebSocketFrame(1001, "Idle timeout"));
                // 依赖 WebSocketServerProtocolHandler 自动关闭连接
                // .addListener(ChannelFutureListener.CLOSE);
            }
        } else {
            Invoker invoker = InvokerHolder.getInvoker(IWebSocketFrameHandler.class, "userEventTriggered");
            if (invoker == null) {
                return;
            }
            invoker.invoke(this, ctx, evt);
        }
    }

    /**
     * <p>
     * 当客户端有消息发过来时方法触发
     * </p>
     *
     * @param ctx   {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *              ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *              每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *              ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param frame {@link WebSocketFrame} Websocket 数据帧
     * @author 皮锋
     * @custom.date 2023/3/29 17:11
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception {
        Invoker invoker = InvokerHolder.getInvoker(IWebSocketFrameHandler.class, "channelRead0");
        if (invoker == null) {
            return;
        }
        invoker.invoke(this, ctx, frame);
    }

}
