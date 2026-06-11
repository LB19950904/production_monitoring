package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.SysDomain;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorNetworkDeviceSysDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorNetworkDeviceSys;
import com.gitee.pifeng.monitoring.server.business.server.service.INetworkDeviceSysService;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * 网络设备系统服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-07
 */
@Service
public class NetworkDeviceSysServiceImpl extends ServiceImpl<IMonitorNetworkDeviceSysDao, MonitorNetworkDeviceSys> implements INetworkDeviceSysService {

    /**
     * <p>
     * 把网络设备系统信息添加或更新到数据库
     * </p>
     * 此处不加事务，不加事务能提高并发性能，并且对数据的一致性要求也没那么高
     *
     * @param ip        IP地址
     * @param sysDomain 系统信息
     * @author 皮锋
     * @custom.date 2025-3-7 17:04
     */
    @Retryable
    @Override
    public void operateNetworkDeviceSys(String ip, SysDomain sysDomain) {
        if (sysDomain != null) {
            // 当前时间
            Date currentTime = new Date();
            LambdaQueryWrapper<MonitorNetworkDeviceSys> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(MonitorNetworkDeviceSys::getIp, ip);
            int selectCountDb = this.count(lambdaQueryWrapper);
            // 封装对象
            MonitorNetworkDeviceSys monitorNetworkDeviceSys = new MonitorNetworkDeviceSys();
            monitorNetworkDeviceSys.setIp(ip);
            monitorNetworkDeviceSys.setSysDescr(sysDomain.getSysDescr());
            monitorNetworkDeviceSys.setSysUpTime(sysDomain.getSysUpTime());
            monitorNetworkDeviceSys.setSysContact(sysDomain.getSysContact());
            monitorNetworkDeviceSys.setSysName(sysDomain.getSysName());
            monitorNetworkDeviceSys.setSysLocation(sysDomain.getSysLocation());
            monitorNetworkDeviceSys.setSysServices(sysDomain.getSysServices());
            // 没有
            if (selectCountDb == 0) {
                monitorNetworkDeviceSys.setInsertTime(currentTime);
                this.save(monitorNetworkDeviceSys);
            }
            // 有
            else {
                monitorNetworkDeviceSys.setUpdateTime(currentTime);
                LambdaUpdateWrapper<MonitorNetworkDeviceSys> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(MonitorNetworkDeviceSys::getIp, ip);
                this.update(monitorNetworkDeviceSys, lambdaUpdateWrapper);
            }
        }
    }

}