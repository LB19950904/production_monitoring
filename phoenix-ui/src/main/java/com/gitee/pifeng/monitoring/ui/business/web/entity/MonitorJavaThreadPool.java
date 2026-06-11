package com.gitee.pifeng.monitoring.ui.business.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
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
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_JAVA_THREAD_POOL")
@Schema(description = "MonitorJavaThreadPool对象")
public class MonitorJavaThreadPool implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @Schema(description = "应用实例ID")
    @TableField("INSTANCE_ID")
    private String instanceId;

    @Schema(description = "线程池名字")
    @TableField("NAME")
    private String name;

    @Schema(description = "活跃线程数")
    @TableField("ACTIVE_COUNT")
    private Integer activeCount;

    @Schema(description = "已完成的任务数")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("COMPLETED_TASK_COUNT")
    private Long completedTaskCount;

    @Schema(description = "曾经接收过的总任务数")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("TASK_COUNT")
    private Long taskCount;

    @Schema(description = "历史上最多同时存在的线程数")
    @TableField("LARGEST_POOL_SIZE")
    private Integer largestPoolSize;

    @Schema(description = "当前线程总数")
    @TableField("POOL_SIZE")
    private Integer poolSize;

    @Schema(description = "核心线程数")
    @TableField("CORE_POOL_SIZE")
    private Integer corePoolSize;

    @Schema(description = "最大线程数")
    @TableField("MAXIMUM_POOL_SIZE")
    private Integer maximumPoolSize;

    @Schema(description = "队列大小")
    @TableField("QUEUE_SIZE")
    private Integer queueSize;

    @Schema(description = "拒绝的任务数量")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableField("REJECTED_TASK_COUNT")
    private Long rejectedTaskCount;

    @Schema(description = "拒绝策略名字")
    @TableField("REJECTED_EXECUTION_HANDLER_NAME")
    private String rejectedExecutionHandlerName;

    @Schema(description = "队列剩余容量")
    @TableField("QUEUE_REMAINING_CAPACITY")
    private Integer queueRemainingCapacity;

    @Schema(description = "队列类型")
    @TableField("QUEUE_TYPE")
    private String queueType;

    @Schema(description = "队列容量")
    @TableField("QUEUE_CAPACITY")
    private Long queueCapacity;

    @Schema(description = "是否允许核心线程超时")
    @TableField("ALLOW_CORE_THREAD_TIME_OUT")
    private Boolean allowCoreThreadTimeOut;

    @Schema(description = "空闲线程回收时间（秒）")
    @TableField("KEEP_ALIVE_TIME")
    private Long keepAliveTime;

    @Schema(description = "线程池利用率")
    @TableField("UTILIZATION_RATE")
    private Double utilizationRate;

    @Schema(description = "新增时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
