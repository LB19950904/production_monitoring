package com.gitee.pifeng.monitoring.common.threadpool;

import cn.hutool.core.util.NumberUtil;
import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import com.gitee.pifeng.monitoring.common.exception.MonitoringUniversalException;
import com.gitee.pifeng.monitoring.common.threadpool.queue.QueueTypeEnum;
import com.gitee.pifeng.monitoring.common.threadpool.queue.ResizableLinkedBlockingQueue;
import com.gitee.pifeng.monitoring.common.threadpool.rejected.RejectedPolicyTypeEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * <p>
 * 线程池管理器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/1/18 21:38
 */
@Slf4j
public class ThreadPoolManager {

    /**
     * 注册的需要关闭的线程池
     */
    private static final Map<String, ThreadPoolExecutor> NEED_SHUTDOWN_THREAD_POOLS = Maps.newConcurrentMap();

    /**
     * 注册的不需要关闭的线程池
     */
    private static final Map<String, ThreadPoolExecutor> UN_NEED_SHUTDOWN_THREAD_POOLS = Maps.newConcurrentMap();

    /**
     * <p>
     * 注册线程池
     * </p>
     *
     * @param threadPoolName 线程池名字
     * @param executor       {@link ThreadPoolExecutor} 线程池执行器
     * @param needShutdown   是否需要管理线程池关闭
     * @author 皮锋
     * @custom.date 2025/1/18 21:43
     */
    public static void register(String threadPoolName, ThreadPoolExecutor executor, boolean needShutdown) {
        if (needShutdown) {
            // 使用 putIfAbsent 确保原子性
            if (NEED_SHUTDOWN_THREAD_POOLS.putIfAbsent(threadPoolName, executor) != null) {
                throw new MonitoringUniversalException("线程池已经存在，无法注册，请修改线程池名字！");
            }
        } else {
            if (UN_NEED_SHUTDOWN_THREAD_POOLS.putIfAbsent(threadPoolName, executor) != null) {
                throw new MonitoringUniversalException("线程池已经存在，无法注册，请修改线程池名字！");
            }
        }
    }

    /**
     * <p>
     * 取消注册线程池
     * </p>
     *
     * @param threadPoolName 线程池名字
     * @author 皮锋
     * @custom.date 2025/1/19 13:29
     */
    public static void unregister(String threadPoolName) {
        NEED_SHUTDOWN_THREAD_POOLS.remove(threadPoolName);
        UN_NEED_SHUTDOWN_THREAD_POOLS.remove(threadPoolName);
    }

    /**
     * <p>
     * 优雅地关闭所有需要关闭的线程池并且取消注册
     * </p>
     *
     * @author 皮锋
     * @custom.date 2025/1/21 23:00
     */
    public static void shutdownAllGracefullyAndUnregister() {
        List<Map.Entry<String, ThreadPoolExecutor>> entries = Lists.newArrayList(NEED_SHUTDOWN_THREAD_POOLS.entrySet());
        for (Map.Entry<String, ThreadPoolExecutor> entry : entries) {
            String key = entry.getKey();
            ThreadPoolExecutor executor = entry.getValue();
            // 优雅地关闭线程池
            shutdownGracefully(executor, key);
            // 取消注册线程池
            unregister(key);
        }
    }

    /**
     * <p>
     * 优雅地关闭线程池
     * </p>
     *
     * @param threadPoolExecutor {@link ThreadPoolExecutor}
     * @param alias              线程池别名
     * @author 皮锋
     * @custom.date 2020/11/29 12:30
     */
    public static void shutdownGracefully(ThreadPoolExecutor threadPoolExecutor, String alias) {
        try {
            if (!threadPoolExecutor.isShutdown()) {
                log.info("“{}”线程池开始关闭！", alias);
                // 使新任务无法提交
                threadPoolExecutor.shutdown();
                // 超时时长
                long timeout = 15L;
                // 等待未完成任务结束
                if (!threadPoolExecutor.awaitTermination(timeout, TimeUnit.SECONDS)) {
                    // 取消当前执行的任务
                    threadPoolExecutor.shutdownNow();
                    log.warn("中断工作进程，这可能导致某些任务不一致。请检查业务日志！");
                }
                // 等待任务取消的响应
                if (!threadPoolExecutor.awaitTermination(timeout, TimeUnit.SECONDS)) {
                    log.error("即使工作线程中断，线程池也无法关闭，这可能会导致某些任务不一致。请检查业务日志！");
                }
            }
        } catch (InterruptedException e) {
            // 重新取消当前线程进行中断
            threadPoolExecutor.shutdownNow();
            // 保留中断状态
            Thread.currentThread().interrupt();
        }
        log.info("“{}”线程池关闭！", alias);
    }

