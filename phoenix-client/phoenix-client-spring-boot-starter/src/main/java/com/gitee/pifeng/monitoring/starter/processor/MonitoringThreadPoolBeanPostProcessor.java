package com.gitee.pifeng.monitoring.starter.processor;

import com.gitee.pifeng.monitoring.common.threadpool.MonitoredScheduledThreadPoolExecutor;
import com.gitee.pifeng.monitoring.common.threadpool.MonitoredThreadPoolExecutor;
import com.gitee.pifeng.monitoring.starter.annotation.MonitoringThreadPool;
import com.gitee.pifeng.monitoring.starter.util.AnnotationUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * <p>
 * 识别带有 @MonitoringThreadPool 注解的线程池bean，并将其注册到 ThreadPoolManager 中进行管理
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/1/27 16:43
 */
@Slf4j
@Component
public class MonitoringThreadPoolBeanPostProcessor implements BeanFactoryAware, BeanPostProcessor {

    private DefaultListableBeanFactory beanFactory;

    @Override
    public void setBeanFactory(@NonNull BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName) throws BeansException {
        Class<?> beanType = null;
        try {
            beanType = AutoProxyUtils.determineTargetClass(this.beanFactory, beanName);
        } catch (NoSuchBeanDefinitionException ex) {
            if (log.isDebugEnabled()) {
                log.debug("无法解析bean [{}] 的目标类！", beanName, ex);
            }
        }
        if (Objects.isNull(beanType)) {
            if (log.isDebugEnabled()) {
                log.debug("无法解析bean [{}] 的类型！", beanName);
            }
            return bean;
        }
        // 如果不是 ThreadPoolExecutor类 或者其子类，直接返回
        if (!(ThreadPoolExecutor.class.isAssignableFrom(beanType))) {
            return bean;
        }
        // 已经是被监控的线程池
        if (MonitoredScheduledThreadPoolExecutor.class.equals(beanType)) {
            return bean;
        }
        if (MonitoredThreadPoolExecutor.class.equals(beanType)) {
            return bean;
        }
        // 注册线程池到线程池管理器
        return this.register(bean, beanType, beanName);
    }

