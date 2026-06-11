package com.gitee.pifeng.monitoring.agent.business.client.service.impl;

import com.gitee.pifeng.monitoring.agent.business.client.service.INetworkDeviceService;
import com.gitee.pifeng.monitoring.agent.core.MethodExecuteHandler;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 网络设备信息服务实现
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/20 14:12
 */
@Deprecated
@Service
public class NetworkDeviceServiceImpl implements INetworkDeviceService {

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
    @Override
    public BaseResponsePackage dealNetworkDevicePackage(NetworkDevicePackage networkDevicePackage) {
        // 把网络设备信息包转发到服务端
        return MethodExecuteHandler.sendNetworkDevicePackage2Server(networkDevicePackage);
    }

}
