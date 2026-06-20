package com.gitee.pifeng.monitoring.server.business.server.monitor.networkdevice;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gitee.pifeng.monitoring.common.constant.CommProtocolTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.ZeroOrOneConstants;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmLevelEnums;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmReasonEnums;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorSubTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpProtocolVersionConstants;
import com.gitee.pifeng.monitoring.common.domain.Alarm;
import com.gitee.pifeng.monitoring.common.domain.NetworkDevice;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.IfDomain;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.SysDomain;
import com.gitee.pifeng.monitoring.common.dto.AlarmPackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.OId;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.v2c.Connection;
import com.gitee.pifeng.monitoring.common.threadpool.MonitoredThreadPoolExecutor;
import com.gitee.pifeng.monitoring.common.util.DateTimeUtils;
import com.gitee.pifeng.monitoring.common.util.Md5Utils;
import com.gitee.pifeng.monitoring.common.util.snmp.v2c.NetworkDeviceUtils;
import com.gitee.pifeng.monitoring.server.business.server.core.MonitoringConfigPropertiesLoader;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorNetworkDevice;
import com.gitee.pifeng.monitoring.server.business.server.monitor.enums.MonitorEventTitleEnum;
import com.gitee.pifeng.monitoring.server.business.server.service.IAlarmService;
import com.gitee.pifeng.monitoring.server.business.server.service.INetworkDeviceIfService;
import com.gitee.pifeng.monitoring.server.business.server.service.INetworkDeviceService;
import com.gitee.pifeng.monitoring.server.business.server.service.INetworkDeviceSysService;
import com.gitee.pifeng.monitoring.server.constant.ComponentOrderConstants;
import com.gitee.pifeng.monitoring.server.inf.INetworkDeviceListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 在项目启动后，定时扫描“MONITOR_NETWORK_DEVICE”表中的所有网络设备，更新网络设备状态，发送告警。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-3-25 16:01
 */
@Slf4j
@Component
@Order(ComponentOrderConstants.NETWORK_DEVICE + 1)
@DisallowConcurrentExecution
public class NetworkDeviceMonitorJob extends QuartzJobBean implements CommandLineRunner, INetworkDeviceListener {

    /**
     * 监控配置属性加载器
     */
    @Autowired
    private MonitoringConfigPropertiesLoader monitoringConfigPropertiesLoader;

    /**
     * 服务端包构造器
     */
    @Autowired
    private ServerPackageConstructor serverPackageConstructor;

    /**
     * 网络设备服务接口
     */
    @Autowired
    private INetworkDeviceService networkDeviceService;

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
     * 告警服务接口
     */
    @Autowired
    private IAlarmService alarmService;

    /**
     * 线程池
     */
    @Autowired
    @Qualifier("networkDeviceMonitorThreadPoolExecutor")
    private MonitoredThreadPoolExecutor networkDeviceMonitorThreadPoolExecutor;

    /**
     * {@link NetworkDeviceMonitorJob#run(String...)}这个方法是否已经运行，<br>
     * 静态变量是类级别的变量，因此所有该类的实例对象共享。
     */
    private static volatile boolean commandLineRunnerHasRun = false;

