package com.gitee.pifeng.monitoring.agent.business.client.service;

import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;

/**
 * <p>
 * 网络设备信息服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/20 14:10
 */
@Deprecated
public interface INetworkDeviceService {

    /**
     * <p>
     * 处理网络设备信息包
     * </p>
     *
     * @param networkDevicePackage 网络设备信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2024/11/20 14:11
     */
    BaseResponsePackage dealNetworkDevicePackage(NetworkDevicePackage networkDevicePackage);

}
