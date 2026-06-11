package com.gitee.pifeng.monitoring.ui.business.web.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * docker表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-04
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_DOCKER")
@Schema(description = "MonitorDocker对象")
public class MonitorDocker implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @Schema(description = "服务器IP")
    @TableField("SERVER_IP")
    private String serverIp;

    @Schema(description = "架构")
    @TableField("ARCHITECTURE")
    private String architecture;

    @Schema(description = "容器数量")
    @TableField("CONTAINERS")
    private Integer containers;

    @Schema(description = "停止的容器数量")
    @TableField("CONTAINERS_STOPPED")
    private Integer containersStopped;

    @Schema(description = "暂停的容器数量")
    @TableField("CONTAINERS_PAUSED")
    private Integer containersPaused;

    @Schema(description = "运行中的容器数量")
    @TableField("CONTAINERS_RUNNING")
    private Integer containersRunning;

    @Schema(description = "是否debug模式（0：否，1：是）")
    @TableField("IS_DEBUG")
    private String isDebug;

    @Schema(description = "docker根目录")
    @TableField("DOCKER_ROOT_DIR")
    private String dockerRootDir;

    @Schema(description = "镜像数量")
    @TableField("IMAGES")
    private Integer images;

    @Schema(description = "内核版本")
    @TableField("KERNEL_VERSION")
    private String kernelVersion;

    @Schema(description = "是否限制内存大小（0：否，1：是）")
    @TableField("IS_MEMORY_LIMIT")
    private String isMemoryLimit;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "内存总大小")
    @TableField("MEM_TOTAL")
    private Long memTotal;

    @Schema(description = "服务版本")
    @TableField("SERVER_VERSION")
    private String serverVersion;

    @Schema(description = "CPU核数")
    @TableField("CPU_NUM")
    private Integer cpuNum;

    @Schema(description = "监听的事件数量")
    @TableField("EVENTS_LISTENER_NUM")
    private Integer eventsListenerNum;

    @Schema(description = "完整信息（Json字符串）")
    @TableField("RAW_VALUES")
    private String rawValues;

    @Schema(description = "docker服务状态（0：离线，1：在线）")
    @TableField("IS_ONLINE")
    private String isOnline;

    @Schema(description = "是否开启监控（0：不开启监控；1：开启监控）")
    @TableField("IS_ENABLE_MONITOR")
    private String isEnableMonitor;

    @Schema(description = "是否开启告警（0：不开启告警；1：开启告警）")
    @TableField("IS_ENABLE_ALARM")
    private String isEnableAlarm;

    @Schema(description = "离线次数")
    @TableField("OFFLINE_COUNT")
    private Integer offlineCount;

    @Schema(description = "连接频率")
    @TableField("CONN_FREQUENCY")
    private Integer connFrequency;

    @Schema(description = "监控环境")
    @TableField(value = "MONITOR_ENV", updateStrategy = FieldStrategy.IGNORED)
    private String monitorEnv;

    @Schema(description = "监控分组")
    @TableField(value = "MONITOR_GROUP", updateStrategy = FieldStrategy.IGNORED)
    private String monitorGroup;

    @Schema(description = "docker摘要")
    @TableField("DOCKER_SUMMARY")
    private String dockerSummary;

    @Schema(description = "监控代理通信客户端ID")
    @TableField("AGENT_COMM_CLIENT_ID")
    private String agentCommClientId;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

    @Schema(description = "新增时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

}
