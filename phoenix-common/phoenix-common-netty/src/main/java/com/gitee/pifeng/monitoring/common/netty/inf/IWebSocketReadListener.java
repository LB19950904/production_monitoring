package com.gitee.pifeng.monitoring.common.netty.inf;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <p>
 * WebSocket 读监听器接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/5/2 16:28
 */
public interface IWebSocketReadListener {

    /**
     * <p>
     * 读取从 WebSocket 发送过来的数据
     * </p>
     *
     * @param ctx   {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *              ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *              每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *              ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param frame WebSocket 数据帧
     * @author 皮锋
     * @custom.date 2023/5/2 16:30
     */
    void read(ChannelHandlerContext ctx, WebSocketFrame frame);

}
