package com.gitee.pifeng.monitoring.common.exception;

/**
 * <p>
 * 自定义的 Websocket 异常
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/2/27 16:09
 */
public class WebSocketException extends MonitoringUniversalException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -6389940318716091798L;

    public WebSocketException(String message) {
        super(message);
    }

    public WebSocketException(String message, Throwable cause) {
        super(message, cause);
    }

}