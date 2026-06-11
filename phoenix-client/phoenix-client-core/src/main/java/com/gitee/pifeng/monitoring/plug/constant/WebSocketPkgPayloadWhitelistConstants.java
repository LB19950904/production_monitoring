package com.gitee.pifeng.monitoring.plug.constant;

import com.gitee.pifeng.monitoring.common.dto.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * <p>
 * WebSocket 消息载荷白名单
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/6 10:52
 */
public class WebSocketPkgPayloadWhitelistConstants {

    /**
     * 定义允许的 WebSocket 上行实际接收的 类全限定名 白名单
     */
    public static final Set<String> UPSTREAM_ALLOWED_CLASS_NAMES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            // 心跳包
            HeartbeatPackage.class.getName(),
            // Java虚拟机信息包
            JvmPackage.class.getName(),
            // Java线程池信息包
            JavaThreadPoolPackage.class.getName(),
            // 网络设备信息包
            NetworkDevicePackage.class.getName(),
            // 服务器信息包
            ServerPackage.class.getName(),
            // docker信息包
            DockerPackage.class.getName(),
            // 下线信息包
            OfflinePackage.class.getName(),
            // 监控告警信息包
            AlarmPackage.class.getName(),
            // 监控异常信息包
            ExceptionPackage.class.getName()
    )));

    /**
     * 定义允许的 WebSocket 下行实际接收的 类全限定名 白名单
     */
    public static final Set<String> DOWNSTREAM_ALLOWED_CLASS_NAMES = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            // 命令信息包
            CommandPackage.class.getName(),
            // Java线程池信息包
            JavaThreadPoolPackage.class.getName()
    )));

}