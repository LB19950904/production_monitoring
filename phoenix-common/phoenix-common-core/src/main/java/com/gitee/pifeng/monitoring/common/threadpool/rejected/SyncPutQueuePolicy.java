package com.gitee.pifeng.monitoring.common.threadpool.rejected;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 同步阻塞放入队列策略：当线程池拒绝新任务时，通过阻塞方式将任务放入队列，直到队列有空间为止。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/23 00:00
 */
@Slf4j
public class SyncPutQueuePolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            log.error("向线程池队列中添加任务失败！", e);
        }
    }

}
