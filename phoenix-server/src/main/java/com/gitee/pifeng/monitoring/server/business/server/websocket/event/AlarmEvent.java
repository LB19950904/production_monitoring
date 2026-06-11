package com.gitee.pifeng.monitoring.server.business.server.websocket.event;

import com.gitee.pifeng.monitoring.common.dto.AlarmPackage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 * 告警消息事件
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/12 08:58
 */
@Getter
public class AlarmEvent extends ApplicationEvent {

    /**
     * Netty {@link Channel} 上下文，用于获取连接信息或向客户端回写消息
     */
    private final ChannelHandlerContext ctx;

    /**
     * 监控告警信息包
     */
    private final AlarmPackage alarmPackage;

    /**
     * <p>
     * 构造事件对象
     * </p>
     *
     * @param ctx          {@link Channel} 上下文
     * @param alarmPackage {@link AlarmPackage} 监控告警信息包
     * @author 皮锋
     * @custom.date 2026/3/3 14:25
     */
    public AlarmEvent(ChannelHandlerContext ctx, AlarmPackage alarmPackage) {
        super(ctx);
        this.ctx = ctx;
        this.alarmPackage = alarmPackage;
    }

}