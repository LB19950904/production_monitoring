package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDeviceIf;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceIfVo;

import java.util.List;

/**
 * <p>
 * 网络设备接口服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
public interface IMonitorNetworkDeviceIfService extends IService<MonitorNetworkDeviceIf> {

    /**
     * <p>
     * 获取网络设备接口信息
     * </p>
     *
     * @param ip 网络设备IP地址
     * @return 网络设备接口表现层对象列表
     * @author 皮锋
     * @custom.date 2025-3-20 11:41
     */
    List<MonitorNetworkDeviceIfVo> getNetworkDeviceIfInfo(String ip);

}
