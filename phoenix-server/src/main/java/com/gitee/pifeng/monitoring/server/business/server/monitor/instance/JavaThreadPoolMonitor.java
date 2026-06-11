package com.gitee.pifeng.monitoring.server.business.server.monitor.instance;

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
import com.gitee.pifeng.monitoring.server.business.server.dto.JavaThreadPoolMonitorDto;
import com.gitee.pifeng.monitoring.server.business.server.service.IAlarmService;
import com.gitee.pifeng.monitoring.server.business.server.service.IJavaThreadPoolService;
import com.gitee.pifeng.monitoring.server.inf.IJavaThreadPoolListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 监听Java线程池
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-2-17 16:55
 */
@Slf4j
@Component
public class JavaThreadPoolMonitor implements IJavaThreadPoolListener {

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
     * java线程池信息服务层接口
     */
    @Autowired
    private IJavaThreadPoolService javaThreadPoolService;

    /**
     * <p>
     * 唤醒监听Java线程池信息
     * </p>
     *
     * @param param 回调参数
     * @author 皮锋
     * @custom.date 2020/3/30 20:18
     */
    @Override
    public void wakeUpMonitor(Object... param) {
        // 是否监控应用实例
        boolean isEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getInstanceProperties().isEnable();
        // 不需要监控应用实例
        if (!isEnable) {
            return;
        }
        // 是否监控应用实例线程池
        boolean isThreadPoolEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getInstanceProperties().getInstanceThreadPoolProperties().isEnable();
        if (!isThreadPoolEnable) {
            return;
        }
        // 应用实例ID
        String instanceId = String.valueOf(param[0]);
        List<JavaThreadPoolMonitorDto> threadPoolMonitorDtos = this.javaThreadPoolService.getThreadPoolMonitorList(instanceId);
        for (JavaThreadPoolMonitorDto threadPoolMonitorDto : threadPoolMonitorDtos) {
            // 是否开启监控（0：不开启监控；1：开启监控）
            String isEnableMonitor = threadPoolMonitorDto.getIsEnableMonitor();
            // 没有开启监控，直接结束
            if (!StringUtils.equals(ZeroOrOneConstants.ONE, isEnableMonitor)) {
                return;
            }
            // 监控java线程池被拒绝的任务数量
            this.monitorRejectedTaskCount(threadPoolMonitorDto);
        }
    }

    /**
     * <p>
     * 监控java线程池被拒绝的任务数量
     * </p>
     *
     * @param threadPoolMonitorDto java线程池监控传输层对象
     * @author 皮锋
     * @custom.date 2025-2-19 8:32
     */
    private void monitorRejectedTaskCount(JavaThreadPoolMonitorDto threadPoolMonitorDto) {
        // 拒绝的任务数量
        Long rejectedTaskCount = threadPoolMonitorDto.getRejectedTaskCount();
        // 有被拒绝的任务数
        if (rejectedTaskCount > 0) {
            // 发送告警消息
            this.sendAlarmInfo("提交给Java线程池的任务被拒绝", AlarmLevelEnums.FATAL, AlarmReasonEnums.NORMAL_2_ABNORMAL, threadPoolMonitorDto);
        }
        // 没有被拒绝的任务数
        else {
            // 发送告警消息
            this.sendAlarmInfo("恢复接收提交给Java线程池的任务", AlarmLevelEnums.INFO, AlarmReasonEnums.ABNORMAL_2_NORMAL, threadPoolMonitorDto);
        }
    }

