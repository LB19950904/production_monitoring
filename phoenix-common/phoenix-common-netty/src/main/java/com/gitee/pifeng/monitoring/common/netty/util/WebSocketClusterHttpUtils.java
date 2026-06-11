package com.gitee.pifeng.monitoring.common.netty.util;

import io.netty.handler.codec.http.HttpHeaders;

/**
 * <p>
 * WebSocket集群Http工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/31 9:16
 */
public class WebSocketClusterHttpUtils {

    /**
     * <p>
     * 查找客户端IP
     * </p>
     *
     * @param headers {@link HttpHeaders}
     * @return 客户端IP
     * @author 皮锋
     * @custom.date 2023/3/31 9:15
     */
    public static String findClientIp(HttpHeaders headers) {
        String hostStr = headers.get("X-Forwarded-For");
        if (hostStr == null) {
            return null;
        }
        int index = hostStr.indexOf(',');
        if (index > 0) {
            hostStr = hostStr.substring(0, index);
        }
        return hostStr;
    }

    /**
     * <p>
     * 查找客户端端口
     * </p>
     *
     * @param headers {@link HttpHeaders}
     * @return 客户端端口
     * @author 皮锋
     * @custom.date 2023/3/31 9:15
     */
    public static Integer findClientPort(HttpHeaders headers) {
        String portStr = headers.get("X-Real-Port");
        if (portStr != null) {
            return Integer.parseInt(portStr);
        }
        return null;
    }

}