    /**
     * <p>
     * 获取所有线程池信息
     * </p>
     *
     * @return {@link JavaThreadPool}
     * @author 皮锋
     * @custom.date 2025/1/19 13:31
     */
    public static JavaThreadPool getAllThreadPoolInfo() {
        List<JavaThreadPool.ThreadPoolInfoDomain> threadPoolInfoDomains = Lists.newArrayList();
        // 迭代
        for (Map.Entry<String, ThreadPoolExecutor> entry : NEED_SHUTDOWN_THREAD_POOLS.entrySet()) {
            JavaThreadPool.ThreadPoolInfoDomain threadPoolInfoDomain = wrapThreadPoolInfoDomain(entry);
            threadPoolInfoDomains.add(threadPoolInfoDomain);
        }
        for (Map.Entry<String, ThreadPoolExecutor> entry : UN_NEED_SHUTDOWN_THREAD_POOLS.entrySet()) {
            JavaThreadPool.ThreadPoolInfoDomain threadPoolInfoDomain = wrapThreadPoolInfoDomain(entry);
            threadPoolInfoDomains.add(threadPoolInfoDomain);
        }
        return JavaThreadPool.builder().threadPoolInfoDomains(threadPoolInfoDomains).build();
    }

    /**
     * <p>
     * 封装单个线程池信息
     * </p>
     *
     * @param poolExecutorEntry {@link Map.Entry}
     * @return {@link JavaThreadPool.ThreadPoolInfoDomain}
     * @author 皮锋
     * @custom.date 2025/1/21 13:48
     */
    private static JavaThreadPool.ThreadPoolInfoDomain wrapThreadPoolInfoDomain(Map.Entry<String, ThreadPoolExecutor> poolExecutorEntry) {
        String key = poolExecutorEntry.getKey();
        ThreadPoolExecutor executor = poolExecutorEntry.getValue();
        JavaThreadPool.ThreadPoolInfoDomain threadPoolInfoDomain = new JavaThreadPool.ThreadPoolInfoDomain();
        threadPoolInfoDomain.setName(key);
        threadPoolInfoDomain.setActiveCount(executor.getActiveCount());
        threadPoolInfoDomain.setCompletedTaskCount(executor.getCompletedTaskCount());
        threadPoolInfoDomain.setTaskCount(executor.getTaskCount());
        threadPoolInfoDomain.setLargestPoolSize(executor.getLargestPoolSize());
        threadPoolInfoDomain.setPoolSize(executor.getPoolSize());
        threadPoolInfoDomain.setCorePoolSize(executor.getCorePoolSize());
        threadPoolInfoDomain.setMaximumPoolSize(executor.getMaximumPoolSize());
        threadPoolInfoDomain.setRejectedExecutionHandlerName(executor.getRejectedExecutionHandler().getClass().getSimpleName());
        threadPoolInfoDomain.setAllowCoreThreadTimeOut(executor.allowsCoreThreadTimeOut());
        threadPoolInfoDomain.setKeepAliveTime(executor.getKeepAliveTime(TimeUnit.SECONDS));
        BlockingQueue<Runnable> blockingQueue = executor.getQueue();
        if (blockingQueue != null) {
            int queueSize = blockingQueue.size();
            int remainingCapacity = blockingQueue.remainingCapacity();
            threadPoolInfoDomain.setQueueSize(queueSize);
            threadPoolInfoDomain.setQueueType(blockingQueue.getClass().getSimpleName());
            threadPoolInfoDomain.setQueueRemainingCapacity(remainingCapacity);
            // 无界队列（如DelayedWorkQueue、LinkedBlockingQueue无参构造等）的remainingCapacity()返回Integer.MAX_VALUE，
            // 这只是一个哨兵值表示"无限制"，直接加上queueSize会导致容量值超过Integer.MAX_VALUE，语义错误。
            if (remainingCapacity >= Integer.MAX_VALUE) {
                threadPoolInfoDomain.setQueueCapacity((long) Integer.MAX_VALUE);
            } else {
                threadPoolInfoDomain.setQueueCapacity((long) queueSize + (long) remainingCapacity);
            }
        }
        if (executor.getPoolSize() == 0) {
            threadPoolInfoDomain.setUtilizationRate((double) 0);
        } else {
            threadPoolInfoDomain.setUtilizationRate(NumberUtil.div(executor.getActiveCount(), executor.getPoolSize(), 4, RoundingMode.HALF_UP));
        }
        if (executor instanceof MonitoredScheduledThreadPoolExecutor) {
            long rejectedTaskCount = ((MonitoredScheduledThreadPoolExecutor) executor).getRejectedTaskCount();
            threadPoolInfoDomain.setRejectedTaskCount(rejectedTaskCount);
        } else if (executor instanceof MonitoredThreadPoolExecutor) {
            long rejectedTaskCount = ((MonitoredThreadPoolExecutor) executor).getRejectedTaskCount();
            threadPoolInfoDomain.setRejectedTaskCount(rejectedTaskCount);
        }
        return threadPoolInfoDomain;
    }

