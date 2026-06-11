package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmReasonEnums;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Alarm;
import com.gitee.pifeng.monitoring.common.property.server.MonitoringAlarmProperties;
import com.gitee.pifeng.monitoring.plug.core.InstanceGenerator;
import com.gitee.pifeng.monitoring.server.business.server.core.MonitoringConfigPropertiesLoader;
import com.gitee.pifeng.monitoring.server.business.server.core.MysqlDistributedLock;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorRealtimeMonitoringDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorRealtimeMonitoring;
import com.gitee.pifeng.monitoring.server.business.server.service.IRealtimeMonitoringService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * 实时监控服务接口实现
 * </p>
 * <pre>
 * 状态机逻辑说明：<br>
 *
 *                           ┌──────────────┐
 *                           │   NoRecord   │ ← 初始（DB 无记录）
 *                           └──────┬───────┘
 *                                  │
 *           ┌──────────────────────┴──────────────────────┐
 *           │                                             │
 *           │ DISCOVERY                                   │ NORMAL_2_ABNORMAL
 *           │ recoveryCount=null                          │ recoveryCount=0
 *           │ failureCount=null                           │ failureCount=1
 *           ▼                                             ▼
 * ┌──────────────────┐                           ┌────────────────────┐
 * │   NORMAL         │──────────────────────────►│    ABNORMAL        │
 * │ - recoveryCount  │    NORMAL_2_ABNORMAL      │ - failureCount     │
 * │   (0 ~ n)        │                           │   (0 ~ m)          │
 * │ - failureCount=0 │                           │ - recoveryCount=0  │
 * │                  │◄──────────────────────────│                    │
 * └──────────────────┘    ABNORMAL_2_NORMAL      └────────────────────┘
 *    │            ▲                                  │            ▲
 *    │            │                                  │            │
 *    │            │                                  │            │
 *    └────────────┘                                  └────────────┘
 *        NORMAL                                          ABNORMAL
 *    recoveryCount++                                 failureCount++
 *
 * 规则：
 * n = maxRecoveryAlarmCount（最大恢复告警次数）
 * m = maxFailureAlarmCount（最大故障告警次数）
 * recoveryCount 和 failureCount 为 null 时当做 0
 *
 * - 从 NORMAL → ABNORMAL：
 * • recoveryCount 清零
 * • failureCount += 1
 * • 若 failureCount ≤ m → 告警
 *
 * - 从 ABNORMAL → NORMAL：
 * • failureCount 清零
 * • recoveryCount += 1
 * • 若 recoveryCount ≤ n → 告警
 *
 * - 同状态内重复事件：
 * • 对应计数器继续累加（不超过n、m）
 * • 超过n、m次后不再告警
 * • 下一次告警时间遵循指数退避推迟，第1次：2分钟，第2次：4分钟，第3次：8分钟...
 * </pre>
 *
 * @author 皮锋
 * @custom.date 2021/1/29 14:56
 */
@Slf4j
@Service
public class RealtimeMonitoringServiceImpl extends ServiceImpl<IMonitorRealtimeMonitoringDao, MonitorRealtimeMonitoring> implements IRealtimeMonitoringService {

    /**
     * MySQL实现的分布式锁
     */
    @Autowired
    private MysqlDistributedLock mysqlDistributedLock;

    /**
     * 监控配置属性加载器
     */
    @Autowired
    private MonitoringConfigPropertiesLoader monitoringConfigPropertiesLoader;

    /**
     * 最大恢复告警指数退避时间（毫秒），1小时
     */
    private static final long MAX_RECOVERY_ALARM_BACKOFF_MS = 3600_000;

    /**
     * 最大故障告警指数退避时间（毫秒），1小时
     */
    private static final long MAX_FAILURE_ALARM_BACKOFF_MS = 3600_000;

