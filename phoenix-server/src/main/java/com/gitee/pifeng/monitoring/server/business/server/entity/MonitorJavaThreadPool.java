package com.gitee.pifeng.monitoring.server.business.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * java线程池信息表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_JAVA_THREAD_POOL")
public class MonitorJavaThreadPool {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 应用实例ID
     */
    @TableField("INSTANCE_ID")
    private String instanceId;

    /**
     * 线程池名字
     */
    @TableField("NAME")
    private String name;

    /**
     * 活跃线程数
     */
    @TableField("ACTIVE_COUNT")
    private Integer activeCount;

    /**
     * 已完成的任务数
     */
    @TableField("COMPLETED_TASK_COUNT")
    private Long completedTaskCount;

    /**
     * 曾经接收过的总任务数
     */
    @TableField("TASK_COUNT")
    private Long taskCount;

    /**
     * 历史上最多同时存在的线程数
     */
    @TableField("LARGEST_POOL_SIZE")
    private Integer largestPoolSize;

    /**
     * 当前线程总数
     */
    @TableField("POOL_SIZE")
    private Integer poolSize;

    /**
     * 核心线程数
     */
    @TableField("CORE_POOL_SIZE")
    private Integer corePoolSize;

    /**
     * 最大线程数
     */
    @TableField("MAXIMUM_POOL_SIZE")
    private Integer maximumPoolSize;

    /**
     * 队列大小
     */
    @TableField("QUEUE_SIZE")
    private Integer queueSize;

    /**
     * 拒绝的任务数量
     */
    @TableField("REJECTED_TASK_COUNT")
    private Long rejectedTaskCount;

    /**
     * 拒绝策略名字
     */
    @TableField("REJECTED_EXECUTION_HANDLER_NAME")
    private String rejectedExecutionHandlerName;

    /**
     * 队列剩余容量
     */
    @TableField("QUEUE_REMAINING_CAPACITY")
    private Integer queueRemainingCapacity;

    /**
     * 队列类型
     */
    @TableField("QUEUE_TYPE")
    private String queueType;

    /**
     * 队列容量
     */
    @TableField("QUEUE_CAPACITY")
    private Long queueCapacity;

    /**
     * 是否允许核心线程超时
     */
    @TableField("ALLOW_CORE_THREAD_TIME_OUT")
    private Boolean allowCoreThreadTimeOut;

    /**
     * 空闲线程回收时间（秒）
     */
    @TableField("KEEP_ALIVE_TIME")
    private Long keepAliveTime;

    /**
     * 线程池利用率
     */
    @TableField("UTILIZATION_RATE")
    private Double utilizationRate;

    /**
     * 新增时间
     */
    @TableField("INSERT_TIME")
    private Date insertTime;

    /**
     * 更新时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
