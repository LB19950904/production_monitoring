package com.gitee.pifeng.monitoring.common.util.snmp.v3;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.alibaba.fastjson.JSON;
import com.gitee.pifeng.monitoring.common.domain.NetworkDevice;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.OId;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.v3.Connection;
import com.gitee.pifeng.monitoring.common.util.server.NetUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * <p>
 * 测试网络设备工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/07 20:46
 */
@Slf4j
public class NetworkDeviceUtilsTest {

    /**
     * <p>
     * 测试获取网络设备信息
     * </p>
     *
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/07 20:47
     */
    @Test
    public void testGetNetworkDeviceInfo() throws IOException {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        // SNMP连接请求参数
        Connection connection = Connection.builder().ip("10.43.1.254").build();
        // MIB-II OID 请求参数
        OId oId = new OId();
        NetworkDevice networkDeviceInfo = NetworkDeviceUtils.getNetworkDeviceInfo(connection, oId);
        assertNotNull(networkDeviceInfo);
        log.info(networkDeviceInfo.toJsonString());
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        log.info("耗时：{}", betweenDay);
    }

    /**
     * <p>
     * 测试 根据指定的OID获取信息
     * </p>
     *
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/14 10:48
     */
    @Test
    public void testGetInfoByOid() throws IOException {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        // SNMP连接请求参数
        Connection connection = Connection.builder().ip("10.43.1.254").build();
        // 网络接口信息 MIB-II OID 请求参数
        String oid = "1.3.6.1.2.1.1.1.0";
        String info = NetworkDeviceUtils.getSysService().getInfoByOid(connection, oid);
        assertTrue(StringUtils.isNotBlank(info));
        log.info(info);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        log.info("耗时：{}", betweenDay);
    }

    /**
     * <p>
     * 测试 发现能通过SNMP协议通信的网络设备
     * </p>
     *
     * @author 皮锋
     * @custom.date 2024/11/14 12:40
     */
    @Test
    public void testDiscoverDevices() {
        String ipAddress = NetUtils.getLocalIp();
        String netmask = NetUtils.getLocalSubnetMask();
        Connection connection = Connection.builder().ip("10.43.1.254").build();
        List<Connection> snmpConnectionParameters = Collections.singletonList(connection);
        Map<String, LinkedHashMap<String, String>> map = NetworkDeviceUtils.discoverDevices(ipAddress, netmask, snmpConnectionParameters);
        assertTrue(MapUtils.isNotEmpty(map));
        log.info(JSON.toJSONString(map));
    }

}