    /**
     * <p>
     * 动态修改线程池配置
     * </p>
     *
     * @param threadPoolInfoDomain {@link JavaThreadPool.ThreadPoolInfoDomain} 线程池配置信息
     * @return 配置是否成功
     * @author 皮锋
     * @custom.date 2026/3/23 00:00
     */
    public static boolean dynamicUpdateThreadPool(JavaThreadPool.ThreadPoolInfoDomain threadPoolInfoDomain) {
        String name = threadPoolInfoDomain.getName();
        // 从两个注册表中查找线程池
        ThreadPoolExecutor executor = NEED_SHUTDOWN_THREAD_POOLS.get(name);
        if (executor == null) {
            executor = UN_NEED_SHUTDOWN_THREAD_POOLS.get(name);
        }
        if (executor == null) {
            log.warn("未找到名为“{}”的线程池，无法配置！", name);
            return false;
        }
        try {
            Integer corePoolSize = threadPoolInfoDomain.getCorePoolSize();
            Integer maximumPoolSize = threadPoolInfoDomain.getMaximumPoolSize();
            // ScheduledThreadPoolExecutor 的最大线程数由 DelayedWorkQueue 的无界特性决定，修改无意义
            if (executor instanceof ScheduledThreadPoolExecutor) {
                if (corePoolSize != null) {
                    executor.setCorePoolSize(corePoolSize);
                }
            } else {
                if (corePoolSize != null && maximumPoolSize != null) {
                    // 避免 corePoolSize > maximumPoolSize 导致 IllegalArgumentException，需要根据大小关系决定设置顺序
                    if (corePoolSize > executor.getMaximumPoolSize()) {
                        executor.setMaximumPoolSize(maximumPoolSize);
                        executor.setCorePoolSize(corePoolSize);
                    } else {
                        executor.setCorePoolSize(corePoolSize);
                        executor.setMaximumPoolSize(maximumPoolSize);
                    }
                } else if (corePoolSize != null) {
                    executor.setCorePoolSize(corePoolSize);
                } else if (maximumPoolSize != null) {
                    executor.setMaximumPoolSize(maximumPoolSize);
                }
            }
            // 设置队列类型和容量
            changeQueueTypeAndCapacity(executor, threadPoolInfoDomain, name);
            // 设置空闲线程回收时间（秒）
            Long keepAliveTime = threadPoolInfoDomain.getKeepAliveTime();
            if (keepAliveTime != null) {
                executor.setKeepAliveTime(keepAliveTime, TimeUnit.SECONDS);
            }
            // 设置核心线程是否允许超时回收
            Boolean allowCoreThreadTimeOut = threadPoolInfoDomain.getAllowCoreThreadTimeOut();
            if (allowCoreThreadTimeOut != null) {
                if (allowCoreThreadTimeOut && executor.getKeepAliveTime(TimeUnit.NANOSECONDS) <= 0) {
                    log.warn("线程池“{}”的keepAliveTime为0, 无法开启allowCoreThreadTimeOut, 跳过设置！", name);
                } else {
                    executor.allowCoreThreadTimeOut(allowCoreThreadTimeOut);
                }
            }
            // 设置拒绝策略
            String rejectedExecutionHandlerName = threadPoolInfoDomain.getRejectedExecutionHandlerName();
            if (rejectedExecutionHandlerName != null) {
                RejectedExecutionHandler handler = RejectedPolicyTypeEnum.createPolicy(rejectedExecutionHandlerName);
                if (handler != null) {
                    executor.setRejectedExecutionHandler(handler);
                } else {
                    log.warn("无法解析的拒绝策略“{}”，跳过设置！", rejectedExecutionHandlerName);
                }
            }
            log.info("线程池“{}”动态配置成功！", name);
            return true;
        } catch (Exception e) {
            log.error("线程池“{}”动态配置失败！", name, e);
            return false;
        }
    }

