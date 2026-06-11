package com.gitee.pifeng.monitoring.plug.scheduler;

import com.gitee.pifeng.monitoring.plug.core.ConfigLoader;
import com.gitee.pifeng.monitoring.plug.core.ThreadPoolAcquirer;
import com.gitee.pifeng.monitoring.plug.thread.JavaThreadPoolThread;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 发送Java线程池信息任务调度器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/13 15:55
 */
public class JavaThreadPoolTaskScheduler {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/3/13 15:55
     */
    private JavaThreadPoolTaskScheduler() {
    }

    /**
     * <p>
     * 如果监控配置文件中配置了发送Java线程池信息，则延迟45秒启动定时任务，定时发送Java线程池信息包，
     * 定时任务的执行频率一般为监控配置文件中配置的Java线程池信息包发送频率，如果监控配置文件中没有配置Java线程池信息包的发送频率，
     * 则由类{@link ConfigLoader}提供默认的发送Java线程池信息频率。
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/3/13 16:13
     */
    public static void run() {
        // 是否发送Java线程池
        boolean javaThreadPoolInfoEnable = ConfigLoader.getMonitoringProperties().getJavaThreadPoolInfo().getEnable();
        if (javaThreadPoolInfoEnable) {
            // 发送Java线程池的频率
            long rate = ConfigLoader.getMonitoringProperties().getJavaThreadPoolInfo().getRate();
            ThreadPoolAcquirer.getInstanceScheduledThreadPoolExecutor().scheduleWithFixedDelay(new JavaThreadPoolThread(), 45, rate, TimeUnit.SECONDS);
        }
    }

}