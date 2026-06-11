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
 * 网络设备系统表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_NETWORK_DEVICE_SYS")
@Schema(description = "MonitorNetworkDeviceSys对象")
public class MonitorNetworkDeviceSys implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @Schema(description = "IP地址")
    @TableField("IP")
    private String ip;

    @Schema(description = "系统描述，包含设备的型号、操作系统版本等信息")
    @TableField("SYS_DESCR")
    private String sysDescr;

    @Schema(description = "系统自上次重启以来的运行时间（以百分之一秒为单位）")
    @TableField("SYS_UP_TIME")
    private String sysUpTime;

    @Schema(description = "系统管理员的联系信息")
    @TableField("SYS_CONTACT")
    private String sysContact;

    @Schema(description = "系统的名称")
    @TableField("SYS_NAME")
    private String sysName;

    @Schema(description = "系统的物理位置")
    @TableField("SYS_LOCATION")
    private String sysLocation;

    @Schema(description = "系统提供的服务类型")
    @TableField("SYS_SERVICES")
    private String sysServices;

    @Schema(description = "新增时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
