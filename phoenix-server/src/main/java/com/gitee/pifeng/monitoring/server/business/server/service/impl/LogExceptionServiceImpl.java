package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.constant.ZeroOrOneConstants;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmLevelEnums;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmReasonEnums;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Alarm;
import com.gitee.pifeng.monitoring.common.domain.ExceptionInfo;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.AlarmPackage;
import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;
import com.gitee.pifeng.monitoring.common.util.DateTimeUtils;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorInstanceDao;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorLogExceptionDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorInstance;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorLogException;
import com.gitee.pifeng.monitoring.server.business.server.service.IAlarmService;
import com.gitee.pifeng.monitoring.server.business.server.service.ILogExceptionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * <p>
 * 异常日志服务层接口实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021-06-09
 */
@Service
public class LogExceptionServiceImpl extends ServiceImpl<IMonitorLogExceptionDao, MonitorLogException> implements ILogExceptionService {

    /**
     * 告警服务接口
     */
    @Autowired
    private IAlarmService alarmService;

    /**
     * 服务端包构造器
     */
    @Autowired
    private ServerPackageConstructor serverPackageConstructor;

    /**
     * 应用实例数据访问对象
     */
    @Autowired
    private IMonitorInstanceDao monitorInstanceDao;

    /**
     * <p>
     * 处理异常包
     * </p>
     *
     * @param exceptionPackage 异常包
     * @return {@link Result} 返回结果
     * @author 皮锋
     * @custom.date 2024/2/28 11:47
     */
    @Override
    public Result dealExceptionPackage(ExceptionPackage exceptionPackage) {
        Date date = new Date();
        // 应用实例ID
        String instanceId = exceptionPackage.getInstanceId();
        // 异常信息
        ExceptionInfo exceptionInfo = exceptionPackage.getExceptionInfo();
        MonitorLogException monitorLogException = new MonitorLogException();
        monitorLogException.setInstanceId(instanceId);
        monitorLogException.setReqParam(exceptionInfo.getReqParam());
        monitorLogException.setExcName(exceptionInfo.getExcName());
        monitorLogException.setExcMessage(exceptionInfo.getExcMessage());
        monitorLogException.setUserId(exceptionInfo.getUserId());
        monitorLogException.setUsername(exceptionInfo.getUsername());
        monitorLogException.setOperMethod(exceptionInfo.getOperationMethod());
        monitorLogException.setUri(exceptionInfo.getReqUri());
        monitorLogException.setIp(exceptionInfo.getReqIp());
        monitorLogException.setInsertTime(date);
        // 是否开启异常信息告警
        boolean alarmEnable = exceptionPackage.isAlarmEnable();
        if (alarmEnable) {
            // 虽然设置的是发送告警，但是最终有没有发告警，此处保证不了呢
            monitorLogException.setIsAlarm(ZeroOrOneConstants.ONE);
        } else {
            monitorLogException.setIsAlarm(ZeroOrOneConstants.ZERO);
        }
        // 入库
        this.baseMapper.insert(monitorLogException);
        if (alarmEnable) {
            // 发送告警
            this.sendAlarmInfo(exceptionInfo, instanceId, date);
        }
        // 返回结果
        return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
    }

    /**
     * <p>
     * 发送告警信息
     * </p>
     *
     * @param exceptionInfo 异常信息
     * @param instanceId    应用实例ID
     * @param alarmDate     告警时间
     * @author 皮锋
     * @custom.date 2024/2/29 8:46
     */
    private void sendAlarmInfo(ExceptionInfo exceptionInfo, String instanceId, Date alarmDate) {
        String excName = exceptionInfo.getExcName();
        String excMessage = exceptionInfo.getExcMessage();
        String username = exceptionInfo.getUsername();
        String operationMethod = exceptionInfo.getOperationMethod();
        String reqParam = exceptionInfo.getReqParam();
        String reqUri = exceptionInfo.getReqUri();
        String reqIp = exceptionInfo.getReqIp();
        // 根据instanceId查询应用实例
        LambdaQueryWrapper<MonitorInstance> monitorInstanceLambdaQueryWrapper = new LambdaQueryWrapper<>();
        monitorInstanceLambdaQueryWrapper.eq(MonitorInstance::getInstanceId, instanceId);
        MonitorInstance monitorInstance = this.monitorInstanceDao.selectOne(monitorInstanceLambdaQueryWrapper);
        if (monitorInstance == null) {
            return;
        }
        // 拼接告警消息
        StringBuilder msgBuilder = new StringBuilder();
        msgBuilder.append("应用ID：").append(instanceId);
        msgBuilder.append("，<br>应用名称：").append(monitorInstance.getInstanceName());
        // 应用实例描述
        String instanceDesc = monitorInstance.getInstanceDesc();
        if (StringUtils.isNotBlank(instanceDesc)) {
            // 应用实例摘要
            String instanceSummary = monitorInstance.getInstanceSummary();
            // 如果应用实例摘要不为空，则把摘要当做描述。因为：摘要是用户通过UI界面设置的，优先级比描述高
            if (StringUtils.isNotBlank(instanceSummary)) {
                msgBuilder.append("，<br>应用描述：").append(instanceSummary);
            } else {
                msgBuilder.append("，<br>应用描述：").append(instanceDesc);
            }
        }
        if (StringUtils.isNotBlank(excName)) {
            msgBuilder.append("，<br>异常名称：").append(excName);
        }
        if (StringUtils.isNotBlank(excMessage)) {
            msgBuilder.append("，<br>异常信息：").append(excMessage);
        }
        if (StringUtils.isNotBlank(username)) {
            msgBuilder.append("，<br>操作用户：").append(username);
        }
        if (StringUtils.isNotBlank(operationMethod)) {
            msgBuilder.append("，<br>操作方法：").append(operationMethod);
        }
        if (StringUtils.isNotBlank(reqParam)) {
            msgBuilder.append("，<br>请求参数：").append(reqParam);
        }
        if (StringUtils.isNotBlank(reqUri)) {
            msgBuilder.append("，<br>请求URI：").append(reqUri);
        }
        if (StringUtils.isNotBlank(reqIp)) {
            msgBuilder.append("，<br>请求IP：").append(reqIp);
        }
        msgBuilder.append("，<br>时间：").append(DateTimeUtils.dateToString(alarmDate));
        // 发送告警
        Alarm alarm = Alarm.builder()
                .alarmLevel(AlarmLevelEnums.ERROR)
                .alarmReason(AlarmReasonEnums.IGNORE)
                .monitorType(MonitorTypeEnums.INSTANCE)
                .charset(StandardCharsets.UTF_8)
                .title(excName)
                .msg(msgBuilder.toString())
                .build();
        AlarmPackage alarmPackage = this.serverPackageConstructor.structureAlarmPackage(alarm);
        this.alarmService.dealAlarmPackage(alarmPackage);
    }

}
