package com.gitee.pifeng.monitoring.server.business.server.websocket.dispatcher;

import com.gitee.pifeng.monitoring.common.dto.*;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.*;
import com.gitee.pifeng.monitoring.server.business.server.websocket.factory.IWebSocketEventFactory;
import com.gitee.pifeng.monitoring.server.business.server.websocket.handler.impl.MonitoringFrameHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * WebSocket 消息分发器
 * </p>
 * 核心功能：<br>
 * 1. 维护 “类名” 到 “事件创建工厂” 的映射表；<br>
 * 2. 根据消息中的 className 快速查找对应的工厂；<br>
 * 3. 发布具体的业务事件。
 *
 * @author 皮锋
 * @custom.date 2026/3/12 08:22
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketMessageDispatcher {

    /**
     * Spring 应用事件发布器
     */
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 路由表：Key 为包类的完全限定名 (className)，Value 为对应的事件创建工厂，
     * 使用 static 确保所有实例共享同一份配置，且只在类加载时初始化一次（或者在 PostConstruct 中初始化）
     */
    private static final Map<String, IWebSocketEventFactory> EVENT_FACTORY_MAP = new HashMap<>();

    /*
     *初始化注册表，在此处集中注册所有支持的包类型，新增类型时，只需在此添加一行 register 代码，无需修改分发逻辑
     */
    static {
        // 注册 监控告警信息包 (AlarmPackage) -> AlarmEvent
        register(AlarmPackage.class, (ctx, payload) -> new AlarmEvent(ctx, (AlarmPackage) payload));
        // 注册 docker信息包 (DockerPackage) -> DockerEvent
        register(DockerPackage.class, (ctx, payload) -> new DockerEvent(ctx, (DockerPackage) payload));
        // 注册 监控异常信息包 (ExceptionPackage) -> ExceptionEvent
        register(ExceptionPackage.class, (ctx, payload) -> new ExceptionEvent(ctx, (ExceptionPackage) payload));
        // 注册 心跳包 (HeartbeatPackage) -> HeartbeatEvent
        register(HeartbeatPackage.class, (ctx, payload) -> new HeartbeatEvent(ctx, (HeartbeatPackage) payload));
        // 注册 Java虚拟机信息包 (JvmPackage) -> JvmEvent
        register(JvmPackage.class, (ctx, payload) -> new JvmEvent(ctx, (JvmPackage) payload));
        // 注册 Java线程池信息包 (JavaThreadPoolPackage) -> JavaThreadPoolEvent
        register(JavaThreadPoolPackage.class, (ctx, payload) -> new JavaThreadPoolEvent(ctx, (JavaThreadPoolPackage) payload));
        // 注册 网络设备信息包 (NetworkDevicePackage) -> NetworkDeviceEvent
        register(NetworkDevicePackage.class, (ctx, payload) -> new NetworkDeviceEvent(ctx, (NetworkDevicePackage) payload));
        // 注册 下线信息包 (OfflinePackage) -> OfflineEvent
        register(OfflinePackage.class, (ctx, payload) -> new OfflineEvent(ctx, (OfflinePackage) payload));
        // 注册 服务器信息包 (ServerPackage) -> ServerEvent
        register(ServerPackage.class, (ctx, payload) -> new ServerEvent(ctx, (ServerPackage) payload));
    }

    /**
     * <p>
     * 注册方法
     * </p>
     *
     * @param clazz   包类型的 Class 对象
     * @param factory WebSocket 消息 “事件创建工厂” 接口
     * @author 皮锋
     * @custom.date 2026/3/12 08:33
     */
    private static void register(Class<?> clazz, IWebSocketEventFactory factory) {
        // 使用类的全限定名作为 Key，避免客户端传递字符串时的大小写或路径问题不一致
        EVENT_FACTORY_MAP.put(clazz.getName(), factory);
    }

    /**
     * <p>
     * 核心分发逻辑，被 {@link MonitoringFrameHandler#onMessageReceived(ChannelHandlerContext, TextWebSocketFrame)} 调用
     * </p>
     * 核心功能：<br>
     * 1. 根据 className 查找工厂；<br>
     * 2. 若找到，创建事件并发布；<br>
     * 3. 若未找到，记录警告日志。
     *
     * @param ctx Netty {@link Channel} 上下文
     * @param pkg 已解析的 WebSocket 消息数据包
     * @author 皮锋
     * @custom.date 2026/3/12 08:35
     */
    public void dispatch(ChannelHandlerContext ctx, WebSocketPackage pkg) {
        if (pkg == null || pkg.getClassName() == null) {
            log.warn("收到无效的WebSocket消息数据包：pkg is null or className is null");
            return;
        }
        String className = pkg.getClassName();
        Object payload = pkg.getPayload();
        // 1. 查表
        IWebSocketEventFactory factory = EVENT_FACTORY_MAP.get(className);
        if (factory == null) {
            // 2. 处理未知类型
            // 这种情况通常意味着：客户端版本过高发了新包，或者恶意攻击
            log.warn("收到未注册的WebSocket消息类型，跳过处理，className：{}", className);
            // 可选：在这里发送一个错误响应给客户端
            return;
        }
        try {
            // 3. 执行工厂逻辑 (创建具体的事件对象)
            // 注意：这里会执行 Lambda 中的强转逻辑，如果 payload 类型不匹配会抛 ClassCastException
            ApplicationEvent event = factory.create(ctx, payload);
            // 4. 发布事件
            this.eventPublisher.publishEvent(event);
            // 调试日志：生产环境建议关闭或改为 debug 级别，避免刷屏
            if (log.isDebugEnabled()) {
                log.debug("成功分发WebSocket消息事件：{}，类型：{}", event.getClass().getSimpleName(), className);
            }
        } catch (ClassCastException e) {
            // 类型转换失败：说明 WebSocketPackage.convert 解析出的 payload 类型与注册时预期的不符
            log.error("WebSocket消息体类型不匹配，期望类型与注册类型不一致，className：{}", className, e);
        } catch (Exception e) {
            // 其他未知异常
            log.error("分发WebSocket消息事件时发生未知异常，className：{}", className, e);
        }
    }

}