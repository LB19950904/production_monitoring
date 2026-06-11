package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDeviceIf;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * 网络设备接口表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-3-20 8:29
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "网络设备接口表现层对象")
public class MonitorNetworkDeviceIfVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "IP地址")
    private String ip;

    @Schema(description = "网络接口的索引号")
    private Integer ifIndex;

    @Schema(description = "网络接口的描述")
    private String ifDescr;

    @Schema(description = "网络接口的类型")
    private String ifType;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口的最大传输单元（MTU）")
    private Long ifMtu;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口的速率（以比特/秒为单位）")
    private Long ifSpeed;

    @Schema(description = "网络接口的物理地址（MAC地址）")
    private String ifPhysAddress;

    @Schema(description = "网络接口的管理状态")
    private String ifAdminStatus;

    @Schema(description = "网络接口的操作状态")
    private String ifOperStatus;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口接收到的字节数")
    private Long ifInOctets;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口发送的字节数")
    private Long ifOutOctets;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口实时接收速率（以比特/秒为单位）")
    private Long ifInRealTimeSpeed;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络接口实时发送速率（以比特/秒为单位）")
    private Long ifOutRealTimeSpeed;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "新增时间")
    private Date insertTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * <p>
     * MonitorNetworkDeviceIfVo转MonitorNetworkDeviceIf
     * </p>
     *
     * @return {@link MonitorNetworkDeviceIf}
     * @author 皮锋
     * @custom.date 2025/3/19 9:20
     */
    public MonitorNetworkDeviceIf convertTo() {
        MonitorNetworkDeviceIf monitorNetworkDeviceIf = MonitorNetworkDeviceIf.builder().build();
        BeanUtils.copyProperties(this, monitorNetworkDeviceIf);
        return monitorNetworkDeviceIf;
    }

    /**
     * <p>
     * MonitorNetworkDeviceIf转MonitorNetworkDeviceIfVo
     * </p>
     *
     * @param monitorNetworkDeviceIf {@link MonitorNetworkDeviceIf}
     * @return {@link MonitorNetworkDeviceIfVo}
     * @author 皮锋
     * @custom.date 2025/3/19 9:22
     */
    public MonitorNetworkDeviceIfVo convertFor(MonitorNetworkDeviceIf monitorNetworkDeviceIf) {
        if (null != monitorNetworkDeviceIf) {
            BeanUtils.copyProperties(monitorNetworkDeviceIf, this);
        }
        return this;
    }

}