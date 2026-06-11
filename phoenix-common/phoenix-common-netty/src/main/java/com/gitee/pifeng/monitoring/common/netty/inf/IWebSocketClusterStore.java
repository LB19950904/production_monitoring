package com.gitee.pifeng.monitoring.common.netty.inf;

import com.gitee.pifeng.monitoring.common.netty.core.server.info.WebSocketClientClusterInfo;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 保存 WebSocket client 连接到哪个具体的 server，集群部署时使用
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/31 10:15
 */
public interface IWebSocketClusterStore {

    /**
     * <p>
     * 添加客户端
     * </p>
     *
     * @param clientId                   客户端ID
     * @param webSocketClientClusterInfo WebSocket 客户端集群信息
     * @param expire                     超时时长
     * @param timeUnit                   时间单位
     * @author 皮锋
     * @custom.date 2023/3/31 10:18
     */
    void addClient(String clientId, WebSocketClientClusterInfo webSocketClientClusterInfo, long expire, TimeUnit timeUnit);

    /**
     * <p>
     * 查找客户端
     * </p>
     *
     * @param clientId 客户端ID
     * @return WebSocket 客户端集群信息
     * @author 皮锋
     * @custom.date 2023/3/31 10:21
     */
    WebSocketClientClusterInfo findClient(String clientId);

    /**
     * <p>
     * 移除客户端
     * </p>
     *
     * @param clientId 客户端ID
     * @author 皮锋
     * @custom.date 2023/3/31 10:21
     */
    void removeClient(String clientId);

    /**
     * <p>
     * 获取所有客户端ID
     * </p>
     *
     * @return 客户端ID集合
     * @author 皮锋
     * @custom.date 2023/3/31 10:22
     */
    Collection<String> allClientIds();

    /**
     * <p>
     * 获取客户端信息
     * </p>
     *
     * @param appName app名字
     * @return 客户端信息
     * @author 皮锋
     * @custom.date 2023/3/31 10:23
     */
    Map<String, WebSocketClientClusterInfo> clientInfo(String appName);

}
