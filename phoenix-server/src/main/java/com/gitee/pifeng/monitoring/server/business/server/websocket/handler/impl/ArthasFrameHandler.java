package com.gitee.pifeng.monitoring.server.business.server.websocket.handler.impl;

import com.gitee.pifeng.monitoring.common.constant.arthas.ArthasMethodConstants;
import com.gitee.pifeng.monitoring.common.constant.arthas.ArthasURIConstants;
import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketRelayHandler;
import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketServer;
import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketSimpleChannelInboundHandler;
import com.gitee.pifeng.monitoring.common.netty.core.server.info.WebSocketClientConnectionInfo;
import com.gitee.pifeng.monitoring.common.netty.core.server.info.WebSocketClientInfo;
import com.gitee.pifeng.monitoring.common.netty.util.NettyChannelUtils;
import com.gitee.pifeng.monitoring.plug.constant.WebSocketBusinessTypeConstants;
import com.gitee.pifeng.monitoring.server.business.server.websocket.handler.IWebSocketBusinessHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Arthas WebSocket 数据帧处理器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/30 17:12
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "ws.server.enable", havingValue = "true")
public class ArthasFrameHandler implements IWebSocketBusinessHandler {

    /**
     * WebSocket 服务端
     */
    @Autowired
    private WebSocketServer webSocketServer;

    @Override
    public String businessType() {
        return WebSocketBusinessTypeConstants.ARTHAS;
    }

    @Override
    public void handle(WebSocketSimpleChannelInboundHandler handler, ChannelHandlerContext ctx, HandshakeComplete handshake, UriComponents uriComponents) {
        String uri = handshake.requestUri();
        MultiValueMap<String, String> parameters = uriComponents.getQueryParams();
        String method = parameters.getFirst(ArthasURIConstants.METHOD);
        if (StringUtils.isBlank(method)) {
            log.warn("Arthas WebSocket 连接缺少 method 参数，URI：{}", uri);
            // 可选：主动关闭连接
            ctx.writeAndFlush(new CloseWebSocketFrame(1008, "Arthas WebSocket 连接缺少 method 参数！"));
            // 依赖 WebSocketServerProtocolHandler 自动关闭连接
            // .addListener(ChannelFutureListener.CLOSE);
            return;
        }
        switch (method) {
            // 前端浏览器
            case ArthasMethodConstants.CONNECT_ARTHAS:
                this.connectArthas(handler, ctx, parameters);
                break;
            // Arthas Agent 向服务端注册自身
            case ArthasMethodConstants.AGENT_REGISTER:
                this.agentRegister(ctx, handshake, uri);
                break;
            // 建立浏览器与已注册 Agent 之间的通信隧道
            case ArthasMethodConstants.OPEN_TUNNEL:
                String clientConnectionId = parameters.getFirst(ArthasURIConstants.CLIENT_CONNECTION_ID);
                this.openTunnel(ctx, clientConnectionId);
                break;
            default:
                break;
        }
    }

