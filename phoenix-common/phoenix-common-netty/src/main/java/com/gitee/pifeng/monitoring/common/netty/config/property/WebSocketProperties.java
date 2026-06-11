package com.gitee.pifeng.monitoring.common.netty.config.property;

import com.gitee.pifeng.monitoring.common.netty.common.WebSocketConfigConstants;
import com.gitee.pifeng.monitoring.common.util.server.NetUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * <p>
 * WebSocket 配置属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/30 9:06
 */
@Data
@Component
@ConfigurationProperties(prefix = "ws")
public class WebSocketProperties {

    /**
     * WebSocket 服务器配置
     */
    private Server server;

    /**
     * <p>
     * WebSocket 服务器配置
     * </p>
     *
     * @author 皮锋
     * @custom.date 2023/3/30 9:09
     */
    @Data
    public static class Server {

        /**
         * 是否开启 WebSocket
         */
        private Boolean enable;

        /**
         * 主机名
         */
        private String host;

        /**
         * 端口
         */
        private int port;

        /**
         * SSL 配置
         */
        private Ssl ssl;

        /**
         * 路径
         */
        private String path = WebSocketConfigConstants.DEFAULT_WEBSOCKET_PATH;

        /**
         * 客户端连接的地址，集群部署时需要，不配置则会自动获取
         */
        private String clientConnectHost = NetUtils.getLocalIp();

        /**
         * <p>
         * SSL 配置
         * </p>
         *
         * @author 皮锋
         * @custom.date 2023/5/13 9:49
         */
        @Data
        public static class Ssl {

            /**
             * 是否使用 SSL
             */
            private boolean enabled;

            /**
             * 证书名字
             */
            private String keyStore;

            /**
             * 密钥库密码
             */
            private String keyStorePassword;

            /**
             * 密钥库类型
             */
            private String keyStoreType;

            /**
             * 秘钥别名
             */
            private String keyAlias;
        }

    }

}
