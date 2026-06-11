package com.gitee.pifeng.monitoring.plug.core.wsclient;

/**
 * <p>
 * Websocket 客户端ID 生成器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/2/27 15:24
 */
public class WebsocketClientIdGenerator {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/2/27 15:39
     */
    private WebsocketClientIdGenerator() {
    }

    /**
     * <p>
     * 生成 Websocket 客户端ID
     * </p>
     *
     * @param instanceEndpoint 应用实例端点
     * @param instanceId       应用实例ID
     * @return Websocket 客户端ID
     * @author 皮锋
     * @custom.date 2026/2/27 15:27
     */
    public static String generate(String instanceEndpoint, String instanceId) {
        return "phoenix_" + instanceEndpoint + "_" + instanceId;
    }

}