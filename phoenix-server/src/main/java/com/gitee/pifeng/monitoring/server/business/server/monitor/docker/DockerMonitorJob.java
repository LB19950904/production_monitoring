package com.gitee.pifeng.monitoring.server.business.server.monitor.docker;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.gitee.pifeng.monitoring.common.constant.ZeroOrOneConstants;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmLevelEnums;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmReasonEnums;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorSubTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Alarm;
import com.gitee.pifeng.monitoring.common.dto.AlarmPackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.common.util.DateTimeUtils;
import com.gitee.pifeng.monitoring.common.util.Md5Utils;
import com.gitee.pifeng.monitoring.server.business.server.core.MonitoringConfigPropertiesLoader;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDocker;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorServer;
import com.gitee.pifeng.monitoring.server.business.server.service.IAlarmService;
import com.gitee.pifeng.monitoring.server.business.server.service.IDockerService;
import com.gitee.pifeng.monitoring.server.business.server.service.IServerService;
import com.gitee.pifeng.monitoring.server.constant.ComponentOrderConstants;
import com.gitee.pifeng.monitoring.server.business.server.monitor.enums.MonitorEventTitleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 在项目启动后，定时扫描“MONITOR_DOCKER”表中的所有docker服务，更新docker服务状态，发送告警。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/7 20:12
 */
@Component
@Slf4j
@Order(ComponentOrderConstants.DOCKER + 1)
@DisallowConcurrentExecution
public class DockerMonitorJob extends QuartzJobBean implements CommandLineRunner {

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
     * 告警服务接口
     */
    @Autowired
    private IAlarmService alarmService;

    /**
     * docker信息服务层接口
     */
    @Autowired
    private IDockerService dockerService;

    /**
     * 服务器信息服务层接口
     */
    @Autowired
    private IServerService serverService;

    /**
     * {@link DockerMonitorJob#run(String...)}这个方法是否已经运行，<br>
     * 静态变量是类级别的变量，因此所有该类的实例对象共享。
     */
    private static volatile boolean commandLineRunnerHasRun = false;

    /**
     * <p>
     * 项目启动后，先把之前为在线状态的docker服务“更新时间”设置为当前时间，继续保证在线状态。
     * </p>
     *
     * @param args 传入的主方法参数
     * @author 皮锋
     * @custom.date 2022/7/7 20:12
     */
    @Override
    public void run(String... args) {
        List<MonitorDocker> initMonitorDockers = this.dockerService.list(new LambdaQueryWrapper<>());
        initMonitorDockers.forEach(initMonitorDocker -> {
            // 在线
            boolean isOnline = StringUtils.equals(initMonitorDocker.getIsOnline(), ZeroOrOneConstants.ONE);
            if (isOnline) {
                this.dockerService.updateById(MonitorDocker.builder().id(initMonitorDocker.getId()).updateTime(new Date()).build());
            }
        });
        commandLineRunnerHasRun = true;
    }

    /**
     * <p>
     * 扫描“MONITOR_DOCKER”表中的所有docker服务，实时更新docker服务状态，发送告警。
     * </p>
     *
     * @param jobExecutionContext 作业执行上下文
     * @author 皮锋
     * @custom.date 2021/12/3 13:13
     */
    @Override
    protected void executeInternal(@NonNull JobExecutionContext jobExecutionContext) {
        if (!commandLineRunnerHasRun) {
            return;
        }
        // 是否监控docker服务
        boolean isEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDockerProperties().isEnable();
        // 不需要监控docker服务
        if (!isEnable) {
            return;
        }
        // 是否监控docker服务状态
        boolean isStatusEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDockerProperties().getDockerStatusProperties().isEnable();
        if (!isStatusEnable) {
            return;
        }
        synchronized (DockerMonitorJob.class) {
            try {
                // 查询数据库中的所有docker服务
                List<MonitorDocker> monitorDockers = this.dockerService.list(new LambdaQueryWrapper<>());
                // 循环所有docker服务
                for (MonitorDocker monitorDocker : monitorDockers) {
                    // 是否开启监控（0：不开启监控；1：开启监控）
                    String isEnableMonitor = monitorDocker.getIsEnableMonitor();
                    // 没有开启监控，直接跳过
                    if (!StringUtils.equals(ZeroOrOneConstants.ONE, isEnableMonitor)) {
                        continue;
                    }
                    // 允许的误差时间
                    int thresholdSecond = monitorDocker.getConnFrequency() * this.monitoringConfigPropertiesLoader.getMonitoringProperties().getThreshold();
                    // 最后一次通过docker服务信息包更新的时间
                    Date dateTime = monitorDocker.getUpdateTime() == null ? monitorDocker.getInsertTime() : monitorDocker.getUpdateTime();
                    // 判决时间（在允许的误差时间内，再增加30秒误差）
                    DateTime judgeDateTime = new DateTime(dateTime).plusSeconds(thresholdSecond).plusSeconds(30);
                    // 注册上来的docker服务失去响应
                    if (judgeDateTime.isBeforeNow()) {
                        // 离线
                        this.offLine(monitorDocker);
                    }
                    // 注册上来的docker服务恢复响应
                    else {
                        // 恢复在线
                        this.onLine(monitorDocker);
                    }
                }
            } catch (Exception e) {
                log.error("定时扫描“MONITOR_DOCKER”表中的所有docker服务异常！", e);
            }
        }
    }

