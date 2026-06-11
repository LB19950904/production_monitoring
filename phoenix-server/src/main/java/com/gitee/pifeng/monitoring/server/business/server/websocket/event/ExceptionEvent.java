package com.gitee.pifeng.monitoring.server.business.server.websocket.event;

import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 * 异常消息事件
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/12 08:58
 */
@Getter
public class ExceptionEvent extends ApplicationEvent {

    /**
     * Netty {@link Channel} 上下文，用于获取连接信息或向客户端回写消息
     */
    private final ChannelHandlerContext ctx;

    /**
     * 监控异常信息包
     */
    private final ExceptionPackage exceptionPackage;

    /**
     * <p>
     * 构造事件对象
     * </p>
     *
     * @param ctx              {@link Channel} 上下文
     * @param exceptionPackage {@link ExceptionPackage} 监控异常信息包
     * @author 皮锋
     * @custom.date 2026/3/3 14:25
     */
    public ExceptionEvent(ChannelHandlerContext ctx, ExceptionPackage exceptionPackage) {
        super(ctx);
        this.ctx = ctx;
        this.exceptionPackage = exceptionPackage;
    }

}