package com.gitee.pifeng.monitoring.plug.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.domain.NetworkDevice;
import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.OId;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.v2c.Connection;
import com.gitee.pifeng.monitoring.common.util.snmp.v2c.NetworkDeviceUtils;
import com.gitee.pifeng.monitoring.plug.core.ClientPackageConstructor;
import com.gitee.pifeng.monitoring.plug.core.DataExchanger;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 发送网络设备信息线程
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/15 15:39
 */
@Slf4j
public class NetworkDeviceThread implements Runnable {

    /**
     * 客户端包构造器
     */
    private final ClientPackageConstructor clientPackageConstructor = ClientPackageConstructor.getInstance();

    /**
     * IP地址
     */
    private final String ip;

    /**
     * 社区字符串
     */
    private final String community;

    /**
     * <p>
     * 发送网络设备信息线程的构造方法
     * </p>
     *
     * @param ip        IP地址
     * @param community 社区字符串
     * @author 皮锋
     * @custom.date 2024/11/15 15:49
     */
    public NetworkDeviceThread(String ip, String community) {
        this.ip = ip;
        this.community = community;
    }

    /**
     * <p>
     * 构建+发送网络设备信息包
     * </p>
     *
     * @author 皮锋
     * @custom.date 2024/11/15 15:40
     */
    @Override
    public void run() {
        if (!DataExchanger.isReady()) {
            return;
        }
        // 计时器
        TimeInterval timer = DateUtil.timer();
        try {
            // 获取网络设备信息
            Connection connection = Connection.builder().ip(ip).community(community).build();
            NetworkDevice networkDevice = NetworkDeviceUtils.getNetworkDeviceInfo(connection, new OId());
            // 构建网络设备信息包
            NetworkDevicePackage networkDevicePackage = this.clientPackageConstructor.structureNetworkDevicePackage(networkDevice);
            // 发送请求
            WebSocketPackage requestPackage = new WebSocketPackage();
            requestPackage.setClassName(NetworkDevicePackage.class.getName());
            requestPackage.setPayload(networkDevicePackage);
            DataExchanger.sendMessage(requestPackage);
            // 改成用 WebSocket，弃用 HTTP
            // String result = Sender.send(UrlConstants.NETWORK_DEVICE_URL, networkDevicePackage.toJsonString());
            // if (log.isDebugEnabled()) {
            // log.debug("网络设备信息包响应消息：{}", result);
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
                log.warn("构建+发送网络设备信息包耗时：{}", betweenDay);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("构建+发送网络设备信息包耗时：{}", betweenDay);
                }
            }
        }
    }

}
