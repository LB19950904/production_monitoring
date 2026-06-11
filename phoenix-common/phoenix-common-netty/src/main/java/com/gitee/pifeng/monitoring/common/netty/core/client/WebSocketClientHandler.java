package com.gitee.pifeng.monitoring.common.netty.core.client;

import com.gitee.pifeng.monitoring.common.netty.inf.IWebSocketReadListener;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * WebSocket 客户端消息处理器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/4/25 9:03
 */
@Slf4j
public class WebSocketClientHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    /**
     * WebSocket 客户端
     */
    private final WebSocketClient client;

    /**
     * ChannelPromise 接口扩展了 {@link Promise} 和 {@link ChannelFuture}，绑定了 {@link Channel}，既设置异步执行结果同时又具备了监听处理结果的功能，
     * 是 Netty 实际编程使用的表示异步执行的接口，其实现类为 {@link DefaultChannelPromise}。
     */
    private ChannelPromise promise;

    /**
     * WebSocket 读监听器接口
     */
    private IWebSocketReadListener webSocketReadListener;

    /**
     * <p>
     * 注册 WebSocket 读监听器
     * </p>
     *
     * @param webSocketReadListener WebSocket 读监听器接口
     * @author 皮锋
     * @custom.date 2023/5/4 8:17
     */
    public void registerWebSocketReadListener(IWebSocketReadListener webSocketReadListener) {
        this.webSocketReadListener = webSocketReadListener;
    }

    /**
     * <p>
     * 构造方法
     * </p>
     *
     * @param client WebSocket 客户端
     * @author 皮锋
     * @custom.date 2023/4/30 22:17
     */
    public WebSocketClientHandler(WebSocketClient client) {
        this.client = client;
    }

    /**
     * <p>
     * 注册 {@link ChannelFuture}
     * </p>
     *
     * @return {@link ChannelFuture} 异步IO操作的结果
     * @author 皮锋
     * @custom.date 2023/4/30 22:51
     */
    public ChannelFuture registerFuture() {
        return this.promise;
    }

    /**
     * <p>
     * 添加 {@link ChannelPromise}，当把 {@link ChannelHandler} 添加到 {@link ChannelPipeline} 中时被调用
     * </p>
     *
     * @param ctx {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *            ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *            每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *            ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @author 皮锋
     * @custom.date 2023/4/30 22:48
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        this.promise = ctx.newPromise();
    }

    /**
     * <p>
     * 当 {@link Channel} 从它的 {@link EventLoop} 注销并且无法处理任何I/O时被调用
     * </p>
     *
     * @param ctx {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *            ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *            每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *            ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @author 皮锋
     * @custom.date 2023/4/30 22:30
     */
    @Override
    public void channelUnregistered(final ChannelHandlerContext ctx) {
        this.client.setConnected(false);
        // 定时任务一直重连
        ctx.channel().eventLoop().schedule(() -> {
            log.warn("尝试重连WebSocket服务端，URL: {}", this.client.getUrl());
            try {
                // 重连
                this.client.connect(true);
            } catch (Throwable e) {
                log.error("重连WebSocket服务端出错，URL: {}", this.client.getUrl(), e.getCause());
            }
        }, this.client.getReconnectDelay(), TimeUnit.SECONDS);
    }

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
            ctx.writeAndFlush(new PingWebSocketFrame());
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * <p>
     * 当处理过程中在 {@link ChannelPipeline} 中有错误产生时被调用
     * </p>
     *
     * @param ctx   {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *              ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *              每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *              ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param cause 异常/错误
     * @author 皮锋
     * @custom.date 2023/4/24 8:26
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("WebSocket客户端出错，URL：{}", this.client.getUrl(), cause);
        if (!promise.isDone()) {
            promise.setFailure(cause);
        }
        ctx.close();
    }

    /**
     * <p>
     * 读取从 WebSocket 发送过来的数据
     * </p>
     *
     * @param ctx   {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *              ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *              每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *              ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param frame WebSocket 数据帧
     * @author 皮锋
     * @custom.date 2023/4/30 22:46
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame frame) {
        // 触发监听器，通知监听器读取从 WebSocket 发送过来的数据
        this.webSocketReadListener.read(ctx, frame);
    }

    /**
     * <p>
     * 向 WebSocket 服务端发送消息
     * </p>
     *
     * @param message 文本消息
     * @author 皮锋
     * @custom.date 2023/4/30 21:33
     */
    public void sendMessage(String message) {
        this.promise.channel().writeAndFlush(new TextWebSocketFrame(message));
    }

}
