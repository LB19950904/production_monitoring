package com.gitee.pifeng.monitoring.server.business.server.websocket.factory;

import io.netty.channel.ChannelHandlerContext;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 * WebSocket 消息 “事件创建工厂” 接口
 * </p>
 * 用于将原始的上下文(ctx)和负载(payload)转换为具体的 Spring 事件对象，使用函数式接口以便支持 Lambda 表达式和方法引用。
 *
 * @author 皮锋
 * @custom.date 2026/3/12 08:17
 */
@FunctionalInterface
public interface IWebSocketEventFactory {

    /**
     * <p>
     * 创建事件实例
     * </p>
     *
     * @param ctx     WebSocket 上下文 ({@link ChannelHandlerContext})
     * @param payload 解析后的业务负载对象
     * @return 构建好的 Spring 事件对象
     * @author 皮锋
     * @custom.date 2026/3/12 08:20
     */
    ApplicationEvent create(ChannelHandlerContext ctx, Object payload);

}