    /**
     * <p>
     * arthas客户端注册
     * </p>
     *
     * @param ctx        {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *                   ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *                   每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *                   ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param handshake  {@link HandshakeComplete} 握手已成功完成，通道已升级到网络套接字。
     * @param requestUri 请求URI
     * @author 皮锋
     * @custom.date 2023/3/31 11:12
     */
    private void agentRegister(ChannelHandlerContext ctx,
                               HandshakeComplete handshake,
                               String requestUri) {
        QueryStringDecoder queryDecoder = new QueryStringDecoder(requestUri);
        Map<String, List<String>> parameters = queryDecoder.parameters();

        String appName = null;
        List<String> appNameList = parameters.get(ArthasURIConstants.APP_NAME);
        if (CollectionUtils.isNotEmpty(appNameList)) {
            appName = appNameList.get(0);
        }
        // generate a random agent id
        String id;
        if (appName != null) {
            // 如果有传 app name，则生成带 app name前缀的id，方便管理
            id = appName + "_" + RandomStringUtils.random(20, true, true).toUpperCase();
        } else {
            id = RandomStringUtils.random(20, true, true).toUpperCase();
        }
        // agent传过来，则优先用 agent的
        List<String> idList = parameters.get(ArthasURIConstants.ID);
        if (CollectionUtils.isNotEmpty(idList)) {
            id = idList.get(0);
        }

        String arthasVersion = null;
        List<String> arthasVersionList = parameters.get(ArthasURIConstants.ARTHAS_VERSION);
        if (arthasVersionList != null && !arthasVersionList.isEmpty()) {
            arthasVersion = arthasVersionList.get(0);
        }

        final String finalId = id;

        // URI responseUri = new URI("response", null, "/", "method=" + ArthasMethodConstants.AGENT_REGISTER + "&id=" + id, null);
        URI responseUri = UriComponentsBuilder.newInstance().scheme(ArthasURIConstants.RESPONSE).path("/")
                .queryParam(ArthasURIConstants.METHOD, ArthasMethodConstants.AGENT_REGISTER).queryParam(ArthasURIConstants.ID, id).build()
                .encode().toUri();

        WebSocketClientInfo webSocketClient = new WebSocketClientInfo(ctx, handshake, arthasVersion);
        this.webSocketServer.addClient(id, webSocketClient);
        ctx.channel().closeFuture().addListener(future -> this.webSocketServer.removeClient(finalId));

        ctx.channel().writeAndFlush(new TextWebSocketFrame(responseUri.toString()));
        log.info("Arthas 客户端注册成功：{}，地址：{}:{}，URI：{}", finalId, webSocketClient.getHost(), webSocketClient.getPort(), requestUri);
    }

