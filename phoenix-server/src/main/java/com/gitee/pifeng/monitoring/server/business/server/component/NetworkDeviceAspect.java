package com.gitee.pifeng.monitoring.server.business.server.component;

import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import com.gitee.pifeng.monitoring.common.threadpool.ThreadPool;
import com.gitee.pifeng.monitoring.server.business.server.controller.NetworkDeviceController;
import com.gitee.pifeng.monitoring.server.inf.INetworkDeviceListener;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * <p>
 * 网络设备信息包切面
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025年3月29日 下午19:24:06
 */
@Deprecated
@Slf4j
@Aspect
@Component
public class NetworkDeviceAspect {

    /**
     * 网络设备信息监听器
     */
    @Autowired(required = false)
    private List<INetworkDeviceListener> networkDeviceListeners;

    /**
     * <p>
     * 定义切入点，切入点为{@link NetworkDeviceController#acceptNetworkDevicePackage(NetworkDevicePackage)}这一个方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2025/3/29 19:31
     */
    @Pointcut("execution(public * com.gitee.pifeng.monitoring.server.business.server.controller.NetworkDeviceController.acceptNetworkDevicePackage(..))")
    public void tangentPoint() {
    }

    /**
     * <p>
     * 通过后置通知，调用监听器回调接口。
     * </p>
     *
     * @param joinPoint 提供对连接点上可用状态和有关状态的静态信息的反射访问。
     * @author 皮锋
     * @custom.date 2025年3月29日 下午19:29:45
     */
    @After("tangentPoint()")
    public void afterWakeUp(JoinPoint joinPoint) {
        NetworkDevicePackage networkDevicePackage = (NetworkDevicePackage) joinPoint.getArgs()[0];
        // IP地址
        String ip = networkDevicePackage.getNetworkDevice().getConnectionDomain().getIp();
        // 调用监听器回调接口
        if (this.networkDeviceListeners != null) {
            this.networkDeviceListeners.forEach(o -> ThreadPool.getCommonIoIntensiveThreadPoolExecutor().execute(() -> {
                try {
                    o.wakeUpMonitor(ip);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }));
        }
    }

}