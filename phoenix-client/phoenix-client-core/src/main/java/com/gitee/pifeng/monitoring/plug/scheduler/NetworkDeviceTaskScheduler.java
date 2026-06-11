package com.gitee.pifeng.monitoring.plug.scheduler;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gitee.pifeng.monitoring.common.util.DirUtils;
import com.gitee.pifeng.monitoring.common.util.server.NetUtils;
import com.gitee.pifeng.monitoring.common.util.snmp.v2c.NetworkDeviceUtils;
import com.gitee.pifeng.monitoring.plug.core.ConfigLoader;
import com.gitee.pifeng.monitoring.plug.core.ThreadPoolAcquirer;
import com.gitee.pifeng.monitoring.plug.thread.NetworkDeviceThread;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 * 发送网络设备信息任务调度器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/15 15:37
 */
@Slf4j
public class NetworkDeviceTaskScheduler {

    /**
     * 文件读写锁
     * ReentrantReadWriteLock是Java中实现读写锁的一个类，它支持一个读锁和一个写锁。
     * 读锁可以由多个线程同时持有，而写锁是排他的，一次只能由一个线程持有。
     * 这种锁非常适合读多写少的场景，因为它允许多个线程同时进行读操作，从而提高了程序的并发性能。
     */
    private static final ReentrantReadWriteLock FILE_READ_WRITE_LOCK = new ReentrantReadWriteLock();

    /**
     * 通过SNMP协议发现的网络设备记录文件（相对路径 + 文件名）
     */
    private static final String RELATIVE_PATH_FILENAME = "liblog4phoenix" + File.separator + "data" + File.separator + "networkDevice.json";

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2024/11/15 15:38
     */
    private NetworkDeviceTaskScheduler() {
    }

