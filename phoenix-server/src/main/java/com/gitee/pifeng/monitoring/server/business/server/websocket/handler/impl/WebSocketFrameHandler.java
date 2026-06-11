package com.gitee.pifeng.monitoring.server.business.server.websocket.handler.impl;

import com.gitee.pifeng.monitoring.common.netty.core.server.WebSocketSimpleChannelInboundHandler;
import com.gitee.pifeng.monitoring.common.netty.inf.IWebSocketFrameHandler;
import com.gitee.pifeng.monitoring.server.business.server.websocket.handler.IWebSocketBusinessHandler;
import com.google.common.collect.Maps;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler.HandshakeComplete;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * WebSocket 业务路由分发器
 * </p>
 * 在握手完成后，根据 URI 路径末段（如 {@code /arthas}）将请求路由至对应的 {@link IWebSocketBusinessHandler} 实现类处理，实现网络层与业务逻辑解耦，
 * 新增业务只需实现 {@link IWebSocketBusinessHandler} 并注册为 Spring Bean，无需修改本类。
 *
 * @author 皮锋
 * @custom.date 2023/3/29 14:38
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "ws.server.enable", havingValue = "true")
public class WebSocketFrameHandler implements IWebSocketFrameHandler {

    /**
     * 当前 WebSocket 连接的业务类型（如 "monitoring", "arthas"）
     */
    public static final AttributeKey<String> WS_BUSINESS_TYPE = AttributeKey.valueOf("ws_business_type");

    /**
     * 业务处理器映射表：key 为业务类型（小写），value 为对应的处理器实例
     */
    private final Map<String, IWebSocketBusinessHandler> businessHandlers;

    /**
     * <p>
     * 构造函数：通过 Spring 自动注入所有 {@link IWebSocketBusinessHandler} 实现类，并构建业务类型到处理器的映射关系
     * </p>
     *
     * @param handlers Spring 容器中所有已注册的 {@link IWebSocketBusinessHandler} Bean 列表
     * @author 皮锋
     * @custom.date 2026/2/8 01:58
     */
    public WebSocketFrameHandler(@Autowired List<IWebSocketBusinessHandler> handlers) {
        this.businessHandlers = Maps.newHashMap();
        for (IWebSocketBusinessHandler handler : handlers) {
            // 将每个处理器按其声明的业务类型注册到路由表（统一转为小写以支持大小写不敏感匹配）
            this.businessHandlers.put(handler.businessType().toLowerCase(), handler);
        }
    }

    /**
     * <p>
     * 处理 WebSocket 用户事件
     * </p>
     * 当检测到 {@link HandshakeComplete} 事件时，解析请求 URI 的路径末段作为业务类型，并委托给对应的 {@link IWebSocketBusinessHandler} 进行后续处理
     *
     * @param simpleChannelInboundHandler {@link WebSocketSimpleChannelInboundHandler} WebSocket 简单通道入站消息处理器
     * @param ctx                         {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *                                    ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *                                    每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *                                    ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param evt                         触发的用户事件对象
     * @author 皮锋
     * @custom.date 2023/3/29 17:07
     */
    @Override
    public void userEventTriggered(WebSocketSimpleChannelInboundHandler simpleChannelInboundHandler, ChannelHandlerContext ctx, Object evt) {
        if (evt instanceof HandshakeComplete) {
            HandshakeComplete handshake = (HandshakeComplete) evt;
            // http request uri
            String uri = handshake.requestUri();
            // log.info("WebSocket 握手完成，URI：{}", uri);
            // 解析 URI 结构（路径 + 查询参数）
            UriComponents uriComponents = UriComponentsBuilder.fromUriString(uri).build();
            List<String> pathSegments = uriComponents.getPathSegments();
            // 若路径为空，交由后续 Handler 处理
            if (CollectionUtils.isEmpty(pathSegments)) {
                ctx.fireUserEventTriggered(evt);
                return;
            }
            // WebSocket业务类型（从 URI 路径中取出最后一个路径段（path segment），作为业务类型标识（business type））
            String webSocketBusinessType = pathSegments.get(pathSegments.size() - 1).toLowerCase();
            // 根据业务类型查找对应处理器
            IWebSocketBusinessHandler handler = this.businessHandlers.get(webSocketBusinessType);
            if (handler != null) {
                // 委托具体业务处理器执行握手后逻辑
                handler.handle(simpleChannelInboundHandler, ctx, handshake, uriComponents);
                // 存储业务类型到 Channel 属性（Arthas 也会存，但后 Arthas 续会被移除 handler）
                ctx.channel().attr(WS_BUSINESS_TYPE).set(webSocketBusinessType);
            } else {
                // 未知业务类型，记录警告（实际关闭连接由各业务处理器或上层控制）
                log.warn("不支持的 WebSocket 业务类型：{}, URI：{}", webSocketBusinessType, uri);
                // 主动关闭连接
                ctx.writeAndFlush(new CloseWebSocketFrame(1008, "不支持的业务类型：" + webSocketBusinessType));
            }
        } else {
            // 非握手事件，继续向 Pipeline 下游传播
            ctx.fireUserEventTriggered(evt);
        }
    }

    /**
     * <p>
     * 处理客户端发送的 WebSocket 数据帧
     * </p>
     *
     * @param simpleChannelInboundHandler {@link WebSocketSimpleChannelInboundHandler} WebSocket 简单通道入站消息处理器
     * @param ctx                         {@link ChannelHandlerContext} ChannelHandler 上下文，管理它所关联的 {@link ChannelHandler}：<br>
     *                                    ChannelHandlerContext 里就包含着 ChannelHandler 中的上下文信息，
     *                                    每一个 ChannelHandler 被添加到 {@link ChannelPipeline} 中都会创建一个与其对应的 ChannelHandlerContext。
     *                                    ChannelHandlerContext 的功能就是用来管理它所关联的 ChannelHandler 和与在同一个 ChannelPipeline 中 ChannelHandler 的交互。
     * @param frame                       {@link WebSocketFrame} 接收到的 Websocket 数据帧
     * @author 皮锋
     * @custom.date 2023/3/29 17:11
     */
    @Override
    public void channelRead0(WebSocketSimpleChannelInboundHandler simpleChannelInboundHandler, ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            // 从 Channel 属性中获取业务类型（在握手完成时由 userEventTriggered 设置）
            String businessType = ctx.channel().attr(WS_BUSINESS_TYPE).get();
            if (StringUtils.isNotBlank(businessType)) {
                // 根据业务类型查找对应的业务处理器
                IWebSocketBusinessHandler handler = this.businessHandlers.get(businessType);
                // 处理客户端发送的 WebSocket 文本消息
                handler.onMessageReceived(ctx, textFrame);
                // 消费完毕，不再传播
                return;
            }
        }
        // 二进制帧等其他类型可在此扩展，但建议由具体业务处理器处理

        // 继续向下游传播，让业务 Handler 处理实际逻辑
        // retain() 防止被 SimpleChannelInboundHandler 的 autoRelease 机制双重释放
        ctx.fireChannelRead(frame.retain());
    }

}
