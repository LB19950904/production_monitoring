package com.gitee.pifeng.monitoring.common.util.snmp.v3;

import com.gitee.pifeng.monitoring.common.constant.CommProtocolTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpAuthProtocolEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpDefaultConstants;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpPrivProtocolEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpProtocolVersionConstants;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.ConnectionDomain;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.v3.Connection;
import com.gitee.pifeng.monitoring.common.util.snmp.SnmpCommonUtils;
import com.gitee.pifeng.monitoring.common.util.snmp.SnmpVersionStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.PDUFactory;

import java.util.Arrays;

/**
 * <p>
 * SNMP v3 版本策略实现。SNMP v3 相比 v2c 提供了更强的安全性，
 * 支持基于用户的安全模型（USM），包括认证（MD5/SHA）和加密（DES/AES）功能。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/4/9 22:00
 */
@Slf4j
public class SnmpV3Strategy implements SnmpVersionStrategy<Connection> {

    /**
     * <p>
     * 参数校验，给SNMP v3 连接请求参数设置默认值
     * </p>
     *
     * @param connection {@link Connection} SNMP v3 连接请求参数
     * @return {@link Connection} SNMP v3 连接请求参数
     * @author 皮锋
     * @custom.date 2024/12/15 10:30
     */
    @Override
    public Connection defaultIfBlank(Connection connection) {
        String ip = SnmpCommonUtils.verifyIpAndGet(connection.getIp());
        Integer port = connection.getPort() != null ? connection.getPort() : SnmpDefaultConstants.DEFAULT_PORT;
        CommProtocolTypeEnums protocol = connection.getProtocol() != null ? connection.getProtocol() : CommProtocolTypeEnums.UDP;
        String securityName = StringUtils.defaultIfBlank(connection.getSecurityName(), SnmpDefaultConstants.DEFAULT_SECURITY_NAME);
        SnmpAuthProtocolEnums authProtocol = connection.getAuthProtocol() != null ? connection.getAuthProtocol() : SnmpDefaultConstants.DEFAULT_AUTH_PROTOCOL;
        String authPassword = StringUtils.defaultIfBlank(connection.getAuthPassword(), SnmpDefaultConstants.DEFAULT_AUTH_PASSWORD);
        SnmpPrivProtocolEnums privProtocol = connection.getPrivProtocol() != null ? connection.getPrivProtocol() : SnmpDefaultConstants.DEFAULT_PRIV_PROTOCOL;
        String privPassword = StringUtils.defaultIfBlank(connection.getPrivPassword(), SnmpDefaultConstants.DEFAULT_PRIV_PASSWORD);
        Integer securityLevel = connection.getSecurityLevel() != null ? connection.getSecurityLevel() : SnmpDefaultConstants.DEFAULT_SECURITY_LEVEL;
        return Connection.builder()
                .ip(ip)
                .port(port)
                .protocol(protocol)
                .securityName(securityName)
                .authProtocol(authProtocol)
                .authPassword(authPassword)
                .privProtocol(privProtocol)
                .privPassword(privPassword)
                .securityLevel(securityLevel)
                .contextEngineId(connection.getContextEngineId())
                .contextName(connection.getContextName())
                .build();
    }

    /**
     * <p>
     * 创建 SNMP v3 的UserTarget
     * </p>
     *
     * @param connection {@link Connection} SNMP v3 连接请求参数
     * @return {@link Target}
     * @author 皮锋
     * @custom.date 2024/12/15 10:30
     */
    @Override
    public Target createTarget(Connection connection) {
        // 创建UserTarget
        UserTarget target = new UserTarget();
        target.setAddress(GenericAddress.parse(String.format("%s:%s/%d", connection.getProtocol().name().toLowerCase(), connection.getIp(), connection.getPort())));
        target.setRetries(2);
        target.setTimeout(1500);
        // 使用SNMP v3
        target.setVersion(SnmpConstants.version3);
        // 设置安全用户名
        target.setSecurityName(new OctetString(connection.getSecurityName()));
        // 设置安全级别
        target.setSecurityLevel(connection.getSecurityLevel());
        return target;
    }

    /**
     * <p>
     * 创建SNMP v3的ScopedPDU
     * </p>
     *
     * @param oids    MIB-II OID 数组
     * @param pduType PDU类型
     * @return {@link PDU} 协议数据单元
     * @author 皮锋
     * @custom.date 2024/12/15 10:30
     */
    @Override
    public PDU createPDU(String[] oids, int pduType) {
        ScopedPDU pdu = new ScopedPDU();
        Arrays.stream(oids).forEach(oid -> pdu.add(new VariableBinding(new OID(oid))));
        pdu.setType(pduType);
        return pdu;
    }

    /**
     * <p>
     * 初始化SNMP v3会话，创建USM用户并添加到SNMP消息分发器
     * </p>
     *
     * @param snmp       {@link Snmp} SNMP对象
     * @param connection {@link Connection} SNMP v3 连接请求参数
     * @author 皮锋
     * @custom.date 2024/12/15 10:30
     */
    @Override
    public void initSnmp(Snmp snmp, Connection connection) {
        addUsmUser(snmp, connection);
    }

