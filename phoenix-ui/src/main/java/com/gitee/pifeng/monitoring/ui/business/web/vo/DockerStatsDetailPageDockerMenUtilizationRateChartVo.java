package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * docker资源统计详情页面内存使用率图表信息表现层对象
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
@Schema(description = "docker资源统计详情页面内存使用率图表信息表现层对象")
public class DockerStatsDetailPageDockerMenUtilizationRateChartVo implements ISuperBean {

    @Schema(description = "内存使用率")
    private Double menUtilizationRate;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "新增时间")
    private Date insertTime;

}
