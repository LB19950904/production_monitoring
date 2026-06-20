package com.gitee.pifeng.monitoring.server.business.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 网络设备表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_NETWORK_DEVICE")
public class MonitorNetworkDevice {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * IP地址（来源）
     */
    @TableField("IP_SOURCE")
    private String ipSource;

    /**
     * IP地址（目的地）
     */
    @TableField("IP_TARGET")
    private String ipTarget;

    /**
     * 通信协议
     */
    @TableField("CP_NAME")
    private String cpName;

    /**
     * 通信协议版本
     */
    @TableField("CP_VERSION")
    private String cpVersion;

    /**
     * 通信端口号
     */
    @TableField("CP_PORT")
    private Integer cpPort;

    /**
     * 社区字符串
     */
    @TableField("CP_COMMUNITY")
    private String cpCommunity;

    /**
     * MIB OID
     */
    @TableField("OID")
    private String oid;

    /**
     * 网络设备类型
     */
    @TableField("NETWORK_DEVICE_TYPE")
    private String networkDeviceType;

    /**
     * 网络设备摘要
     */
    @TableField("NETWORK_DEVICE_SUMMARY")
    private String networkDeviceSummary;

    /**
     * 总端口数
     */
    @TableField("IF_NUMBER")
    private Integer ifNumber;

    /**
     * 占用端口数
     */
    @TableField("IF_USED_COUNT")
    private Integer ifUsedCount;

    /**
     * 总接收速率（bps）
     */
    @TableField("TOTAL_IN_SPEED")
    private Long totalInSpeed;

    /**
     * 总发送速率（bps）
     */
    @TableField("TOTAL_OUT_SPEED")
    private Long totalOutSpeed;

    /**
     * 网络设备状态（0：离线，1：在线）
     */
    @TableField("IS_ONLINE")
    private String isOnline;

    /**
     * 是否开启监控（0：不开启监控；1：开启监控）
     */
    @TableField("IS_ENABLE_MONITOR")
    private String isEnableMonitor;

    /**
     * 是否开启告警（0：不开启告警；1：开启告警）
     */
    @TableField("IS_ENABLE_ALARM")
    private String isEnableAlarm;

    /**
     * 离线次数
     */
    @TableField("OFFLINE_COUNT")
    private Integer offlineCount;

    /**
     * 连接频率
     */
    @TableField("CONN_FREQUENCY")
    private Integer connFrequency;

    /**
     * 监控环境
     */
    @TableField("MONITOR_ENV")
    private String monitorEnv;

    /**
     * 新增方式（1：手动新增；2.自动发现）
     */
    @TableField("INSERT_TYPE")
    private String insertType;

    /**
     * 监控分组
     */
    @TableField("MONITOR_GROUP")
    private String monitorGroup;

    /**
     * 新增时间
     */
    @TableField("INSERT_TIME")
    private Date insertTime;

    /**
     * 更新时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;

}