    /**
     * <p>
     * 创建SNMP v3的PDU工厂（用于TreeUtils批量查询，使用GETNEXT类型）
     * </p>
     *
     * @return {@link PDUFactory} PDU工厂
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    @Override
    public PDUFactory createTreePDUFactory() {
        return new DefaultPDUFactory(PDU.GETNEXT);
    }

    /**
     * <p>
     * 根据SNMP v3连接参数构建SNMP连接信息域对象
     * </p>
     *
     * @param connection {@link Connection} SNMP v3 连接请求参数
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
                .snmpVersion(SnmpProtocolVersionConstants.VERSION_3)
                .securityName(connection.getSecurityName())
                .authProtocol(connection.getAuthProtocol() != null ? connection.getAuthProtocol().getNameCn() : null)
                .privProtocol(connection.getPrivProtocol() != null ? connection.getPrivProtocol().getNameCn() : null)
                .build();
    }

    /**
     * <p>
     * 创建USM用户并添加到SNMP消息分发器
     * </p>
     *
     * @param snmp       {@link Snmp} SNMP对象
     * @param connection {@link Connection} SNMP v3 连接请求参数
     * @author 皮锋
     * @custom.date 2024/12/15 10:30
     */
    private void addUsmUser(Snmp snmp, Connection connection) {
        // 获取或创建USM
        USM usm = snmp.getUSM();
        if (usm == null) {
            // 创建本地引擎ID
            OctetString localEngineId = new OctetString(MPv3.createLocalEngineID());
            usm = new USM(SecurityProtocols.getInstance(), localEngineId, 0);
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3(usm));
        }
        // 创建认证协议
        AuthenticationProtocol authProtocol = createAuthProtocol(connection.getAuthProtocol());
        // 创建隐私协议
        PrivacyProtocol privProtocol = createPrivProtocol(connection.getPrivProtocol());
        // 创建USM用户
        UsmUser usmUser;
        OctetString securityName = new OctetString(connection.getSecurityName());
        OctetString authPassword = StringUtils.isNotBlank(connection.getAuthPassword()) ? new OctetString(connection.getAuthPassword()) : null;
        OctetString privPassword = StringUtils.isNotBlank(connection.getPrivPassword()) ? new OctetString(connection.getPrivPassword()) : null;
        if (authProtocol == null && privProtocol == null) {
            // 无认证无加密
            usmUser = new UsmUser(securityName, null, null, null, null);
        } else if (authProtocol != null && privProtocol == null) {
            // 认证无加密
            usmUser = new UsmUser(securityName, authProtocol.getID(), authPassword, null, null);
        } else if (authProtocol != null) {
            // 认证且加密（此时 privProtocol 必定不为 null）
            usmUser = new UsmUser(securityName, authProtocol.getID(), authPassword, privProtocol.getID(), privPassword);
        } else {
            // 无认证有加密（SNMP v3 不支持此配置，需要认证才能使用加密）
            log.warn("SNMP v3 安全配置错误：使用加密协议 {} 需要同时配置认证协议！", connection.getPrivProtocol());
            usmUser = new UsmUser(securityName, null, null, null, null);
        }
        // 添加用户到USM
        usm.addUser(securityName, usmUser);
    }

    /**
     * <p>
     * 创建认证协议
     * </p>
     *
     * @param authProtocol {@link SnmpAuthProtocolEnums} 认证协议枚举
     * @return {@link AuthenticationProtocol} 认证协议
     * @author 皮锋
     * @custom.date 2024/12/15 10:30
     */
    private AuthenticationProtocol createAuthProtocol(SnmpAuthProtocolEnums authProtocol) {
        if (authProtocol == null || authProtocol == SnmpAuthProtocolEnums.NONE) {
            return null;
        }
        SecurityProtocols securityProtocols = SecurityProtocols.getInstance();
        switch (authProtocol) {
            case MD5:
                return securityProtocols.getAuthenticationProtocol(AuthMD5.ID);
            case SHA:
                return securityProtocols.getAuthenticationProtocol(AuthSHA.ID);
            case SHA_224:
                return securityProtocols.getAuthenticationProtocol(AuthHMAC128SHA224.ID);
            case SHA_256:
                return securityProtocols.getAuthenticationProtocol(AuthHMAC192SHA256.ID);
            case SHA_384:
                return securityProtocols.getAuthenticationProtocol(AuthHMAC256SHA384.ID);
            case SHA_512:
                return securityProtocols.getAuthenticationProtocol(AuthHMAC384SHA512.ID);
            default:
                return null;
        }
    }

    /**
     * <p>
     * 创建隐私（加密）协议
     * </p>
     *
     * @param privProtocol {@link SnmpPrivProtocolEnums} 隐私协议枚举
     * @return {@link PrivacyProtocol} 隐私协议
     * @author 皮锋
     * @custom.date 2024/12/15 10:30
     */
    private PrivacyProtocol createPrivProtocol(SnmpPrivProtocolEnums privProtocol) {
        if (privProtocol == null || privProtocol == SnmpPrivProtocolEnums.NONE) {
            return null;
        }
        SecurityProtocols securityProtocols = SecurityProtocols.getInstance();
        switch (privProtocol) {
            case DES:
                return securityProtocols.getPrivacyProtocol(PrivDES.ID);
            case TRIPLE_DES:
                return securityProtocols.getPrivacyProtocol(Priv3DES.ID);
            case AES_128:
                return securityProtocols.getPrivacyProtocol(PrivAES128.ID);
            case AES_192:
                return securityProtocols.getPrivacyProtocol(PrivAES192.ID);
            case AES_256:
                return securityProtocols.getPrivacyProtocol(PrivAES256.ID);
            default:
                return null;
        }
    }

    /**
     * <p>
     * 获取SNMP v3连接参数中的通信协议类型
     * </p>
     *
     * @param connection {@link Connection} SNMP v3 连接请求参数
     * @return {@link CommProtocolTypeEnums} 通信协议类型
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    @Override
    public CommProtocolTypeEnums getProtocol(Connection connection) {
        return connection.getProtocol();
    }

}
