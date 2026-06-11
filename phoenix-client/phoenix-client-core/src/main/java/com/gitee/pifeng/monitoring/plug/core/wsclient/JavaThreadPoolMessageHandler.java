package com.gitee.pifeng.monitoring.plug.core.wsclient;

import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import com.gitee.pifeng.monitoring.common.dto.JavaThreadPoolPackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.common.threadpool.ThreadPoolManager;
import com.gitee.pifeng.monitoring.plug.core.ThreadPoolAcquirer;
import com.gitee.pifeng.monitoring.plug.core.wsclient.inf.IWebsocketMessageHandler;
import com.gitee.pifeng.monitoring.plug.thread.JavaThreadPoolThread;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * Java线程池 WebSocket 消息处理器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/23 15:23
 */
public class JavaThreadPoolMessageHandler implements IWebsocketMessageHandler {

    /**
     * <p>
     * 处理 WebSocket 服务端返回的结构化响应包
     * </p>
     *
     * @param responsePackage WebSocket 响应数据包
     * @author 皮锋
     * @custom.date 2026/2/24 14:58
     */
    @Override
    public void handleMessage(WebSocketPackage responsePackage) {
        String className = responsePackage.getClassName();
        if (!StringUtils.equals(JavaThreadPoolPackage.class.getName(), className)) {
            return;
        }
        Object payload = responsePackage.getPayload();
        JavaThreadPoolPackage javaThreadPoolPackage = (JavaThreadPoolPackage) payload;
        JavaThreadPool threadPool = javaThreadPoolPackage.getJavaThreadPool();
        List<JavaThreadPool.ThreadPoolInfoDomain> threadPoolInfoDomains = threadPool.getThreadPoolInfoDomains();
        // 动态修改线程池配置是否成功
        AtomicBoolean dynamicUpdateSuccess = new AtomicBoolean(false);
        if (CollectionUtils.isNotEmpty(threadPoolInfoDomains)) {
            // 动态修改线程池配置
            threadPoolInfoDomains.forEach((threadPoolInfoDomain) -> {
                boolean success = ThreadPoolManager.dynamicUpdateThreadPool(threadPoolInfoDomain);
                if (success) {
                    dynamicUpdateSuccess.set(true);
                }
            });
        }
        // 存在成功，立即发送Java线程池信息
        if (dynamicUpdateSuccess.get()) {
            ThreadPoolAcquirer.getInstanceScheduledThreadPoolExecutor().execute(new JavaThreadPoolThread());
        }
    }

}