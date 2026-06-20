package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDevice;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * 网络设备表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-3-18 8:46
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "网络设备表现层对象")
public class MonitorNetworkDeviceVo implements ISuperBean {

    @Schema(description = "主键ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;

    @Schema(description = "IP地址（来源）")
    private String ipSource;

    @Schema(description = "IP地址（目的地）")
    private String ipTarget;

    @Schema(description = "通信协议")
    private String cpName;

    @Schema(description = "通信协议版本")
    private String cpVersion;

    @Schema(description = "通信端口号")
    private Integer cpPort;

    @Schema(description = "社区字符串")
    private String cpCommunity;

    @Schema(description = "MIB OID")
    private String oid;

    @Schema(description = "网络设备类型")
    private String networkDeviceType;

    @Schema(description = "网络设备摘要")
    private String networkDeviceSummary;

    @Schema(description = "总端口数")
    private Integer ifNumber;

    @Schema(description = "占用端口数")
    private Integer ifUsedCount;

    @Schema(description = "总接收速率（bps）")
    private Long totalInSpeed;

    @Schema(description = "总发送速率（bps）")
    private Long totalOutSpeed;

    @Schema(description = "网络设备状态（0：离线，1：在线）")
    private String isOnline;

    @Schema(description = "是否开启监控（0：不开启监控；1：开启监控）")
    private String isEnableMonitor;

    @Schema(description = "是否开启告警（0：不开启告警；1：开启告警）")
    private String isEnableAlarm;

    @Schema(description = "离线次数")
    private Integer offlineCount;

    @Schema(description = "连接频率")
    private Integer connFrequency;

    @Schema(description = "新增方式（1：手动新增；2.自动发现）")
    private String insertType;

    @Schema(description = "监控环境")
    private String monitorEnv;

    @Schema(description = "监控分组")
    private String monitorGroup;

    @Schema(description = "新增时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date insertTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date updateTime;

    @Schema(description = "最后心跳时间")
    private String finalHeartbeat;

    /**
     * <p>
     * MonitorNetworkDeviceVo转MonitorNetworkDevice
     * </p>
     *
     * @return {@link MonitorNetworkDevice}
     * @author 皮锋
     * @custom.date 2025/3/18 9:20
     */
    public MonitorNetworkDevice convertTo() {
        MonitorNetworkDevice monitorNetworkDevice = MonitorNetworkDevice.builder().build();
        BeanUtils.copyProperties(this, monitorNetworkDevice);
        return monitorNetworkDevice;
    }

    /**
     * <p>
     * MonitorNetworkDevice转MonitorNetworkDeviceVo
     * </p>
     *
     * @param monitorNetworkDevice {@link MonitorNetworkDevice}
     * @return {@link MonitorNetworkDeviceVo}
     * @author 皮锋
     * @custom.date 2025/3/18 9:22
     */
    public MonitorNetworkDeviceVo convertFor(MonitorNetworkDevice monitorNetworkDevice) {
        if (null != monitorNetworkDevice) {
            BeanUtils.copyProperties(monitorNetworkDevice, this);
        }
        return this;
    }

}