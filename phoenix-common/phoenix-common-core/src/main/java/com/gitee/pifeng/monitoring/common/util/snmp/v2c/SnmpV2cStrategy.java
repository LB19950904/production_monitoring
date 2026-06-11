package com.gitee.pifeng.monitoring.common.util.snmp.v2c;

import com.gitee.pifeng.monitoring.common.constant.CommProtocolTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpDefaultConstants;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpProtocolVersionConstants;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.ConnectionDomain;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.v2c.Connection;
import com.gitee.pifeng.monitoring.common.util.snmp.SnmpCommonUtils;
import com.gitee.pifeng.monitoring.common.util.snmp.SnmpVersionStrategy;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;

import java.util.Arrays;

/**
 * <p>
 * SNMP v2c 版本策略实现。使用社区字符串（Community String）进行身份验证。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/4/9 22:00
 */
public class SnmpV2cStrategy implements SnmpVersionStrategy<Connection> {

    /**
     * <p>
     * 参数校验，给SNMP v2c 连接请求参数设置默认值
     * </p>
     *
     * @param connection {@link Connection} SNMP连接请求参数
     * @return {@link Connection} SNMP连接请求参数
     * @author 皮锋
     * @custom.date 2024/11/08 21:07
     */
    @Override
    public Connection defaultIfBlank(Connection connection) {
        String ip = SnmpCommonUtils.verifyIpAndGet(connection.getIp());
        Integer port = connection.getPort() != null ? connection.getPort() : SnmpDefaultConstants.DEFAULT_PORT;
        CommProtocolTypeEnums protocol = connection.getProtocol() != null ? connection.getProtocol() : CommProtocolTypeEnums.UDP;
        String community = StringUtils.defaultIfBlank(connection.getCommunity(), SnmpDefaultConstants.DEFAULT_COMMUNITY_STRING);
        return Connection.builder().ip(ip).port(port).protocol(protocol).community(community).build();
    }

    /**
     * <p>
     * 创建SNMP v2c的CommunityTarget
     * </p>
     *
     * @param connection {@link Connection} SNMP连接请求参数
     * @return {@link Target}
     * @author 皮锋
     * @custom.date 2024/11/07 22:12
     */
    @Override
    public Target createTarget(Connection connection) {
        // 创建CommunityTarget
        CommunityTarget target = new CommunityTarget();
        // 社区字符串
        target.setCommunity(new OctetString(connection.getCommunity()));
        target.setAddress(GenericAddress.parse(String.format("%s:%s/%d", connection.getProtocol().name().toLowerCase(), connection.getIp(), connection.getPort())));
        target.setRetries(2);
        target.setTimeout(1500);
        // 使用SNMP v2c
        target.setVersion(SnmpConstants.version2c);
        return target;
    }

    /**
     * <p>
     * 创建SNMP v2c的PDU
     * </p>
     *
     * @param oids    MIB-II OID 数组
     * @param pduType PDU类型
     * @return {@link PDU} 协议数据单元
     * @author 皮锋
     * @custom.date 2024/11/07 22:19
     */
    @Override
    public PDU createPDU(String[] oids, int pduType) {
        PDU pdu = new PDU();
        Arrays.stream(oids).forEach(oid -> pdu.add(new VariableBinding(new OID(oid))));
        pdu.setType(pduType);
        return pdu;
    }

    /**
     * <p>
     * SNMP v2c 不需要额外初始化操作，空实现
     * </p>
     *
     * @param snmp       {@link Snmp} SNMP对象
     * @param connection {@link Connection} SNMP连接请求参数
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    @Override
    public void initSnmp(Snmp snmp, Connection connection) {
        // v2c 不需要额外初始化
    }

    /**
     * <p>
     * 创建SNMP v2c的PDU工厂（用于TreeUtils批量查询）
     * </p>
     *
     * @return {@link PDUFactory} PDU工厂
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    @Override
    public PDUFactory createTreePDUFactory() {
        return new DefaultPDUFactory();
    }

    /**
     * <p>
     * 根据SNMP v2c连接参数构建SNMP连接信息域对象
     * </p>
     *
     * @param connection {@link Connection} SNMP连接请求参数
     * @return {@link ConnectionDomain} SNMP连接信息
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    @Override
    public ConnectionDomain buildConnectionDomain(Connection connection) {
        return ConnectionDomain.builder()
                .ip(connection.getIp())
                .port(connection.getPort())
                .protocol("SNMP(" + connection.getProtocol().name() + ")")
                .snmpVersion(SnmpProtocolVersionConstants.VERSION_2C)
                .community(connection.getCommunity())
                .build();
    }

    /**
     * <p>
     * 获取SNMP v2c连接参数中的通信协议类型
     * </p>
     *
     * @param connection {@link Connection} SNMP连接请求参数
     * @return {@link CommProtocolTypeEnums} 通信协议类型
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    @Override
    public CommProtocolTypeEnums getProtocol(Connection connection) {
        return connection.getProtocol();
    }

}
