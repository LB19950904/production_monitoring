package com.gitee.pifeng.monitoring.common.property.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 网络设备信息属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/18 8:39
 */
@Data
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class MonitoringNetworkDeviceInfoProperties {

    /**
     * 是否采集网络设备信息(需先通过SNMP协议发现网络设备，再采集网络设备信息)
     */
    private Boolean enable;

    /**
     * 发送网络设备信息的频率
     */
    private Long rate;

    /**
     * 采集网络设备信息snmp协议连接社区字符串列表
     */
    private List<String> snmpConnectionCommunities;

}
