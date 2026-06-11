package com.gitee.pifeng.monitoring.server.business.server.websocket.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import com.gitee.pifeng.monitoring.server.business.server.service.INetworkDeviceService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.NetworkDeviceEvent;
import com.gitee.pifeng.monitoring.server.inf.INetworkDeviceListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 网络设备消息事件监听器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/4 21:16
 */
@Slf4j
@Component
public class NetworkDeviceListener {

    /**
     * 网络设备服务接口
     */
    @Autowired
    private INetworkDeviceService networkDeviceService;

    /**
     * 网络设备信息监听器
     */
    @Autowired(required = false)
    private List<INetworkDeviceListener> networkDeviceListeners;

    /**
     * <p>
     * 监听监控代理端程序或者监控客户端程序发的网络设备信息包
     * </p>
     *
     * @param event 网络设备消息事件
     * @author 皮锋
     * @custom.date 2026/3/3 16:20
     */
    @Async
    @EventListener
    public void handleNetworkDevicePackage(NetworkDeviceEvent event) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        NetworkDevicePackage networkDevicePackage = event.getNetworkDevicePackage();
        this.networkDeviceService.dealNetworkDevicePackage(networkDevicePackage);
        this.afterWakeUp(networkDevicePackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理网络设备信息包耗时：{}", betweenDay);
        }
    }

    /**
     * <p>
     * 通过后置通知，调用监听器回调接口
     * </p>
     *
     * @param networkDevicePackage 网络设备信息包
     * @author 皮锋
     * @custom.date 2025年3月29日 下午19:29:45
     */
    public void afterWakeUp(NetworkDevicePackage networkDevicePackage) {
        // IP地址
        String ip = networkDevicePackage.getNetworkDevice().getConnectionDomain().getIp();
        // 调用监听器回调接口
        if (this.networkDeviceListeners != null) {
            this.networkDeviceListeners.forEach(o -> {
                try {
                    o.wakeUpMonitor(ip);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }
    }

}