    /**
     * <p>
     * 连接arthas
     * </p>
     *
     * @param simpleChannelInboundHandler {@link WebSocketSimpleChannelInboundHandler} WebSocket 简单通道入站消息处理器
     * @param ctx                         {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *                                    ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *                                    每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *                                    ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param parameters                  参数
     * @author 皮锋
     * @custom.date 2023/3/31 14:35
     */
    private void connectArthas(WebSocketSimpleChannelInboundHandler simpleChannelInboundHandler,
                               ChannelHandlerContext ctx,
                               MultiValueMap<String, String> parameters) {
        List<String> agentId = parameters.getOrDefault("id", Collections.emptyList());

        if (agentId.isEmpty()) {
            String errorMsg = "Arthas Agent ID 不能为空，参数：" + parameters;
            log.error(errorMsg);
            ctx.channel().writeAndFlush(new CloseWebSocketFrame(1008, errorMsg));
            // throw new IllegalArgumentException("Arthas Agent ID 不能为空！");
            return;
        }

        log.info("尝试连接 Arthas Agent，ID：{}", agentId.get(0));

        Optional<WebSocketClientInfo> findAgent = this.webSocketServer.findClient(agentId.get(0));

        if (findAgent.isPresent()) {
            ChannelHandlerContext agentCtx = findAgent.get().getChannelHandlerContext();

            String clientConnectionId = RandomStringUtils.random(20, true, true).toUpperCase();

            log.info("生成客户端连接 ID：{}", clientConnectionId);
            // URI uri = new URI("response", null, "/",
            //        "method=" + ArthasMethodConstants.START_TUNNEL + "&id=" + agentId.get(0) + "&clientConnectionId=" + clientConnectionId, null);
            URI uri = UriComponentsBuilder.newInstance().scheme(ArthasURIConstants.RESPONSE).path("/")
                    .queryParam(ArthasURIConstants.METHOD, ArthasMethodConstants.START_TUNNEL).queryParam(ArthasURIConstants.ID, agentId)
                    .queryParam(ArthasURIConstants.CLIENT_CONNECTION_ID, clientConnectionId).build().toUri();

            log.info("发送隧道启动响应：{}", uri);

            WebSocketClientConnectionInfo clientConnectionInfo = new WebSocketClientConnectionInfo();
            SocketAddress remoteAddress = ctx.channel().remoteAddress();
            if (remoteAddress instanceof InetSocketAddress) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;
                clientConnectionInfo.setHost(inetSocketAddress.getHostString());
                clientConnectionInfo.setPort(inetSocketAddress.getPort());
            }
            clientConnectionInfo.setChannelHandlerContext(ctx);

            // when the agent open tunnel success, will set result into the promise
            Promise<Channel> promise = GlobalEventExecutor.INSTANCE.newPromise();
            promise.addListener((FutureListener<Channel>) future -> {
                final Channel outboundChannel = future.getNow();
                if (future.isSuccess()) {
                    ctx.pipeline().remove(simpleChannelInboundHandler);

                    // outboundChannel is form arthas agent
                    outboundChannel.pipeline().removeLast();

                    outboundChannel.pipeline().addLast(new WebSocketRelayHandler(ctx.channel()));
                    ctx.pipeline().addLast(new WebSocketRelayHandler(outboundChannel));
                } else {
                    log.error("等待 Agent 连接失败，Agent ID：{}, 客户端连接 ID：{}", agentId, clientConnectionId);
                    NettyChannelUtils.closeOnFlush(agentCtx.channel());
                }
            });

            clientConnectionInfo.setPromise(promise);
            this.webSocketServer.addClientConnectionInfo(clientConnectionId, clientConnectionInfo);
            ctx.channel().closeFuture().addListener(future -> this.webSocketServer.removeClientConnectionInfo(clientConnectionId));

            agentCtx.channel().writeAndFlush(new TextWebSocketFrame(uri.toString()));

            log.info("浏览器连接已建立，等待 Arthas Agent 打开隧道...");
            boolean awaitResult = promise.awaitUninterruptibly(20, TimeUnit.SECONDS);
            if (awaitResult) {
                log.info("Arthas Agent 隧道打开成功，Agent ID：{}, 客户端连接 ID：{}", agentId, clientConnectionId);
            } else {
                String errorMsg = String.format("等待 Arthas Agent 打开隧道超时，Agent ID：%s，客户端连接 ID：%s", agentId, clientConnectionId);
                log.error(errorMsg);
                ctx.channel().writeAndFlush(new CloseWebSocketFrame(1011, errorMsg));
                //ctx.close();
            }
        } else {
            String errorMsg = "未找到指定的 Arthas Agent，ID：" + agentId.get(0);
            // arthas官方源码中是2000，但是实践好像发现2000有点问题，就还是用了1008
            ctx.channel().writeAndFlush(new CloseWebSocketFrame(1008, errorMsg));
            // 依赖 WebSocketServerProtocolHandler 自动关闭连接
            // .addListener(ChannelFutureListener.CLOSE);
            log.error(errorMsg);
            // throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * <p>
     * 打开通道
     * </p>
     *
     * @param ctx                {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *                           ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *                           每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *                           ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param clientConnectionId 客户端连接ID
     * @author 皮锋
     * @custom.date 2023/3/31 16:01
     */
    private void openTunnel(ChannelHandlerContext ctx,
                            String clientConnectionId) {
        Optional<WebSocketClientConnectionInfo> infoOptional = this.webSocketServer.findClientConnection(clientConnectionId);

        if (infoOptional.isPresent()) {
            WebSocketClientConnectionInfo info = infoOptional.get();
            log.info("收到 Arthas Agent 隧道打开请求，客户端连接 ID：{}", clientConnectionId);

            Promise<Channel> promise = info.getPromise();
            promise.setSuccess(ctx.channel());
        } else {
            log.error("未找到对应的客户端连接，ID：{}", clientConnectionId);
        }
    }

}
