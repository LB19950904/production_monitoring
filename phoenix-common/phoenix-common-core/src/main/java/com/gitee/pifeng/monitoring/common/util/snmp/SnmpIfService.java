package com.gitee.pifeng.monitoring.common.util.snmp;

import cn.hutool.core.collection.CollectionUtil;
import com.gitee.pifeng.monitoring.common.constant.snmp.IfAdminStatusEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.IfOperStatusEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.IfTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.IfDomain;
import com.gitee.pifeng.monitoring.common.exception.MonitoringUniversalException;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.IfInfoOId;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * SNMP网络接口信息服务类，通过组合持有{@link SnmpVersionStrategy}策略实例，
 * 统一实现v2c和v3版本的网络接口信息获取逻辑。
 * </p>
 *
 * @param <C> 连接参数类型（如 v2c.Connection 或 v3.Connection）
 * @author 皮锋
 * @custom.date 2026/4/9 22:00
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class SnmpIfService<C> {

    /**
     * SNMP版本策略
     */
    private final SnmpVersionStrategy<C> strategy;

    /**
     * <p>
     * 获取网络接口信息
     * </p>
     *
     * @param connection SNMP连接请求参数
     * @param ifInfoOid  {@link IfInfoOId} 网络接口信息 MIB-II OID 请求参数
     * @return {@link IfDomain} 网络接口信息
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/07 20:25
     */
    @Deprecated
    public IfDomain getIfInfo(C connection, IfInfoOId ifInfoOid) throws IOException {
        // 参数校验，给SNMP连接请求参数设置默认值
        connection = this.strategy.defaultIfBlank(connection);
        // 参数校验，给网络接口信息 MIB-II OID 请求参数设置默认值
        ifInfoOid = SnmpCommonUtils.defaultIfBlank(ifInfoOid);
        // 创建一个传输映射，并启动监听
        TransportMapping<? extends Address> transport = SnmpCommonUtils.createTransportMappingAndListen(this.strategy.getProtocol(connection));
        // 创建SNMP对象
        Snmp snmp = new Snmp(transport);
        try {
            // 初始化SNMP会话（v3需要添加USM用户）
            this.strategy.initSnmp(snmp, connection);
            // 创建Target
            Target target = this.strategy.createTarget(connection);
            // 获取网络接口数量
            int ifNumber = this.getIfNumber(snmp, target, ifInfoOid.getIfNumberOid());
            // 获取网络接口的索引号
            List<String> ifIndexes = this.getIfIndexes(snmp, target, ifInfoOid.getIfIndexOid());
            // 构建返回数据
            List<IfDomain.IfInfoDomain> ifList = Lists.newArrayList();
            if (CollectionUtil.isNotEmpty(ifIndexes)) {
                for (String ifIndex : ifIndexes) {
                    // 获取网络接口的描述
                    String ifDescr = this.getIfDescr(snmp, target, ifInfoOid.getIfDescrOid(), ifIndex);
                    // 获取网络接口的类型
                    String ifType = this.getIfType(snmp, target, ifInfoOid.getIfTypeOid(), ifIndex);
                    // 获取网络接口的最大传输单元（MTU）
                    Long ifMtu = this.getIfMtu(snmp, target, ifInfoOid.getIfMtuOid(), ifIndex);
                    // 获取网络接口的速率（以比特/秒为单位）
                    Long ifSpeed = this.getIfSpeed(snmp, target, ifInfoOid.getIfSpeedOid(), ifIndex);
                    // 获取网络接口的物理地址（MAC地址）
                    String ifPhysAddress = this.getIfPhysAddress(snmp, target, ifInfoOid.getIfPhysAddressOid(), ifIndex);
                    // 获取网络接口的管理状态
                    String ifAdminStatus = this.getIfAdminStatus(snmp, target, ifInfoOid.getIfAdminStatusOid(), ifIndex);
                    // 获取网络接口的操作状态
                    String ifOperStatus = this.getIfOperStatus(snmp, target, ifInfoOid.getIfOperStatusOid(), ifIndex);
                    // 获取网络接口接收到的字节数
                    Long ifInOctets = this.getIfInOctets(snmp, target, ifInfoOid.getIfInOctetsOid(), ifIndex);
                    // 获取网络接口发送的字节数
                    Long ifOutOctets = this.getIfOutOctets(snmp, target, ifInfoOid.getIfOutOctetsOid(), ifIndex);
                    ifList.add(IfDomain.IfInfoDomain.builder()
                            .ifIndex(Integer.valueOf(ifIndex))
                            .ifDescr(ifDescr)
                            .ifType(ifType)
                            .ifMtu(ifMtu)
                            .ifSpeed(ifSpeed)
                            .ifPhysAddress(ifPhysAddress)
                            .ifAdminStatus(ifAdminStatus)
                            .ifOperStatus(ifOperStatus)
                            .ifInOctets(ifInOctets)
                            .ifOutOctets(ifOutOctets)
                            .build());
                }
            }
            // 返回
            return IfDomain.builder().ifNumber(ifNumber).ifList(ifList).build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            // 关闭连接
            SnmpCommonUtils.closeQuietly(snmp);
            // 关闭传输映射
            SnmpCommonUtils.closeQuietly(transport);
        }
    }

    /**
     * <p>
     * 高效获取网络接口信息
     * </p>
     *
     * @param connection SNMP连接请求参数
     * @param ifInfoOid  {@link IfInfoOId} 网络接口信息 MIB-II OID 请求参数
     * @return {@link IfDomain} 网络接口信息
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/13 20:25
     */
    public IfDomain getIfInfoEfficient(C connection, IfInfoOId ifInfoOid) throws IOException {
        // 参数校验，给SNMP连接请求参数设置默认值
        connection = this.strategy.defaultIfBlank(connection);
        // 参数校验，给网络接口信息 MIB-II OID 请求参数设置默认值
        ifInfoOid = SnmpCommonUtils.defaultIfBlank(ifInfoOid);
        // 创建一个传输映射，并启动监听
        TransportMapping<? extends Address> transport = SnmpCommonUtils.createTransportMappingAndListen(this.strategy.getProtocol(connection));
        // 创建SNMP对象
        Snmp snmp = new Snmp(transport);
        try {
            // 初始化SNMP会话（v3需要添加USM用户）
            this.strategy.initSnmp(snmp, connection);
            // 创建Target
            Target target = this.strategy.createTarget(connection);
            // 获取网络接口数量
            int ifNumber = this.getIfNumber(snmp, target, ifInfoOid.getIfNumberOid());
            // 获取网络接口的索引号
            List<String> ifIndexes = this.getIfIndexes(snmp, target, ifInfoOid.getIfIndexOid());
            // 获取每一个网络接口的详细信息
            List<IfDomain.IfInfoDomain> ifList = SnmpCommonUtils.getIfInfoDomain(snmp, target, ifInfoOid, ifIndexes, this.strategy.createTreePDUFactory());
            // 返回
            return IfDomain.builder().ifNumber(ifNumber).ifList(ifList).build();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            // 关闭连接
            SnmpCommonUtils.closeQuietly(snmp);
            // 关闭传输映射
            SnmpCommonUtils.closeQuietly(transport);
        }
    }

    /**
     * <p>
     * 获取网络接口数量
     * </p>
     *
     * @param snmp   {@link Snmp}
     * @param target {@link Target}
     * @param oid    MIB-II OID
     * @return 网络接口数量
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/07 20:48
     */
    private int getIfNumber(Snmp snmp, Target target, String oid) throws IOException {
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        Integer result = SnmpCommonUtils.getIntegerVariable(oid, responsePdu);
        if (result == null) {
            throw new MonitoringUniversalException("获取网络接口数量失败！");
        }
        return result;
    }

    /**
     * <p>
     * 获取网络接口的索引号
     * </p>
     *
     * @param snmp   {@link Snmp}
     * @param target {@link Target}
     * @param oid    MIB-II OID
     * @return 网络接口的索引号
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/09 12:00
     */
    private List<String> getIfIndexes(Snmp snmp, Target target, String oid) throws IOException {
        // 返回值
        List<String> result = Lists.newArrayList();
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GETNEXT);
        while (true) {
            // 发送请求
            ResponseEvent response = snmp.send(pdu, target);
            // 校验和获取PDU
            PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
            for (VariableBinding vb : responsePdu.getVariableBindings()) {
                // 获取OID和变量值
                String vbOid = vb.getOid().toString();
                String ifIndex = vb.getVariable().toString();
                // 检查OID是否仍然在ifIndex范围内
                if (StringUtils.startsWith(vbOid, oid + ".")) {
                    result.add(ifIndex);
                } else {
                    // 如果OID不再属于ifIndex，停止循环
                    return result;
                }
            }
            // 更新OID为下一个OID
            if (responsePdu.size() > 0) {
                VariableBinding nextVb = responsePdu.getVariableBindings().get(0);
                pdu.setVariableBindings(Collections.singletonList(nextVb));
            } else {
                break;
            }
        }
        return result;
    }

    /**
     * <p>
     * 获取网络接口的描述
     * </p>
     *
     * @param snmp    {@link Snmp}
     * @param target  {@link Target}
     * @param oid     MIB-II OID
     * @param ifIndex 网络接口的索引号
     * @return 网络接口的描述
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/09 12:09
     */
    private String getIfDescr(Snmp snmp, Target target, String oid, String ifIndex) throws IOException {
        oid = oid + "." + ifIndex;
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        String result = SnmpCommonUtils.getStringVariable(oid, responsePdu);
        if (StringUtils.isEmpty(result)) {
            throw new MonitoringUniversalException("获取网络接口的描述失败！");
        }
        return result;
    }

    /**
     * <p>
     * 获取网络接口的类型
     * </p>
     *
     * @param snmp    {@link Snmp}
     * @param target  {@link Target}
     * @param oid     MIB-II OID
     * @param ifIndex 网络接口的索引号
     * @return 网络接口的类型
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/09 13:28
     */
    private String getIfType(Snmp snmp, Target target, String oid, String ifIndex) throws IOException {
        oid = oid + "." + ifIndex;
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        Integer result = SnmpCommonUtils.getIntegerVariable(oid, responsePdu);
        if (result == null) {
            throw new MonitoringUniversalException("获取网络接口的类型失败！");
        }
        // 网络接口类型ID转中文名字
        return IfTypeEnums.getNameCnById(result);
    }

    /**
     * <p>
     * 获取网络接口的最大传输单元（MTU）
     * </p>
     *
     * @param snmp    {@link Snmp}
     * @param target  {@link Target}
     * @param oid     MIB-II OID
     * @param ifIndex 网络接口的索引号
     * @return 网络接口的最大传输单元（MTU）
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/10 10:28
     */
    private Long getIfMtu(Snmp snmp, Target target, String oid, String ifIndex) throws IOException {
        oid = oid + "." + ifIndex;
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        Long result = SnmpCommonUtils.getLongVariable(oid, responsePdu);
        if (result == null) {
            throw new MonitoringUniversalException("获取网络接口的最大传输单元（MTU）失败！");
        }
        return result;
    }

    /**
     * <p>
     * 获取网络接口的速率（以比特/秒为单位）
     * </p>
     *
     * @param snmp    {@link Snmp}
     * @param target  {@link Target}
     * @param oid     MIB-II OID
     * @param ifIndex 网络接口的索引号
     * @return 网络接口的速率（以比特/秒为单位）
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/10 10:24
     */
    private Long getIfSpeed(Snmp snmp, Target target, String oid, String ifIndex) throws IOException {
        oid = oid + "." + ifIndex;
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        Long result = SnmpCommonUtils.getLongVariable(oid, responsePdu);
        if (result == null) {
            throw new MonitoringUniversalException("获取网络接口的速率失败！");
        }
        return result;
    }

    /**
     * <p>
     * 获取网络接口的物理地址（MAC地址）
     * </p>
     *
     * @param snmp    {@link Snmp}
     * @param target  {@link Target}
     * @param oid     MIB-II OID
     * @param ifIndex 网络接口的索引号
     * @return 网络接口的物理地址（MAC地址）
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/10 17:32
     */
    private String getIfPhysAddress(Snmp snmp, Target target, String oid, String ifIndex) throws IOException {
        oid = oid + "." + ifIndex;
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        String result = SnmpCommonUtils.getStringVariable(oid, responsePdu);
        if (StringUtils.isEmpty(result)) {
            throw new MonitoringUniversalException("获取网络接口的物理地址（MAC地址）失败！");
        }
        return result;
    }

    /**
     * <p>
     * 获取网络接口的管理状态
     * </p>
     *
     * @param snmp    {@link Snmp}
     * @param target  {@link Target}
     * @param oid     MIB-II OID
     * @param ifIndex 网络接口的索引号
     * @return 网络接口的管理状态
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/10 17:34
     */
    private String getIfAdminStatus(Snmp snmp, Target target, String oid, String ifIndex) throws IOException {
        oid = oid + "." + ifIndex;
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        Integer result = SnmpCommonUtils.getIntegerVariable(oid, responsePdu);
        if (result == null) {
            throw new MonitoringUniversalException("获取网络接口的管理状态失败！");
        }
        // 网络接口的管理状态ID转中文名字
        return IfAdminStatusEnums.getNameCnById(result);
    }

    /**
     * <p>
     * 获取网络接口的操作状态
     * </p>
     *
     * @param snmp    {@link Snmp}
     * @param target  {@link Target}
     * @param oid     MIB-II OID
     * @param ifIndex 网络接口的索引号
     * @return 网络接口的操作状态
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/10 17:58
     */
    private String getIfOperStatus(Snmp snmp, Target target, String oid, String ifIndex) throws IOException {
        oid = oid + "." + ifIndex;
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        Integer result = SnmpCommonUtils.getIntegerVariable(oid, responsePdu);
        if (result == null) {
            throw new MonitoringUniversalException("获取网络接口的操作状态失败！");
        }
        // 网络接口的操作状态ID转中文名字
        return IfOperStatusEnums.getNameCnById(result);
    }

    /**
     * <p>
     * 获取网络接口接收到的字节数
     * </p>
     *
     * @param snmp    {@link Snmp}
     * @param target  {@link Target}
     * @param oid     MIB-II OID
     * @param ifIndex 网络接口的索引号
     * @return 网络接口接收到的字节数
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/10 18:12
     */
    private Long getIfInOctets(Snmp snmp, Target target, String oid, String ifIndex) throws IOException {
        oid = oid + "." + ifIndex;
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        Long result = SnmpCommonUtils.getLongVariable(oid, responsePdu);
        if (result == null) {
            throw new MonitoringUniversalException("获取网络接口接收到的字节数失败！");
        }
        return result;
    }

    /**
     * <p>
     * 获取网络接口发送的字节数
     * </p>
     *
     * @param snmp    {@link Snmp}
     * @param target  {@link Target}
     * @param oid     MIB-II OID
     * @param ifIndex 网络接口的索引号
     * @return 网络接口发送的字节数
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/10 18:16
     */
    private Long getIfOutOctets(Snmp snmp, Target target, String oid, String ifIndex) throws IOException {
        oid = oid + "." + ifIndex;
        // 创建PDU
        PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
        // 发送请求
        ResponseEvent response = snmp.send(pdu, target);
        // 校验和获取PDU
        PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
        // 返回值
        Long result = SnmpCommonUtils.getLongVariable(oid, responsePdu);
        if (result == null) {
            throw new MonitoringUniversalException("获取网络接口发送的字节数失败！");
        }
        return result;
    }

}
