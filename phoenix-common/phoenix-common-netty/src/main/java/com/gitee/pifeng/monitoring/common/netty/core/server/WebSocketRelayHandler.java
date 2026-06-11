package com.gitee.pifeng.monitoring.common.netty.core.server;

import com.gitee.pifeng.monitoring.common.netty.util.NettyChannelUtils;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * WebSocket 中继、转发的处理器
 * </p>
 * <a href="https://www.cnblogs.com/htkj/p/10932637.html">参考文档：Netty引导流程解读</a>
 *
 * @author 皮锋
 * @custom.date 2023/3/31 14:55
 */
@Slf4j
public final class WebSocketRelayHandler extends ChannelInboundHandlerAdapter {

    /**
     * <p>用于中继、转发的 netty 通道。</p>
     * {@link Channel} 是 netty 网络操作抽象类，它聚合了一组功能，包括但不限于网络的读、写，客户端发起连接，主动关闭连接，链路关闭，获取通信双方的网络地址等。
     */
    private final Channel relayChannel;

    /**
     * <p>
     * 构造方法
     * </p>
     *
     * @param relayChannel 用于中继、转发的 netty 通道
     * @author 皮锋
     * @custom.date 2023/4/21 10:52
     */
    public WebSocketRelayHandler(Channel relayChannel) {
        this.relayChannel = relayChannel;
    }

    /**
     * <p>
     * 当 {@link Channel} 处于活动状态时被调用（Channel 已经连接/绑定并且已经就绪）
     * </p>
     *
     * @param ctx {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *            ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *            每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *            ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @author 皮锋
     * @custom.date 2023/4/24 8:20
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        // writeAndFlush(Unpooled.EMPTY_BUFFER) 表示将空的字节缓冲区(Unpooled.EMPTY_BUFFER)写入当前的 ChannelPipeline 并立即刷新（发送）它。
        // 该方法不会产生任何数据的写入，因为空缓冲区中没有任何内容，但是它将导致 ChannelPipeline 中的所有处理器将缓冲区刷新到套接字。
        // 该方法一般用于发送一个必须立即传输的控制信号，而不需要任何有效载荷数据。
        // 例如，在实现 heartbeat 检查时，可以使用 writeAndFlush(Unpooled.EMPTY_BUFFER) 发送一个空的字节缓冲区来保持连接的活动状态。
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    /**
     * <p>
     * 当 {@link Channel} 离开活动状态并且不再连接它的远程节点时被调用
     * </p>
     *
     * @param ctx {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *            ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *            每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *            ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @author 皮锋
     * @custom.date 2023/4/24 8:24
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        //if (this.relayChannel.isActive()) {
        // Unpooled.EMPTY_BUFFER表示空消息，addListener(ChannelFutureListener.CLOSE)表示写完后，就关闭连接
        // this.relayChannel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        //}
        // 改成了直接用工具类
        NettyChannelUtils.closeOnFlush(ctx.channel());
    }

    /**
     * <p>
     * 接收到消息后，将其透明转发至对端通道，实现 WebSocket 隧道中继，消息在此被消费，不会继续向 Pipeline 后续 Handler 传递。
     * </p>
     *
     * @param ctx {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *            ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *            每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *            ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param msg 数据/消息
     * @author 皮锋
     * @custom.date 2023/4/24 8:22
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (this.relayChannel.isActive()) {
            // write仅仅是写到缓冲区，没有发送，flush才会真正写到网络上去，writeAndFlush方法表示写入并发送消息
            this.relayChannel.writeAndFlush(msg);
        } else {
            ReferenceCountUtil.release(msg);
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
        log.error("WebSocket 中继发生异常，远程地址：{}", ctx.channel().remoteAddress(), cause);
        // 关闭
        ctx.close();
    }

}
