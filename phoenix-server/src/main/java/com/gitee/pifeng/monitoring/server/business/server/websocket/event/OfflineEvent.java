package com.gitee.pifeng.monitoring.server.business.server.websocket.event;

import com.gitee.pifeng.monitoring.common.dto.OfflinePackage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 * 下线消息事件
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/12 08:58
 */
@Getter
public class OfflineEvent extends ApplicationEvent {

    /**
     * Netty {@link Channel} 上下文，用于获取连接信息或向客户端回写消息
     */
    private final ChannelHandlerContext ctx;

    /**
     * 下线信息包
     */
    private final OfflinePackage offlinePackage;

    /**
     * <p>
     * 构造事件对象
     * </p>
     *
     * @param ctx            {@link Channel} 上下文
     * @param offlinePackage {@link OfflinePackage} 下线信息包
     * @author 皮锋
     * @custom.date 2026/3/3 14:25
     */
    public OfflineEvent(ChannelHandlerContext ctx, OfflinePackage offlinePackage) {
        super(ctx);
        this.ctx = ctx;
        this.offlinePackage = offlinePackage;
    }

}