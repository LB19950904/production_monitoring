package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorNetworkDeviceSysDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDeviceSys;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorNetworkDeviceSysService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceSysVo;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 网络设备系统服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
@Service
public class MonitorNetworkDeviceSysServiceImpl extends ServiceImpl<IMonitorNetworkDeviceSysDao, MonitorNetworkDeviceSys> implements IMonitorNetworkDeviceSysService {

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
    @Override
    public MonitorNetworkDeviceSysVo getNetworkDeviceSysInfo(String ip) {
        LambdaQueryWrapper<MonitorNetworkDeviceSys> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MonitorNetworkDeviceSys::getIp, ip);
        List<MonitorNetworkDeviceSys> monitorNetworkDeviceSysList = this.baseMapper.selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(monitorNetworkDeviceSysList)) {
            MonitorNetworkDeviceSys monitorNetworkDeviceSys = monitorNetworkDeviceSysList.get(0);
            return MonitorNetworkDeviceSysVo.builder().build().convertFor(monitorNetworkDeviceSys);
        }
        return MonitorNetworkDeviceSysVo.builder().build();
    }

}