    /**
     * <p>
     * 对告警进行前置判断，防止重复发送相同的告警
     * </p>
     *
     * @param alarm 告警信息
     * @return boolean
     * @author 皮锋
     * @custom.date 2021/2/1 11:20
     */
    @Override
    @Retryable
    @Transactional(rollbackFor = Throwable.class, timeout = 10)
    public boolean beforeAlarmJudge(Alarm alarm) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        // 监控类型
        MonitorTypeEnums monitorTypeEnum = alarm.getMonitorType();
        // 告警原因
        AlarmReasonEnums alarmReasonEnum = alarm.getAlarmReason();
        // 自定义业务告警 || 没有设置告警原因==>直接放过
        if (monitorTypeEnum == MonitorTypeEnums.CUSTOM || alarmReasonEnum == AlarmReasonEnums.IGNORE) {
            return true;
        }
        // 监控类型名
        String typeEnumName = monitorTypeEnum.name();
        // 告警代码
        String alarmCode = alarm.getCode();
        if (StringUtils.isEmpty(alarmCode)) {
            return true;
        }
        // 生成细粒度锁键（type:code）
        String lockKey = "alarm_judge:" + typeEnumName + ":" + alarmCode;
        // 锁持有者
        String instanceId = InstanceGenerator.getInstanceId();
        // 是否获取到分布式锁
        boolean lockAcquired = false;
        try {
            // 尝试获取锁：最多等待 5 秒，锁自动过期 15 秒
            // 注意：分布式锁在 Spring 事务提交前持有，需确保事务执行时间 < 锁过期时间（15秒）
            // 高并发场景下 Redisson 分布式锁是更优选择
            lockAcquired = this.mysqlDistributedLock.tryLock(lockKey, instanceId, 15, 5);
            if (!lockAcquired) {
                // 获取锁超时，放弃本次告警判断（避免并发冲突）
                log.warn("尝试获取分布式锁超时：lockKey={}，instanceId={}", lockKey, instanceId);
                return false;
            }
            // 告警配置属性
            MonitoringAlarmProperties alarmProperties = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getAlarmProperties();
            // 全局配置的 故障告警次数
            int maxFailureAlarmCount = Math.max(0, alarmProperties.getFailureAlarmCount());
            // 全局配置的 恢复告警次数
            int maxRecoveryAlarmCount = Math.max(0, alarmProperties.getRecoveryAlarmCount());
            // 查询数据库中有没有此实时监控信息
            LambdaQueryWrapper<MonitorRealtimeMonitoring> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(MonitorRealtimeMonitoring::getType, typeEnumName);
            lambdaQueryWrapper.eq(MonitorRealtimeMonitoring::getCode, alarmCode);
            MonitorRealtimeMonitoring monitorRealtimeMonitoringDb = this.baseMapper.selectOne(lambdaQueryWrapper);
            // 当前时间
            Date currentDateTime = new Date();
            // 是否需要发送告警
            boolean shouldAlarm = false;
            // 一.数据库中没有此实时监控信息
            if (monitorRealtimeMonitoringDb == null) {
                MonitorRealtimeMonitoring monitorRealtimeMonitoring = new MonitorRealtimeMonitoring();
                monitorRealtimeMonitoring.setType(typeEnumName);
                monitorRealtimeMonitoring.setSubType(alarm.getMonitorSubType().name());
                monitorRealtimeMonitoring.setCode(alarmCode);
                monitorRealtimeMonitoring.setAlertedEntityId(alarm.getAlertedEntityId());
                monitorRealtimeMonitoring.setInsertTime(currentDateTime);
                monitorRealtimeMonitoring.setUpdateTime(currentDateTime);
                // 如果是发现（应用程序、服务器、...），failureAlarmCount 和 recoveryAlarmCount 都为 null，方便后面“异常变正常”做判断，可以避开一开始就是“正常”的数据，从而不告警
                if (alarmReasonEnum == AlarmReasonEnums.DISCOVERY) {
                    shouldAlarm = true;
                }
                // 如果是“正常变异常”
                else if (alarmReasonEnum == AlarmReasonEnums.NORMAL_2_ABNORMAL) {
                    monitorRealtimeMonitoring.setRecoveryAlarmCount(0);
                    monitorRealtimeMonitoring.setFailureAlarmCount(0);
                    if (maxFailureAlarmCount > 0) {
                        monitorRealtimeMonitoring.setFailureAlarmCount(1);
                        // 指数退避：2^1 * 60_000 = 120_000
                        long failureBackoffMs = (long) Math.pow(2, 1) * 60_000;
                        monitorRealtimeMonitoring.setNextAllowedFailureAlarmTime(new Date(currentDateTime.getTime() + failureBackoffMs));
                        shouldAlarm = true;
                    }
                }
                // 其他情况，不需要管，failureAlarmCount 和 recoveryAlarmCount 都为 null，方便后面“异常变正常”做判断，可以避开一开始就是“正常”的数据，从而不告警
                // 入库
                this.baseMapper.insert(monitorRealtimeMonitoring);
                return shouldAlarm;
            }
            // 当前 故障告警次数
            int currentFailureAlarmCount = monitorRealtimeMonitoringDb.getFailureAlarmCount() != null ? monitorRealtimeMonitoringDb.getFailureAlarmCount() : 0;
            // 当前 恢复告警次数
            int currentRecoveryAlarmCount = monitorRealtimeMonitoringDb.getRecoveryAlarmCount() != null ? monitorRealtimeMonitoringDb.getRecoveryAlarmCount() : 0;
            // 允许的下次故障报警时间
            Date nextAllowedFailureAlarmTime = monitorRealtimeMonitoringDb.getNextAllowedFailureAlarmTime() != null ? monitorRealtimeMonitoringDb.getNextAllowedFailureAlarmTime() : currentDateTime;
            // 允许的下次恢复告警时间
            Date nextAllowedRecoveryAlarmTime = monitorRealtimeMonitoringDb.getNextAllowedRecoveryAlarmTime() != null ? monitorRealtimeMonitoringDb.getNextAllowedRecoveryAlarmTime() : currentDateTime;
            // 二.数据库中有此实时监控信息
            MonitorRealtimeMonitoring monitorRealtimeMonitoring = new MonitorRealtimeMonitoring();
            monitorRealtimeMonitoring.setUpdateTime(currentDateTime);
            monitorRealtimeMonitoring.setNextAllowedRecoveryAlarmTime(monitorRealtimeMonitoringDb.getNextAllowedRecoveryAlarmTime());
            monitorRealtimeMonitoring.setNextAllowedFailureAlarmTime(monitorRealtimeMonitoringDb.getNextAllowedFailureAlarmTime());
            // 如果是“异常变正常”
            if (alarmReasonEnum == AlarmReasonEnums.ABNORMAL_2_NORMAL && monitorRealtimeMonitoringDb.getFailureAlarmCount() != null) {
                if (currentRecoveryAlarmCount < maxRecoveryAlarmCount) {
                    if (currentDateTime.compareTo(nextAllowedRecoveryAlarmTime) >= 0) {
                        currentRecoveryAlarmCount = Math.min(currentRecoveryAlarmCount + 1, maxRecoveryAlarmCount);
                        monitorRealtimeMonitoring.setRecoveryAlarmCount(currentRecoveryAlarmCount);
                        monitorRealtimeMonitoring.setFailureAlarmCount(0);
                        // 例如：第1次：2分钟，第2次：4分钟，第3次：8分钟，最大 MAX_RECOVERY_ALARM_BACKOFF_MS 分钟
                        long recoveryBackoffMs = Math.min((long) Math.pow(2, currentRecoveryAlarmCount) * 60_000, MAX_RECOVERY_ALARM_BACKOFF_MS);
                        monitorRealtimeMonitoring.setNextAllowedRecoveryAlarmTime(new Date(currentDateTime.getTime() + recoveryBackoffMs));
                        monitorRealtimeMonitoring.setNextAllowedFailureAlarmTime(null);
                        shouldAlarm = true;
                    }
                }
            }
            // 如果是“正常变异常”
            else if (alarmReasonEnum == AlarmReasonEnums.NORMAL_2_ABNORMAL) {
                if (currentFailureAlarmCount < maxFailureAlarmCount) {
                    if (currentDateTime.compareTo(nextAllowedFailureAlarmTime) >= 0) {
                        currentFailureAlarmCount = Math.min(currentFailureAlarmCount + 1, maxFailureAlarmCount);
                        monitorRealtimeMonitoring.setFailureAlarmCount(currentFailureAlarmCount);
                        monitorRealtimeMonitoring.setRecoveryAlarmCount(0);
                        monitorRealtimeMonitoring.setNextAllowedRecoveryAlarmTime(null);
                        // 例如：第1次：2分钟，第2次：4分钟，第3次：8分钟，最大 MAX_FAILURE_ALARM_BACKOFF_MS 分钟
                        long failureBackoffMs = Math.min((long) Math.pow(2, currentFailureAlarmCount) * 60_000, MAX_FAILURE_ALARM_BACKOFF_MS);
                        monitorRealtimeMonitoring.setNextAllowedFailureAlarmTime(new Date(currentDateTime.getTime() + failureBackoffMs));
                        shouldAlarm = true;
                    }
                }
            }
            LambdaUpdateWrapper<MonitorRealtimeMonitoring> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MonitorRealtimeMonitoring::getType, typeEnumName);
            lambdaUpdateWrapper.eq(MonitorRealtimeMonitoring::getCode, alarmCode);
            // 更新实时监控信息
            this.baseMapper.update(monitorRealtimeMonitoring, lambdaUpdateWrapper);
            return shouldAlarm;
        } finally {
            if (lockAcquired) {
                try {
                    boolean released = this.mysqlDistributedLock.releaseLock(lockKey, instanceId);
                    if (!released) {
                        log.warn("尝试释放分布式锁失败（可能已被自动清理）：lockKey={}，instanceId={}", lockKey, instanceId);
                    }
                } catch (Exception e) {
                    // 可记录日志，但不要抛出异常影响主流程
                    log.error("尝试释放分布式锁失败：lockKey={}，instanceId={}", lockKey, instanceId, e);
                }
            }
            // 时间差（毫秒）
            String betweenDay = timer.intervalPretty();
            // 临界值
            int criticalValue = 10;
            if (timer.intervalSecond() > criticalValue) {
                log.warn("告警前置判断耗时：{}", betweenDay);
            }
        }
    }

}