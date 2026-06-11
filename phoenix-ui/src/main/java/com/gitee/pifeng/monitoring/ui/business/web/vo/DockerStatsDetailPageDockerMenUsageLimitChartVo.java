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
 * 获取docker当前使用的内存和最大可以使用的内存图表信息表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020/10/19 14:21
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "获取docker当前使用的内存和最大可以使用的内存图表信息表现层对象")
public class DockerStatsDetailPageDockerMenUsageLimitChartVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "当前使用的内存（单位：byte）")
    private Long menUsage;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "最大可以使用的内存（单位：byte）")
    private Long menLimit;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "新增时间")
    private Date insertTime;

}
