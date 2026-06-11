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
 * docker资源统计详情页面网络图表信息表现层对象
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
@Schema(description = "docker资源统计详情页面网络图表信息表现层对象")
public class DockerStatsDetailPageDockerNetChartVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络 input 数据（单位：byte）")
    private Long netIn;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络 output 数据（单位：byte）")
    private Long netOut;

    @Schema(description = "网络 input 数据 速率，单位：B/s")
    private Double netInSpeed;

    @Schema(description = "网络 output 数据 速率，单位：B/s")
    private Double netOutSpeed;

    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "新增时间")
    private Date insertTime;

}
