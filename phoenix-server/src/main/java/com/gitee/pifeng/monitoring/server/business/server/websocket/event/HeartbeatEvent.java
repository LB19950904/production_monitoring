package com.gitee.pifeng.monitoring.server.business.server.websocket.event;

import com.gitee.pifeng.monitoring.common.dto.HeartbeatPackage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 * 心跳消息事件
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/12 08:58
 */
@Getter
public class HeartbeatEvent extends ApplicationEvent {

    /**
     * Netty {@link Channel} 上下文，用于获取连接信息或向客户端回写消息
     */
    private final ChannelHandlerContext ctx;

    /**
     * 心跳包
     */
    private final HeartbeatPackage heartbeatPackage;

    /**
     * <p>
     * 构造事件对象
     * </p>
     *
     * @param ctx              {@link Channel} 上下文
     * @param heartbeatPackage {@link HeartbeatPackage} 心跳包
     * @author 皮锋
     * @custom.date 2026/3/3 14:25
     */
    public HeartbeatEvent(ChannelHandlerContext ctx, HeartbeatPackage heartbeatPackage) {
        super(ctx);
        this.ctx = ctx;
        this.heartbeatPackage = heartbeatPackage;
    }

}