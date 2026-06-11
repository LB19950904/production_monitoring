package com.gitee.pifeng.monitoring.server.inf;

import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import com.gitee.pifeng.monitoring.server.business.server.component.NetworkDeviceAspect;
import com.gitee.pifeng.monitoring.server.business.server.websocket.listener.NetworkDeviceListener;
import org.aspectj.lang.JoinPoint;

/**
 * <p>
 * 网络设备信息监听器。
 * </p>
 * 一个被spring容器管理的类只要实现此监听器接口，当网络设备信息发生改变时，就会自动调用监听器中相应的方法。
 *
 * @author 皮锋
 * @custom.date 2025/3/29 19:22
 */
@FunctionalInterface
public interface INetworkDeviceListener {

    /**
     * <p>
     * 收到网络设备信息包时，唤醒执行监控回调方法。
     * </p>
     * 此方法在
     * {@link NetworkDeviceAspect#afterWakeUp(JoinPoint)}、
     * {@link NetworkDeviceListener#afterWakeUp(NetworkDevicePackage)}
     * 中被注册监听。
     *
     * @param param 回调参数
     * @author 皮锋
     * @custom.date 2025/3/29 19:23
     */
    void wakeUpMonitor(Object... param);

}
