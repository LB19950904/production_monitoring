package com.gitee.pifeng.monitoring.common.netty.core.server;

import com.gitee.pifeng.monitoring.common.netty.common.WebSocketConfigConstants;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * <p>
 * 通道初始化器 {@link ChannelInitializer}
 * </p>
 * {@link ChannelInitializer} 是一种特殊的 {@link ChannelInboundHandler}，可以通过一种简单的方式（调用 initChannel 方法）来初始化 {@link Channel}。<br>
 * {@link ChannelInitializer} 的主要目的是为程序员提供了一个简单的工具，用于在某个 {@link Channel} 注册到 {@link EventLoop} 后，对这个 {@link Channel} 执行一些初始化操作。
 * {@link ChannelInitializer} 虽然一开始会被注册到 {@link Channel} 相关的 pipeline 里，但是在初始化完成之后，{@link ChannelInitializer} 会将自己从 pipeline 中移除，不会影响后续的操作。
 * 注意：当 initChannel 被执行完后，会将当前的 handler 从 pipeline 中移除。
 *
 * @author hengyunabc
 * @custom.date 2023/3/29 9:39
 */
public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {

    /**
     * SSL 上下文
     */
    private final SslContext sslCtx;

    /**
     * WebSocket 服务端
     */
    private final WebSocketServer webSocketServer;

    /**
     * <p>
     * 构造方法
     * </p>
     *
     * @param webSocketServer WebSocket 服务端 {@link WebSocketServer}
     * @param sslCtx          {@link SslContext}
     * @author 皮锋
     * @custom.date 2023/3/29 12:22
     */
    public WebSocketServerInitializer(WebSocketServer webSocketServer, SslContext sslCtx) {
        this.sslCtx = sslCtx;
        this.webSocketServer = webSocketServer;
    }

    /**
     * <p>
     * 初始化通道
     * </p>
     *
     * @param ch socket通道
     * @author 皮锋
     * @custom.date 2023/3/29 12:17
     */
    @Override
    public void initChannel(SocketChannel ch) {
        // 职责链，负责事件在职责链中的有序传播，同时负责动态地编排职责链
        ChannelPipeline pipeline = ch.pipeline();
        if (this.sslCtx != null) {
            pipeline.addLast(this.sslCtx.newHandler(ch.alloc()));
        }
        // HTTP协议的编解码器，将请求和应答消息编码或解码成HTTP消息
        pipeline.addLast(new HttpServerCodec());
        // HTTP消息聚合器，HttpObjectAggregator 是 Netty 提供的 HTTP 消息聚合器，通过它可以把 HttpMessage 和 HttpContent 聚合成一个 FullHttpRequest 或者 FullHttpResponse(取决于是处理请求还是响应），方便我们使用。
        //另外，消息体比较大的话，可能还会分成好几个消息体来处理，HttpObjectAggregator 可以将这些消息聚合成一个完整的，方便我们处理。
        pipeline.addLast(new HttpObjectAggregator(WebSocketConfigConstants.MAX_HTTP_CONTENT_LENGTH));
        // WebSocket服务端压缩处理器
        pipeline.addLast(new WebSocketServerCompressionHandler());
        // 5分钟无入站数据触发 READER_IDLE
        pipeline.addLast(new IdleStateHandler(300, 0, 0));
        // 它负责网络套接字握手以及控制帧（Close、Ping、Pong）的处理
        pipeline.addLast(new WebSocketServerProtocolHandler(this.webSocketServer.getPath(), null, true, WebSocketConfigConstants.MAX_HTTP_CONTENT_LENGTH, false, true, 10000L));
        // WebSocket简单通道入站消息处理器
        pipeline.addLast(new WebSocketSimpleChannelInboundHandler());
    }

}
