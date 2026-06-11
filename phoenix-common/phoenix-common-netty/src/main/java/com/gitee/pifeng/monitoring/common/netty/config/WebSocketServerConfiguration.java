package com.gitee.pifeng.monitoring.common.netty.config;

import com.gitee.pifeng.monitoring.common.netty.config.property.WebSocketProperties;
import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketServer;
import com.gitee.pifeng.monitoring.common.netty.inf.IWebSocketClusterStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * WebSocket 服务端配置
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/29 14:55
 */
@Configuration
@ConditionalOnProperty(name = "ws.server.enable", havingValue = "true")
@AutoConfigureAfter(value = {WebSocketClusterStoreConfiguration.class})
public class WebSocketServerConfiguration {

    /**
     * WebSocket 服务器配置
     */
    @Autowired
    private WebSocketProperties webSocketProperties;

    /**
     * <p>
     * 在程序启动的时候开启 WebSocket 服务，在程序关闭的时候关闭 WebSocket 服务
     * </p>
     *
     * @param clusterStore {@link IWebSocketClusterStore} 保存 WebSocket client 连接到哪个具体的 server，集群部署时使用
     * @return {@link WebSocketServer} WebSocket 服务端
     * @author 皮锋
     * @custom.date 2023/3/30 16:40
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public WebSocketServer webSocketServer(@Autowired(required = false) IWebSocketClusterStore clusterStore) {
        WebSocketServer webSocketServer = new WebSocketServer();
        webSocketServer.setHost(this.webSocketProperties.getServer().getHost());
        webSocketServer.setPort(this.webSocketProperties.getServer().getPort());
        webSocketServer.setSsl(this.webSocketProperties.getServer().getSsl());
        webSocketServer.setPath(this.webSocketProperties.getServer().getPath());
        webSocketServer.setClientConnectHost(this.webSocketProperties.getServer().getClientConnectHost());
        if (clusterStore != null) {
            webSocketServer.setClusterStore(clusterStore);
        }
        return webSocketServer;
    }

}
