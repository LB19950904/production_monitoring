package com.gitee.pifeng.monitoring.plug.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.HeartbeatPackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.plug.core.ClientPackageConstructor;
import com.gitee.pifeng.monitoring.plug.core.DataExchanger;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 心跳线程
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月5日 下午2:50:46
 */
@Slf4j
public class HeartbeatThread implements Runnable {

    /**
     * 客户端包构造器
     */
    private final ClientPackageConstructor clientPackageConstructor = ClientPackageConstructor.getInstance();

    /**
     * <p>
     * 构建+发送心跳包
     * </p>
     *
     * @author 皮锋
     * @custom.date 2020/4/9 17:32
     */
    @Override
    public void run() {
        if (!DataExchanger.isReady()) {
            return;
        }
        // 计时器
        TimeInterval timer = DateUtil.timer();
        try {
            // 构建心跳数据包
            HeartbeatPackage heartbeatPackage = this.clientPackageConstructor.structureHeartbeatPackage();
            // 发送请求
            WebSocketPackage requestPackage = new WebSocketPackage();
            requestPackage.setClassName(HeartbeatPackage.class.getName());
            requestPackage.setPayload(heartbeatPackage);
            DataExchanger.sendMessage(requestPackage);
            // 改成用 WebSocket，弃用 HTTP
            // String result = Sender.send(UrlConstants.HEARTBEAT_URL, heartbeatPackage.toJsonString());
            // if (log.isDebugEnabled()) {
            // log.debug("心跳包响应消息：{}", result);
            // }
            // } catch (IOException e) {
            // log.error("IO异常！", e);
        } catch (NetException e) {
            log.error("获取网络信息异常！", e);
        } catch (Exception e) {
            log.error("其它异常！", e);
        } finally {
            // 时间差（毫秒）
            String betweenDay = timer.intervalPretty();
            // 临界值
            int criticalValue = 5;
            if (timer.intervalSecond() > criticalValue) {
                log.warn("构建+发送心跳包耗时：{}", betweenDay);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("构建+发送心跳包耗时：{}", betweenDay);
                }
            }
        }
    }

}
