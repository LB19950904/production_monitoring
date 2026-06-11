package com.gitee.pifeng.monitoring.common.web.core.websocket;

import com.gitee.pifeng.monitoring.common.web.util.WebSocketUtils;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;

import javax.websocket.CloseReason;
import javax.websocket.Session;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>
 * WebSocket 中继代理助手
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/2/4 15:43
 */
@Slf4j
public class WebSocketRelayHelper {

    /**
     * <p>
     * 建立会话配对关系
     * </p>
     *
     * @param sessionCache         Session 对象缓存
     * @param downstreamToUpstream 下游到上游的会话配对映射表
     * @param upstreamToDownstream 上游到下游的会话配对映射表
     * @param downstreamSessionId  下游 Session ID
     * @param upstreamSessionId    上游 Session ID
     * @param downstreamSession    下游 Session 对象
     * @param upstreamSession      上游 Session 对象
     * @author 皮锋
     * @custom.date 2026/1/30 15:49
     */
    public static void establishPairing(Cache<String, Session> sessionCache,
                                        ConcurrentMap<String, String> downstreamToUpstream,
                                        ConcurrentMap<String, String> upstreamToDownstream,
                                        String downstreamSessionId,
                                        String upstreamSessionId,
                                        Session downstreamSession,
                                        Session upstreamSession) {
        // 使用 putIfAbsent 实现原子操作
        downstreamToUpstream.putIfAbsent(downstreamSessionId, upstreamSessionId);
        upstreamToDownstream.putIfAbsent(upstreamSessionId, downstreamSessionId);
        // 缓存Session
        sessionCache.put(downstreamSessionId, downstreamSession);
        sessionCache.put(upstreamSessionId, upstreamSession);
    }

    /**
     * <p>
     * 清理一对会话的配对关系和资源
     * </p>
     *
     * @param sessionCache         Session 对象缓存
     * @param downstreamToUpstream 下游到上游的会话配对映射表
     * @param upstreamToDownstream 上游到下游的会话配对映射表
     * @param downstreamSessionId  下游 Session Id
     * @param upstreamSessionId    上游 Session Id
     * @param closeCode            关闭原因代码，定义在 {@link CloseReason.CloseCodes} 中
     * @param reason               关闭原因描述文本，将发送给对端
     * @author 皮锋
     * @custom.date 2026/1/30 15:47
     */
    public static void cleanupPair(Cache<String, Session> sessionCache,
                                   ConcurrentMap<String, String> downstreamToUpstream,
                                   ConcurrentMap<String, String> upstreamToDownstream,
                                   String downstreamSessionId,
                                   String upstreamSessionId,
                                   CloseReason.CloseCode closeCode,
                                   String reason) {
        // 1. 移除下游到上游的映射
        boolean removedDownstreamToUpstream = downstreamToUpstream.remove(downstreamSessionId, upstreamSessionId);
        // 2. 移除上游到下游的映射
        boolean removedUpstreamToDownstream = upstreamToDownstream.remove(upstreamSessionId, downstreamSessionId);
        // 3. 清理缓存和连接
        cleanupSession(sessionCache, downstreamSessionId, closeCode, reason);
        cleanupSession(sessionCache, upstreamSessionId, closeCode, reason);
        if (removedDownstreamToUpstream || removedUpstreamToDownstream) {
            log.info("WebSocket配对关系清理，下游Session[{}] <--> 上游Session[{}]，剩余Session数：{}", downstreamSessionId, upstreamSessionId, sessionCache.size());
        }
    }

    /**
     * <p>
     * 清理单个会话资源，关闭指定的 WebSocket 连接并从缓存中移除对应的 Session 对象
     * </p>
     *
     * @param sessionCache Session 对象缓存
     * @param sessionId    需要清理的 Session ID
     * @param closeCode    关闭原因代码，定义在 {@link CloseReason.CloseCodes} 中
     * @param reason       关闭原因描述文本，将发送给对端
     * @author 皮锋
     * @custom.date 2026/1/30 15:42
     */
    public static void cleanupSession(Cache<String, Session> sessionCache, String sessionId, CloseReason.CloseCode closeCode, String reason) {
        Session session = sessionCache.getIfPresent(sessionId);
        if (session != null && session.isOpen()) {
            // 安全关闭 WebSocket 会话
            WebSocketUtils.safeClose(session, closeCode, reason);
        }
        sessionCache.invalidate(sessionId);
    }

    /**
     * <p>
     * 判断 WebSocket 连接数是否已达到或超过指定的最大限制
     * </p>
     *
     * @param sessionCache       当前的 Session 缓存（Guava Cache）
     * @param maxConnectionLimit 允许的最大连接数
     * @return true 表示已达到或超过上限，应拒绝新连接；false 表示仍可接受
     * @author 皮锋
     * @custom.date 2026/2/4 16:45
     */
    public static boolean isConnectionLimitReached(Cache<String, Session> sessionCache, int maxConnectionLimit) {
        if (sessionCache == null || maxConnectionLimit <= 0) {
            return false;
        }
        return sessionCache.size() >= maxConnectionLimit;
    }

}