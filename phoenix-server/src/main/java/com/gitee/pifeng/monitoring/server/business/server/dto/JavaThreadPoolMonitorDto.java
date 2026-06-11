package com.gitee.pifeng.monitoring.server.business.server.dto;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * java线程池监控传输层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-2-18 9:15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JavaThreadPoolMonitorDto implements ISuperBean {

    /**
     * 应用实例主键ID
     */
    private Long id;

    /**
     * 应用实例ID
     */
    private String instanceId;

    /**
     * 端点（client、agent、server）
     */
    private String endpoint;

    /**
     * 应用实例名
     */
    private String instanceName;

    /**
     * 应用实例描述
     */
    private String instanceDesc;

    /**
     * 应用实例摘要
     */
    private String instanceSummary;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 是否开启监控（0：不开启监控；1：开启监控）
     */
    private String isEnableMonitor;

    /**
     * 是否开启告警（0：不开启告警；1：开启告警）
     */
    private String isEnableAlarm;

    /**
     * 监控环境
     */
    private String monitorEnv;

    /**
     * 监控分组
     */
    private String monitorGroup;

    /**
     * 线程池名字
     */
    private String name;

    /**
     * 活跃线程数
     */
    private Integer activeCount;

    /**
     * 已完成的任务数
     */
    private Long completedTaskCount;

    /**
     * 曾经接收过的总任务数
     */
    private Long taskCount;

    /**
     * 历史上最多同时存在的线程数
     */
    private Integer largestPoolSize;

    /**
     * 当前线程总数
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