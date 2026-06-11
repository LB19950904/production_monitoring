package com.gitee.pifeng.monitoring.common.util.snmp;

import com.gitee.pifeng.monitoring.common.constant.CommProtocolTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.IfAdminStatusEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.IfOperStatusEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.IfTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.IfDomain;
import com.gitee.pifeng.monitoring.common.exception.MonitoringUniversalException;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.IfInfoOId;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.SysInfoOId;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.PDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.gitee.pifeng.monitoring.common.constant.snmp.SnmpOIdConstants.*;

/**
 * <p>
 * SNMP公共工具类，提供v2c和v3共用的静态工具方法。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/4/9 22:00
 */
@Slf4j
public final class SnmpCommonUtils {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    private SnmpCommonUtils() {
    }

    /**
     * 全局缓存：每个设备上一次采集的网络接口字节数快照，键为设备地址
     */
    private static final Map<String, IfOctetsSnapshot> IF_OCTETS_SNAPSHOT_CACHE = new ConcurrentHashMap<>();

    /**
     * <p>
     * 网络接口字节数快照，用于缓存某一时刻各网络接口的收发字节数，配合两次调度周期间的差值计算实时速率
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/4/14 20:00
     */
    @AllArgsConstructor
    private static final class IfOctetsSnapshot {

        /**
         * 采集时间戳（毫秒）
         */
        final long timestamp;

        /**
         * 网络接口字节数表，行key为接口索引（ifIndex），列key为OID（ifInOctets/ifOutOctets），值为字节数
         */
        final Table<String, String, Long> octetsTable;

    }

    /**
     * <p>
     * 安静地关闭Snmp对象，忽略关闭过程中的异常
     * </p>
     *
     * @param snmp {@link Snmp} SNMP对象
     * @author 皮锋
     * @custom.date 2026/4/10 10:00
     */
    public static void closeQuietly(Snmp snmp) {
        if (snmp != null) {
            try {
                snmp.close();
            } catch (IOException e) {
                log.warn("关闭SNMP对象时发生异常：{}", e.getMessage());
            }
        }
    }

    /**
     * <p>
     * 安静地关闭TransportMapping对象，忽略关闭过程中的异常
     * </p>
     *
     * @param transport {@link TransportMapping} 传输映射
     * @author 皮锋
     * @custom.date 2026/4/10 10:00
     */
    public static void closeQuietly(TransportMapping<? extends Address> transport) {
        if (transport != null) {
            try {
                transport.close();
            } catch (IOException e) {
                log.warn("关闭传输映射时发生异常：{}", e.getMessage());
            }
        }
    }

    /**
     * <p>
     * 校验IP地址是否为空，不为空则返回IP地址，否则抛出异常
     * </p>
     *
     * @param ip IP地址
     * @return IP地址
     * @author 皮锋
     * @custom.date 2024/11/07 22:42
     */
    public static String verifyIpAndGet(String ip) {
        if (StringUtils.isBlank(ip)) {
            throw new MonitoringUniversalException("IP地址不能为空！");
        }
        return ip;
    }

    /**
     * <p>
     * 根据指定的通信协议类型（TCP或UDP）创建一个SNMP传输映射，并启动监听
     * </p>
     *
     * @param protocol {@link CommProtocolTypeEnums} 通信协议类型
     * @return {@link TransportMapping}
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/07 22:11
     */
    public static TransportMapping<? extends Address> createTransportMappingAndListen(CommProtocolTypeEnums protocol) throws IOException {
        // 创建传输映射
        TransportMapping<? extends Address> transport;
        if (CommProtocolTypeEnums.TCP.equals(protocol)) {
            transport = new DefaultTcpTransportMapping();
        } else {
            transport = new DefaultUdpTransportMapping();
        }
        // 启动监听
        transport.listen();
        return transport;
    }

    /**
     * <p>
     * 校验和获取PDU
     * </p>
     *
     * @param responseEvent {@link ResponseEvent}
     * @return {@link PDU} 协议数据单元
     * @author 皮锋
     * @custom.date 2024/11/07 22:58
     */
    public static PDU verifyResponseAndGet(ResponseEvent responseEvent) {
        if (responseEvent == null || responseEvent.getResponse() == null) {
            throw new MonitoringUniversalException("没有接收到响应协议数据单元（PDU）！");
        }
        PDU pdu = responseEvent.getResponse();
        if (pdu.getErrorStatus() != PDU.noError) {
            throw new MonitoringUniversalException("接收响应协议数据单元（PDU）错误：" + pdu.getErrorStatusText());
        }
        return pdu;
    }

