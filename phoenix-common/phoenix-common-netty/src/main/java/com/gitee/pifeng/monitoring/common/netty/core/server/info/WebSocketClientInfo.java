package com.gitee.pifeng.monitoring.common.netty.core.server.info;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.gitee.pifeng.monitoring.common.netty.util.WebSocketClusterHttpUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * <p>
 * WebSocket 客户端信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/31 9:54
 */
@Getter
public class WebSocketClientInfo {

    /**
     * netty 通道处理器上下文
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private final ChannelHandlerContext channelHandlerContext;

    /**
     * 主机
     */
    private final String host;

    /**
     * 端口
     */
    private final int port;

    /**
     * 版本号
     */
    private final String version;

    /**
     * <p>
     * 构建 WebSocket 客户端信息对象
     * </p>
     *
     * @param ctx       Netty Channel 上下文
     * @param handshake 握手完成事件，包含原始 HTTP 请求信息
     * @param version   版本号
     * @author 皮锋
     * @custom.date 2026/3/17 16:57
     */
    public WebSocketClientInfo(ChannelHandlerContext ctx, HandshakeComplete handshake, String version) {
        // 前面可能有nginx代理
        HttpHeaders headers = handshake.requestHeaders();
        String host = WebSocketClusterHttpUtils.findClientIp(headers);
        if (host == null) {
            // 若未通过代理，则直接使用 Netty Channel 的远程地址
            SocketAddress remoteAddress = ctx.channel().remoteAddress();
            if (remoteAddress instanceof InetSocketAddress) {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) remoteAddress;
                this.host = inetSocketAddress.getHostString();
                this.port = inetSocketAddress.getPort();
            } else {
                this.host = "unknown";
                this.port = 0;
            }
        } else {
            // 使用代理传递的真实 IP
            this.host = host;
            // 尝试获取真实端口（部分代理可能不传递）
            Integer port = WebSocketClusterHttpUtils.findClientPort(headers);
            if (port != null) {
                this.port = port;
            } else {
                // 如果代理没有传递端口信息，使用默认端口或尝试从地址中解析
                SocketAddress remoteAddress = ctx.channel().remoteAddress();
                if (remoteAddress instanceof InetSocketAddress) {
                    this.port = ((InetSocketAddress) remoteAddress).getPort();
                } else {
                    this.port = 0;
                }
            }
        }
        this.channelHandlerContext = ctx;
        this.version = version;
    }

}
