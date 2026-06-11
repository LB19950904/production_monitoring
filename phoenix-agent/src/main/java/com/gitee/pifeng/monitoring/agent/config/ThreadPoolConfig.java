package com.gitee.pifeng.monitoring.agent.config;

import com.gitee.pifeng.monitoring.common.threadpool.MonitoredScheduledThreadPoolExecutor;
import com.gitee.pifeng.monitoring.common.util.server.ProcessorsUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.concurrent.ThreadPoolExecutor;

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
     * Docker服务状态监控 延迟/周期执行线程池
     *
     * @return {@link MonitoredScheduledThreadPoolExecutor}
     * @author 皮锋
     * @custom.date 2024/12/05 19:48
     */
    @Lazy
    @Bean(name = "dockerMonitorScheduledThreadPoolExecutor", destroyMethod = "shutdown")
    public MonitoredScheduledThreadPoolExecutor dockerMonitorScheduledThreadPoolExecutor() {
        return new MonitoredScheduledThreadPoolExecutor(
                // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                new BasicThreadFactory.Builder()
                        // 设置线程名
                        .namingPattern("phoenix-docker-scheduled-pool-thread-%d")
                        // 设置为守护线程
                        .daemon(true)
                        .build(),
                new ThreadPoolExecutor.AbortPolicy(), "phoenix-docker-scheduled-pool", false);
    }

}
