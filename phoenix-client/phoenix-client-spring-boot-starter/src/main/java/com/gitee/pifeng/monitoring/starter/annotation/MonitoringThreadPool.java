package com.gitee.pifeng.monitoring.starter.annotation;

import java.lang.annotation.*;

/**
 * <p>
 * 自定义注解，标记为被持续监控的线程池
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/1/27 16:24
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MonitoringThreadPool {

    /**
     * <p>
     * 线程池名字
     * </p>
     *
     * @return 线程池名字
     */
    String value() default "";

    /**
     * <p>
     * 是否需要管理线程池关闭
     * </p>
     *
     * @return 是否
     */
    boolean needShutdown() default false;

}