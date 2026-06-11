package com.gitee.pifeng.monitoring.agent.business.server.service.impl;

import com.gitee.pifeng.monitoring.agent.business.server.service.IHttpService;
import com.gitee.pifeng.monitoring.agent.business.server.service.INetworkDeviceService;
import com.gitee.pifeng.monitoring.agent.constant.UrlConstants;
import com.gitee.pifeng.monitoring.agent.core.AgentPackageConstructor;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 跟服务端相关的网络设备信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/22 8:56
 */
@Deprecated
@Service
public class NetworkDeviceServiceImpl implements INetworkDeviceService {

    /**
     * 包构造器接口
     */
    @Autowired
    private AgentPackageConstructor agentPackageConstructor;

    /**
     * 跟服务端相关的HTTP服务接口
     */
    @Autowired
    private IHttpService httpService;

    /**
     * <p>
     * 给服务端发网络设备信息包
     * </p>
     *
     * @param networkDevicePackage 网络设备信息包
     * @return {@link BaseResponsePackage}
     * @throws Exception 所有异常
     * @author 皮锋
     * @custom.date 2024/11/22 8:54
     */
    @Override
    public BaseResponsePackage sendNetworkDevicePackage(NetworkDevicePackage networkDevicePackage) throws Exception {
        // 添加链路信息
        networkDevicePackage.setChain(this.agentPackageConstructor.getChain(networkDevicePackage));
        BaseResponsePackage baseResponsePackage = this.httpService.sendHttpPost(networkDevicePackage.toJsonString(), UrlConstants.NETWORK_DEVICE_URL);
        // 添加链路信息
        baseResponsePackage.setChain(this.agentPackageConstructor.getChain(baseResponsePackage));
        return baseResponsePackage;
    }

}
