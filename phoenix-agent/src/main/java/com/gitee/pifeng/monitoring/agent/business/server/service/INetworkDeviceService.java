package com.gitee.pifeng.monitoring.agent.business.server.service;

import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetInf;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetMethod;

/**
 * <p>
 * 跟服务端相关的网络设备信息服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/22 8:49
 */
@Deprecated
@TargetInf
public interface INetworkDeviceService {

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
    @TargetMethod
    BaseResponsePackage sendNetworkDevicePackage(NetworkDevicePackage networkDevicePackage) throws Exception;

}
