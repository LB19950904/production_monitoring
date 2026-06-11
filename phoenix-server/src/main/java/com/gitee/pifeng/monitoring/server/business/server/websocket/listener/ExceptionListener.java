package com.gitee.pifeng.monitoring.server.business.server.websocket.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;
import com.gitee.pifeng.monitoring.server.business.server.service.ILogExceptionService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.ExceptionEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 异常消息事件监听器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/8 15:17
 */
@Slf4j
@Component
public class ExceptionListener {

    /**
     * 异常日志服务层接口
     */
    @Autowired
    private ILogExceptionService logExceptionService;

    /**
     * <p>
     * 监听监控代理端程序或者监控客户端程序发的异常包
     * </p>
     *
     * @param event 异常消息事件
     * @author 皮锋
     * @custom.date 2026/3/8 15:17
     */
    @Async
    @EventListener
    public void handleExceptionPackage(ExceptionEvent event) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        ExceptionPackage exceptionPackage = event.getExceptionPackage();
        this.logExceptionService.dealExceptionPackage(exceptionPackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理异常包耗时：{}", betweenDay);
        }
    }

}