    /**
     * <p>
     * 如果监控配置文件中配置了发送网络设备信息，则延迟50秒启动定时任务，定时发现网络设备、发送网络设备信息包，
     * 定时任务的执行频率一般为监控配置文件中配置的网络设备信息包发送频率，如果监控配置文件中没有配置网络设备信息包的发送频率，
     * 则由类{@link ConfigLoader}提供默认的发送网络设备信息频率。
     * </p>
     *
     * @author 皮锋
     * @custom.date 2024/11/19 16:32
     */
    public static void run() {
        // 是否发送网络设备信息
        boolean networkDeviceInfoEnable = ConfigLoader.getMonitoringProperties().getNetworkDeviceInfo().getEnable();
        if (networkDeviceInfoEnable) {
            // 发送网络设备的频率
            long rate = ConfigLoader.getMonitoringProperties().getNetworkDeviceInfo().getRate();

            // 发现设备：发现能通过SNMP协议通信的网络设备(一天一次：1 天 = 24 小时 = 24 × 3600 = 86400 秒)
            ThreadPoolAcquirer.getNetworkDeviceScheduledThreadPoolExecutor().scheduleWithFixedDelay(() -> {
                try {
                    // 采集网络设备信息snmp协议连接社区字符串列表
                    List<String> snmpConnectionCommunities = ConfigLoader.getMonitoringProperties().getNetworkDeviceInfo().getSnmpConnectionCommunities();
                    // 发现能通过SNMP协议通信的网络设备
                    String deviceJson = discoverDevices(snmpConnectionCommunities);
                    // 把通过SNMP协议发现的网络设备信息写入文件
                    writeNetworkDeviceFile(deviceJson);
                } catch (Exception ignored) {
                }
            }, 50, 86400, TimeUnit.SECONDS);

            // 发送设备数据：为每一个网络设备定时发送网络设备信息
            ThreadPoolAcquirer.getNetworkDeviceScheduledThreadPoolExecutor().scheduleWithFixedDelay(() -> {
                try {
                    // 读取通过SNMP协议发现的网络设备记录文件的文件内容，并转成Map
                    Map<String, LinkedHashMap<String, String>> devices = readNetworkDeviceFile();
                    if (MapUtils.isNotEmpty(devices)) {
                        for (Map.Entry<String, LinkedHashMap<String, String>> entry : devices.entrySet()) {
                            String ip = entry.getKey();
                            LinkedHashMap<String, String> communityToSysDescr = entry.getValue();
                            if (MapUtils.isNotEmpty(communityToSysDescr)) {
                                Map.Entry<String, String> firstEntry = communityToSysDescr.entrySet().stream().findFirst().orElse(null);
                                if (firstEntry != null) {
                                    String community = firstEntry.getKey();
                                    NetworkDeviceThread thread = new NetworkDeviceThread(ip, community);
                                    ThreadPoolAcquirer.getNetworkDeviceScheduledThreadPoolExecutor().submit(thread);
                                }
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }, 50, rate, TimeUnit.SECONDS);
        }
    }

    /**
     * <p>
     * 把通过SNMP协议发现的网络设备信息写入文件
     * </p>
     *
     * @param deviceJson 通过SNMP协议发现的网络设备信息
     * @author 皮锋
     * @custom.date 2024/11/26 9:16
     */
    private static void writeNetworkDeviceFile(String deviceJson) {
        if (StringUtils.isBlank(deviceJson)) {
            return;
        }
        // 获取通过SNMP协议发现的网络设备记录文件路径+文件名
        String pathname = DirUtils.getAbsolutePathByRelativePath(RELATIVE_PATH_FILENAME);
        // 获取写锁
        ReentrantReadWriteLock.WriteLock writeLock = FILE_READ_WRITE_LOCK.writeLock();
        // 加锁
        writeLock.lock();
        try {
            FileWriter writer = new FileWriter(pathname);
            writer.write(deviceJson, false);
        } catch (Exception ignored) {
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * <p>
     * 读取通过SNMP协议发现的网络设备记录文件的文件内容，并转成列表
     * </p>
     *
     * @return 通过SNMP协议发现的网络设备
     * @author 皮锋
     * @custom.date 2024/11/26 9:19
     */
    private static Map<String, LinkedHashMap<String, String>> readNetworkDeviceFile() {
        // 获取通过SNMP协议发现的网络设备记录文件路径+文件名
        String pathname = DirUtils.getAbsolutePathByRelativePath(RELATIVE_PATH_FILENAME);
        // 获取读锁
        ReentrantReadWriteLock.ReadLock readLock = FILE_READ_WRITE_LOCK.readLock();
        if (readLock.tryLock()) {
            try {
                FileReader fileReader = new FileReader(pathname);
                String strJson = fileReader.readString();
                if (StringUtils.isNotBlank(strJson)) {
                    return JSONUtil.toBean(strJson, new TypeReference<LinkedHashMap<String, LinkedHashMap<String, String>>>() {
                    }, true);
                }
            } catch (Exception ignored) {
            } finally {
                readLock.unlock();
            }
        }
        return Maps.newLinkedHashMap();
    }

    /**
     * <p>
     * 发现能通过SNMP协议通信的网络设备
     * </p>
     *
     * @param snmpConnectionCommunities 采集网络设备信息snmp协议连接社区字符串列表
     * @return 通过SNMP协议发现的网络设备信息（JSON字符串格式）
     * @author 皮锋
     * @custom.date 2024/11/15 16:38
     */
    private static String discoverDevices(List<String> snmpConnectionCommunities) {
        log.info("开始搜索能通过SNMP协议通信的网络设备，请等待搜索完成！");
        Map<String, LinkedHashMap<String, String>> devices = NetworkDeviceUtils.discoverDevices(NetUtils.getLocalIp(), NetUtils.getLocalSubnetMask(), snmpConnectionCommunities);
        if (MapUtils.isNotEmpty(devices)) {
            return JSONUtil.formatJsonStr(JSON.toJSONString(devices, SerializerFeature.WriteMapNullValue));
        }
        return null;
    }

}
