package com.gitee.pifeng.monitoring.common.netty.config;

import com.gitee.pifeng.monitoring.common.netty.common.WebSocketCacheConstants;
import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketInCaffeineClusterStore;
import com.gitee.pifeng.monitoring.common.netty.inf.IWebSocketClusterStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * WebSocket 集群存储配置
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/31 17:23
 */
@Configuration
@ConditionalOnProperty(name = "ws.server.enable", havingValue = "true")
@AutoConfigureAfter(value = {CacheAutoConfiguration.class})
public class WebSocketClusterStoreConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(name = "spring.cache.type", havingValue = "caffeine")
    public IWebSocketClusterStore webSocketClusterStore(@Autowired CacheManager cacheManager) {
        Cache inCaffeineWebSocketClusterCache = cacheManager.getCache(WebSocketCacheConstants.CACHE_IN_CAFFEINE_WEB_SOCKET_CLUSTER);
        WebSocketInCaffeineClusterStore webSocketInCaffeineClusterStore = new WebSocketInCaffeineClusterStore();
        webSocketInCaffeineClusterStore.setCache(inCaffeineWebSocketClusterCache);
        return webSocketInCaffeineClusterStore;
    }

}
