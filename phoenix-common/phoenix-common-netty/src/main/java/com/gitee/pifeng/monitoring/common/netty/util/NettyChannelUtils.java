package com.gitee.pifeng.monitoring.common.netty.util;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

/**
 * <p>
 * netty 通道工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/31 14:52
 */
public final class NettyChannelUtils {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2023/3/31 14:53
     */
    private NettyChannelUtils() {
    }

    /**
     * <p>
     * 刷新所有排队的写入请求后，关闭指定的通道
     * </p>
     *
     * @param ch netty通道
     * @author 皮锋
     * @custom.date 2023/4/20 9:58
     */
    public static void closeOnFlush(Channel ch) {
        if (ch.isActive()) {
            ch.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

}