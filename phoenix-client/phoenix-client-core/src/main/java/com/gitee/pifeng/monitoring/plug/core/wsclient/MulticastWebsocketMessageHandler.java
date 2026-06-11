package com.gitee.pifeng.monitoring.plug.core.wsclient;

import com.gitee.pifeng.monitoring.common.dto.CiphertextPackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.plug.core.wsclient.inf.IWebsocketMessageHandler;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

import static com.gitee.pifeng.monitoring.plug.constant.WebSocketPkgPayloadWhitelistConstants.DOWNSTREAM_ALLOWED_CLASS_NAMES;

/**
 * <p>
 * WebSocket 消息多播分发器
 * </p>
 * 1.负责接收原始密文消息，执行解密、解压、反序列化为 {@link WebSocketPackage}，并广播给所有已注册的 {@link IWebsocketMessageHandler} 处理器；<br>
 * 2.各处理器独立执行，异常相互隔离，确保单个处理器失败不影响整体消息处理流程。<br>
 *
 * @author 皮锋
 * @custom.date 2026/2/24 14:48
 */
@Slf4j
public class MulticastWebsocketMessageHandler {

    /**
     * 已注册的消息处理器列表
     */
    private final List<IWebsocketMessageHandler> handlers = Lists.newArrayList();

    /**
     * <p>
     * 注册一个新的 WebSocket 消息处理器
     * </p>
     *
     * @param handler 消息处理器实例，若为 {@code null} 则忽略
     * @author 皮锋
     * @custom.date 2026/2/24 15:00
     */
    public void registerHandler(IWebsocketMessageHandler handler) {
        if (handler != null) {
            this.handlers.add(handler);
        }
    }

    /**
     * <p>
     * 接收原始 WebSocket 文本消息并进行统一解包与分发
     * </p>
     * 处理流程：<br>
     * 1.解析为 {@link CiphertextPackage}；<br>
     * 2.根据标志位判断是否需要 Gzip 解压；<br>
     * 3.执行对称解密；<br>
     * 4.反序列化为 {@link WebSocketPackage}；<br>
     * 5.多播至所有注册处理器。<br>
     *
     * @param message 原始 WebSocket 文本消息，可能为 {@code null}
     * @author 皮锋
     * @custom.date 2026/2/24 15:01
     */
    @SneakyThrows
    public void onRawMessage(String message) {
        if (StringUtils.isBlank(message)) {
            return;
        }
        // 将原始 “密文 WebSocket 消息JSON字符串” 解析并转换为 “WebSocketPackage 数据包
        WebSocketPackage pkg = WebSocketPackage.convert(message, DOWNSTREAM_ALLOWED_CLASS_NAMES);
        for (IWebsocketMessageHandler handler : this.handlers) {
            try {
                handler.handleMessage(pkg);
            } catch (Exception e) {
                // 隔离异常，防止一个处理器失败影响其他处理器
                log.error("Websocket消息处理器执行异常，消息：{}", message, e);
            }
        }
    }

    /**
     * <p>
     * 获取已注册的处理器数量
     * </p>
     *
     * @return 处理器数量
     * @author 皮锋
     * @custom.date 2026/2/24 15:01
     */
    public int getHandlerCount() {
        return this.handlers.size();
    }

}