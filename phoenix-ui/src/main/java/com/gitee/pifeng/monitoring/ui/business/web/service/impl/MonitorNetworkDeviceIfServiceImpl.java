package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorNetworkDeviceIfDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDeviceIf;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorNetworkDeviceIfService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceIfVo;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 网络设备接口服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
@Service
public class MonitorNetworkDeviceIfServiceImpl extends ServiceImpl<IMonitorNetworkDeviceIfDao, MonitorNetworkDeviceIf> implements IMonitorNetworkDeviceIfService {

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
    @Override
    public List<MonitorNetworkDeviceIfVo> getNetworkDeviceIfInfo(String ip) {
        List<MonitorNetworkDeviceIfVo> result = Lists.newArrayList();
        LambdaQueryWrapper<MonitorNetworkDeviceIf> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorNetworkDeviceIf::getIp, ip);
        lambdaQueryWrapper.orderByAsc(MonitorNetworkDeviceIf::getIfIndex);
        List<MonitorNetworkDeviceIf> monitorNetworkDeviceIfs = this.baseMapper.selectList(lambdaQueryWrapper);
        for (MonitorNetworkDeviceIf monitorNetworkDeviceIf : monitorNetworkDeviceIfs) {
            MonitorNetworkDeviceIfVo monitorNetworkDeviceIfVo = MonitorNetworkDeviceIfVo.builder().build().convertFor(monitorNetworkDeviceIf);
            result.add(monitorNetworkDeviceIfVo);
        }
        return result;
    }

}
