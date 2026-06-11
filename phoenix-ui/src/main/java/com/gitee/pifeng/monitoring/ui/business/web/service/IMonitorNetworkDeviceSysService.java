package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDeviceSys;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceSysVo;

/**
 * <p>
 * 网络设备系统服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
public interface IMonitorNetworkDeviceSysService extends IService<MonitorNetworkDeviceSys> {

    /**
     * <p>
     * 获取网络设备系统信息
     * </p>
     *
     * @param ip 网络设备IP地址
     * @return 网络设备系统表现层对象
     * @author 皮锋
     * @custom.date 2025-3-19 17:20
     */
    MonitorNetworkDeviceSysVo getNetworkDeviceSysInfo(String ip);

}
