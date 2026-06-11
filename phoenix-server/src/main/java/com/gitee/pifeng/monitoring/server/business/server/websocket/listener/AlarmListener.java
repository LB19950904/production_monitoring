package com.gitee.pifeng.monitoring.server.business.server.websocket.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.AlarmPackage;
import com.gitee.pifeng.monitoring.server.business.server.service.IAlarmService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.AlarmEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 告警消息事件监听器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/8 15:13
 */
@Slf4j
@Component
public class AlarmListener {

    /**
     * 告警服务接口
     */
    @Autowired
    private IAlarmService alarmService;

    /**
     * <p>
     * 监听监控代理端程序或者监控客户端程序发的告警包
     * </p>
     *
     * @param event 告警消息事件
     * @author 皮锋
     * @custom.date 2026/3/8 15:13
     */
    @Async
    @EventListener
    public void handleAlarmPackage(AlarmEvent event) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        AlarmPackage alarmPackage = event.getAlarmPackage();
        this.alarmService.dealAlarmPackage(alarmPackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理告警包耗时：{}", betweenDay);
        }
    }

}
