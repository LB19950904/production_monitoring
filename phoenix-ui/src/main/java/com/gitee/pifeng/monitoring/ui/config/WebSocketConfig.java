package com.gitee.pifeng.monitoring.ui.config;

import com.gitee.pifeng.monitoring.ui.business.web.endpoint.server.RelayWebSocketEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.websocket.server.ServerEndpoint;

/**
 * <p>
 * WebSocket配置
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/1/26 17:02
 */
@Slf4j
@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebSocketConfig {

    /**
     * <p>
     * 创建并注册 {@link ServerEndpointExporter} Bean
     * </p>
     * 该 Bean 负责在应用启动时，自动检测所有被 {@link ServerEndpoint}
     * 注解标记的类，并将其注册为 WebSocket 端点，从而使其能够响应客户端的 WebSocket 连接请求。
     *
     * @return {@link ServerEndpointExporter} 实例
     * @author 皮锋
     * @custom.date 2026/1/26 17:14
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        ServerEndpointExporter serverEndpointExporter = new ServerEndpointExporter();
        // 手动指定要注册的端点类（传原始 Class，不是 Bean 实例）
        serverEndpointExporter.setAnnotatedEndpointClasses(RelayWebSocketEndpoint.class);
        log.info("WebSocket配置成功！");
        return serverEndpointExporter;
    }

}