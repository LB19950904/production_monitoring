package com.gitee.pifeng.monitoring.common.netty.core.server;

import com.gitee.pifeng.monitoring.common.netty.core.server.info.WebSocketClientClusterInfo;
import com.gitee.pifeng.monitoring.common.netty.inf.IWebSocketClusterStore;
import com.google.common.collect.Maps;
import lombok.Data;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.caffeine.CaffeineCache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 基于 Caffeine 的集群存储
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/31 17:05
 */
@Data
public class WebSocketInCaffeineClusterStore implements IWebSocketClusterStore {

    /**
     * 缓存对象
     */
    private Cache cache;

    /**
     * <p>
     * 添加客户端
     * </p>
     *
     * @param clientId                   客户端ID
     * @param webSocketClientClusterInfo WebSocket客户端集群信息
     * @param expire                     超时时长
     * @param timeUnit                   时间单位
     * @author 皮锋
     * @custom.date 2023/3/31 10:18
     */
    @Override
    public void addClient(String clientId, WebSocketClientClusterInfo webSocketClientClusterInfo, long expire, TimeUnit timeUnit) {
        // 在缓存配置中统一设置过期时间：spring.cache.caffeine.spec: maximumSize=5000,expireAfterAccess=3600s
        this.cache.put(clientId, webSocketClientClusterInfo);
    }

    /**
     * <p>
     * 查找客户端
     * </p>
     *
     * @param clientId 客户端ID
     * @return WebSocket客户端集群信息
     * @author 皮锋
     * @custom.date 2023/3/31 10:21
     */
    @Override
    public WebSocketClientClusterInfo findClient(String clientId) {
        ValueWrapper valueWrapper = this.cache.get(clientId);
        if (valueWrapper == null) {
            return null;
        }
        return (WebSocketClientClusterInfo) valueWrapper.get();
    }

    /**
     * <p>
     * 移除客户端
     * </p>
     *
     * @param clientId 客户端ID
     * @author 皮锋
     * @custom.date 2023/3/31 10:21
     */
    @Override
    public void removeClient(String clientId) {
        this.cache.evict(clientId);
    }

    /**
     * <p>
     * 获取所有客户端ID
     * </p>
     *
     * @return 客户端ID集合
     * @author 皮锋
     * @custom.date 2023/3/31 10:22
     */
    @SuppressWarnings("unchecked")
    @Override
    public Collection<String> allClientIds() {
        CaffeineCache caffeineCache = (CaffeineCache) this.cache;
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
        return (Collection<String>) (Collection<?>) nativeCache.asMap().keySet();
    }

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
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, WebSocketClientClusterInfo> clientInfo(String appName) {
        CaffeineCache caffeineCache = (CaffeineCache) this.cache;
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache = caffeineCache.getNativeCache();
        ConcurrentMap<String, WebSocketClientClusterInfo> map = (ConcurrentMap<String, WebSocketClientClusterInfo>) (ConcurrentMap<?, ?>) nativeCache.asMap();
        Map<String, WebSocketClientClusterInfo> result = Maps.newHashMap();
        String prefix = appName + "_";
        for (Map.Entry<String, WebSocketClientClusterInfo> entry : map.entrySet()) {
            String agentId = entry.getKey();
            if (agentId.startsWith(prefix)) {
                result.put(agentId, entry.getValue());
            }
        }
        return result;
    }

}