    /**
     * <p>
     * 处理恢复在线
     * </p>
     *
     * @param monitorDocker docker服务
     * @author 皮锋
     * @custom.date 2022/7/7 20:39
     */
    private void onLine(MonitorDocker monitorDocker) {
        try {
            if (StringUtils.isBlank(monitorDocker.getIsOnline())) {
                // 发送发现新的docker服务通知信息
                this.sendAlarmInfo("发现新docker服务", AlarmLevelEnums.INFO, AlarmReasonEnums.DISCOVERY, monitorDocker);
            } else {
                // 发送在线通知信息
                this.sendAlarmInfo("docker服务上线", AlarmLevelEnums.INFO, AlarmReasonEnums.ABNORMAL_2_NORMAL, monitorDocker);
            }
        } catch (Exception e) {
            log.error("docker服务告警异常！", e);
        }
        // 是否在线
        boolean isOnline = StringUtils.equals(monitorDocker.getIsOnline(), ZeroOrOneConstants.ONE);
        // 离线
        if (!isOnline) {
            monitorDocker.setIsOnline(ZeroOrOneConstants.ONE);
            // 更新数据库
            this.dockerService.updateById(monitorDocker);
        }
    }

    /**
     * <p>
     * 处理离线
     * </p>
     *
     * @param monitorDocker docker服务
     * @author 皮锋
     * @custom.date 2022/7/7 20:36
     */
    private void offLine(MonitorDocker monitorDocker) {
        try {
            // 发送离线告警信息
            this.sendAlarmInfo("docker服务离线", AlarmLevelEnums.FATAL, AlarmReasonEnums.NORMAL_2_ABNORMAL, monitorDocker);
        } catch (Exception e) {
            log.error("docker服务告警异常！", e);
        }
        // 是否在线
        boolean isOnline = StringUtils.equals(monitorDocker.getIsOnline(), ZeroOrOneConstants.ONE);
        // 在线
        if (isOnline) {
            // 离线次数 +1
            int offlineCount = monitorDocker.getOfflineCount() == null ? 0 : monitorDocker.getOfflineCount();
            monitorDocker.setOfflineCount(offlineCount + 1);
            monitorDocker.setIsOnline(ZeroOrOneConstants.ZERO);
            // 更新数据库
            this.dockerService.updateById(monitorDocker);
        }
    }

    /**
     * <p>
     * 发送告警信息
     * </p>
     *
     * @param title           告警标题
     * @param alarmLevelEnum  告警级别
     * @param alarmReasonEnum 告警原因
     * @param monitorDocker   docker服务
     * @throws NetException 自定义获取网络信息异常
     * @author 皮锋
     * @custom.date 2022/7/7 20:36
     */
    private void sendAlarmInfo(String title, AlarmLevelEnums alarmLevelEnum, AlarmReasonEnums alarmReasonEnum, MonitorDocker monitorDocker) throws NetException {
        // 告警是否打开
        boolean alarmEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDockerProperties().getDockerStatusProperties().isAlarmEnable();
        if (!alarmEnable) {
            return;
        }
        // 是否开启告警（0：不开启告警；1：开启告警）
        String isEnableAlarm = monitorDocker.getIsEnableAlarm();
        // 没有开启告警，直接结束
        if (!StringUtils.equals(ZeroOrOneConstants.ONE, isEnableAlarm)) {
            return;
        }
        // 服务器IP
        String serverIp = monitorDocker.getServerIp();
        // 获取docker服务对应的服务器信息
        LambdaQueryWrapper<MonitorServer> monitorServerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        monitorServerLambdaQueryWrapper.eq(MonitorServer::getIp, serverIp);
        MonitorServer monitorServer = this.serverService.getOne(monitorServerLambdaQueryWrapper);
        // 服务器名
        String serverName = null;
        if (monitorServer != null) {
            serverName = monitorServer.getServerName();
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IP地址/IP：").append(serverIp);
        if (StringUtils.isNotBlank(serverName)) {
            stringBuilder.append("，<br>服务器/Server：").append(serverName);
        }
        String dockerSummary = monitorDocker.getDockerSummary();
        if (StringUtils.isNotBlank(dockerSummary)) {
            stringBuilder.append("，<br>docker服务描述/Service desc：").append(dockerSummary);
        }
        String monitorEnv = monitorDocker.getMonitorEnv();
        if (StringUtils.isNotBlank(monitorEnv)) {
            stringBuilder.append("，<br>环境/Env：").append(monitorEnv);
        }
        String monitorGroup = monitorDocker.getMonitorGroup();
        if (StringUtils.isNotBlank(monitorGroup)) {
            stringBuilder.append("，<br>分组/Group：").append(monitorGroup);
        }
        stringBuilder.append("，<br>时间/Time：").append(DateTimeUtils.dateToString(new Date()));
        Alarm alarm = Alarm.builder()
                // 保证code的唯一性
                .code(Md5Utils.encrypt32(serverIp + serverName + DockerMonitorJob.class.getName()))
                .title(title)
                .titleEn(MonitorEventTitleEnum.getEnglishTitle(title))
                .msg(stringBuilder.toString())
                .alarmLevel(alarmLevelEnum)
                .alarmReason(alarmReasonEnum)
                .monitorType(MonitorTypeEnums.DOCKER)
                .monitorSubType(MonitorSubTypeEnums.SERVICE_STATUS)
                .alertedEntityId(String.valueOf(monitorDocker.getId()))
                .build();
        AlarmPackage alarmPackage = this.serverPackageConstructor.structureAlarmPackage(alarm);
        this.alarmService.dealAlarmPackage(alarmPackage);
    }

}