    /**
     * <p>
     * 发送告警信息
     * </p>
     *
     * @param title                告警标题
     * @param alarmLevelEnum       告警级别
     * @param alarmReasonEnum      告警原因
     * @param threadPoolMonitorDto java线程池监控传输层对象
     * @throws NetException 获取网络信息异常
     * @author 皮锋
     * @custom.date 2025-2-18 9:41
     */
    private void sendAlarmInfo(String title, AlarmLevelEnums alarmLevelEnum, AlarmReasonEnums alarmReasonEnum, JavaThreadPoolMonitorDto threadPoolMonitorDto)
            throws NetException {
        // 告警是否打开
        boolean alarmEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getInstanceProperties().getInstanceThreadPoolProperties().isAlarmEnable();
        if (!alarmEnable) {
            return;
        }
        // 是否开启告警（0：不开启告警；1：开启告警）
        String isEnableAlarm = threadPoolMonitorDto.getIsEnableAlarm();
        // 没有开启告警，直接结束
        if (!StringUtils.equals(ZeroOrOneConstants.ONE, isEnableAlarm)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("应用ID/App ID：").append(threadPoolMonitorDto.getInstanceId())
                .append("，<br>应用名称/App name：").append(threadPoolMonitorDto.getInstanceName());
        // 应用实例描述
        String instanceDesc = threadPoolMonitorDto.getInstanceDesc();
        if (StringUtils.isNotBlank(instanceDesc)) {
            // 应用实例摘要
            String instanceSummary = threadPoolMonitorDto.getInstanceSummary();
            // 如果应用实例摘要不为空，则把摘要当做描述。因为：摘要是用户通过UI界面设置的，优先级比描述高。
            if (StringUtils.isNotBlank(instanceSummary)) {
                builder.append("，<br>应用描述/App desc：").append(instanceSummary);
            } else {
                builder.append("，<br>应用描述/App desc：").append(instanceDesc);
            }
        }
        String monitorEnv = threadPoolMonitorDto.getMonitorEnv();
        if (StringUtils.isNotBlank(monitorEnv)) {
            builder.append("，<br>应用环境/App env：").append(monitorEnv);
        }
        String monitorGroup = threadPoolMonitorDto.getMonitorGroup();
        if (StringUtils.isNotBlank(monitorGroup)) {
            builder.append("，<br>应用分组/App group：").append(monitorGroup);
        }
        builder.append("，<br>应用端点/App endpoint：").append(threadPoolMonitorDto.getEndpoint())
                .append("，<br>IP地址/IP：").append(threadPoolMonitorDto.getIp());
        // 线程池信息
        builder.append(",<br>线程池名字/ThreadPool name：").append(threadPoolMonitorDto.getName())
                .append(",<br>活跃线程数/Active thread count：").append(threadPoolMonitorDto.getActiveCount())
                .append(",<br>当前线程数/Current thread count：").append(threadPoolMonitorDto.getPoolSize())
                .append(",<br>核心线程数/Core thread count：").append(threadPoolMonitorDto.getCorePoolSize())
                .append(",<br>最大线程数/Maximum threads number：").append(threadPoolMonitorDto.getMaximumPoolSize())
                .append(",<br>历史最大线程数/Maximum History threads：").append(threadPoolMonitorDto.getLargestPoolSize())
                .append(",<br>总任务数/Total task count：").append(threadPoolMonitorDto.getTaskCount())
                .append(",<br>拒绝任务数/Rejected tasks number：").append(threadPoolMonitorDto.getRejectedTaskCount())
                .append(",<br>拒绝策略/Refusal strategy：").append(threadPoolMonitorDto.getRejectedExecutionHandlerName())
                .append(",<br>已完成任务数/Completed tasks number：").append(threadPoolMonitorDto.getCompletedTaskCount())
                .append(",<br>利用率/Use ratio：").append(new DecimalFormat("0.00%").format(threadPoolMonitorDto.getUtilizationRate()))
                .append(",<br>队列大小/Queue size：").append(threadPoolMonitorDto.getQueueSize())
                .append(",<br>队列类型/Queue type：").append(threadPoolMonitorDto.getQueueType())
                .append(",<br>队列容量/Queue capacity：").append(threadPoolMonitorDto.getQueueCapacity());
        Boolean allowCoreThreadTimeOut = threadPoolMonitorDto.getAllowCoreThreadTimeOut();
        if (allowCoreThreadTimeOut != null) {
            builder.append(",<br>允许核心线程超时/Allow core thread timeout：").append(allowCoreThreadTimeOut ? "是" : "否");
        }
        Long keepAliveTime = threadPoolMonitorDto.getKeepAliveTime();
        if (keepAliveTime != null) {
            builder.append(",<br>空闲线程回收时间/Idle thread recycling time：").append(keepAliveTime).append("秒");
        }
        builder.append(",<br>队列剩余容量/Queue remaining capacity：").append(threadPoolMonitorDto.getQueueRemainingCapacity());
        // 时间
        builder.append("，<br>时间/Time：").append(DateTimeUtils.dateToString(new Date()));
        Alarm alarm = Alarm.builder()
                // 保证code的唯一性
                .code(Md5Utils.encrypt32(threadPoolMonitorDto.getInstanceId() + threadPoolMonitorDto.getName()))
                .title(title)
                .msg(builder.toString())
                .alarmLevel(alarmLevelEnum)
                .alarmReason(alarmReasonEnum)
                .monitorType(MonitorTypeEnums.INSTANCE)
                .monitorSubType(MonitorSubTypeEnums.INSTANCE__THREAD_POOL)
                .alertedEntityId(String.valueOf(threadPoolMonitorDto.getId()))
                .build();
        AlarmPackage alarmPackage = this.serverPackageConstructor.structureAlarmPackage(alarm);
        this.alarmService.dealAlarmPackage(alarmPackage);
    }

}