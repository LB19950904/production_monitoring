package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.CommProtocolTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.constant.ZeroOrOneConstants;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpProtocolVersionConstants;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.ConnectionDomain;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.IfDomain;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.SysDomain;
import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.OId;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.v2c.Connection;
import com.gitee.pifeng.monitoring.common.threadpool.MonitoredThreadPoolExecutor;
import com.gitee.pifeng.monitoring.common.util.server.NetUtils;
import com.gitee.pifeng.monitoring.common.util.snmp.v2c.NetworkDeviceUtils;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorNetworkDeviceDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorNetworkDevice;
import com.gitee.pifeng.monitoring.server.business.server.service.INetService;
import com.gitee.pifeng.monitoring.server.business.server.service.INetworkDeviceIfService;
import com.gitee.pifeng.monitoring.server.business.server.service.INetworkDeviceService;
import com.gitee.pifeng.monitoring.server.business.server.service.INetworkDeviceSysService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * 网络设备服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-07
 */
@Slf4j
@Service
public class NetworkDeviceServiceImpl extends ServiceImpl<IMonitorNetworkDeviceDao, MonitorNetworkDevice> implements INetworkDeviceService {

    /**
     * 网络信息服务接口
     */
    @Autowired
    private INetService netService;

    /**
     * 网络设备系统服务接口
     */
    @Autowired
    private INetworkDeviceSysService networkDeviceSysService;

    /**
     * 网络设备接口服务接口
     */
    @Autowired
    private INetworkDeviceIfService networkDeviceIfService;

    /**
     * 网络设备监控线程池
     */
    @Autowired
    @Qualifier("networkDeviceMonitorThreadPoolExecutor")
    private MonitoredThreadPoolExecutor networkDeviceMonitorThreadPoolExecutor;

