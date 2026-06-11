package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.IfDomain;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorNetworkDeviceIfDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorNetworkDeviceIf;
import com.gitee.pifeng.monitoring.server.business.server.service.INetworkDeviceIfService;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 网络设备接口服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-07
 */
@Service
public class NetworkDeviceIfServiceImpl extends ServiceImpl<IMonitorNetworkDeviceIfDao, MonitorNetworkDeviceIf> implements INetworkDeviceIfService {

    /**
     * <p>
     * 把网络设备接口信息添加或更新到数据库
     * </p>
     * 此处不加事务，不加事务能提高并发性能，并且对数据的一致性要求也没那么高
     *
     * @param ip       IP地址
     * @param ifDomain 网络接口信息
     * @author 皮锋
     * @custom.date 2025-3-7 17:05
     */
    @Retryable
    @Override
    public void operateNetworkDeviceIf(String ip, IfDomain ifDomain) {
        if (ifDomain != null) {
            // 当前时间
            Date currentTime = new Date();
            List<IfDomain.IfInfoDomain> ifList = ifDomain.getIfList();
            // 要添加的网络设备接口信息集合
            List<MonitorNetworkDeviceIf> saveMonitorNetworkDeviceIfs = Lists.newArrayList();
            for (IfDomain.IfInfoDomain ifInfoDomain : ifList) {
                // 查询数据库中有没有此IP和此网络设备接口索引号的网络设备接口信息
                LambdaQueryWrapper<MonitorNetworkDeviceIf> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(MonitorNetworkDeviceIf::getIp, ip);
                lambdaQueryWrapper.eq(MonitorNetworkDeviceIf::getIfIndex, ifInfoDomain.getIfIndex());
                int selectCountDb = this.count(lambdaQueryWrapper);
                // 封装对象
                MonitorNetworkDeviceIf monitorNetworkDeviceIf = new MonitorNetworkDeviceIf();
                monitorNetworkDeviceIf.setIp(ip);
                monitorNetworkDeviceIf.setIfIndex(ifInfoDomain.getIfIndex());
                monitorNetworkDeviceIf.setIfDescr(ifInfoDomain.getIfDescr());
                monitorNetworkDeviceIf.setIfType(ifInfoDomain.getIfType());
                monitorNetworkDeviceIf.setIfMtu(ifInfoDomain.getIfMtu());
                monitorNetworkDeviceIf.setIfSpeed(ifInfoDomain.getIfSpeed());
                monitorNetworkDeviceIf.setIfPhysAddress(ifInfoDomain.getIfPhysAddress());
                monitorNetworkDeviceIf.setIfAdminStatus(ifInfoDomain.getIfAdminStatus());
                monitorNetworkDeviceIf.setIfOperStatus(ifInfoDomain.getIfOperStatus());
                monitorNetworkDeviceIf.setIfInOctets(ifInfoDomain.getIfInOctets());
                monitorNetworkDeviceIf.setIfOutOctets(ifInfoDomain.getIfOutOctets());
                monitorNetworkDeviceIf.setIfInRealTimeSpeed(ifInfoDomain.getIfInRealTimeSpeed());
                monitorNetworkDeviceIf.setIfOutRealTimeSpeed(ifInfoDomain.getIfOutRealTimeSpeed());
                // 没有
                if (selectCountDb == 0) {
                    monitorNetworkDeviceIf.setInsertTime(currentTime);
                    saveMonitorNetworkDeviceIfs.add(monitorNetworkDeviceIf);
                }
                // 有
                else {
                    monitorNetworkDeviceIf.setUpdateTime(currentTime);
                    LambdaUpdateWrapper<MonitorNetworkDeviceIf> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    lambdaUpdateWrapper.eq(MonitorNetworkDeviceIf::getIp, ip);
                    lambdaUpdateWrapper.eq(MonitorNetworkDeviceIf::getIfIndex, ifInfoDomain.getIfIndex());
                    this.update(monitorNetworkDeviceIf, lambdaUpdateWrapper);
                }
            }
            // 有要新增的网络设备接口
            if (CollectionUtils.isNotEmpty(saveMonitorNetworkDeviceIfs)) {
                ((INetworkDeviceIfService) AopContext.currentProxy()).saveBatch(saveMonitorNetworkDeviceIfs);
            }
        }
    }

}