package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDeviceSys;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * 网络设备系统表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "网络设备系统表现层对象")
public class MonitorNetworkDeviceSysVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "IP地址")
    private String ip;

    @Schema(description = "系统描述，包含设备的型号、操作系统版本等信息")
    private String sysDescr;

    @Schema(description = "系统自上次重启以来的运行时间（以百分之一秒为单位）")
    private String sysUpTime;

    @Schema(description = "系统管理员的联系信息")
    private String sysContact;

    @Schema(description = "系统的名称")
    private String sysName;

    @Schema(description = "系统的物理位置")
    private String sysLocation;

    @Schema(description = "系统提供的服务类型")
    private String sysServices;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "新增时间")
    private Date insertTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * <p>
     * MonitorNetworkDeviceSysVo转MonitorNetworkDeviceSys
     * </p>
     *
     * @return {@link MonitorNetworkDeviceSys}
     * @author 皮锋
     * @custom.date 2025/3/19 9:20
     */
    public MonitorNetworkDeviceSys convertTo() {
        MonitorNetworkDeviceSys monitorNetworkDeviceSys = MonitorNetworkDeviceSys.builder().build();
        BeanUtils.copyProperties(this, monitorNetworkDeviceSys);
        return monitorNetworkDeviceSys;
    }

    /**
     * <p>
     * MonitorNetworkDeviceSys转MonitorNetworkDeviceSysVo
     * </p>
     *
     * @param monitorNetworkDeviceSys {@link MonitorNetworkDeviceSys}
     * @return {@link MonitorNetworkDeviceSysVo}
     * @author 皮锋
     * @custom.date 2025/3/19 9:22
     */
    public MonitorNetworkDeviceSysVo convertFor(MonitorNetworkDeviceSys monitorNetworkDeviceSys) {
        if (null != monitorNetworkDeviceSys) {
            BeanUtils.copyProperties(monitorNetworkDeviceSys, this);
        }
        return this;
    }

}
