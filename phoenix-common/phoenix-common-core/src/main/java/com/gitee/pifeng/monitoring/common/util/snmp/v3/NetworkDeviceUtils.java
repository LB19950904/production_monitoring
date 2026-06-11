package com.gitee.pifeng.monitoring.common.util.snmp.v3;

import cn.hutool.core.util.NumberUtil;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpOIdConstants;
import com.gitee.pifeng.monitoring.common.domain.NetworkDevice;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.OId;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.v3.Connection;
import com.gitee.pifeng.monitoring.common.threadpool.MonitoredThreadPoolExecutor;
import com.gitee.pifeng.monitoring.common.threadpool.ThreadPool;
import com.gitee.pifeng.monitoring.common.util.server.IpAddressUtils;
import com.gitee.pifeng.monitoring.common.util.snmp.SnmpIfService;
import com.gitee.pifeng.monitoring.common.util.snmp.SnmpSysService;
import com.gitee.pifeng.monitoring.common.util.snmp.SnmpVersionStrategy;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * SNMP v3 网络设备工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/4/9 21:45
 */
@Slf4j
public class NetworkDeviceUtils {

    /**
     * SNMP v3 版本策略
     */
    private static final SnmpVersionStrategy<Connection> STRATEGY = new SnmpV3Strategy();

    /**
     * SNMP系统信息服务（基于v3策略）
     */
    @Getter
    private static SnmpSysService<Connection> sysService = new SnmpSysService<>(STRATEGY);

    /**
     * SNMP网络接口信息服务（基于v3策略）
     */
    @Getter
    private static SnmpIfService<Connection> ifService = new SnmpIfService<>(STRATEGY);

    /**
     * 全局日志锁对象
     */
    private static final Object LOG_LOCK = new Object();

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/4/9 21:45
     */
    private NetworkDeviceUtils() {
    }

    /**
     * <p>
     * 获取网络设备信息
     * </p>
     *
     * @param connection {@link Connection} SNMP v3 连接请求参数
     * @param oId        {@link OId} MIB-II OID 请求参数
     * @return {@link NetworkDevice} 网络设备信息
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2026/4/9 21:45
     */
    public static NetworkDevice getNetworkDeviceInfo(Connection connection, OId oId)
            throws IOException {
        // 参数校验，给SNMP v3 连接请求参数设置默认值
        connection = STRATEGY.defaultIfBlank(connection);
        return NetworkDevice.builder()
                .connectionDomain(STRATEGY.buildConnectionDomain(connection))
                .sysDomain(sysService.getSysInfo(connection, oId.getSysInfoOid()))
                .ifDomain(ifService.getIfInfoEfficient(connection, oId.getIfInfoOid()))
                .build();
    }

