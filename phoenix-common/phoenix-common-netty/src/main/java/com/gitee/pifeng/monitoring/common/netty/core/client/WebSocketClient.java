package com.gitee.pifeng.monitoring.common.netty.core.client;

import com.gitee.pifeng.monitoring.common.constant.CommProtocolTypeEnums;
import com.gitee.pifeng.monitoring.common.netty.common.WebSocketConfigConstants;
import com.gitee.pifeng.monitoring.common.netty.inf.IWebSocketReadListener;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.QueryStringEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketClientProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.SSLException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * <p>
 * WebSocket 客户端，用于与 WebSocket 服务器建立长连接通信
 * </p>
 * <h3>使用示例：</h3>
 * <pre>
 * {@code
 * // 1. 创建消息监听器
 * IWebSocketReadListener listener = (ctx, frame) -> {
 *     if (frame instanceof TextWebSocketFrame) {
 *         String msg = ((TextWebSocketFrame) frame).text();
 *         log.info("收到消息: {}", msg);
 *     }
 * };
 *
 * // 2. 创建并连接客户端
 * WebSocketClient client = new WebSocketClient(listener);
 * client.setUrl("ws://localhost:8080/websocket");
 * ChannelFuture future = client.connect(false);
 *
 * // 3. 发送消息
 * client.sendMessage("Hello Server");
 *
 * // 4. 关闭连接
 * // client.stop();
 * }
 * </pre>
 *
 * <h3>主要功能：</h3>
 * <ul>
 *   <li>支持WS/WSS协议连接</li>
 *   <li>自动重连机制（默认5秒重试）</li>
 *   <li>心跳保活（30秒空闲发送Ping）</li>
 *   <li>异步消息收发</li>
 * </ul>
 *
 * @author 皮锋
 * @custom.date 2023/4/24 14:35
 * @see WebSocketClientHandler
 */
@Slf4j
public class WebSocketClient {

