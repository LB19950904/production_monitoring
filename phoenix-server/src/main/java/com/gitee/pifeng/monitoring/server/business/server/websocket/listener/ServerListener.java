package com.gitee.pifeng.monitoring.server.business.server.websocket.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.ServerPackage;
import com.gitee.pifeng.monitoring.server.business.server.service.IServerService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.ServerEvent;
import com.gitee.pifeng.monitoring.server.inf.ILinkListener;
import com.gitee.pifeng.monitoring.server.inf.IServerListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 服务器消息事件监听器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/5 14:53
 */
@Slf4j
@Component
public class ServerListener {

    /**
     * 服务器信息服务层接口
     */
    @Autowired
    private IServerService serverService;

    /**
     * 链路信息监听器
     */
    @Autowired(required = false)
    private List<ILinkListener> linkListeners;

    /**
     * 服务器信息监听器
     */
    @Autowired(required = false)
    private List<IServerListener> serverListeners;

    /**
     * <p>
     * 监听监控代理端程序或者监控客户端程序发的服务器信息包
     * </p>
     *
     * @param event 服务器消息事件
     * @author 皮锋
     * @custom.date 2026/3/3 16:20
     */
    @Async
    @EventListener
    public void handleServerPackage(ServerEvent event) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        ServerPackage serverPackage = event.getServerPackage();
        this.beforeWakeUp(serverPackage);
        this.serverService.dealServerPackage(serverPackage);
        this.afterWakeUp(serverPackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理服务器信息包耗时：{}", betweenDay);
        }
    }

    /**
     * <p>
     * 通过前置通知，调用监听器回调接口
     * </p>
     *
     * @param serverPackage 服务器信息包
     * @author 皮锋
     * @custom.date 2022/12/22 10:01
     */
    public void beforeWakeUp(ServerPackage serverPackage) {
        // 调用监听器回调接口
        if (this.linkListeners != null) {
            this.linkListeners.forEach(o -> {
                try {
                    o.wakeUpMonitor(serverPackage);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

    /**
     * <p>
     * 通过后置通知，调用监听器回调接口
     * </p>
     *
     * @param serverPackage 服务器信息包
     * @author 皮锋
     * @custom.date 2020年4月1日 下午3:34:06
     */
    public void afterWakeUp(ServerPackage serverPackage) {
        // IP地址
        String ip = serverPackage.getIp();
        // 调用监听器回调接口
        if (this.serverListeners != null) {
            this.serverListeners.forEach(o -> {
                try {
                    o.wakeUpMonitor(ip);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

}