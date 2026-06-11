package com.gitee.pifeng.monitoring.plug.core;

import com.gitee.pifeng.monitoring.common.threadpool.MonitoredScheduledThreadPoolExecutor;
import com.gitee.pifeng.monitoring.common.util.server.ProcessorsUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * 线程池获取器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/12/6 12:46
 */
public class ThreadPoolAcquirer {

    /**
     * 应用实例任务调度 延迟/周期执行线程池
     */
    private static volatile MonitoredScheduledThreadPoolExecutor instanceScheduledThreadPoolExecutor;

    /**
     * 网络设备信息任务调度 延迟/周期执行线程池
     */
    private static volatile MonitoredScheduledThreadPoolExecutor networkDeviceScheduledThreadPoolExecutor;

    /**
     * 服务器信息任务调度 延迟/周期执行线程池
     */
    private static volatile MonitoredScheduledThreadPoolExecutor serverScheduledThreadPoolExecutor;

    /**
     * websocket客户端 延迟/周期执行线程池
     */
    private static volatile MonitoredScheduledThreadPoolExecutor websocketClientReconnectScheduledThreadPoolExecutor;

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2025/2/9 22:36
     */
    private ThreadPoolAcquirer() {
    }

    /**
     * <p>
     * 获取 应用实例任务调度 延迟/周期执行线程池
     * </p>
     *
     * @return {@link MonitoredScheduledThreadPoolExecutor} 应用实例任务调度 延迟/周期执行线程池
     * @custom.date 2025/2/9 22:37
     */
    public static MonitoredScheduledThreadPoolExecutor getInstanceScheduledThreadPoolExecutor() {
        if (instanceScheduledThreadPoolExecutor == null) {
            synchronized (ThreadPoolAcquirer.class) {
                if (instanceScheduledThreadPoolExecutor == null) {
                    instanceScheduledThreadPoolExecutor = new MonitoredScheduledThreadPoolExecutor(
                            // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                            (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                            new BasicThreadFactory.Builder()
                                    // 设置线程名
                                    .namingPattern("phoenix-instance-scheduled-pool-thread-%d")
                                    // 设置为守护线程
                                    .daemon(true)
                                    .build(),
                            new ThreadPoolExecutor.AbortPolicy(), "phoenix-instance-scheduled-pool", true);
                }
            }
        }
        return instanceScheduledThreadPoolExecutor;
    }

    /**
     * <p>
     * 获取 网络设备信息任务调度 延迟/周期执行线程池
     * </p>
     *
     * @return {@link MonitoredScheduledThreadPoolExecutor} 网络设备信息任务调度 延迟/周期执行线程池
     * @custom.date 2025/2/9 22:37
     */
    public static MonitoredScheduledThreadPoolExecutor getNetworkDeviceScheduledThreadPoolExecutor() {
        if (networkDeviceScheduledThreadPoolExecutor == null) {
            synchronized (ThreadPoolAcquirer.class) {
                if (networkDeviceScheduledThreadPoolExecutor == null) {
                    networkDeviceScheduledThreadPoolExecutor = new MonitoredScheduledThreadPoolExecutor(
                            // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                            (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                            new BasicThreadFactory.Builder()
                                    // 设置线程名
                                    .namingPattern("phoenix-networkdevice-scheduled-pool-thread-%d")
                                    // 设置为守护线程
                                    .daemon(true)
                                    .build(),
                            new ThreadPoolExecutor.AbortPolicy(), "phoenix-networkdevice-scheduled-pool", true);
                }
            }
        }
        return networkDeviceScheduledThreadPoolExecutor;
    }

    /**
     * <p>
     * 获取 服务器信息任务调度 延迟/周期执行线程池
     * </p>
     *
     * @return {@link MonitoredScheduledThreadPoolExecutor} 服务器信息任务调度 延迟/周期执行线程池
     * @custom.date 2025/2/9 22:37
     */
    public static MonitoredScheduledThreadPoolExecutor getServerScheduledThreadPoolExecutor() {
        if (serverScheduledThreadPoolExecutor == null) {
            synchronized (ThreadPoolAcquirer.class) {
                if (serverScheduledThreadPoolExecutor == null) {
                    serverScheduledThreadPoolExecutor = new MonitoredScheduledThreadPoolExecutor(
                            // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                            (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                            new BasicThreadFactory.Builder()
                                    // 设置线程名
                                    .namingPattern("phoenix-server-scheduled-pool-thread-%d")
                                    // 设置为守护线程
                                    .daemon(true)
                                    .build(),
                            new ThreadPoolExecutor.AbortPolicy(), "phoenix-server-scheduled-pool", true);
                }
            }
        }
        return serverScheduledThreadPoolExecutor;
    }

    /**
     * <p>
     * 获取 websocket客户端 延迟/周期执行线程池
     * </p>
     *
     * @return {@link MonitoredScheduledThreadPoolExecutor} websocket客户端 延迟/周期执行线程池
     * @author 皮锋
     * @custom.date 2026/2/12 16:17
     */
    public static MonitoredScheduledThreadPoolExecutor getWebsocketClientReconnectScheduledThreadPoolExecutor() {
        if (websocketClientReconnectScheduledThreadPoolExecutor == null) {
            synchronized (ThreadPoolAcquirer.class) {
                if (websocketClientReconnectScheduledThreadPoolExecutor == null) {
                    websocketClientReconnectScheduledThreadPoolExecutor = new MonitoredScheduledThreadPoolExecutor(
                            // 线程数 = Ncpu /（1 - 阻塞系数），IO密集型阻塞系数相对较大
                            (int) (ProcessorsUtils.getAvailableProcessors() / (1 - 0.8)),
                            new BasicThreadFactory.Builder()
                                    // 设置线程名
                                    .namingPattern("phoenix-websocket-client-scheduled-pool-thread-%d")
                                    // 设置为守护线程
                                    .daemon(true)
                                    .build(),
                            new ThreadPoolExecutor.AbortPolicy(), "phoenix-websocket-client-scheduled-pool", true);
                }
            }
        }
        return websocketClientReconnectScheduledThreadPoolExecutor;
    }

}
