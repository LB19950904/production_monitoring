package com.gitee.pifeng.monitoring.common.threadpool.rejected;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 执行最老任务策略：当线程池拒绝新任务时，丢弃队列中最早的任务并执行它，然后尝试将新任务放入队列。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/23 00:00
 */
public class RunsOldestTaskPolicy implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (executor.isShutdown()) {
            return;
        }
        BlockingQueue<Runnable> workQueue = executor.getQueue();
        Runnable firstWork = workQueue.poll();
        boolean newTaskAdd = workQueue.offer(r);
        if (firstWork != null) {
            firstWork.run();
        }
        if (!newTaskAdd) {
            executor.execute(r);
        }
    }

}
