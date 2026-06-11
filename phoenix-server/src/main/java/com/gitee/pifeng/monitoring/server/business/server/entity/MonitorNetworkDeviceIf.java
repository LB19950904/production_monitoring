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
 * 网络设备接口表
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
@TableName("MONITOR_NETWORK_DEVICE_IF")
public class MonitorNetworkDeviceIf {

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
     * 网络接口的索引号
     */
    @TableField("IF_INDEX")
    private Integer ifIndex;

    /**
     * 网络接口的描述
     */
    @TableField("IF_DESCR")
    private String ifDescr;

    /**
     * 网络接口的类型
     */
    @TableField("IF_TYPE")
    private String ifType;

    /**
     * 网络接口的最大传输单元（MTU）
     */
    @TableField("IF_MTU")
    private Long ifMtu;

    /**
     * 网络接口的速率（以比特/秒为单位）
     */
    @TableField("IF_SPEED")
    private Long ifSpeed;

    /**
     * 网络接口的物理地址（MAC地址）
     */
    @TableField("IF_PHYS_ADDRESS")
    private String ifPhysAddress;

    /**
     * 网络接口的管理状态（1=up,2=down,3=testing）
     */
    @TableField("IF_ADMIN_STATUS")
    private String ifAdminStatus;

    /**
     * 网络接口的操作状态（1=up,2=down,3=testing,4=unknown,5=dormant,6=notPresent,7=lowerLayerDown）
     */
    @TableField("IF_OPER_STATUS")
    private String ifOperStatus;

    /**
     * 网络接口接收到的字节数
     */
    @TableField("IF_IN_OCTETS")
    private Long ifInOctets;

    /**
     * 网络接口发送的字节数
     */
    @TableField("IF_OUT_OCTETS")
    private Long ifOutOctets;

    /**
     * 网络接口实时接收速率（以比特/秒为单位）
     */
    @TableField("IF_IN_REAL_TIME_SPEED")
    private Long ifInRealTimeSpeed;

    /**
     * 网络接口实时发送速率（以比特/秒为单位）
     */
    @TableField("IF_OUT_REAL_TIME_SPEED")
    private Long ifOutRealTimeSpeed;
    
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