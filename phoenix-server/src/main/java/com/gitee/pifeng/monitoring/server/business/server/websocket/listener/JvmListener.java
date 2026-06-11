package com.gitee.pifeng.monitoring.server.business.server.websocket.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.JvmPackage;
import com.gitee.pifeng.monitoring.server.business.server.service.IJvmService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.JvmEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * <p>
 * Java虚拟机消息事件监听器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/4 19:41
 */
@Slf4j
@Component
public class JvmListener {

    /**
     * java虚拟机信息服务层接口
     */
    @Autowired
    private IJvmService jvmService;

    /**
     * <p>
     * 监听监控代理端程序或者监控客户端程序发的Java虚拟机信息包
     * </p>
     *
     * @param event Java虚拟机消息事件
     * @author 皮锋
     * @custom.date 2026/3/3 16:20
     */
    @Async
    @EventListener
    public void handleJvmPackage(JvmEvent event) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        JvmPackage jvmPackage = event.getJvmPackage();
        this.jvmService.dealJvmPackage(jvmPackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理JVM信息包耗时：{}", betweenDay);
        }
    }

}