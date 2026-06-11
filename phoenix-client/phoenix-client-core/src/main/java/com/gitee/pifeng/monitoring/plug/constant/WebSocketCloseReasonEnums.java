package com.gitee.pifeng.monitoring.plug.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.websocket.CloseReason;

/**
 * <p>
 * 自定义 WebSocket 关闭原因码（4000~4999 范围）
 * </p>
 * 根据 RFC 6455 规范，WebSocket 关闭状态码中：
 *  <ul>
 *   <li>1000~2999：保留给协议标准使用</li>
 *   <li>3000~3999：可由第三方框架/库使用</li>
 *   <li><b>4000~4999：推荐用于应用程序自定义业务关闭原因</b></li>
 *  </ul>
 * * 本枚举仅定义应用层自定义关闭码，并提供通用解析支持。
 *
 * @author 皮锋
 * @custom.date 2026/2/27 09:36
 */
@Getter
@AllArgsConstructor
public enum WebSocketCloseReasonEnums implements CloseReason.CloseCode {

    /**
     * 重复连接：客户端尝试建立新连接时，服务端检测到已有活跃会话，拒绝重复连接
     */
    DUPLICATE_CONNECTION(4000, "Duplicate connection");

    /**
     * WebSocket 关闭状态码（必须在 4000~4999 范围内）
     */
    private final int code;

    /**
     * WebSocket 关闭原因
     */
    private final String reason;

    /**
     * <p>
     * 根据给定的关闭码值，获取对应的 {@link CloseReason.CloseCode} 实例
     * </p>
     *
     * @param code WebSocket 关闭状态码（整数）
     * @return 对应的 {@link CloseReason.CloseCode} 实例
     * @throws IllegalArgumentException 当 {@code code} 不在 [3000, 4999] 范围内时抛出
     * @author 皮锋
     * @custom.date 2026/2/27 09:55
     */
    public static CloseReason.CloseCode getCloseCode(final int code) {
        for (WebSocketCloseReasonEnums item : values()) {
            if (item.code == code) {
                return item;
            }
        }
        // 不在枚举中？返回通用实现（确保 3000~4999 合法）
        if (code >= 3000 && code <= 4999) {
            return () -> code;
        }
        throw new IllegalArgumentException("Invalid custom close code: " + code);
    }

}