    /**
     * <p>
     * 找到与请求的OID匹配的{@link VariableBinding}，并提取其值，转成整数返回
     * </p>
     *
     * @param oid MIB-II OID
     * @param pdu {@link PDU} 协议数据单元
     * @return {@link Variable}整数
     * @author 皮锋
     * @custom.date 2024/11/10 10:12
     */
    public static Integer getIntegerVariable(String oid, PDU pdu) {
        for (VariableBinding vb : pdu.getVariableBindings()) {
            String vbOid = vb.getOid().toString();
            if (StringUtils.equals(oid, vbOid)) {
                String vbVariable = vb.getVariable().toString();
                if (StringUtils.isNotEmpty(vbVariable)) {
                    try {
                        return Integer.parseInt(vbVariable);
                    } catch (NumberFormatException e) {
                        log.warn("OID[{}]的值[{}]无法转换为整数！", oid, vbVariable);
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * <p>
     * 找到与请求的OID匹配的{@link VariableBinding}，并提取其值，转成字符串返回
     * </p>
     *
     * @param oid MIB-II OID
     * @param pdu {@link PDU} 协议数据单元
     * @return {@link Variable}字符串
     * @author 皮锋
     * @custom.date 2024/11/10 10:00
     */
    public static String getStringVariable(String oid, PDU pdu) {
        for (VariableBinding vb : pdu.getVariableBindings()) {
            String vbOid = vb.getOid().toString();
            if (StringUtils.equals(oid, vbOid)) {
                return vb.getVariable().toString();
            }
        }
        return null;
    }

    /**
     * <p>
     * 找到与请求的OID匹配的{@link VariableBinding}，并提取其值，转成长整数返回
     * </p>
     *
     * @param oid MIB-II OID
     * @param pdu {@link PDU} 协议数据单元
     * @return {@link Variable}长整数
     * @author 皮锋
     * @custom.date 2024/11/10 10:00
     */
    public static Long getLongVariable(String oid, PDU pdu) {
        for (VariableBinding vb : pdu.getVariableBindings()) {
            String vbOid = vb.getOid().toString();
            if (StringUtils.equals(oid, vbOid)) {
                String vbVariable = vb.getVariable().toString();
                if (StringUtils.isNotEmpty(vbVariable)) {
                    try {
                        return Long.parseLong(vbVariable);
                    } catch (NumberFormatException e) {
                        log.warn("OID[{}]的值[{}]无法转换为长整数！", oid, vbVariable);
                        return null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * <p>
     * 将 sysServices 数值转换为具体的含义
     * </p>
     *
     * @param sysServices sysServices 数值
     * @return sysServices 具体含义
     * @author 皮锋
     * @custom.date 2025-3-10 17:27
     */
    public static String convertSysServices(int sysServices) {
        StringBuilder services = new StringBuilder();
        if ((sysServices & 1) != 0) {
            if (services.length() > 0) {
                services.append("、");
            }
            services.append("网络层服务(Network Layer Services)");
        }
        if ((sysServices & 2) != 0) {
            if (services.length() > 0) {
                services.append("、");
            }
            services.append("应用层网关服务(Application Layer Gateway Services)");
        }
        if ((sysServices & 4) != 0) {
            if (services.length() > 0) {
                services.append("、");
            }
            services.append("路由器服务(Routing Services)");
        }
        if ((sysServices & 8) != 0) {
            if (services.length() > 0) {
                services.append("、");
            }
            services.append("时间同步服务(Time Sync Services)");
        }
        if ((sysServices & 16) != 0) {
            if (services.length() > 0) {
                services.append("、");
            }
            services.append("名称解析服务(Name Resolution Services)");
        }
        if ((sysServices & 32) != 0) {
            if (services.length() > 0) {
                services.append("、");
            }
            services.append("安全过滤服务(Security Filtering Services)");
        }
        if (services.length() == 0) {
            services.append("其他(Other)");
        }
        return services.toString();
    }

    /**
     * <p>
     * 参数校验，给系统信息 MIB-II OID 请求参数设置默认值
     * </p>
     *
     * @param sysInfoOid {@link SysInfoOId} 系统信息 MIB-II OID 请求参数
     * @return {@link SysInfoOId} 系统信息 MIB-II OID 请求参数
     * @author 皮锋
     * @custom.date 2024/11/08 21:32
     */
    public static SysInfoOId defaultIfBlank(SysInfoOId sysInfoOid) {
        if (sysInfoOid == null) {
            sysInfoOid = new SysInfoOId();
        }
        if (StringUtils.isBlank(sysInfoOid.getSysDescrOid())) {
            sysInfoOid.setSysDescrOid(SYS_DESCR_OID);
        }
        if (StringUtils.isBlank(sysInfoOid.getSysUpTimeOid())) {
            sysInfoOid.setSysUpTimeOid(SYS_UP_TIME_OID);
        }
        if (StringUtils.isBlank(sysInfoOid.getSysContactOid())) {
            sysInfoOid.setSysContactOid(SYS_CONTACT_OID);
        }
        if (StringUtils.isBlank(sysInfoOid.getSysNameOid())) {
            sysInfoOid.setSysNameOid(SYS_NAME_OID);
        }
        if (StringUtils.isBlank(sysInfoOid.getSysLocationOid())) {
            sysInfoOid.setSysLocationOid(SYS_LOCATION_OID);
        }
        if (StringUtils.isBlank(sysInfoOid.getSysServicesOid())) {
            sysInfoOid.setSysServicesOid(SYS_SERVICES_OID);
        }
        return sysInfoOid;
    }

    /**
     * <p>
     * 参数校验，给网络接口信息 MIB-II OID 请求参数设置默认值
     * </p>
     *
     * @param ifInfoOid {@link IfInfoOId} 网络接口信息 MIB-II OID 请求参数
     * @return {@link IfInfoOId} 网络接口信息 MIB-II OID 请求参数
     * @author 皮锋
     * @custom.date 2024/11/09 10:51
     */
    public static IfInfoOId defaultIfBlank(IfInfoOId ifInfoOid) {
        if (ifInfoOid == null) {
            ifInfoOid = new IfInfoOId();
        }
        if (StringUtils.isBlank(ifInfoOid.getIfNumberOid())) {
            ifInfoOid.setIfNumberOid(IF_NUMBER_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfIndexOid())) {
            ifInfoOid.setIfIndexOid(IF_INDEX_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfDescrOid())) {
            ifInfoOid.setIfDescrOid(IF_DESCR_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfTypeOid())) {
            ifInfoOid.setIfTypeOid(IF_TYPE_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfMtuOid())) {
            ifInfoOid.setIfMtuOid(IF_MTU_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfSpeedOid())) {
            ifInfoOid.setIfSpeedOid(IF_SPEED_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfPhysAddressOid())) {
            ifInfoOid.setIfPhysAddressOid(IF_PHYS_ADDRESS_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfAdminStatusOid())) {
            ifInfoOid.setIfAdminStatusOid(IF_ADMIN_STATUS_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfOperStatusOid())) {
            ifInfoOid.setIfOperStatusOid(IF_OPER_STATUS_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfInOctetsOid())) {
            ifInfoOid.setIfInOctetsOid(IF_IN_OCTETS_OID);
        }
        if (StringUtils.isBlank(ifInfoOid.getIfOutOctetsOid())) {
            ifInfoOid.setIfOutOctetsOid(IF_OUT_OCTETS_OID);
        }
        return ifInfoOid;
    }

    /**
     * <p>
     * 获取每一个网络接口的详细信息
     * </p>
     *
     * @param snmp       {@link Snmp}
     * @param target     {@link Target}
     * @param ifInfoOid  网络接口信息 MIB-II OID 请求参数
     * @param ifIndexes  网络接口的索引号列表
     * @param pduFactory {@link PDUFactory} PDU工厂，用于适配不同SNMP版本的PDU创建
     * @return 每一个网络接口的详细信息
     * @author 皮锋
     * @custom.date 2024/11/13 11:36
     */
    public static List<IfDomain.IfInfoDomain> getIfInfoDomain(Snmp snmp, Target target, IfInfoOId ifInfoOid, List<String> ifIndexes, PDUFactory pduFactory) {
        // 创建TreeUtils对象
        TreeUtils treeUtils = new TreeUtils(snmp, pduFactory);
        // 使用TreeUtils进行批量查询
        List<TreeEvent> events = Lists.newArrayList();
        events.addAll(treeUtils.getSubtree(target, new OID(ifInfoOid.getIfDescrOid())));
        events.addAll(treeUtils.getSubtree(target, new OID(ifInfoOid.getIfTypeOid())));
        events.addAll(treeUtils.getSubtree(target, new OID(ifInfoOid.getIfMtuOid())));
        events.addAll(treeUtils.getSubtree(target, new OID(ifInfoOid.getIfSpeedOid())));
        events.addAll(treeUtils.getSubtree(target, new OID(ifInfoOid.getIfPhysAddressOid())));
        events.addAll(treeUtils.getSubtree(target, new OID(ifInfoOid.getIfAdminStatusOid())));
        events.addAll(treeUtils.getSubtree(target, new OID(ifInfoOid.getIfOperStatusOid())));
        events.addAll(treeUtils.getSubtree(target, new OID(ifInfoOid.getIfInOctetsOid())));
        events.addAll(treeUtils.getSubtree(target, new OID(ifInfoOid.getIfOutOctetsOid())));
        // 记录本次采样时间
        long currentTime = System.currentTimeMillis();
        // 处理查询结果
        Table<String, String, Variable> table = HashBasedTable.create();
        for (TreeEvent event : events) {
            if (event.isError()) {
                continue;
            }
            VariableBinding[] vars = event.getVariableBindings();
            if (vars == null) {
                continue;
            }
            for (VariableBinding var : vars) {
                String oid = var.getOid().toString();
                Variable variable = var.getVariable();
                if (variable == null) {
                    continue;
                }
                for (String ifIndex : ifIndexes) {
                    if (StringUtils.equals(oid, ifInfoOid.getIfDescrOid() + "." + ifIndex)) {
                        table.put(ifIndex, ifInfoOid.getIfDescrOid(), variable);
                    } else if (StringUtils.equals(oid, ifInfoOid.getIfTypeOid() + "." + ifIndex)) {
                        table.put(ifIndex, ifInfoOid.getIfTypeOid(), variable);
                    } else if (StringUtils.equals(oid, ifInfoOid.getIfMtuOid() + "." + ifIndex)) {
                        table.put(ifIndex, ifInfoOid.getIfMtuOid(), variable);
                    } else if (StringUtils.equals(oid, ifInfoOid.getIfSpeedOid() + "." + ifIndex)) {
                        table.put(ifIndex, ifInfoOid.getIfSpeedOid(), variable);
                    } else if (StringUtils.equals(oid, ifInfoOid.getIfPhysAddressOid() + "." + ifIndex)) {
                        table.put(ifIndex, ifInfoOid.getIfPhysAddressOid(), variable);
                    } else if (StringUtils.equals(oid, ifInfoOid.getIfAdminStatusOid() + "." + ifIndex)) {
                        table.put(ifIndex, ifInfoOid.getIfAdminStatusOid(), variable);
                    } else if (StringUtils.equals(oid, ifInfoOid.getIfOperStatusOid() + "." + ifIndex)) {
                        table.put(ifIndex, ifInfoOid.getIfOperStatusOid(), variable);
                    } else if (StringUtils.equals(oid, ifInfoOid.getIfInOctetsOid() + "." + ifIndex)) {
                        table.put(ifIndex, ifInfoOid.getIfInOctetsOid(), variable);
                    } else if (StringUtils.equals(oid, ifInfoOid.getIfOutOctetsOid() + "." + ifIndex)) {
                        table.put(ifIndex, ifInfoOid.getIfOutOctetsOid(), variable);
                    }
                }
            }
        }
        // 获取设备地址作为缓存键
        String deviceKey = target.getAddress().toString();
        // 查找上一次采集的快照
        IfOctetsSnapshot lastSnapshot = IF_OCTETS_SNAPSHOT_CACHE.get(deviceKey);
        // 构建本次字节数快照
        Table<String, String, Long> currentOctetsTable = HashBasedTable.create();
        for (String ifIndex : ifIndexes) {
            Variable ifInOctets = table.get(ifIndex, ifInfoOid.getIfInOctetsOid());
            Variable ifOutOctets = table.get(ifIndex, ifInfoOid.getIfOutOctetsOid());
            if (ifInOctets != null) {
                currentOctetsTable.put(ifIndex, ifInfoOid.getIfInOctetsOid(), ifInOctets.toLong());
            }
            if (ifOutOctets != null) {
                currentOctetsTable.put(ifIndex, ifInfoOid.getIfOutOctetsOid(), ifOutOctets.toLong());
            }
        }
        // 更新缓存为本次快照
        IF_OCTETS_SNAPSHOT_CACHE.put(deviceKey, new IfOctetsSnapshot(currentTime, currentOctetsTable));
        // 计算采样时间差（秒）
        double elapsedTimeInSeconds = lastSnapshot != null ? (double) (currentTime - lastSnapshot.timestamp) / 1000 : 0;
        // 构建返回数据
        List<IfDomain.IfInfoDomain> ifList = Lists.newArrayListWithCapacity(ifIndexes.size());
        for (String ifIndex : ifIndexes) {
            Variable ifDescr = table.get(ifIndex, ifInfoOid.getIfDescrOid());
            Variable ifType = table.get(ifIndex, ifInfoOid.getIfTypeOid());
            Variable ifMtu = table.get(ifIndex, ifInfoOid.getIfMtuOid());
            Variable ifSpeed = table.get(ifIndex, ifInfoOid.getIfSpeedOid());
            Variable ifPhysAddress = table.get(ifIndex, ifInfoOid.getIfPhysAddressOid());
            Variable ifAdminStatus = table.get(ifIndex, ifInfoOid.getIfAdminStatusOid());
            Variable ifOperStatus = table.get(ifIndex, ifInfoOid.getIfOperStatusOid());
            Variable ifInOctets = table.get(ifIndex, ifInfoOid.getIfInOctetsOid());
            Variable ifOutOctets = table.get(ifIndex, ifInfoOid.getIfOutOctetsOid());
            // 计算实时速率（比特/秒）：与上一次采集的字节数差值 * 8 / 时间差
            Long ifInRealTimeSpeed = null;
            Long ifOutRealTimeSpeed = null;
            if (lastSnapshot != null && elapsedTimeInSeconds > 0) {
                Long lastIfInOctets = lastSnapshot.octetsTable.get(ifIndex, ifInfoOid.getIfInOctetsOid());
                Long lastIfOutOctets = lastSnapshot.octetsTable.get(ifIndex, ifInfoOid.getIfOutOctetsOid());
                // 计算接收实时速率
                if (ifInOctets != null && lastIfInOctets != null) {
                    long diff = ifInOctets.toLong() - lastIfInOctets;
                    if (diff >= 0) {
                        ifInRealTimeSpeed = (long) (diff * 8 / elapsedTimeInSeconds);
                    }
                }
                // 计算发送实时速率
                if (ifOutOctets != null && lastIfOutOctets != null) {
                    long diff = ifOutOctets.toLong() - lastIfOutOctets;
                    if (diff >= 0) {
                        ifOutRealTimeSpeed = (long) (diff * 8 / elapsedTimeInSeconds);
                    }
                }
            }
            ifList.add(IfDomain.IfInfoDomain.builder()
                    .ifIndex(Integer.valueOf(ifIndex))
                    .ifDescr(ifDescr != null ? ifDescr.toString() : null)
                    .ifType(ifType != null ? IfTypeEnums.getNameCnById(ifType.toInt()) : null)
                    .ifMtu(ifMtu != null ? ifMtu.toLong() : null)
                    .ifSpeed(ifSpeed != null ? ifSpeed.toLong() : null)
                    .ifPhysAddress(ifPhysAddress != null ? ifPhysAddress.toString() : null)
                    .ifAdminStatus(ifAdminStatus != null ? IfAdminStatusEnums.getNameCnById(ifAdminStatus.toInt()) : null)
                    .ifOperStatus(ifOperStatus != null ? IfOperStatusEnums.getNameCnById(ifOperStatus.toInt()) : null)
                    .ifInOctets(ifInOctets != null ? ifInOctets.toLong() : null)
                    .ifOutOctets(ifOutOctets != null ? ifOutOctets.toLong() : null)
                    .ifInRealTimeSpeed(ifInRealTimeSpeed)
                    .ifOutRealTimeSpeed(ifOutRealTimeSpeed)
                    .build());
        }
        return ifList;
    }

}
