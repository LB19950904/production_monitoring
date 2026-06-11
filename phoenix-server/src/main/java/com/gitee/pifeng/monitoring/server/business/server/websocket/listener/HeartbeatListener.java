package com.gitee.pifeng.monitoring.server.business.server.websocket.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.HeartbeatPackage;
import com.gitee.pifeng.monitoring.server.business.server.service.IHeartbeatService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.HeartbeatEvent;
import com.gitee.pifeng.monitoring.server.inf.ILinkListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 心跳消息事件监听器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/3 16:57
 */
@Slf4j
@Component
public class HeartbeatListener {

    /**
     * 心跳服务接口
     */
    @Autowired
    private IHeartbeatService heartbeatService;

    /**
     * 链路信息监听器
     */
    @Autowired(required = false)
    private List<ILinkListener> linkListeners;

    /**
     * <p>
     * 监听监控代理端程序或者监控客户端程序发的心跳包
     * </p>
     *
     * @param event 心跳消息事件
     * @author 皮锋
     * @custom.date 2026/3/3 16:20
     */
    @Async
    @EventListener
    public void handleHeartbeatPackage(HeartbeatEvent event) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        HeartbeatPackage heartbeatPackage = event.getHeartbeatPackage();
        this.beforeWakeUp(heartbeatPackage);
        this.heartbeatService.dealHeartbeatPackage(heartbeatPackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理心跳包耗时：{}", betweenDay);
        }
    }

    /**
     * <p>
     * 通过前置通知，调用监听器回调接口
     * </p>
     *
     * @param heartbeatPackage 心跳包
     * @author 皮锋
     * @custom.date 2020年4月1日 下午3:34:06
     */
    public void beforeWakeUp(HeartbeatPackage heartbeatPackage) {
        // 调用监听器回调接口
        if (this.linkListeners != null) {
            this.linkListeners.forEach(o -> {
                try {
                    o.wakeUpMonitor(heartbeatPackage);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

}