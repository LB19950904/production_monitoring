package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerStats;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * docker资源统计信息表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/8/21 20:37
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "docker资源统计信息表现层对象")
public class MonitorDockerStatsVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "服务器IP")
    private String serverIp;

    @Schema(description = "容器ID")
    private String containerId;

    @Schema(description = "容器名字")
    private String containerName;

    @Schema(description = "CPU的使用情况")
    private String cpuUtilizationRate;

    @Schema(description = "当前使用的内存和最大可以使用的内存")
    private String menUsageLimit;

    @Schema(description = "内存使用情况")
    private String menUtilizationRate;

    @Schema(description = "网络 I/O 数据")
    private String netIo;

    @Schema(description = "磁盘 I/O 数据")
    private String blockIo;

    @Schema(description = "PID号")
    private String pids;

    @Schema(description = "新增时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date insertTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date updateTime;

    @Schema(description = "监控环境")
    private String monitorEnv;

    @Schema(description = "监控分组")
    private String monitorGroup;

    @Schema(description = "docker摘要")
    private String dockerSummary;

    @Schema(description = "docker服务状态（0：离线，1：在线）")
    private String isOnline;

    /**
     * <p>
     * MonitorDockerStatsVo转MonitorDockerStats
     * </p>
     *
     * @return {@link MonitorDockerStats}
     * @author 皮锋
     * @custom.date 2020/9/3 9:20
     */
    public MonitorDockerStats convertTo() {
        MonitorDockerStats monitorDockerStats = MonitorDockerStats.builder().build();
        BeanUtils.copyProperties(this, monitorDockerStats);
        return monitorDockerStats;
    }

    /**
     * <p>
     * MonitorDockerStats转MonitorDockerStatsVo
     * </p>
     *
     * @param monitorDockerStats {@link MonitorDockerStats}
     * @return {@link MonitorDockerStatsVo}
     * @author 皮锋
     * @custom.date 2020/9/3 9:22
     */
    public MonitorDockerStatsVo convertFor(MonitorDockerStats monitorDockerStats) {
        if (null != monitorDockerStats) {
            BeanUtils.copyProperties(monitorDockerStats, this);
        }
        return this;
    }

}
