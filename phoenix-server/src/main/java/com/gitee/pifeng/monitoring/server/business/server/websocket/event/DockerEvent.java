package com.gitee.pifeng.monitoring.server.business.server.websocket.event;

import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * <p>
 * docker消息事件
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/12 08:58
 */
@Getter
public class DockerEvent extends ApplicationEvent {

    /**
     * Netty {@link Channel} 上下文，用于获取连接信息或向客户端回写消息
     */
    private final ChannelHandlerContext ctx;

    /**
     * docker信息包
     */
    private final DockerPackage dockerPackage;

    /**
     * <p>
     * 构造事件对象
     * </p>
     *
     * @param ctx           {@link Channel} 上下文
     * @param dockerPackage {@link DockerPackage} docker信息包
     * @author 皮锋
     * @custom.date 2026/3/3 14:25
     */
    public DockerEvent(ChannelHandlerContext ctx, DockerPackage dockerPackage) {
        super(ctx);
        this.ctx = ctx;
        this.dockerPackage = dockerPackage;
    }

}