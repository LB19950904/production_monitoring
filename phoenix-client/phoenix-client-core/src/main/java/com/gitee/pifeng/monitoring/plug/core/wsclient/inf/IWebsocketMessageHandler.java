package com.gitee.pifeng.monitoring.plug.core.wsclient.inf;

import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.plug.core.wsclient.MulticastWebsocketMessageHandler;

/**
 * <p>
 * WebSocket 消息处理器接口
 * </p>
 * 用于处理已解密、解压并反序列化后的结构化响应消息，现类可通过 Java SPI 机制自动注册，由 {@link MulticastWebsocketMessageHandler} 统一调用。
 *
 * @author 皮锋
 * @custom.date 2026/2/24 14:45
 */
@FunctionalInterface
public interface IWebsocketMessageHandler {

    /**
     * <p>
     * 处理 WebSocket 服务端返回的结构化响应包
     * </p>
     *
     * @param responsePackage WebSocket 响应数据包
     * @author 皮锋
     * @custom.date 2026/2/24 14:58
     */
    void handleMessage(WebSocketPackage responsePackage);

}