    /**
     * <p>
     * 项目启动后，先把之前“自动添加”的为在线状态的网络设备“更新时间”设置为当前时间，继续保证在线状态。
     * </p>
     *
     * @param args 传入的主方法参数
     * @author 皮锋
     * @custom.date 2025/3/29 19:40
     */
    @Override
    public void run(String... args) {
        // 查询所有的自动发现的网络设备
        LambdaQueryWrapper<MonitorNetworkDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorNetworkDevice::getInsertType, ZeroOrOneConstants.TWO);
        List<MonitorNetworkDevice> initMonitorNetworkDevices = this.networkDeviceService.list(lambdaQueryWrapper);
        initMonitorNetworkDevices.forEach(networkDevice -> {
            boolean isOnline = StringUtils.equals(networkDevice.getIsOnline(), ZeroOrOneConstants.ONE);
            // 在线
            if (isOnline) {
                MonitorNetworkDevice monitorNetworkDevice = MonitorNetworkDevice.builder().id(networkDevice.getId()).updateTime(new Date()).build();
                this.networkDeviceService.updateById(monitorNetworkDevice);
            }
        });
        commandLineRunnerHasRun = true;
    }

    /**
     * 扫描数据库“MONITOR_NETWORK_DEVICE”表中的所有“手动添加”和“自动添加”的网络设备信息，实时更新网络设备状态，发送告警。
     *
     * @param jobExecutionContext 作业执行上下文
     * @author 皮锋
     * @custom.date 2025/3/29 19:13
     */
    @Override
    protected void executeInternal(@NonNull JobExecutionContext jobExecutionContext) throws JobExecutionException {
        if (!commandLineRunnerHasRun) {
            return;
        }
        // 是否监控网络设备
        boolean isEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getNetworkDeviceProperties().isEnable();
        // 不需要监控网络设备
        if (!isEnable) {
            return;
        }
        // 是否监控网络设备状态
        boolean isStatusEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getNetworkDeviceProperties().getNetworkDeviceStatusProperties().isEnable();
        if (!isStatusEnable) {
            return;
        }
        synchronized (NetworkDeviceMonitorJob.class) {
            try {
                // 查询数据库中的所有的网络设备
                List<MonitorNetworkDevice> monitorNetworkDevices = this.networkDeviceService.list(new LambdaQueryWrapper<>());
                if (CollectionUtils.isNotEmpty(monitorNetworkDevices)) {
                    // “自动添加”的网络设备列表
                    List<MonitorNetworkDevice> automaticNetworkDevices = monitorNetworkDevices.stream().filter(e -> StringUtils.equals(e.getInsertType(), ZeroOrOneConstants.TWO)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(automaticNetworkDevices)) {
                        this.networkDeviceMonitorThreadPoolExecutor.execute(() -> {
                            // 循环所有“自动添加”的网络设备
                            for (MonitorNetworkDevice automaticNetworkDevice : automaticNetworkDevices) {
                                // 是否开启监控（0：不开启监控；1：开启监控）
                                String isEnableMonitor = automaticNetworkDevice.getIsEnableMonitor();
                                // 没有开启监控，直接跳过
                                if (!StringUtils.equals(ZeroOrOneConstants.ONE, isEnableMonitor)) {
                                    continue;
                                }
                                // 允许的误差时间
                                int thresholdSecond = automaticNetworkDevice.getConnFrequency() * this.monitoringConfigPropertiesLoader.getMonitoringProperties().getThreshold();
                                // 最后一次通过网络设备信息包更新的时间
                                Date dateTime = automaticNetworkDevice.getUpdateTime() == null ? automaticNetworkDevice.getInsertTime() : automaticNetworkDevice.getUpdateTime();
                                // 判决时间（在允许的误差时间内，再增加30秒误差）
                                DateTime judgeDateTime = new DateTime(dateTime).plusSeconds(thresholdSecond).plusSeconds(30);
                                // 注册上来的网络设备失去响应
                                if (judgeDateTime.isBeforeNow()) {
                                    // 离线
                                    this.offLine(automaticNetworkDevice);
                                }
                                // 注册上来的网络设备恢复响应
                                else {
                                    // 恢复在线
                                    this.onLine(automaticNetworkDevice, null);
                                }
                            }
                        });
                    }
                    // “手动添加”的网络设备列表
                    List<MonitorNetworkDevice> handheldNetworkDevices = monitorNetworkDevices.stream().filter(e -> StringUtils.equals(e.getInsertType(), ZeroOrOneConstants.ONE)).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(handheldNetworkDevices)) {
                        this.networkDeviceMonitorThreadPoolExecutor.execute(() -> {
                            // 按每个list大小为10拆分成多个list
                            List<List<MonitorNetworkDevice>> subHandheldNetworkLists = com.gitee.pifeng.monitoring.common.util.CollectionUtils.split(handheldNetworkDevices, 10);
                            for (List<MonitorNetworkDevice> subHandheldNetworkDevices : subHandheldNetworkLists) {
                                this.networkDeviceMonitorThreadPoolExecutor.execute(() -> {
                                    // 循环处理每一个网络设备
                                    for (MonitorNetworkDevice handheldNetworkDevice : subHandheldNetworkDevices) {
                                        try {
                                            // 是否开启监控（0：不开启监控；1：开启监控）
                                            String isEnableMonitor = handheldNetworkDevice.getIsEnableMonitor();
                                            // 没有开启监控，直接跳过
                                            if (!StringUtils.equals(ZeroOrOneConstants.ONE, isEnableMonitor)) {
                                                continue;
                                            }
                                            // 获取到的网络设备信息
                                            NetworkDevice networkDevice = null;
                                            // 监控阈值
                                            int threshold = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getThreshold();
                                            for (int i = 0; i < threshold; i++) {
                                                networkDevice = this.getNetworkDeviceInfo(handheldNetworkDevice);
                                                if (networkDevice != null) {
                                                    break;
                                                }
                                            }
                                            // 在线
                                            if (networkDevice != null) {
                                                this.onLine(handheldNetworkDevice, networkDevice);
                                            }
                                            // 离线
                                            else {
                                                this.offLine(handheldNetworkDevice);
                                            }
                                        } catch (Exception ignored) {
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            } catch (Exception e) {
                log.error("定时扫描“MONITOR_NETWORK_DEVICE”表中的所有“手动添加”和“自动添加”的网络设备信息异常！", e);
            }
        }
    }

    /**
     * <p>
     * 获取网络设备信息
     * </p>
     *
     * @param handheldNetworkDevice “手动添加”的网络设备
     * @return {@link NetworkDevice} 网络设备信息
     * @author 皮锋
     * @custom.date 2025年06月04日 下午16:54:10
     */
    private NetworkDevice getNetworkDeviceInfo(MonitorNetworkDevice handheldNetworkDevice) {
        String ipTarget = handheldNetworkDevice.getIpTarget();
        Integer cpPort = handheldNetworkDevice.getCpPort();
        String cpName = handheldNetworkDevice.getCpName();
        CommProtocolTypeEnums protocol = StringUtils.containsIgnoreCase(cpName, CommProtocolTypeEnums.TCP.name()) ? CommProtocolTypeEnums.TCP : CommProtocolTypeEnums.UDP;
        String cpCommunity = handheldNetworkDevice.getCpCommunity();
        String oid = handheldNetworkDevice.getOid();
        OId oId = StringUtils.isBlank(oid) ? new OId() : JSON.parseObject(oid, OId.class);
        String cpVersion = handheldNetworkDevice.getCpVersion();
        // 返回值
        NetworkDevice networkDevice = null;
        if (StringUtils.equalsIgnoreCase(cpVersion, SnmpProtocolVersionConstants.VERSION_2C)) {
            // 获取网络设备信息
            Connection connection = Connection.builder().ip(ipTarget).port(cpPort).protocol(protocol).community(cpCommunity).build();
            try {
                networkDevice = NetworkDeviceUtils.getNetworkDeviceInfo(connection, oId);
            } catch (Exception ignored) {
            }
        }
        return networkDevice;
    }

    /**
     * <p>
     * 处理恢复在线
     * </p>
     *
     * @param monitorNetworkDevice 网络设备
     * @param networkDeviceInfo    网络设备信息
     * @author 皮锋
     * @custom.date 2025/3/29 20:55
     */
    private void onLine(MonitorNetworkDevice monitorNetworkDevice, NetworkDevice networkDeviceInfo) {
        if (networkDeviceInfo != null) {
            String ip = networkDeviceInfo.getConnectionDomain().getIp();
            SysDomain sysDomain = networkDeviceInfo.getSysDomain();
            IfDomain ifDomain = networkDeviceInfo.getIfDomain();
            // 把网络设备系统信息添加或更新到数据库
            this.networkDeviceSysService.operateNetworkDeviceSys(ip, sysDomain);
            // 把网络设备接口信息添加或更新到数据库
            this.networkDeviceIfService.operateNetworkDeviceIf(ip, ifDomain);
            // 从IfDomain中计算端口汇总信息
            setIfSummary(monitorNetworkDevice, ifDomain);
        }
        // 更新网络设备状态
        monitorNetworkDevice.setIsOnline(ZeroOrOneConstants.ONE);
        monitorNetworkDevice.setUpdateTime(new Date());
        this.networkDeviceService.updateById(monitorNetworkDevice);
        // 是否在线
        // boolean isOnline = StringUtils.equals(monitorNetworkDevice.getIsOnline(), ZeroOrOneConstants.ONE);
        // 离线
        // if (!isOnline) {
        try {
            if (StringUtils.isBlank(monitorNetworkDevice.getIsOnline())) {
                // 发送发现新的网络设备通知信息
                this.sendAlarmInfo("发现新网络设备", AlarmLevelEnums.INFO, AlarmReasonEnums.DISCOVERY, monitorNetworkDevice);
            } else {
                // 发送在线通知信息
                this.sendAlarmInfo("网络设备上线", AlarmLevelEnums.INFO, AlarmReasonEnums.ABNORMAL_2_NORMAL, monitorNetworkDevice);
            }
        } catch (Exception e) {
            log.error("网络设备告警异常！", e);
        }
        // }
    }

    /**
     * <p>
     * 处理离线
     * </p>
     *
     * @param monitorNetworkDevice 网络设备
     * @author 皮锋
     * @custom.date 2025/3/29 20:56
     */
    private void offLine(MonitorNetworkDevice monitorNetworkDevice) {
        try {
            // 发送离线告警信息
            this.sendAlarmInfo("网络设备离线", AlarmLevelEnums.FATAL, AlarmReasonEnums.NORMAL_2_ABNORMAL, monitorNetworkDevice);
        } catch (Exception e) {
            log.error("网络设备告警异常！", e);
        }
        // 是否在线
        boolean isOnline = StringUtils.equals(monitorNetworkDevice.getIsOnline(), ZeroOrOneConstants.ONE);
        // 在线
        if (isOnline) {
            // 离线次数 +1
            int offlineCount = monitorNetworkDevice.getOfflineCount() == null ? 0 : monitorNetworkDevice.getOfflineCount();
            monitorNetworkDevice.setOfflineCount(offlineCount + 1);
            monitorNetworkDevice.setIsOnline(ZeroOrOneConstants.ZERO);
            // 更新数据库
            this.networkDeviceService.updateById(monitorNetworkDevice);
        }
    }

    /**
     * <p>
     * 收到网络设备信息包时，唤醒执行监控回调方法，操作“自动添加”的网络设备
     * </p>
     *
     * @param obj 回调参数
     * @author 皮锋
     * @custom.date 2025/3/29 19:57
     */
    @Override
    public void wakeUpMonitor(Object... obj) {

    }

    /**
     * <p>
     * 发送告警信息
     * </p>
     *
     * @param title           告警标题
     * @param alarmLevelEnum  告警级别
     * @param alarmReasonEnum 告警原因
     * @param networkDevice   网络设备
     * @throws NetException 自定义获取网络信息异常
     * @author 皮锋
     * @custom.date 2025/3/8 21:08
     */
    private void sendAlarmInfo(String title, AlarmLevelEnums alarmLevelEnum, AlarmReasonEnums alarmReasonEnum, MonitorNetworkDevice networkDevice) throws NetException {
        // 告警是否打开
        boolean alarmEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getNetworkDeviceProperties().getNetworkDeviceStatusProperties().isAlarmEnable();
        if (!alarmEnable) {
            return;
        }
        // 是否开启告警（0：不开启告警；1：开启告警）
        String isEnableAlarm = networkDevice.getIsEnableAlarm();
        // 没有开启告警，直接结束
        if (!StringUtils.equals(ZeroOrOneConstants.ONE, isEnableAlarm)) {
            return;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IP地址/IP：").append(networkDevice.getIpTarget());
        String networkDeviceType = networkDevice.getNetworkDeviceType();
        if (StringUtils.isNotBlank(networkDeviceType)) {
            stringBuilder.append("，<br>类型/Type：").append(networkDeviceType);
        }
        String networkDeviceSummary = networkDevice.getNetworkDeviceSummary();
        if (StringUtils.isNotBlank(networkDeviceSummary)) {
            stringBuilder.append("，<br>描述/Desc：").append(networkDeviceSummary);
        }
        String monitorEnv = networkDevice.getMonitorEnv();
        if (StringUtils.isNotBlank(monitorEnv)) {
            stringBuilder.append("，<br>环境/Env：").append(monitorEnv);
        }
        String monitorGroup = networkDevice.getMonitorGroup();
        if (StringUtils.isNotBlank(monitorGroup)) {
            stringBuilder.append("，<br>分组/Group：").append(monitorGroup);
        }
        stringBuilder.append("，<br>时间/Time：").append(DateTimeUtils.dateToString(new Date()));
        Alarm alarm = Alarm.builder()
                // 保证code的唯一性
                .code(Md5Utils.encrypt32(networkDevice.getIpTarget() + NetworkDeviceMonitorJob.class.getName()))
                .title(title)
                .titleEn(MonitorEventTitleEnum.getEnglishTitle(title))
                .msg(stringBuilder.toString())
                .alarmLevel(alarmLevelEnum)
                .alarmReason(alarmReasonEnum)
                .monitorType(MonitorTypeEnums.NETWORK_DEVICE)
                .monitorSubType(MonitorSubTypeEnums.SERVICE_STATUS)
                .alertedEntityId(String.valueOf(networkDevice.getId()))
                .build();
        AlarmPackage alarmPackage = this.serverPackageConstructor.structureAlarmPackage(alarm);
        this.alarmService.dealAlarmPackage(alarmPackage);
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