package com.gitee.pifeng.monitoring.common.threadpool.rejected;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * <p>
 * 自定义拒绝策略SPI接口：用户可通过 Java SPI 机制提供自定义的线程池拒绝策略实现。
 * </p>
 * 使用方式：在 {@code META-INF/services/com.gitee.pifeng.monitoring.common.threadpool.rejected.CustomRejectedExecutionHandler} 文件中声明实现类的全限定名。
 *
 * @author 皮锋
 * @custom.date 2026/3/23 00:00
 */
public interface CustomRejectedExecutionHandler {

    /**
     * <p>
     * 获取自定义拒绝策略名称
     * </p>
     *
     * @return 拒绝策略名称
     * @author 皮锋
     * @custom.date 2026/3/23 00:00
     */
    String getName();

    /**
     * <p>
     * 生成自定义拒绝策略实例
     * </p>
     *
     * @return {@link RejectedExecutionHandler} 拒绝策略实例
     * @author 皮锋
     * @custom.date 2026/3/23 00:00
     */
    RejectedExecutionHandler generateRejected();

}
