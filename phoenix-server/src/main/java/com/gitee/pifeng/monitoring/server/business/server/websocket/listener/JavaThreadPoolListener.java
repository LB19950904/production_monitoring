package com.gitee.pifeng.monitoring.server.business.server.websocket.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.JavaThreadPoolPackage;
import com.gitee.pifeng.monitoring.server.business.server.service.IJavaThreadPoolService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.JavaThreadPoolEvent;
import com.gitee.pifeng.monitoring.server.inf.IJavaThreadPoolListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * Java线程池消息事件监听器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/13 23:12
 */
@Slf4j
@Component
public class JavaThreadPoolListener {

    /**
     * java线程池信息服务层接口
     */
    @Autowired
    private IJavaThreadPoolService javaThreadPoolService;

    /**
     * Java线程池信息监听器
     */
    @Autowired(required = false)
    private List<IJavaThreadPoolListener> javaThreadPoolListeners;

    /**
     * <p>
     * 监听监控代理端程序或者监控客户端程序发的Java线程池信息包
     * </p>
     *
     * @param event Java线程池消息事件
     * @author 皮锋
     * @custom.date 2026/3/13 23:14
     */
    @Async
    @EventListener
    public void handleJavaThreadPoolPackage(JavaThreadPoolEvent event) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        JavaThreadPoolPackage javaThreadPoolPackage = event.getJavaThreadPoolPackage();
        this.javaThreadPoolService.dealJavaThreadPoolPackage(javaThreadPoolPackage);
        this.afterWakeUp(javaThreadPoolPackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理Java线程池信息包耗时：{}", betweenDay);
        }
    }

    /**
     * <p>
     * 通过后置通知，调用监听器回调接口
     * </p>
     *
     * @param javaThreadPoolPackage Java线程池信息包
     * @author 皮锋
     * @custom.date 2025年2月18日 上午10:57:08
     */
    public void afterWakeUp(JavaThreadPoolPackage javaThreadPoolPackage) {
        // 应用实例ID
        String instanceId = javaThreadPoolPackage.getInstanceId();
        // 调用监听器回调接口
        if (this.javaThreadPoolListeners != null) {
            this.javaThreadPoolListeners.forEach(o -> {
                try {
                    o.wakeUpMonitor(instanceId);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

}