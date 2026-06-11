package com.gitee.pifeng.monitoring.common.netty.common;

/**
 * <p>
 * WebSocket配置相关常量
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/29 17:23
 */
public class WebSocketConfigConstants {

    /**
     * 默认的WebSocket路径
     */
    public static final String DEFAULT_WEBSOCKET_PATH = "/phoenix";

    /**
     * 最大HTTP内容长度
     */
    public static final int MAX_HTTP_CONTENT_LENGTH = 1024 * 1024 * 10;

    /**
     * 空闲时间（秒）
     */
    public static final int IDLE_SECONDS = 60;

}