    /**
     * 处理I/O操作的线程池
     */
    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("monitoring-websocket-client", true));

    /**
     * WebSocket 客户端消息处理器
     */
    private WebSocketClientHandler handler;

    /**
     * url地址
     */
    @Setter
    @Getter
    private String url;

    /**
     * 重连时间间隔（秒）
     */
    @Setter
    @Getter
    private int reconnectDelay = 5;

    /**
     * 是否已连接服务端
     */
    @Setter
    @Getter
    private volatile boolean connected = false;

    /**
     * WebSocket 读监听器接口
     */
    private final IWebSocketReadListener webSocketReadListener;

    /**
     * <p>
     * 在构造方法中注册 WebSocket 读监听器
     * </p>
     *
     * @param webSocketReadListener WebSocket 读监听器接口
     * @author 皮锋
     * @custom.date 2023/5/4 8:17
     */
    public WebSocketClient(IWebSocketReadListener webSocketReadListener) {
        this.webSocketReadListener = webSocketReadListener;
    }

    /**
     * <p>
     * 连接 WebSocket 服务端
     * </p>
     *
     * @param reconnect 是否为重连
     * @return {@link ChannelFuture} 异步IO操作的结果
     * @throws SSLException         SSL异常
     * @throws URISyntaxException   URI语法异常
     * @throws InterruptedException 线程中断异常
     * @author 皮锋
     * @custom.date 2023/4/30 21:30
     */
    public ChannelFuture connect(boolean reconnect) throws SSLException, URISyntaxException, InterruptedException {
        QueryStringEncoder queryEncoder = new QueryStringEncoder(this.url);
        // ws://127.0.0.1:81/phoenix-ui
        final URI uri = queryEncoder.toUri();

        log.info("尝试连接WebSocket服务端, URL: {}", uri);

        String scheme = uri.getScheme();
        final String host = uri.getHost();
        final int port = uri.getPort();
        // 判断 WebSocket 协议是否正确
        if (!CommProtocolTypeEnums.WS.name().equalsIgnoreCase(scheme) && !CommProtocolTypeEnums.WSS.name().equalsIgnoreCase(scheme)) {
            throw new IllegalArgumentException("只支持 WS(S) 协议，URL：" + this.url);
        }
        final boolean ssl = CommProtocolTypeEnums.WSS.name().equalsIgnoreCase(scheme);
        final SslContext sslCtx;
        if (ssl) {
            sslCtx = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        } else {
            sslCtx = null;
        }
        // 配置 WebSocket 客户端
        WebSocketClientProtocolConfig clientProtocolConfig = WebSocketClientProtocolConfig.newBuilder()
                .webSocketUri(uri)
                .maxFramePayloadLength(WebSocketConfigConstants.MAX_HTTP_CONTENT_LENGTH)
                .build();

        this.handler = new WebSocketClientHandler(this);
        // 注册 WebSocket 读监听器
        this.handler.registerWebSocketReadListener(this.webSocketReadListener);

        Bootstrap bs = new Bootstrap();

        bs.group(this.eventLoopGroup)
                // 表示与远程节点建立连接的超时时间（以毫秒为单位）。
                // 如果在连接超时之前无法与远程节点建立连接，则连接将失败。
                // 需要注意的是，这个选项只是限制了连接的超时时间，如果从远程节点接收数据的时间超时，需要使用ReadTimeoutHandler选项
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                // 允许每次写入都立即发送，而无需等待缓冲区变满。这可以提高数据传输的实时性和响应性
                .option(ChannelOption.TCP_NODELAY, true)
                .channel(NioSocketChannel.class).remoteAddress(host, port)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        if (sslCtx != null) {
                            p.addLast(sslCtx.newHandler(ch.alloc(), host, port));
                        }
                        // 用于将HTTP消息编码为字节流以及将接收到的字节流解码为HTTP消息。
                        // 它可以处理HTTP协议的常见问题，例如处理较大的HTTP请求或响应，自动处理HTTP压缩等
                        p.addLast(new HttpClientCodec());
                        // HTTP消息聚合器，HttpObjectAggregator 是 Netty 提供的 HTTP 消息聚合器，通过它可以把 HttpMessage 和 HttpContent 聚合成一个 FullHttpRequest 或者 FullHttpResponse(取决于是处理请求还是响应），方便我们使用。
                        //另外，消息体比较大的话，可能还会分成好几个消息体来处理，HttpObjectAggregator 可以将这些消息聚合成一个完整的，方便我们处理。
                        p.addLast(new HttpObjectAggregator(WebSocketConfigConstants.MAX_HTTP_CONTENT_LENGTH));
                        // 用于检测空闲连接并进行相应的处理。它可以帮助我们检测连接是否处于空闲状态，
                        // 如果是则可以向对端发送心跳包，或者关闭连接以释放资源，从而避免出现空闲连接占用资源的情况。
                        // 它在客户端与服务端应用中都能够发挥作用。通过添加在ChannelPipeline中，IdleStateHandler会定时地检测连接的状态，
                        // 并在连接空闲时间超过设定的阈值时触发相应的事件。具体来说，它可以检测以下三种状态：
                        // 1. READER_IDLE：表示当前连接在一段时间内没有读取到数据。
                        // 2. WRITER_IDLE：表示当前连接在一段时间内没有写入数据。
                        // 3. ALL_IDLE：表示当前连接在一段时间内既没有读取到数据，也没有写入数据。
                        // 通过实现IdleStateHandler提供的回调方法，我们可以在连接空闲时间超过设定的阈值时执行相应的操作，比如向对端发送心跳包或者关闭连接等。
                        // IdleStateHandler可以用作网络连接维护的一个重要工具，可以有效防止因为空闲连接的长时间存在而使得服务器负荷过高，从而提高系统的稳定性和可靠性。
                        p.addLast(new IdleStateHandler(30, 30, WebSocketConfigConstants.IDLE_SECONDS));
                        // WebSocket客户端压缩处理器
                        // p.addLast(WebSocketClientCompressionHandler.INSTANCE);
                        // 1. 处理WebSocket连接的握手和关闭操作；
                        // 2. 封装WebSocket的文本、二进制消息和Ping/Pong消息等；
                        // 3. 处理WebSocket协议的编码和解码；
                        // 4. 为WebSocket客户端提供事件驱动的编程模型，方便开发者进行事件处理。
                        p.addLast(new WebSocketClientProtocolHandler(clientProtocolConfig));
                        p.addLast(handler);
                    }
                });
        // 连接
        ChannelFuture connectFuture = bs.connect();
        if (reconnect) {
            connectFuture.addListener((ChannelFutureListener) future -> {
                if (future.cause() != null) {
                    log.error("连接WebSocket服务端异常，URL：{}", this.url, future.cause());
                }
            });
        }
        // Netty中的一种同步等待机制，其作用是等待客户端与服务器端的连接成功，直到连接完成或发生异常才继续执行下面的代码
        connectFuture.sync();
        ChannelFuture channelFuture = this.handler.registerFuture();
        // 设置为已连接
        this.connected = true;
        return channelFuture;
    }

    /**
     * <p>
     * 关闭 WebSocket 客户端
     * </p>
     *
     * @author 皮锋
     * @custom.date 2023/4/25 8:29
     */
    public void stop() {
        this.eventLoopGroup.shutdownGracefully();
        log.info("WebSocket客户端优雅关闭！");
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
        this.handler.sendMessage(message);
    }

}
