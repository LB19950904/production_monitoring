package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorJavaThreadPool;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * java线程池信息表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "java线程池信息表现层对象")
public class MonitorJavaThreadPoolVo implements ISuperBean {

    @Schema(description = "主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "端点（客户端<client>、代理端<agent>、服务端<server>、UI端<ui>）")
    private String endpoint;

    @Schema(description = "应用实例ID")
    private String instanceId;

    @Schema(description = "线程池名字")
    private String name;

    @Schema(description = "活跃线程数")
    private Integer activeCount;

    @Schema(description = "已完成的任务数")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long completedTaskCount;

    @Schema(description = "曾经接收过的总任务数")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long taskCount;

    @Schema(description = "历史上最多同时存在的线程数")
    private Integer largestPoolSize;

    @Schema(description = "当前线程总数")
    private Integer poolSize;

    @Schema(description = "核心线程数")
    private Integer corePoolSize;

    @Schema(description = "最大线程数")
    private Integer maximumPoolSize;

    @Schema(description = "队列大小")
    private Integer queueSize;

    @Schema(description = "拒绝的任务数量")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long rejectedTaskCount;

    @Schema(description = "拒绝策略名字")
    private String rejectedExecutionHandlerName;

    @Schema(description = "队列剩余容量")
    private Integer queueRemainingCapacity;

    @Schema(description = "队列类型")
    private String queueType;

    @Schema(description = "队列容量")
    private Long queueCapacity;

    @Schema(description = "是否允许核心线程超时")
    private Boolean allowCoreThreadTimeOut;

    @Schema(description = "空闲线程回收时间（秒）")
    private Long keepAliveTime;

    @Schema(description = "线程池利用率")
    private String utilizationRate;

    @Schema(description = "新增时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date insertTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date updateTime;

    /**
     * <p>
     * MonitorJavaThreadVo转MonitorJavaThread
     * </p>
     *
     * @return {@link MonitorJavaThreadPool}
     * @author 皮锋
     * @custom.date 2025/1/27 7:40
     */
    public MonitorJavaThreadPool convertTo() {
        MonitorJavaThreadPool monitorJavaThreadPool = MonitorJavaThreadPool.builder().build();
        BeanUtils.copyProperties(this, monitorJavaThreadPool);
        return monitorJavaThreadPool;
    }

    /**
     * <p>
     * MonitorJavaThread转MonitorJavaThreadVo
     * </p>
     *
     * @param monitorJavaThreadPool {@link MonitorJavaThreadPool}
     * @return {@link MonitorJavaThreadPoolVo}
     * @author 皮锋
     * @custom.date 2025/1/27 7:42
     */
    public MonitorJavaThreadPoolVo convertFor(MonitorJavaThreadPool monitorJavaThreadPool) {
        if (null != monitorJavaThreadPool) {
            BeanUtils.copyProperties(monitorJavaThreadPool, this);
        }
        return this;
    }

}