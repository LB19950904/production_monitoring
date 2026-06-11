package com.gitee.pifeng.monitoring.server.config;

import com.gitee.pifeng.monitoring.common.threadpool.MonitoredThreadPoolExecutor;
import com.gitee.pifeng.monitoring.common.util.server.ProcessorsUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 线程池配置
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024年12月05日 下午18:25:50
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * <p>
     * 服务器服务监控线程池
     * </p>
     *
     * @return {@link MonitoredThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2025/9/30 09:39
     */
    @Lazy
    @Bean(name = "serverMonitorThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredThreadPoolExecutor serverMonitorThreadPoolExecutor() {
        return new MonitoredThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                1L,
                TimeUnit.HOURS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-server-server-monitor-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-server-monitor-pool", false);
    }

    /**
     * <p>
     * 应用实例服务监控线程池
     * </p>
     *
     * @return {@link MonitoredThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2025/9/30 12:40
     */
    @Lazy
    @Bean(name = "instanceMonitorThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredThreadPoolExecutor instanceMonitorThreadPoolExecutor() {
        return new MonitoredThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                1L,
                TimeUnit.HOURS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-server-instance-monitor-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-instance-monitor-pool", false);
    }

    /**
     * <p>
     * docker服务监控线程池
     * </p>
     *
     * @return {@link MonitoredThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2025/9/30 12:49
     */
    @Lazy
    @Bean(name = "dockerMonitorThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredThreadPoolExecutor dockerMonitorThreadPoolExecutor() {
        return new MonitoredThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                1L,
                TimeUnit.HOURS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-server-docker-monitor-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-docker-monitor-pool", false);
    }

    /**
     * 数据库服务监控线程池
     *
     * @return {@link MonitoredThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2024/12/05 19:48
     */
    @Lazy
    @Bean(name = "dbMonitorThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredThreadPoolExecutor dbMonitorThreadPoolExecutor() {
        return new MonitoredThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                1L,
                TimeUnit.HOURS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-server-db-monitor-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-db-monitor-pool", false);
    }

    /**
     * HTTP服务监控线程池
     *
     * @return {@link MonitoredThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2024/12/05 19:48
     */
    @Lazy
    @Bean(name = "httpMonitorThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredThreadPoolExecutor httpMonitorThreadPoolExecutor() {
        return new MonitoredThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                1L,
                TimeUnit.HOURS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-server-http-monitor-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-http-monitor-pool", false);
    }

    /**
     * 网络服务监控线程池
     *
     * @return {@link MonitoredThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2024/12/05 19:48
     */
    @Lazy
    @Bean(name = "netMonitorThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredThreadPoolExecutor netMonitorThreadPoolExecutor() {
        return new MonitoredThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                1L,
                TimeUnit.HOURS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-server-net-monitor-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-net-monitor-pool", false);
    }

    /**
     * TCP服务监控线程池
     *
     * @return {@link MonitoredThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2024/12/05 19:48
     */
    @Lazy
    @Bean(name = "tcpMonitorThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredThreadPoolExecutor tcpMonitorThreadPoolExecutor() {
        return new MonitoredThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                1L,
                TimeUnit.HOURS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-server-tcp-monitor-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-tcp-monitor-pool", false);
    }

    /**
     * 网络设备监控线程池
     *
     * @return {@link MonitoredThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2025/3/25 16:23
     */
    @Lazy
    @Bean(name = "networkDeviceMonitorThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredThreadPoolExecutor networkDeviceMonitorThreadPoolExecutor() {
        return new MonitoredThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                1L,
                TimeUnit.HOURS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-server-networkdevice-monitor-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-networkdevice-monitor-pool", false);
    }

    /**
     * 告警服务线程池
     *
     * @return {@link MonitoredThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2024/12/05 19:48
     */
    @Lazy
    @Bean(name = "alarmThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredThreadPoolExecutor alarmThreadPoolExecutor() {
        return new MonitoredThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                1L,
                TimeUnit.HOURS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-server-alarm-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-alarm-pool", false);
    }

}
