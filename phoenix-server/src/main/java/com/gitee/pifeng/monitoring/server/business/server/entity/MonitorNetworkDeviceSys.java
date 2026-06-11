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
 * 网络设备系统表
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
@TableName("MONITOR_NETWORK_DEVICE_SYS")
public class MonitorNetworkDeviceSys {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * IP地址
     */
    @TableField("IP")
    private String ip;

    /**
     * 系统描述，包含设备的型号、操作系统版本等信息
     */
    @TableField("SYS_DESCR")
    private String sysDescr;

    /**
     * 系统自上次重启以来的运行时间（以百分之一秒为单位）
     */
    @TableField("SYS_UP_TIME")
    private String sysUpTime;

    /**
     * 系统管理员的联系信息
     */
    @TableField("SYS_CONTACT")
    private String sysContact;

    /**
     * 系统的名称
     */
    @TableField("SYS_NAME")
    private String sysName;

    /**
     * 系统的物理位置
     */
    @TableField("SYS_LOCATION")
    private String sysLocation;

    /**
     * 系统提供的服务类型
     */
    @TableField("SYS_SERVICES")
    private String sysServices;

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