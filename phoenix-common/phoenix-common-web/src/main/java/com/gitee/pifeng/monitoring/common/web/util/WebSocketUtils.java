package com.gitee.pifeng.monitoring.common.web.util;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * <p>
 * WebSocket工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/1/27 15:31
 */
@Slf4j
public class WebSocketUtils {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/2/4 15:49
     */
    private WebSocketUtils() {
    }

    /**
     * <p>
     * 安全关闭 WebSocket 会话
     * </p>
     * 此方法提供了一种线程安全的优雅关闭方式，避免直接调用 {@link Session#close()} 可能引发的异常。
     * 在关闭前会检查会话状态，只对已打开的会话执行关闭操作，并忽略关闭过程中可能产生的 {@link IOException}。
     * 主要用于资源清理、连接管理和错误恢复场景。
     *
     * @param session   要关闭的 WebSocket 会话，如果为 {@code null} 或已关闭，则方法直接返回
     * @param closeCode 关闭原因代码，定义在 {@link CloseReason.CloseCodes} 中
     * @param reason    关闭原因描述文本，将发送给对端
     * @author 皮锋
     * @custom.date 2026/1/27 15:34
     * @see Session#close(CloseReason)
     * @see CloseReason
     */
    public static void safeClose(Session session, CloseReason.CloseCode closeCode, String reason) {
        if (session != null && session.isOpen()) {
            try {
                session.close(new CloseReason(closeCode, reason));
            } catch (IOException e) {
                // 忽略关闭过程中可能出现的 IO 异常，因为：
                // 1. 连接可能已被对端或网络设备断开
                // 2. 异常关闭不影响业务逻辑的正确性
                // 3. 此方法的设计目标是不抛出任何异常
                if (log.isDebugEnabled()) {
                    log.debug("关闭 WebSocket 会话时发生异常：sessionId={}, code={}", session.getId(), closeCode, e);
                }
            }
        }
    }

    /**
     * <p>
     * 安全关闭 WebSocket 会话
     * </p>
     * 使用默认的 CloseReason.CloseCodes.NORMAL_CLOSURE 关闭代码和“WebSocket正常关闭”原因，适用于简单的关闭场景。
     *
     * @param session 要关闭的 WebSocket 会话，如果为 {@code null} 或已关闭，则方法直接返回
     * @author 皮锋
     * @custom.date 2026/1/27 15:38
     */
    public static void safeClose(Session session) {
        safeClose(session, CloseReason.CloseCodes.NORMAL_CLOSURE, "WebSocket正常关闭");
    }

    /**
     * <p>
     * 配置 WebSocket 会话参数
     * </p>
     *
     * @param session                    需要配置的 WebSocket 会话对象
     * @param maxIdleTimeout             连接空闲超时时间
     * @param maxTextMessageBufferSize   最大文本消息缓冲区大小
     * @param maxBinaryMessageBufferSize 最大二进制消息缓冲区大小
     * @param userProperties             用户自定义属性
     * @author 皮锋
     * @custom.date 2026/1/30 12:12
     */
    public static void configureSession(Session session,
                                        long maxIdleTimeout,
                                        int maxTextMessageBufferSize,
                                        int maxBinaryMessageBufferSize,
                                        Map<String, Object> userProperties) {
        session.setMaxIdleTimeout(maxIdleTimeout);
        session.setMaxTextMessageBufferSize(maxTextMessageBufferSize);
        session.setMaxBinaryMessageBufferSize(maxBinaryMessageBufferSize);
        if (userProperties != null) {
            session.getUserProperties().putAll(userProperties);
        }
        if (log.isDebugEnabled()) {
            log.debug("WebSocket Session配置完成，Session[{}]", session.getId());
        }
    }

    /**
     * <p>
     * 构建 WebSocket 服务 URL
     * </p>
     *
     * @param baseUrl 基础 URL 地址
     * @param subPath URL 路径参数（可为 null 或空白，将被忽略）
     * @param session 会话对象
     * @return 完整的 WebSocket 服务 URL
     * @author 皮锋
     * @custom.date 2026/1/30 11:59
     */
    @SneakyThrows
    public static String buildUrl(String baseUrl, String subPath, Session session) {
        if (StringUtils.isBlank(baseUrl)) {
            throw new IllegalArgumentException("baseUrl 不能为空！");
        }
        if (!StringUtils.startsWith(baseUrl, "ws://") && !StringUtils.startsWith(baseUrl, "wss://")) {
            throw new IllegalArgumentException("baseUrl 必须以 'ws://' 或 'wss://' 开头！");
        }
        if (session == null) {
            throw new IllegalArgumentException("session 不能为空！");
        }
        // 如果以 / 结尾，则去掉末尾的 /
        baseUrl = StringUtils.removeEnd(baseUrl, "/");
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        if (StringUtils.isNotBlank(subPath)) {
            String encodedSubPath = URLEncoder.encode(subPath.trim(), "UTF-8")
                    // 保持空格为 %20 而非 +
                    .replace("+", "%20");
            urlBuilder.append("/").append(encodedSubPath);
        }
        String queryString = session.getQueryString();
        if (StringUtils.isNotBlank(queryString)) {
            urlBuilder.append("?").append(queryString);
        }
        return urlBuilder.toString();
    }

}