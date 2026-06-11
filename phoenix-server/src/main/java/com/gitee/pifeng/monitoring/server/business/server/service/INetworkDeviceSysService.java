package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.SysDomain;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorNetworkDeviceSys;

/**
 * <p>
 * 网络设备系统服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-07
 */
public interface INetworkDeviceSysService extends IService<MonitorNetworkDeviceSys> {

    /**
     * <p>
     * 把网络设备系统信息添加或更新到数据库
     * </p>
     *
     * @param ip        IP地址
     * @param sysDomain 系统信息
     * @author 皮锋
     * @custom.date 2025-3-7 17:04
     */
    void operateNetworkDeviceSys(String ip, SysDomain sysDomain);

}