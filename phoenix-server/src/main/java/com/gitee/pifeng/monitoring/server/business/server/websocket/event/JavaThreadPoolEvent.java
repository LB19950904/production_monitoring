package com.gitee.pifeng.monitoring.server.business.server.websocket.event;

import com.gitee.pifeng.monitoring.common.dto.JavaThreadPoolPackage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 * Java线程池消息事件
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/13 11:44
 */
@Getter
public class JavaThreadPoolEvent extends ApplicationEvent {

    /**
     * Netty {@link Channel} 上下文，用于获取连接信息或向客户端回写消息
     */
    private final ChannelHandlerContext ctx;

    /**
     * Java线程池信息包
     */
    private final JavaThreadPoolPackage javaThreadPoolPackage;

    /**
     * <p>
     * 构造事件对象
     * </p>
     *
     * @param ctx                   {@link Channel} 上下文
     * @param javaThreadPoolPackage {@link JavaThreadPoolPackage} Java线程池信息包
     * @author 皮锋
     * @custom.date 2026/3/13 11:46
     */
    public JavaThreadPoolEvent(ChannelHandlerContext ctx, JavaThreadPoolPackage javaThreadPoolPackage) {
        super(ctx);
        this.ctx = ctx;
        this.javaThreadPoolPackage = javaThreadPoolPackage;
    }

}