    /**
     * <p>
     * 注册线程池到线程池管理器
     * </p>
     *
     * @param bean     经过Spring容器初始化后的Bean实例
     * @param beanType 经过Spring容器初始化后的Bean实例的类型
     * @param beanName 对应bean的名字，在Spring配置中指定
     * @return bean实例
     * @author 皮锋
     * @custom.date 2025/2/8 09:14
     */
    private Object register(Object bean, Class<?> beanType, String beanName) {
        try {
            MonitoringThreadPool monitoringThreadPool = AnnotationUtils.findAnnotationOnBean(this.beanFactory, beanName, MonitoringThreadPool.class);
            if (Objects.isNull(monitoringThreadPool)) {
                return bean;
            }
            // 线程池名字
            String name = monitoringThreadPool.value();
            if (StringUtils.isBlank(name)) {
                name = beanName;
            }
            // 是否需要管理线程池关闭
            boolean needShutdown = monitoringThreadPool.needShutdown();
            // 是否是ScheduledThreadPoolExecutor类或者其子类
            if (ScheduledThreadPoolExecutor.class.isAssignableFrom(beanType)) {
                ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = (ScheduledThreadPoolExecutor) bean;
                // 创建自定义的带有监控管理功能的用于调度任务的线程池执行器
                MonitoredScheduledThreadPoolExecutor monitoredScheduledThreadPoolExecutor = this.createMonitoredScheduledThreadPoolExecutor(scheduledThreadPoolExecutor, name, needShutdown);
                // 复制其他配置（如任务队列、拒绝策略等）
                this.copyThreadPoolExecutorConfig(scheduledThreadPoolExecutor, monitoredScheduledThreadPoolExecutor);
                return monitoredScheduledThreadPoolExecutor;
            }
            // 是否是ThreadPoolExecutor类或者其子类
            if (ThreadPoolExecutor.class.isAssignableFrom(beanType)) {
                ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) bean;
                // 创建自定义的带有监控管理功能的线程池执行器
                MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = this.createMonitoredThreadPoolExecutor(threadPoolExecutor, name, needShutdown);
                // 复制其他配置（如任务队列、拒绝策略等）
                this.copyThreadPoolExecutorConfig(threadPoolExecutor, monitoredThreadPoolExecutor);
                return monitoredThreadPoolExecutor;
            }
            return bean;
        } catch (Exception e) {
            log.error("注册线程池到线程池管理器出错！");
            throw e;
        }
    }

    /**
     * <p>
     * 创建自定义的带有监控管理功能的线程池执行器
     * </p>
     *
     * @param originalExecutor 源线程池执行器
     * @param name             线程池名字
     * @param needShutdown     是否需要管理线程池关闭
     * @return {@link MonitoredThreadPoolExecutor} 自定义的带有监控管理功能的线程池执行器
     * @author 皮锋
     * @custom.date 2025/2/11 15:48
     */
    private MonitoredThreadPoolExecutor createMonitoredThreadPoolExecutor(ThreadPoolExecutor originalExecutor, String name, boolean needShutdown) {
        // 核心线程数
        int corePoolSize = originalExecutor.getCorePoolSize();
        // 最大线程数
        int maximumPoolSize = originalExecutor.getMaximumPoolSize();
        // 当线程数大于核心线程数（corePoolSize）时，多余的空闲线程在终止前等待新任务的最长时间
        long keepAliveTime = originalExecutor.getKeepAliveTime(TimeUnit.NANOSECONDS);
        // keepAliveTime参数的时间单位
        TimeUnit unit = TimeUnit.NANOSECONDS;
        // 来存放等待执行任务的队列
        BlockingQueue<Runnable> workQueue = originalExecutor.getQueue();
        // 线程池名字
        ThreadFactory threadFactory = originalExecutor.getThreadFactory();
        // 当线程池无法接受新任务时的行为
        RejectedExecutionHandler handler = originalExecutor.getRejectedExecutionHandler();
        if (threadFactory != null && handler != null) {
            return new MonitoredThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler, name, needShutdown);
        } else if (threadFactory != null) {
            return new MonitoredThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, name, needShutdown);
        } else if (handler != null) {
            return new MonitoredThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler, name, needShutdown);
        } else {
            return new MonitoredThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, name, needShutdown);
        }
    }

    /**
     * <p>
     * 创建自定义的带有监控管理功能的用于调度任务的线程池执行器
     * </p>
     *
     * @param originalExecutor 源用于调度任务的线程池执行器
     * @param name             线程池名字
     * @param needShutdown     是否需要管理线程池关闭
     * @return {@link MonitoredScheduledThreadPoolExecutor} 自定义的带有监控管理功能的用于调度任务的线程池执行器
     * @author 皮锋
     * @custom.date 2025/2/11 15:25
     */
    private MonitoredScheduledThreadPoolExecutor createMonitoredScheduledThreadPoolExecutor(ScheduledThreadPoolExecutor originalExecutor, String name, boolean needShutdown) {
        // 核心线程数
        int corePoolSize = originalExecutor.getCorePoolSize();
        // 用来创建新线程的工厂
        ThreadFactory threadFactory = originalExecutor.getThreadFactory();
        // 当线程池无法接受新任务时的行为
        RejectedExecutionHandler rejectedExecutionHandler = originalExecutor.getRejectedExecutionHandler();
        if (threadFactory != null && rejectedExecutionHandler != null) {
            return new MonitoredScheduledThreadPoolExecutor(corePoolSize, threadFactory, rejectedExecutionHandler, name, needShutdown);
        } else if (threadFactory != null) {
            return new MonitoredScheduledThreadPoolExecutor(corePoolSize, threadFactory, name, needShutdown);
        } else if (rejectedExecutionHandler != null) {
            return new MonitoredScheduledThreadPoolExecutor(corePoolSize, rejectedExecutionHandler, name, needShutdown);
        } else {
            return new MonitoredScheduledThreadPoolExecutor(corePoolSize, name, needShutdown);
        }
    }

    /**
     * <p>
     * 复制线程池执行器配置
     * </p>
     *
     * @param <T>               线程池执行器
     * @param originalExecutor  源线程池执行器
     * @param monitoredExecutor 被监控的线程池执行器
     * @author 皮锋
     * @custom.date 2025/2/11 16:47
     */
    private <T extends ThreadPoolExecutor> void copyThreadPoolExecutorConfig(T originalExecutor, T monitoredExecutor) {
        // 复制公共字段
        // this.copyCommonFields(originalExecutor, monitoredExecutor);
        // 根据具体类型处理特定字段
    }

    /**
     * <p>
     * 复制线程池执行器的公共配置
     * </p>
     *
     * @param originalExecutor  源线程池执行器
     * @param monitoredExecutor 被监控的线程池执行器
     * @author 皮锋
     * @custom.date 2025/2/11 16:48
     */
    @SuppressWarnings("unchecked")
    @SneakyThrows
    private void copyCommonFields(ThreadPoolExecutor originalExecutor, ThreadPoolExecutor monitoredExecutor) {
        // 复制 workQueue 字段
        Field workQueueField = ThreadPoolExecutor.class.getDeclaredField("workQueue");
        workQueueField.setAccessible(true);
        BlockingQueue<Runnable> workQueue = (BlockingQueue<Runnable>) workQueueField.get(originalExecutor);
        Field workQueueFieldInMonitored = ThreadPoolExecutor.class.getDeclaredField("workQueue");
        workQueueFieldInMonitored.setAccessible(true);
        workQueueFieldInMonitored.set(monitoredExecutor, workQueue);
        // 复制 ctl 字段
        Field ctlField = ThreadPoolExecutor.class.getDeclaredField("ctl");
        ctlField.setAccessible(true);
        Integer ctl = (Integer) ctlField.get(originalExecutor);
        Field ctlFieldInMonitored = ThreadPoolExecutor.class.getDeclaredField("ctl");
        ctlFieldInMonitored.setAccessible(true);
        ctlFieldInMonitored.set(monitoredExecutor, ctl);
        // 其他公共字段也可以通过类似的方式进行复制
        // 注意：某些字段可能无法直接复制，需要根据具体情况进行处理
    }

}