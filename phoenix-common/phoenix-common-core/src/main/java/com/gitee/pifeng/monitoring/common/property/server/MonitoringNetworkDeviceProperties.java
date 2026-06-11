package com.gitee.pifeng.monitoring.common.property.server;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import lombok.*;

/**
 * <p>
 * 网络设备配置属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-3-25 8:16
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MonitoringNetworkDeviceProperties implements ISuperBean {

    /**
     * 是否监控网络设备
     */
    private boolean enable;

    /**
     * 网络设备状态配置属性
     */
    private MonitoringNetworkDeviceStatusProperties networkDeviceStatusProperties;

}