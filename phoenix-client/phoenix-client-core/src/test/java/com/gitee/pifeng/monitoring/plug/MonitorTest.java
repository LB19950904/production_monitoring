package com.gitee.pifeng.monitoring.plug;

import cn.hutool.core.lang.Console;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmLevelEnums;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Alarm;
import com.gitee.pifeng.monitoring.common.domain.ExceptionInfo;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.google.common.base.Charsets;
import org.junit.Test;

/**
 * <p>
 * 测试监控客户端入口类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020/8/17 9:53
 */
public class MonitorTest {

    /**
     * <p>
     * 测试发送告警信息
     * </p>
     *
     * @author 皮锋
     * @custom.date 2020/8/17 9:57
     */
    @Test
    public void testSendAlarm() {
        // 开启监控
        Monitor.start();
        // 封装告警信息
        Alarm alarm = Alarm.builder()
                .alarmLevel(AlarmLevelEnums.INFO)
                .monitorType(MonitorTypeEnums.CUSTOM)
                .title("测试发送告警信息")
                .msg("测试发送告警信息")
                .charset(Charsets.UTF_8)
                .isTest(false)
                .build();
        // 发送告警信息
        Result result = Monitor.sendAlarm(alarm);
        Console.log(result.toJsonString());
    }

    /**
     * <p>
     * 测试异步发送告警信息
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/3/8 13:42
     */
    @Test
    public void testAsyncSendAlarm() {
        // 开启监控
        Monitor.start();
        // 封装告警信息
        Alarm alarm = Alarm.builder()
                .alarmLevel(AlarmLevelEnums.INFO)
                .monitorType(MonitorTypeEnums.CUSTOM)
                .title("测试异步发送告警信息")
                .msg("测试发异步送告警信息")
                .charset(Charsets.UTF_8)
                .isTest(false)
                .build();
        // 异步发送告警信息
        Monitor.asyncSendAlarm(alarm);
    }

    /**
     * <p>
     * 测试采集异常信息
     * </p>
     *
     * @author 皮锋
     * @custom.date 2024/2/28 9:48
     */
    @Test
    public void testSendException() {
        // 开启监控
        Monitor.start();
        Exception exception = new NullPointerException("测试采集异常信息功能！");
        // 封装异常信息
        ExceptionInfo exceptionInfo = ExceptionInfo.builder()
                .excName(exception.getClass().getSimpleName())
                .excMessage(exception.getMessage())
                .build();
        // 采集异常信息
        Result result = Monitor.collectException(exceptionInfo);
        Console.log(result.toJsonString());
    }

    /**
     * <p>
     * 测试采集异常信息
     * </p>
     *
     * @author 皮锋
     * @custom.date 2024/2/28 14:48
     */
    @Test
    public void testAsyncSendException() {
        // 开启监控
        Monitor.start();
        Exception exception = new NullPointerException("测试异步采集异常信息功能！");
        // 封装异常信息
        ExceptionInfo exceptionInfo = ExceptionInfo.builder()
                .excName(exception.getClass().getSimpleName())
                .excMessage(exception.getMessage())
                .build();
        // 异步采集异常信息
        Monitor.asyncCollectException(exceptionInfo);
    }

}