    /**
     * <p>
     * 修改线程池的队列类型和/或队列容量。
     * </p>
     * 统一通过 {@link QueueTypeEnum} 创建新队列，将旧队列中的任务迁移到新队列，并通过反射替换 {@link ThreadPoolExecutor} 的 {@code workQueue} 字段。<br>
     * 注意：{@link ScheduledThreadPoolExecutor} 使用内部 {@code DelayedWorkQueue}，不支持变更。<br>
     *
     * @param executor             {@link ThreadPoolExecutor} 线程池执行器
     * @param threadPoolInfoDomain {@link JavaThreadPool.ThreadPoolInfoDomain} 线程池配置信息
     * @param name                 线程池名称
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    @Deprecated
    private static void obsoleteChangeQueueTypeAndCapacity(ThreadPoolExecutor executor,
                                                           JavaThreadPool.ThreadPoolInfoDomain threadPoolInfoDomain,
                                                           String name) {
        String newQueueType = threadPoolInfoDomain.getQueueType();
        Long newQueueCapacity = threadPoolInfoDomain.getQueueCapacity();
        if (newQueueType == null && newQueueCapacity == null) {
            return;
        }
        // ScheduledThreadPoolExecutor 使用内部私有的 DelayedWorkQueue，不支持变更
        if (executor instanceof ScheduledThreadPoolExecutor) {
            log.warn("线程池“{}”是ScheduledThreadPoolExecutor，其队列为DelayedWorkQueue，不支持变更队列类型和容量，跳过设置！", name);
            return;
        }
        BlockingQueue<Runnable> oldQueue = executor.getQueue();
        String oldQueueType = oldQueue.getClass().getSimpleName();
        // 确定目标队列类型：优先使用新类型，否则沿用旧类型
        String targetQueueType = (newQueueType != null) ? newQueueType : oldQueueType;
        boolean typeChanged = !targetQueueType.equals(oldQueueType);
        // 确定目标容量
        int targetCapacity = (newQueueCapacity != null && newQueueCapacity > 0) ? newQueueCapacity.intValue() : 1024;
        // 创建新队列
        BlockingQueue<Runnable> newQueue = QueueTypeEnum.createBlockingQueue(targetQueueType, targetCapacity);
        if (newQueue == null) {
            log.warn("无法识别的队列类型“{}”，跳过队列变更！", targetQueueType);
            return;
        }
        // 将旧队列中的任务迁移到新队列
        List<Runnable> tasks = new ArrayList<>();
        oldQueue.drainTo(tasks);
        for (Runnable task : tasks) {
            newQueue.offer(task);
        }
        // 通过反射替换 ThreadPoolExecutor 的 workQueue 字段
        replaceWorkQueue(executor, newQueue, name);
        // 触发 interruptIdleWorkers()：唤醒阻塞在旧队列 take()/poll() 上的空闲线程，使其重新读取新的 workQueue
        // setCorePoolSize 缩小时会调用 interruptIdleWorkers()，该方法仅中断 tryLock 成功的空闲线程，不影响正在执行任务的线程
        int corePoolSize = executor.getCorePoolSize();
        executor.setCorePoolSize(0);
        executor.setCorePoolSize(corePoolSize);
        if (typeChanged) {
            log.info("线程池“{}”队列类型已从“{}”变更为“{}”，容量：{}", name, oldQueueType, targetQueueType, targetCapacity);
        } else {
            log.info("线程池“{}”队列({})容量已修改为：{}", name, targetQueueType, targetCapacity);
        }
    }

    /**
     * <p>
     * 通过反射替换 {@link ThreadPoolExecutor} 的 {@code workQueue} 字段。
     * </p>
     * 由于 {@code ThreadPoolExecutor.workQueue} 是 {@code private final} 字段，需要通过反射的 {@code setAccessible(true)} 绕过访问限制来完成替换。
     *
     * @param executor 线程池执行器
     * @param newQueue 新的阻塞队列
     * @param name     线程池名称
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    private static void replaceWorkQueue(ThreadPoolExecutor executor, BlockingQueue<Runnable> newQueue, String name) {
        try {
            Field workQueueField = ThreadPoolExecutor.class.getDeclaredField("workQueue");
            workQueueField.setAccessible(true);
            workQueueField.set(executor, newQueue);
        } catch (Exception e) {
            log.error("线程池“{}”通过反射替换workQueue失败！", name, e);
        }
    }

    /**
     * <p>
     * 修改线程池的队列类型和/或队列容量。
     * </p>
     * 1.如果队列类型发生了变更：通过 {@link QueueTypeEnum} 创建新队列，将旧队列中的任务迁移到新队列，并通过反射替换 {@link ThreadPoolExecutor} 的 {@code workQueue} 字段；<br>
     * 2.如果队列类型未变更，仅修改容量：<br>
     * · 当前队列为 {@link ResizableLinkedBlockingQueue} 时，直接调用 {@link ResizableLinkedBlockingQueue#setCapacity(int)} 动态修改容量；<br>
     * · 当前队列为其他类型时，无法直接修改容量（JDK 队列的 capacity 是 final 的），日志提示跳过。<br>
     *
     * @param executor             {@link ThreadPoolExecutor} 线程池执行器
     * @param threadPoolInfoDomain {@link JavaThreadPool.ThreadPoolInfoDomain} 线程池配置信息
     * @param name                 线程池名称
     * @author 皮锋
     * @custom.date 2026/3/24 00:00
     */
    private static void changeQueueTypeAndCapacity(ThreadPoolExecutor executor,
                                                   JavaThreadPool.ThreadPoolInfoDomain threadPoolInfoDomain,
                                                   String name) {
        String newQueueType = threadPoolInfoDomain.getQueueType();
        Long newQueueCapacity = threadPoolInfoDomain.getQueueCapacity();
        if (newQueueType == null && newQueueCapacity == null) {
            return;
        }
        // ScheduledThreadPoolExecutor 使用内部私有的 DelayedWorkQueue，不支持变更
        if (executor instanceof ScheduledThreadPoolExecutor) {
            log.warn("线程池“{}”是ScheduledThreadPoolExecutor，其队列为DelayedWorkQueue，不支持变更队列类型和容量，跳过设置！", name);
            return;
        }
        BlockingQueue<Runnable> oldQueue = executor.getQueue();
        String oldQueueType = oldQueue.getClass().getSimpleName();
        boolean queueTypeChanged = newQueueType != null && !newQueueType.equals(oldQueueType);
        if (queueTypeChanged) {
            // 队列类型发生了变更，创建新队列并替换
            int capacity = (newQueueCapacity != null && newQueueCapacity > 0) ? newQueueCapacity.intValue() : 1024;
            BlockingQueue<Runnable> newQueue = QueueTypeEnum.createBlockingQueue(newQueueType, capacity);
            if (newQueue == null) {
                log.warn("无法识别的队列类型“{}”，跳过队列变更！", newQueueType);
                return;
            }
            // 将旧队列中的任务迁移到新队列
            List<Runnable> tasks = new ArrayList<>();
            oldQueue.drainTo(tasks);
            for (Runnable task : tasks) {
                newQueue.offer(task);
            }
            // 通过反射替换 ThreadPoolExecutor 的 workQueue 字段
            replaceWorkQueue(executor, newQueue, name);
            log.info("线程池“{}”队列类型已从“{}”变更为“{}”，容量：{}", name, oldQueueType, newQueueType, capacity);
        } else if (newQueueCapacity != null) {
            // 队列类型未变更，仅修改容量
            if (oldQueue instanceof ResizableLinkedBlockingQueue) {
                ((ResizableLinkedBlockingQueue<Runnable>) oldQueue).setCapacity(newQueueCapacity.intValue());
                log.info("线程池“{}”队列(ResizableLinkedBlockingQueue)容量已动态修改为：{}", name, newQueueCapacity);
            } else {
                log.warn("线程池“{}”的队列类型为“{}”，不支持动态修改容量(仅ResizableLinkedBlockingQueue支持)，跳过设置！", name, oldQueueType);
            }
        }
    }

}
