package com.gitee.pifeng.monitoring.common.netty.inf;

import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketServerInitializer;
import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketSimpleChannelInboundHandler;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetInf;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetMethod;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * <p>
 * WebSocket 帧处理器接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/4/3 8:58
 * @see WebSocketServerInitializer
 */
@TargetInf
public interface IWebSocketFrameHandler {

    /**
     * <p>
     * 用户事件被触发时调用
     * </p>
     *
     * @param simpleChannelInboundHandler {@link WebSocketSimpleChannelInboundHandler} WebSocket 简单通道入站消息处理器
     * @param ctx                         {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *                                    ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *                                    每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *                                    ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param evt                         事件
     * @throws Exception 异常
     * @author 皮锋
     * @custom.date 2023/3/29 17:07
     */
    @TargetMethod
    void userEventTriggered(WebSocketSimpleChannelInboundHandler simpleChannelInboundHandler, ChannelHandlerContext ctx, Object evt) throws Exception;

    /**
     * <p>
     * 当客户端有消息发过来时方法触发
     * </p>
     *
     * @param simpleChannelInboundHandler {@link WebSocketSimpleChannelInboundHandler} WebSocket 简单通道入站消息处理器
     * @param ctx                         {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *                                    ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *                                    每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *                                    ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param frame                       {@link WebSocketFrame} Websocket 数据帧
     * @throws Exception 异常
     * @author 皮锋
     * @custom.date 2023/3/29 17:11
     */
    @TargetMethod
    void channelRead0(WebSocketSimpleChannelInboundHandler simpleChannelInboundHandler, ChannelHandlerContext ctx, WebSocketFrame frame) throws Exception;

}
