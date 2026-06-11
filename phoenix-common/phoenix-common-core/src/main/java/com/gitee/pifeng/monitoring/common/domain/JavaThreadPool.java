package com.gitee.pifeng.monitoring.common.domain;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * Java线程池信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/1/20 15:39
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class JavaThreadPool extends AbstractSuperBean {

    /**
     * 线程池信息列表
     */
    private List<JavaThreadPool.ThreadPoolInfoDomain> threadPoolInfoDomains;

    @Data
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class ThreadPoolInfoDomain extends AbstractSuperBean {

        /**
         * 线程池名字
         */
        private String name;

        /**
         * 当前有多少线程正在工作
         */
        private Integer activeCount;

        /**
         * 已完成的任务数量（包括正常完成、被取消或抛出异常的任务）
         */
        private Long completedTaskCount;

        /**
         * 曾经接收过的总任务数，包括已执行完毕、正在执行以及等待执行的任务
         */
        private Long taskCount;

        /**
         * 历史上最多同时存在的线程数，这个值反映了线程池在高峰时段所拥有的线程数目
         */
        private Integer largestPoolSize;

        /**
         * 当前线程池中线程的总数，包括空闲和正在工作的线程
         */
        private Integer poolSize;

        /**
         * 核心线程数
         */
        private Integer corePoolSize;

        /**
         * 最大线程数
         */
        private Integer maximumPoolSize;

        /**
         * 队列大小
         */
        private Integer queueSize;

        /**
         * 拒绝的任务数量
         */
        private Long rejectedTaskCount;

        /**
         * 拒绝策略名字
         */
        private String rejectedExecutionHandlerName;

        /**
         * 队列剩余容量
         */
        private Integer queueRemainingCapacity;

        /**
         * 队列类型
         */
        private String queueType;

        /**
         * 队列容量
         */
        private Long queueCapacity;

        /**
         * 是否允许核心线程超时
         */
        private Boolean allowCoreThreadTimeOut;

        /**
         * 空闲线程回收时间（秒）
         */
        private Long keepAliveTime;

        /**
         * 线程池利用率
         */
        private Double utilizationRate;

    }

}