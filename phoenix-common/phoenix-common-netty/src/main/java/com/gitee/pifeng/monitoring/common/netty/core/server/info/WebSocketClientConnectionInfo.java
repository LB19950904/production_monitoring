package com.gitee.pifeng.monitoring.common.netty.core.server.info;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Promise;
import lombok.Data;

/**
 * <p>
 * WebSocket 客户端连接信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/31 10:28
 */
@Data
public class WebSocketClientConnectionInfo {

    /**
     * netty 通道处理器上下文
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private ChannelHandlerContext channelHandlerContext;

    /**
     * 主机
     */
    private String host;

    /**
     * 端口号
     */
    private int port;

    /**
     * 等待客户端连接
     */
    @JsonIgnore
    @JSONField(serialize = false)
    private Promise<Channel> promise;

}
