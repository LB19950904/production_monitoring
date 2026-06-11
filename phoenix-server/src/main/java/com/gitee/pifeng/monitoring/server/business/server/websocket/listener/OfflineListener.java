package com.gitee.pifeng.monitoring.server.business.server.websocket.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.OfflinePackage;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.OfflineEvent;
import com.gitee.pifeng.monitoring.server.inf.IOfflineListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 下线消息事件监听器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/5 14:34
 */
@Slf4j
@Component
public class OfflineListener {

    /**
     * 下线监听器
     */
    @Autowired(required = false)
    private List<IOfflineListener> offlineListeners;

    /**
     * <p>
     * 监听监控代理端程序或者监控客户端程序发的下线信息包
     * </p>
     *
     * @param event 下线消息事件
     * @author 皮锋
     * @custom.date 2026/3/3 16:20
     */
    @Async
    @EventListener
    public void handleOfflinePackage(OfflineEvent event) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        OfflinePackage offlinePackage = event.getOfflinePackage();
        this.beforeWakeUp(offlinePackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理下线信息包耗时：{}", betweenDay);
        }
    }

    /**
     * <p>
     * 通过前置通知，调用监听器回调接口
     * </p>
     *
     * @param offlinePackage 下线信息包
     * @author 皮锋
     * @custom.date 2023年6月1日 下午20:34:03
     */
    public void beforeWakeUp(OfflinePackage offlinePackage) {
        // 调用监听器回调接口
        if (this.offlineListeners != null) {
            this.offlineListeners.forEach(o -> {
                try {
                    o.notifyOffline(offlinePackage);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

}