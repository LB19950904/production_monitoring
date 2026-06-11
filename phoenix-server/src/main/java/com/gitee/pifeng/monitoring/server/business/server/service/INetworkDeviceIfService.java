package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.IfDomain;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorNetworkDeviceIf;

/**
 * <p>
 * 网络设备接口服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-07
 */
public interface INetworkDeviceIfService extends IService<MonitorNetworkDeviceIf> {

    /**
     * <p>
     * 把网络设备接口信息添加或更新到数据库
     * </p>
     *
     * @param ip       IP地址
     * @param ifDomain 网络接口信息
     * @author 皮锋
     * @custom.date 2025-3-7 17:05
     */
    void operateNetworkDeviceIf(String ip, IfDomain ifDomain);

}