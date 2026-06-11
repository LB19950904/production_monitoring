package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDocker;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * docker信息表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/5 21:28
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "docker信息表现层对象")
public class MonitorDockerVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "服务器IP")
    private String serverIp;

    @Schema(description = "架构")
    private String architecture;

    @Schema(description = "容器数量")
    private Integer containers;

    @Schema(description = "停止的容器数量")
    private Integer containersStopped;

    @Schema(description = "暂停的容器数量")
    private Integer containersPaused;

    @Schema(description = "运行中的容器数量")
    private Integer containersRunning;

    @Schema(description = "是否debug模式（0：否，1：是）")
    private String isDebug;

    @Schema(description = "docker根目录")
    private String dockerRootDir;

    @Schema(description = "镜像数量")
    private Integer images;

    @Schema(description = "内核版本")
    private String kernelVersion;

    @Schema(description = "是否限制内存大小（0：否，1：是）")
    private String isMemoryLimit;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "内存总大小")
    private Long memTotal;

    @Schema(description = "可读的内存总大小")
    private String memTotalStr;

    @Schema(description = "服务版本")
    private String serverVersion;

    @Schema(description = "CPU核数")
    private Integer cpuNum;

    @Schema(description = "监听的事件数量")
    private Integer eventsListenerNum;

    @Schema(description = "完整信息（Json字符串）")
    private String rawValues;

    @Schema(description = "docker服务状态（0：离线，1：在线）")
    private String isOnline;

    @Schema(description = "是否开启监控（0：不开启监控；1：开启监控）")
    private String isEnableMonitor;

    @Schema(description = "是否开启告警（0：不开启告警；1：开启告警）")
    private String isEnableAlarm;

    @Schema(description = "离线次数")
    private Integer offlineCount;

    @Schema(description = "连接频率")
    private Integer connFrequency;

    @Schema(description = "监控环境")
    private String monitorEnv;

    @Schema(description = "监控分组")
    private String monitorGroup;

    @Schema(description = "docker摘要")
    private String dockerSummary;

    @Schema(description = "监控代理通信客户端ID")
    private String agentCommClientId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "新增时间")
    private Date insertTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "更新时间")
    private Date updateTime;

    @Schema(description = "最后心跳时间")
    private String finalHeartbeat;

    /**
     * <p>
     * MonitorDockerVo转MonitorDocker
     * </p>
     *
     * @return {@link MonitorDocker}
     * @author 皮锋
     * @custom.date 2020/9/3 9:20
     */
    public MonitorDocker convertTo() {
        MonitorDocker monitorDocker = MonitorDocker.builder().build();
        BeanUtils.copyProperties(this, monitorDocker);
        return monitorDocker;
    }

    /**
     * <p>
     * MonitorDocker转MonitorDockerVo
     * </p>
     *
     * @param monitorDocker {@link MonitorDocker}
     * @return {@link MonitorDockerVo}
     * @author 皮锋
     * @custom.date 2020/9/3 9:22
     */
    public MonitorDockerVo convertFor(MonitorDocker monitorDocker) {
        if (null != monitorDocker) {
            BeanUtils.copyProperties(monitorDocker, this);
        }
        return this;
    }
}
