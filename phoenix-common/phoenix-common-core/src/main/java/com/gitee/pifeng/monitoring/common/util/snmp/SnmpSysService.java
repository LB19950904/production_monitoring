package com.gitee.pifeng.monitoring.common.util.snmp;

import com.gitee.pifeng.monitoring.common.domain.networkdevice.SysDomain;
import com.gitee.pifeng.monitoring.common.exception.MonitoringUniversalException;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.SysInfoOId;
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

import static com.gitee.pifeng.monitoring.common.constant.snmp.SnmpOIdConstants.SYS_DESCR_OID;

/**
 * <p>
 * SNMP系统信息服务类，通过组合持有{@link SnmpVersionStrategy}策略实例，
 * 统一实现v2c和v3版本的系统信息获取逻辑。
 * </p>
 *
 * @param <C> 连接参数类型（如 v2c.Connection 或 v3.Connection）
 * @author 皮锋
 * @custom.date 2026/4/9 22:00
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class SnmpSysService<C> {

    /**
     * SNMP版本策略
     */
    private final SnmpVersionStrategy<C> strategy;

    /**
     * <p>
     * 获取系统描述信息
     * </p>
     *
     * @param connection SNMP连接请求参数
     * @param oid        MIB-II OID
     * @return 系统描述信息
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/13 16:49
     */
    public String getSysDescr(C connection, String oid) throws IOException {
        // 参数校验，给SNMP连接请求参数设置默认值
        connection = this.strategy.defaultIfBlank(connection);
        // 参数校验，给 MIB-II OID 设置默认值
        oid = StringUtils.defaultIfBlank(oid, SYS_DESCR_OID);
        // 创建一个传输映射，并启动监听
        TransportMapping<? extends Address> transport = SnmpCommonUtils.createTransportMappingAndListen(this.strategy.getProtocol(connection));
        // 创建SNMP对象
        Snmp snmp = new Snmp(transport);
        try {
            // 初始化SNMP会话（v3需要添加USM用户）
            this.strategy.initSnmp(snmp, connection);
            // 创建Target
            Target target = this.strategy.createTarget(connection);
            // 创建PDU
            PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
            // 发送请求
            ResponseEvent response = snmp.send(pdu, target);
            // 校验和获取PDU
            PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
            // 返回值
            String result = SnmpCommonUtils.getStringVariable(oid, responsePdu);
            if (StringUtils.isEmpty(result)) {
                throw new MonitoringUniversalException("获取系统描述信息失败！");
            }
            return result;
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
     * 获取系统信息
     * </p>
     *
     * @param connection SNMP连接请求参数
     * @param sysInfoOid {@link SysInfoOId} 系统信息 MIB-II OID 请求参数
     * @return {@link SysDomain} 系统信息
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/06 20:35
     */
    public SysDomain getSysInfo(C connection, SysInfoOId sysInfoOid) throws IOException {
        // 参数校验，给SNMP连接请求参数设置默认值
        connection = this.strategy.defaultIfBlank(connection);
        // 参数校验，给系统信息 MIB-II OID 请求参数设置默认值
        sysInfoOid = SnmpCommonUtils.defaultIfBlank(sysInfoOid);
        // 创建一个传输映射，并启动监听
        TransportMapping<? extends Address> transport = SnmpCommonUtils.createTransportMappingAndListen(this.strategy.getProtocol(connection));
        // 创建SNMP对象
        Snmp snmp = new Snmp(transport);
        try {
            // 初始化SNMP会话（v3需要添加USM用户）
            this.strategy.initSnmp(snmp, connection);
            // 创建Target
            Target target = this.strategy.createTarget(connection);
            // 创建PDU
            String[] oids = new String[]{
                    sysInfoOid.getSysDescrOid(),
                    sysInfoOid.getSysUpTimeOid(),
                    sysInfoOid.getSysContactOid(),
                    sysInfoOid.getSysNameOid(),
                    sysInfoOid.getSysLocationOid(),
                    sysInfoOid.getSysServicesOid()
            };
            PDU pdu = this.strategy.createPDU(oids, PDU.GET);
            // 发送请求
            ResponseEvent response = snmp.send(pdu, target);
            // 校验和获取PDU
            PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
            // 方法返回值
            SysDomain sysDomain = new SysDomain();
            for (VariableBinding vb : responsePdu.getVariableBindings()) {
                String oid = vb.getOid().toString();
                String variable = vb.getVariable().toString();
                if (StringUtils.equals(oid, sysInfoOid.getSysDescrOid())) {
                    sysDomain.setSysDescr(variable);
                } else if (StringUtils.equals(oid, sysInfoOid.getSysUpTimeOid())) {
                    sysDomain.setSysUpTime(variable);
                } else if (StringUtils.equals(oid, sysInfoOid.getSysContactOid())) {
                    sysDomain.setSysContact(variable);
                } else if (StringUtils.equals(oid, sysInfoOid.getSysNameOid())) {
                    sysDomain.setSysName(variable);
                } else if (StringUtils.equals(oid, sysInfoOid.getSysLocationOid())) {
                    sysDomain.setSysLocation(variable);
                } else if (StringUtils.equals(oid, sysInfoOid.getSysServicesOid())) {
                    try {
                        sysDomain.setSysServices(SnmpCommonUtils.convertSysServices(Integer.parseInt(variable)));
                    } catch (NumberFormatException e) {
                        log.warn("sysServices的值[{}]无法转换为整数！", variable);
                    }
                }
            }
            return sysDomain;
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
     * 根据指定的OID获取信息
     * </p>
     *
     * @param connection SNMP连接请求参数
     * @param oid        MIB-II OID
     * @return OID对应的信息
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2024/11/14 10:49
     */
    public String getInfoByOid(C connection, String oid) throws IOException {
        // 参数校验，给SNMP连接请求参数设置默认值
        connection = this.strategy.defaultIfBlank(connection);
        // MIB-II OID参数校验
        if (StringUtils.isBlank(oid)) {
            throw new MonitoringUniversalException("OID不能为空！");
        }
        // 创建一个传输映射，并启动监听
        TransportMapping<? extends Address> transport = SnmpCommonUtils.createTransportMappingAndListen(this.strategy.getProtocol(connection));
        // 创建SNMP对象
        Snmp snmp = new Snmp(transport);
        try {
            // 初始化SNMP会话（v3需要添加USM用户）
            this.strategy.initSnmp(snmp, connection);
            // 创建Target
            Target target = this.strategy.createTarget(connection);
            // 创建PDU
            PDU pdu = this.strategy.createPDU(new String[]{oid}, PDU.GET);
            // 发送请求
            ResponseEvent response = snmp.send(pdu, target);
            // 校验和获取PDU
            PDU responsePdu = SnmpCommonUtils.verifyResponseAndGet(response);
            // 返回值
            StringBuilder sb = new StringBuilder();
            for (VariableBinding vb : responsePdu.getVariableBindings()) {
                String vbOid = vb.getOid().toString();
                String vbVariable = vb.getVariable().toString();
                sb.append(vbOid).append("=").append(vbVariable).append("\r\n");
            }
            return StringUtils.removeEnd(sb.toString(), "\r\n");
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

}
