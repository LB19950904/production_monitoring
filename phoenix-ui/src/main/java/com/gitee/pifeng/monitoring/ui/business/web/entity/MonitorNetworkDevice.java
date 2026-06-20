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
 * 网络设备表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_NETWORK_DEVICE")
@Schema(description = "MonitorNetworkDevice对象")
public class MonitorNetworkDevice implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @Schema(description = "IP地址（来源）")
    @TableField("IP_SOURCE")
    private String ipSource;

    @Schema(description = "IP地址（目的地）")
    @TableField("IP_TARGET")
    private String ipTarget;

    @Schema(description = "通信协议")
    @TableField("CP_NAME")
    private String cpName;

    @Schema(description = "通信协议版本")
    @TableField("CP_VERSION")
    private String cpVersion;

    @Schema(description = "通信端口号")
    @TableField("CP_PORT")
    private Integer cpPort;

    @Schema(description = "社区字符串")
    @TableField("CP_COMMUNITY")
    private String cpCommunity;

    @Schema(description = "MIB OID")
    @TableField("OID")
    private String oid;

    @Schema(description = "网络设备类型")
    @TableField("NETWORK_DEVICE_TYPE")
    private String networkDeviceType;

    @Schema(description = "网络设备摘要")
    @TableField("NETWORK_DEVICE_SUMMARY")
    private String networkDeviceSummary;

    @Schema(description = "总端口数")
    @TableField("IF_NUMBER")
    private Integer ifNumber;

    @Schema(description = "占用端口数")
    @TableField("IF_USED_COUNT")
    private Integer ifUsedCount;

    @Schema(description = "总接收速率（bps）")
    @TableField("TOTAL_IN_SPEED")
    private Long totalInSpeed;

    @Schema(description = "总发送速率（bps）")
    @TableField("TOTAL_OUT_SPEED")
    private Long totalOutSpeed;

    @Schema(description = "网络设备状态（0：离线，1：在线）")
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

    @Schema(description = "新增方式（1：手动新增；2.自动发现）")
    @TableField("INSERT_TYPE")
    private String insertType;

    @Schema(description = "监控环境")
    @TableField("MONITOR_ENV")
    private String monitorEnv;

    @Schema(description = "监控分组")
    @TableField("MONITOR_GROUP")
    private String monitorGroup;

    @Schema(description = "新增时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