    /**
     * <p>
     * 处理网络设备信息包
     * </p>
     * 此处不加事务，因为操作的表太多，数据太多，不加事务能提高并发性能，而且此处对数据的一致性要求并不是很高。
     *
     * @param networkDevicePackage 网络设备信息包
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2025/3/7 14:48
     */
    @Override
    public Result dealNetworkDevicePackage(NetworkDevicePackage networkDevicePackage) {
        // IP地址
        String ip = networkDevicePackage.getNetworkDevice().getConnectionDomain().getIp();
        // 把网络设备信息添加或更新到数据库
        boolean needOperate = ((INetworkDeviceService) AopContext.currentProxy()).operateNetworkDevice(ip, networkDevicePackage);
        // 不需要处理，直接结束
        if (!needOperate) {
            return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
        }
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                // 把网络设备系统信息添加或更新到数据库
                CompletableFuture.runAsync(() -> {
                    SysDomain sysDomain = networkDevicePackage.getNetworkDevice().getSysDomain();
                    this.networkDeviceSysService.operateNetworkDeviceSys(ip, sysDomain);
                }, this.networkDeviceMonitorThreadPoolExecutor),
                // 把网络设备接口信息添加或更新到数据库
                CompletableFuture.runAsync(() -> {
                    IfDomain ifDomain = networkDevicePackage.getNetworkDevice().getIfDomain();
                    this.networkDeviceIfService.operateNetworkDeviceIf(ip, ifDomain);
                }, this.networkDeviceMonitorThreadPoolExecutor)
        );
        try {
            // 设置超时时间
            allFutures.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("并行处理网络设备信息包被中断：{}", e.getMessage(), e);
            return Result.builder().isSuccess(false).msg("并行处理网络设备信息包被中断！").build();
        } catch (TimeoutException e) {
            log.error("并行处理网络设备信息包超时(30s)：{}", e.getMessage(), e);
            // 取消所有子任务（会触发线程中断）
            allFutures.cancel(true);
            return Result.builder().isSuccess(false).msg("并行处理网络设备信息包超时(30s)！").build();
        } catch (Exception e) {
            log.error("并行处理网络设备信息包出错：{}", e.getMessage(), e);
            return Result.builder().isSuccess(false).msg("并行处理网络设备信息包出错！").build();
        }
        // 返回结果
        return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
    }

    /**
     * <p>
     * 把网络设备信息添加或更新到数据库
     * </p>
     *
     * @param ip                   IP地址
     * @param networkDevicePackage 网络设备信息包
     * @return 是否需要操作
     * @author 皮锋
     * @custom.date 2025-3-7 16:52
     */
    @Retryable
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public boolean operateNetworkDevice(String ip, NetworkDevicePackage networkDevicePackage) {
        // SNMP连接信息
        ConnectionDomain connectionDomain = networkDevicePackage.getNetworkDevice().getConnectionDomain();
        // 当前时间
        Date currentTime = new Date();
        // 查询数据库中是否有此IP的网络设备
        LambdaQueryWrapper<MonitorNetworkDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorNetworkDevice::getIpTarget, ip);
        // 只查询特定字段
        lambdaQueryWrapper.select(MonitorNetworkDevice::getInsertType);
        // 如果存在但不是“自动发现”，则不处理
        List<MonitorNetworkDevice> monitorNetworkDevices = this.list(lambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(monitorNetworkDevices)) {
            boolean anyMatch = monitorNetworkDevices.stream().anyMatch(e -> !ZeroOrOneConstants.TWO.equals(e.getInsertType()));
            if (anyMatch) {
                return false;
            }
        }
        int selectCountDb = monitorNetworkDevices.size();
        // 封装对象
        MonitorNetworkDevice monitorNetworkDevice = new MonitorNetworkDevice();
        monitorNetworkDevice.setIpSource(this.netService.getSourceIp());
        monitorNetworkDevice.setIpTarget(ip);
        monitorNetworkDevice.setCpName(connectionDomain.getProtocol());
        monitorNetworkDevice.setCpVersion(connectionDomain.getSnmpVersion());
        monitorNetworkDevice.setCpPort(connectionDomain.getPort());
        monitorNetworkDevice.setCpCommunity(connectionDomain.getCommunity());
        monitorNetworkDevice.setConnFrequency((int) networkDevicePackage.getRate());
        monitorNetworkDevice.setInsertType(ZeroOrOneConstants.TWO);
        // 从IfDomain中计算端口汇总信息
        IfDomain ifDomain = networkDevicePackage.getNetworkDevice().getIfDomain();
        setIfSummary(monitorNetworkDevice, ifDomain);
        // 没有
        if (selectCountDb == 0) {
            monitorNetworkDevice.setInsertTime(currentTime);
            monitorNetworkDevice.setOfflineCount(0);
            // 默认开启监控和告警
            monitorNetworkDevice.setIsEnableMonitor(ZeroOrOneConstants.ONE);
            monitorNetworkDevice.setIsEnableAlarm(ZeroOrOneConstants.ONE);
            this.save(monitorNetworkDevice);
        }
        // 有
        else {
            monitorNetworkDevice.setUpdateTime(currentTime);
            LambdaUpdateWrapper<MonitorNetworkDevice> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MonitorNetworkDevice::getIpTarget, ip);
            this.update(monitorNetworkDevice, lambdaUpdateWrapper);
        }
        return true;
    }

    /**
     * <p>
     * 测试网络设备连通性
     * </p>
     *
     * @param ipTarget    IP地址（目的地）
     * @param cpPort      通信端口号
     * @param cpName      通信协议
     * @param cpCommunity 社区字符串
     * @param oid         MIB OID
     * @param cpVersion   通信协议版本
     * @return true 或者 false
     * @author 皮锋
     * @custom.date 2025-4-11 13:03
     */
    @Override
    public Boolean testMonitorNetworkDevice(String ipTarget, Integer cpPort, String cpName, String cpCommunity, String oid, String cpVersion) {
        CommProtocolTypeEnums protocol = StringUtils.containsIgnoreCase(cpName, CommProtocolTypeEnums.TCP.name()) ? CommProtocolTypeEnums.TCP : CommProtocolTypeEnums.UDP;
        OId oId = StringUtils.isBlank(oid) ? new OId() : JSON.parseObject(oid, OId.class);
        // 返回值
        boolean isConnected = false;
        if (StringUtils.equalsIgnoreCase(cpVersion, SnmpProtocolVersionConstants.VERSION_2C)) {
            // 获取网络设备信息
            Connection connection = Connection.builder().ip(ipTarget).port(cpPort).protocol(protocol).community(cpCommunity).build();
            try {
                NetworkDeviceUtils.getNetworkDeviceInfo(connection, oId);
                isConnected = true;
            } catch (Exception ignored) {
            }
        }
        // 获取网络设备信息
        LambdaQueryWrapper<MonitorNetworkDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorNetworkDevice::getIpSource, NetUtils.getLocalIp());
        lambdaQueryWrapper.eq(MonitorNetworkDevice::getIpTarget, ipTarget);
        MonitorNetworkDevice monitorNetworkDevice = this.getOne(lambdaQueryWrapper);
        // 如果有网络设备信息，则更新
        if (monitorNetworkDevice != null) {
            monitorNetworkDevice.setIsOnline(isConnected ? ZeroOrOneConstants.ONE : ZeroOrOneConstants.ZERO);
            monitorNetworkDevice.setUpdateTime(new Date());
            // 更新数据库
            this.updateById(monitorNetworkDevice);
        }
        return isConnected;
    }

    /**
     * <p>
     * 从IfDomain中计算端口汇总信息，设置到MonitorNetworkDevice上
     * </p>
     *
     * @param monitorNetworkDevice 网络设备
     * @param ifDomain            网络接口信息
     * @author 皮锋
     * @custom.date 2025/06/18
     */
    private void setIfSummary(MonitorNetworkDevice monitorNetworkDevice, IfDomain ifDomain) {
        if (ifDomain == null) {
            return;
        }
        // 总端口数
        monitorNetworkDevice.setIfNumber(ifDomain.getIfNumber());
        List<IfDomain.IfInfoDomain> ifList = ifDomain.getIfList();
        if (CollectionUtils.isEmpty(ifList)) {
            monitorNetworkDevice.setIfUsedCount(0);
            monitorNetworkDevice.setTotalInSpeed(0L);
            monitorNetworkDevice.setTotalOutSpeed(0L);
            return;
        }
        // 统计占用端口数（ifOperStatus 包含 "up"）
        int usedCount = 0;
        long totalInSpeed = 0L;
        long totalOutSpeed = 0L;
        for (IfDomain.IfInfoDomain ifInfo : ifList) {
            if (StringUtils.containsIgnoreCase(ifInfo.getIfOperStatus(), "up")) {
                usedCount++;
            }
            if (ifInfo.getIfInRealTimeSpeed() != null) {
                totalInSpeed += ifInfo.getIfInRealTimeSpeed();
            }
            if (ifInfo.getIfOutRealTimeSpeed() != null) {
                totalOutSpeed += ifInfo.getIfOutRealTimeSpeed();
            }
        }
        monitorNetworkDevice.setIfUsedCount(usedCount);
        monitorNetworkDevice.setTotalInSpeed(totalInSpeed);
        monitorNetworkDevice.setTotalOutSpeed(totalOutSpeed);
    }

}