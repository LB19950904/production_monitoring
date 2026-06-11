package com.gitee.pifeng.monitoring.plug.core;

import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.common.threadpool.ThreadPool;
import com.gitee.pifeng.monitoring.common.util.MsgPayloadUtils;
import com.gitee.pifeng.monitoring.plug.constant.WebSocketBusinessTypeConstants;
import com.gitee.pifeng.monitoring.plug.core.wsclient.MulticastWebsocketMessageHandler;
import com.gitee.pifeng.monitoring.plug.core.wsclient.WebsocketClient;
import com.gitee.pifeng.monitoring.plug.core.wsclient.inf.IWebsocketMessageHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.ServiceLoader;

/**
 * <p>
 * 基于 Websocket 的双向数据交换器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/2/24 11:44
 */
@Slf4j
public class DataExchanger {

    /**
     * 标识数据交换器是否已经启动
     */
    private static volatile boolean started = false;

    /**
     * Websocket 客户端
     */
    @Getter
    private static volatile WebsocketClient wsClient;

    /**
     * 对象锁
     */
    private static final Object OBJECT_LOCK = new Object();

    /**
     * <p>
     * 运行数据交换器
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/2/24 11:45
     */
    public static void run() {
        // 双重检查锁定（DCL）风格，避免 synchronized 开销
        if (started) {
            if (log.isDebugEnabled()) {
                log.debug("数据交换器已启动，跳过重复初始化！");
            }
            return;
        }
        String serverUri = ConfigLoader.getMonitoringProperties().getComm().getWebsocket().getUrl();
        if (StringUtils.isBlank(serverUri)) {
            return;
        }
        synchronized (OBJECT_LOCK) {
            // 二次检查 + 设置 started
            if (started) {
                // 防止极端并发下重复执行
                return;
            }
            // 1. 创建客户端
            String endpoint = ConfigLoader.getMonitoringProperties().getInstance().getEndpoint();
            String instanceId = InstanceGenerator.getInstanceId();
            String uri = serverUri + "/websocket/relay/" + WebSocketBusinessTypeConstants.MONITORING + "?endpoint=" + endpoint + "&instanceId=" + instanceId;
            wsClient = new WebsocketClient(uri);
            // 标记为已启动（即使后续失败，也认为"已尝试启动"）
            started = true;
            // 捕获局部快照，防止 close() 并发将 wsClient 置 null 导致 NPE
            WebsocketClient clientRef = wsClient;
            ThreadPool.getCommonIoIntensiveThreadPoolExecutor().execute(() -> {
                try {
                    // 2.注册消息处理器
                    MulticastWebsocketMessageHandler dispatcher = new MulticastWebsocketMessageHandler();
                    ServiceLoader<IWebsocketMessageHandler> loader = ServiceLoader.load(IWebsocketMessageHandler.class);
                    for (IWebsocketMessageHandler handler : loader) {
                        dispatcher.registerHandler(handler);
                    }
                    clientRef.setMessageHandler(dispatcher::onRawMessage);
                    log.info("已注册 {}个 WebSocket消息处理器！", dispatcher.getHandlerCount());
                    // 3.阻塞直到连接成功或超时
                    clientRef.connectWithRetry();
                } catch (Exception e) {
                    log.error("运行数据交换器失败(将依赖WebsocketClient内部重连机制)：{}", e.getMessage());
                }
            });
        }
    }

    /**
     * <p>
     * 关闭数据交换器，释放资源
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/2/24 14:23
     */
    public static void close() {
        synchronized (OBJECT_LOCK) {
            if (wsClient != null) {
                wsClient.close();
            }
            wsClient = null;
            // 允许重新启动
            started = false;
        }
    }

    /**
     * <p>
     * 判断数据交换器是否就绪（可发送消息）
     * </p>
     * 检查条件：<br>
     * 1. WebSocket 客户端已初始化（非 null）；<br>
     * 2. WebSocket 连接处于活跃状态。<br>
     *
     * @return 如果可以进行 sendMessage 操作，返回 true，否则返回 false
     * @author 皮锋
     * @custom.date 2026/3/17 17:22
     */
    public static boolean isReady() {
        WebsocketClient client = wsClient;
        // 先判空，防止 NullPointerException
        if (client == null) {
            return false;
        }
        // 再判断连接状态
        return client.isConnected();
    }

    /**
     * <p>
     * 发送消息到 WebSocket 服务端
     * </p>
     * 消息将自动进行以下处理：<br>
     * 1.序列化为 JSON；<br>
     * 2.根据内容长度决定是否 Gzip 压缩；<br>
     * 3.使用对称加密算法加密；<br>
     * 4.封装为密文包后发送。<br>
     *
     * @param requestPackage 待发送的 WebSocket 请求包，不可为 {@code null}
     * @author 皮锋
     * @custom.date 2026/2/24 16:31
     */
    public static void sendMessage(WebSocketPackage requestPackage) {
        String requestPackageJsonStr = requestPackage.toJsonString();
        // 打印发送的数据包
        if (log.isDebugEnabled()) {
            log.debug("发送数据包：{}", requestPackageJsonStr);
        }
        // 将 明文JSON字符串 转换成 密文JSON字符串
        String encryptStr = MsgPayloadUtils.encryptPayload(requestPackageJsonStr);
        WebsocketClient client = wsClient;
        // 读一次 volatile
        if (client == null) {
            log.warn("WebSocket客户端尚未初始化，消息已丢弃！");
            return;
        }
        // 发送文本消息到 WebSocket 服务端
        client.sendMessage(encryptStr);
    }

}