package com.gitee.pifeng.monitoring.common.threadpool.rejected;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ServiceLoader;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 拒绝策略类型枚举：定义线程池支持的所有拒绝策略，并提供根据名称创建策略实例的能力。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/23 00:00
 */
@Getter
@AllArgsConstructor
public enum RejectedPolicyTypeEnum {

    /**
     * 中止策略：直接抛出 {@link RejectedExecutionException}
     */
    ABORT_POLICY("AbortPolicy", new ThreadPoolExecutor.AbortPolicy()),

    /**
     * 调用者运行策略：由提交任务的线程直接执行被拒绝的任务
     */
    CALLER_RUNS_POLICY("CallerRunsPolicy", new ThreadPoolExecutor.CallerRunsPolicy()),

    /**
     * 丢弃最老任务策略：丢弃队列头部（最早入队）的任务，然后重新提交被拒绝的任务
     */
    DISCARD_OLDEST_POLICY("DiscardOldestPolicy", new ThreadPoolExecutor.DiscardOldestPolicy()),

    /**
     * 丢弃策略：直接丢弃被拒绝的任务，不做任何处理
     */
    DISCARD_POLICY("DiscardPolicy", new ThreadPoolExecutor.DiscardPolicy()),

    /**
     * 执行最老任务策略：从队列中取出最早的任务执行，再将新任务放入队列
     */
    RUNS_OLDEST_TASK_POLICY("RunsOldestTaskPolicy", new RunsOldestTaskPolicy()),

    /**
     * 同步阻塞入队策略：通过阻塞方式将任务放入队列，直到队列有空间为止
     */
    SYNC_PUT_QUEUE_POLICY("SyncPutQueuePolicy", new SyncPutQueuePolicy());

    /**
     * 拒绝策略名称
     */
    private final String name;

    /**
     * 拒绝策略处理器实例
     */
    private final RejectedExecutionHandler rejectedHandler;

    /**
     * <p>
     * 根据拒绝策略名称创建对应的 {@link RejectedExecutionHandler} 实例
     * </p>
     * 优先匹配枚举中已定义的策略，未匹配到时尝试通过 Java SPI 机制加载自定义拒绝策略（{@code CustomRejectedPolicy}），均未找到时返回 {@code null}。
     *
     * @param name 拒绝策略名称
     * @return {@link RejectedExecutionHandler} 拒绝策略实例，无法识别时返回 {@code null}
     * @author 皮锋
     * @custom.date 2026/3/23 00:00
     */
    public static RejectedExecutionHandler createPolicy(String name) {
        for (RejectedPolicyTypeEnum policyType : values()) {
            if (policyType.name.equals(name)) {
                return policyType.rejectedHandler;
            }
        }
        // 尝试通过 SPI 加载自定义拒绝策略
        if ("CustomRejectedPolicy".equals(name)) {
            ServiceLoader<CustomRejectedExecutionHandler> serviceLoader = ServiceLoader.load(CustomRejectedExecutionHandler.class);
            for (CustomRejectedExecutionHandler handler : serviceLoader) {
                return handler.generateRejected();
            }
        }
        return null;
    }

}
