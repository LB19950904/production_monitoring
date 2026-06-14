package com.gitee.pifeng.monitoring.ui.business.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 网络设备接口表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_NETWORK_DEVICE_IF")
@Schema(description = "MonitorNetworkDeviceIf对象")
public class MonitorNetworkDeviceIf implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @Schema(description = "IP地址")
    @TableField("IP")
    private String ip;

    @Schema(description = "网络接口的索引号")
    @TableField("IF_INDEX")
    private Integer ifIndex;

    @Schema(description = "网络接口的描述")
    @TableField("IF_DESCR")
    private String ifDescr;

    @Schema(description = "网络接口的类型")
    @TableField("IF_TYPE")
    private String ifType;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口的最大传输单元（MTU）")
    @TableField("IF_MTU")
    private Long ifMtu;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口的速率（以比特/秒为单位）")
    @TableField("IF_SPEED")
    private Long ifSpeed;

    @Schema(description = "网络接口的物理地址（MAC地址）")
    @TableField("IF_PHYS_ADDRESS")
    private String ifPhysAddress;

    @Schema(description = "网络接口的管理状态")
    @TableField("IF_ADMIN_STATUS")
    private String ifAdminStatus;

    @Schema(description = "网络接口的操作状态")
    @TableField("IF_OPER_STATUS")
    private String ifOperStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口接收到的字节数")
    @TableField("IF_IN_OCTETS")
    private Long ifInOctets;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口发送的字节数")
    @TableField("IF_OUT_OCTETS")
    private Long ifOutOctets;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口实时接收速率（以比特/秒为单位）")
    @TableField("IF_IN_REAL_TIME_SPEED")
    private Long ifInRealTimeSpeed;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口实时发送速率（以比特/秒为单位）")
    @TableField("IF_OUT_REAL_TIME_SPEED")
    private Long ifOutRealTimeSpeed;

    @Schema(description = "新增时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
