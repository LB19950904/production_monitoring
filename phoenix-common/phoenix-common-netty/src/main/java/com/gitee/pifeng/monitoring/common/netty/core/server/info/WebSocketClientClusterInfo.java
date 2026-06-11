package com.gitee.pifeng.monitoring.common.netty.core.server.info;

import lombok.Data;

/**
 * <p>
 * WebSocket 客户端集群信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/31 10:10
 */
@Data
public class WebSocketClientClusterInfo {

    /**
     * client本身以哪个ip连接到server
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    /**
     * 版本
     */
    private String version;

    /**
     * client连接到的server的ip
     */
    private String clientConnectHost;

    /**
     * <p>
     * 构造方法
     * </p>
     *
     * @param webSocketClientInfo WebSocket 客户端信息
     * @param clientConnectHost   client 连接到的 server 的 ip
     * @author 皮锋
     * @custom.date 2023/3/31 10:17
     */
    public WebSocketClientClusterInfo(WebSocketClientInfo webSocketClientInfo, String clientConnectHost) {
        this.host = webSocketClientInfo.getHost();
        this.port = webSocketClientInfo.getPort();
        this.version = webSocketClientInfo.getVersion();
        this.clientConnectHost = clientConnectHost;
    }

}
