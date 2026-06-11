package com.gitee.pifeng.monitoring.common.netty.core.server;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.exception.NotFoundConfigFileException;
import com.gitee.pifeng.monitoring.common.netty.common.WebSocketConfigConstants;
import com.gitee.pifeng.monitoring.common.netty.config.property.WebSocketProperties;
import com.gitee.pifeng.monitoring.common.netty.core.server.info.WebSocketClientClusterInfo;
import com.gitee.pifeng.monitoring.common.netty.core.server.info.WebSocketClientConnectionInfo;
import com.gitee.pifeng.monitoring.common.netty.core.server.info.WebSocketClientInfo;
import com.gitee.pifeng.monitoring.common.netty.inf.IWebSocketClusterStore;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * WebSocket 服务端
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/29 9:14
 */
@Setter
@Getter
@Slf4j
public class WebSocketServer {

    /**
     * SSL配置
     */
    private WebSocketProperties.Server.Ssl ssl;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private int port;

    /**
     * WebSocket路径
     */
    private String path = WebSocketConfigConstants.DEFAULT_WEBSOCKET_PATH;

    /**
     * 接收客户端请求的线程池
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("monitoring-websocket-server-boss", true));

    /**
     * 处理I/O操作的线程池
     */
    private EventLoopGroup workerGroup = new NioEventLoopGroup(new DefaultThreadFactory("monitoring-websocket-server-worker", true));

    /**
     * 通道
     */
    private Channel channel;

    /**
     * 在集群部署时，保存clientId和host关系
     */
    private IWebSocketClusterStore clusterStore;

    /**
     * 集群部署时外部连接的host
     */
    private String clientConnectHost;

    /**
     * 客户端信息集合
     */
    private Map<String, WebSocketClientInfo> clientInfoMap = new ConcurrentHashMap<>(16);

    /**
     * 客户端连接信息集合
     */
    private Map<String, WebSocketClientConnectionInfo> clientConnectionInfoMap = new ConcurrentHashMap<>(16);

    /**
     * <p>
     * 开启 WebSocket 服务
     * </p>
     *
     * @throws Exception 异常
     * @author 皮锋
     * @custom.date 2023/3/29 12:39
     */
    public void start() throws Exception {
        // 计时器
        TimeInterval timer = DateUtil.timer();

        SslContext sslCtx = null;
        if (this.ssl != null && this.ssl.isEnabled()) {
            // 构建 SSL 上下文
            sslCtx = this.buildSslContext();
        }
        ServerBootstrap b = new ServerBootstrap();
        b.group(this.bossGroup, this.workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new WebSocketServerInitializer(this, sslCtx));
        if (StringUtils.isBlank(this.host)) {
            this.channel = b.bind(this.port).sync().channel();
        } else {
            this.channel = b.bind(this.host, this.port).sync().channel();
        }
        // 定时检查客户端信息：清理失效连接 + 刷新集群存储
        this.workerGroup.scheduleWithFixedDelay(this::cleanupAndRefreshCluster, 60, 60, TimeUnit.SECONDS);

        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        log.info("WebSocket服务端启动：{}:{}，耗时：{}", StringUtils.defaultIfBlank(this.host, "0.0.0.0"), this.port, betweenDay);
    }

    /**
     * <p>
     * 关闭 WebSocket 服务
     * </p>
     *
     * @author 皮锋
     * @custom.date 2023/3/29 12:39
     */
    public void stop() {
        try {
            if (this.channel != null) {
                this.channel.close().sync();
            }
            this.bossGroup.shutdownGracefully().sync();
            this.workerGroup.shutdownGracefully().sync();
            log.info("WebSocket服务端优雅关闭：{}:{}", StringUtils.defaultIfBlank(this.host, "0.0.0.0"), this.port);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("优雅关闭WebSocket服务端时被中断！", e);
        }
    }

