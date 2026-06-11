package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 应用实例详情页面java线程池图表信息表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/1/23 16:09
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "应用实例详情页面java线程池图表信息表现层对象")
public class InstanceDetailPageJavaThreadPoolChartVo implements ISuperBean {

    @Schema(description = "活跃线程数")
    private Integer activeCount;

    @Schema(description = "已完成的任务数")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long completedTaskCount;

    @Schema(description = "队列大小")
    private Integer queueSize;

    @Schema(description = "拒绝的任务数量")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long rejectedTaskCount;

    @Schema(description = "新增时间")
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+5")
    private Date insertTime;

}