    /**
     * <p>
     * 发现能通过SNMP v3 协议通信的网络设备
     * </p>
     *
     * @param ipAddress                IP地址
     * @param netmask                  子网掩码
     * @param snmpConnectionParameters 采集网络设备信息SNMP v3 协议连接参数列表
     * @return 能通过SNMP v3 协议通信的网络设备集合
     * @author 皮锋
     * @custom.date 2024/12/15 10:30
     */
    public static LinkedHashMap<String, LinkedHashMap<String, String>> discoverDevices(String ipAddress, String netmask, List<Connection> snmpConnectionParameters) {
        LinkedHashMap<String, LinkedHashMap<String, String>> result = Maps.newLinkedHashMap();
        try {
            // 参数校验
            if (StringUtils.isBlank(ipAddress) || StringUtils.isBlank(netmask) || CollectionUtils.isEmpty(snmpConnectionParameters)) {
                log.warn("SNMP v3 探测设备[参数无效：IP={}，子网掩码={}，连接参数={}]", ipAddress, netmask, snmpConnectionParameters);
                return result;
            }
            List<String> ips = IpAddressUtils.getAllIPsInRange(ipAddress, netmask);
            if (CollectionUtils.isEmpty(ips)) {
                log.warn("SNMP v3 探测设备[IP范围内无有效地址：IP={}，子网掩码={}]", ipAddress, netmask);
                return result;
            }
            int size = ips.size() * snmpConnectionParameters.size();
            if (size == 0) {
                return result;
            }
            // 获取IO密集型的线程池
            MonitoredThreadPoolExecutor monitoredThreadPoolExecutor = ThreadPool.getCommonIoIntensiveThreadPoolExecutor();
            // 线程安全的map
            ConcurrentMap<String, ConcurrentMap<String, String>> table = Maps.newConcurrentMap();
            // 原子计数器
            AtomicInteger counter = new AtomicInteger(0);
            // 同步器
            CountDownLatch latch = new CountDownLatch(size);
            // 循环，用多线程处理，加快处理速率
            for (String ip : ips) {
                for (Connection connectionParam : snmpConnectionParameters) {
                    monitoredThreadPoolExecutor.execute(() -> {
                        // 必须重新构建Connection对象，不能直接复用connectionParam：
                        // 1. connectionParam会被多个IP的任务线程共享，直接修改会导致线程安全问题
                        // 2. 每个任务需要独立的Connection对象，确保各自的IP不会相互覆盖
                        Connection connection = Connection.builder()
                                .ip(ip)
                                .securityName(connectionParam.getSecurityName())
                                .authProtocol(connectionParam.getAuthProtocol())
                                .authPassword(connectionParam.getAuthPassword())
                                .privProtocol(connectionParam.getPrivProtocol())
                                .privPassword(connectionParam.getPrivPassword())
                                .securityLevel(connectionParam.getSecurityLevel())
                                .build();
                        String paramKey = String.format("用户名=%s", connection.getSecurityName());
                        try {
                            String sysDescr = sysService.getInfoByOid(connection, SnmpOIdConstants.SYS_DESCR_OID);
                            if (StringUtils.isNotEmpty(sysDescr)) {
                                table.computeIfAbsent(ip, k -> Maps.newConcurrentMap()).put(paramKey, sysDescr);
                            }
                        } catch (Exception e) {
                            if (log.isDebugEnabled()) {
                                log.debug("SNMP v3 探测设备失败[IP={}，{}]：{}", ip, paramKey, e.getMessage());
                            }
                        } finally {
                            synchronized (LOG_LOCK) {
                                // 完成数
                                int completedCount = counter.incrementAndGet();
                                log.info("SNMP v3 探测设备[IP={}，{}，进度：{}/{} ({})]", ip, paramKey, completedCount, size,
                                        // 格式化成两位小数
                                        NumberUtil.formatPercent(completedCount / (double) size, 2));
                            }
                            // 任务完成时递减计数器
                            latch.countDown();
                        }
                    });
                }
            }
            // 等待所有任务完成或超时
            boolean isCompleted = latch.await(30, TimeUnit.MINUTES);
            if (!isCompleted) {
                log.warn("SNMP v3 探测设备超时(30分钟)，仍有 {} 个任务未完成。", latch.getCount());
            }
            // 按原始 IP 顺序整理结果
            for (String ip : ips) {
                ConcurrentMap<String, String> resultMap = table.get(ip);
                if (MapUtils.isNotEmpty(resultMap)) {
                    LinkedHashMap<String, String> orderedRow = Maps.newLinkedHashMap();
                    for (Connection connectionParam : snmpConnectionParameters) {
                        String paramKey = String.format("用户名=%s", connectionParam.getSecurityName());
                        String sysDescr = resultMap.get(paramKey);
                        if (StringUtils.isNotEmpty(sysDescr)) {
                            orderedRow.put(paramKey, sysDescr);
                        }
                    }
                    if (MapUtils.isNotEmpty(orderedRow)) {
                        result.put(ip, orderedRow);
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("SNMP v3 探测设备被中断：{}", e.getMessage());
        } catch (Exception e) {
            log.error("SNMP v3 探测设备失败: {}", e.getMessage());
        }
        return result;
    }

}