    /**
     * <p>
     * 构建 SSL 上下文
     * </p>
     *
     * @return {@link SslContext} SSL 上下文
     * @throws Exception 异常
     * @author 皮锋
     * @custom.date 2026/1/26 09:21
     */
    private SslContext buildSslContext() throws Exception {
        String keyStorePath = this.ssl.getKeyStore();
        String certPassword = this.ssl.getKeyStorePassword();
        String keyStoreType = this.ssl.getKeyStoreType();
        String keyAlias = this.ssl.getKeyAlias();
        // 以类路径开头
        String prefixClassPath = "classpath:";
        if (StringUtils.startsWith(keyStorePath, prefixClassPath)) {
            keyStorePath = StringUtils.removeStart(keyStorePath, prefixClassPath);
        }
        @Cleanup
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStorePath);
        if (inputStream == null) {
            throw new NotFoundConfigFileException("WebSocket无法加载证书文件: " + keyStorePath);
        }
        char[] passwordChars = certPassword.toCharArray();
        KeyStore ks = KeyStore.getInstance(keyStoreType);
        ks.load(inputStream, passwordChars);
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        if (StringUtils.isNotBlank(keyAlias)) {
            KeyStore.Entry entry = ks.getEntry(keyAlias, new KeyStore.PasswordProtection(passwordChars));
            if (entry == null) {
                throw new IllegalArgumentException("WebSocket的Keystore中未找到别名: " + keyAlias);
            }
            // 创建一个只包含指定 alias 的 KeyStore 视图（更安全）
            KeyStore singleEntryKs = KeyStore.getInstance(keyStoreType);
            // 初始化空 keystore
            singleEntryKs.load(null, null);
            singleEntryKs.setEntry(keyAlias, entry, new KeyStore.PasswordProtection(passwordChars));
            kmf.init(singleEntryKs, passwordChars);
        } else {
            kmf.init(ks, passwordChars);
        }
        // SSLContext sslContext = SSLContext.getInstance("TLS");
        // sslContext.init(kmf.getKeyManagers(), null, null);
        // SelfSignedCertificate ssc = new SelfSignedCertificate();
        // final SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).keyManager(kmf).build();
        final SslContext sslCtx = SslContextBuilder.forServer(kmf).build();
        log.info("WebSocket启用SSL，证书文件：{}，密钥别名：{}", keyStorePath, StringUtils.defaultIfBlank(keyAlias, "未指定"));
        return sslCtx;
    }

    /**
     * <p>
     * 清理失效连接 + 刷新集群存储
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/1/26 09:06
     */
    private void cleanupAndRefreshCluster() {
        try {
            // 移除掉已经失去连接的客户端
            this.clientInfoMap.entrySet().removeIf(e -> !e.getValue().getChannelHandlerContext().channel().isActive());
            this.clientConnectionInfoMap.entrySet().removeIf(e -> !e.getValue().getChannelHandlerContext().channel().isActive());
            // 更新集群key信息
            if (this.clusterStore != null && this.clientConnectHost != null) {
                for (Map.Entry<String, WebSocketClientInfo> entry : this.clientInfoMap.entrySet()) {
                    // 因为有个过期时间，所有定时添加能覆盖之前的，也就刷新了过期时间
                    this.clusterStore.addClient(entry.getKey(), new WebSocketClientClusterInfo(entry.getValue(), this.clientConnectHost), 60 * 60, TimeUnit.SECONDS);
                }
            }
        } catch (Throwable t) {
            log.error("定时清理或刷新WebSocket集群信息失败！", t);
        }
    }

    /**
     * <p>
     * 添加客户端
     * </p>
     *
     * @param clientId 客户端ID
     * @param client   WebSocket 客户端信息
     * @author 皮锋
     * @custom.date 2023/3/31 10:33
     */
    public void addClient(String clientId, WebSocketClientInfo client) {
        this.clientInfoMap.put(clientId, client);
        if (this.clusterStore != null) {
            this.clusterStore.addClient(clientId, new WebSocketClientClusterInfo(client, this.clientConnectHost), 60 * 60, TimeUnit.SECONDS);
        }
    }

    /**
     * <p>
     * 移除客户端
     * </p>
     *
     * @param clientId 客户端ID
     * @author 皮锋
     * @custom.date 2023/3/31 10:37
     */
    public void removeClient(String clientId) {
        this.clientInfoMap.remove(clientId);
        if (this.clusterStore != null) {
            this.clusterStore.removeClient(clientId);
        }
    }

    /**
     * <p>
     * 查找客户端
     * </p>
     *
     * @param clientId 客户端ID
     * @return WebSocket 客户端信息
     * @author 皮锋
     * @custom.date 2023/3/31 14:41
     */
    public Optional<WebSocketClientInfo> findClient(String clientId) {
        return Optional.ofNullable(this.clientInfoMap.get(clientId));
    }

    /**
     * <p>
     * 添加客户端连接信息
     * </p>
     *
     * @param clientId                      客户端ID
     * @param webSocketClientConnectionInfo 客户端连接信息
     * @author 皮锋
     * @custom.date 2023/3/31 14:49
     */
    public void addClientConnectionInfo(String clientId, WebSocketClientConnectionInfo webSocketClientConnectionInfo) {
        this.clientConnectionInfoMap.put(clientId, webSocketClientConnectionInfo);
    }

    /**
     * <p>
     * 移除客户端连接信息
     * </p>
     *
     * @param clientId 客户端ID
     * @author 皮锋
     * @custom.date 2023/3/31 14:50
     */
    public void removeClientConnectionInfo(String clientId) {
        this.clientConnectionInfoMap.remove(clientId);
    }

    /**
     * <p>
     * 查找客户端连接
     * </p>
     *
     * @param clientId 客户端ID
     * @return 客户端连接信息
     * @author 皮锋
     * @custom.date 2023/3/31 16:14
     */
    public Optional<WebSocketClientConnectionInfo> findClientConnection(String clientId) {
        return Optional.ofNullable(this.clientConnectionInfoMap.get(clientId));
    }

}
