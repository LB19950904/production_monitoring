package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorNetworkDevice;

/**
 * <p>
 * 网络设备服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-07
 */
public interface INetworkDeviceService extends IService<MonitorNetworkDevice> {

    /**
     * <p>
     * 处理网络设备信息包
     * </p>
     *
     * @param networkDevicePackage 网络设备信息包
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2025/3/7 14:48
     */
    Result dealNetworkDevicePackage(NetworkDevicePackage networkDevicePackage);

    /**
     * <p>
     * 把网络设备信息添加或更新到数据库
     * </p>
     *
     * @param ip                   IP地址
     * @param networkDevicePackage 网络设备信息包
     * @return 是否需要操作
     * @author 皮锋
     * @custom.date 2025-3-7 16:52
     */
    boolean operateNetworkDevice(String ip, NetworkDevicePackage networkDevicePackage);

    /**
     * <p>
     * 测试网络设备连通性
     * </p>
     *
     * @param ipTarget    IP地址（目的地）
     * @param cpPort      通信端口号
     * @param cpName      通信协议
     * @param cpCommunity 社区字符串
     * @param oid         MIB OID
     * @param cpVersion   通信协议版本
     * @return true 或者 false
     * @author 皮锋
     * @custom.date 2025-4-11 13:03
     */
    Boolean testMonitorNetworkDevice(String ipTarget, Integer cpPort, String cpName, String cpCommunity, String oid, String cpVersion);

}
