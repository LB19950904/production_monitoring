package com.gitee.pifeng.monitoring.common.constant.snmp;

/**
 * <p>
 * SNMP协议有关默认值常量
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/07 12:40
 */
public class SnmpDefaultConstants {

    /**
     * 默认社区字符串
     */
    public static final String DEFAULT_COMMUNITY_STRING = "public";

    /**
     * 默认端口号
     */
    public static final int DEFAULT_PORT = 161;

    // ==================== SNMP v3 默认值 ====================

    /**
     * 默认安全用户名
     */
    public static final String DEFAULT_SECURITY_NAME = "snmpuser";

    /**
     * 默认认证协议
     */
    public static final SnmpAuthProtocolEnums DEFAULT_AUTH_PROTOCOL = SnmpAuthProtocolEnums.SHA;

    /**
     * 默认认证密码
     */
    public static final String DEFAULT_AUTH_PASSWORD = "authpassword";

    /**
     * 默认隐私（加密）协议
     */
    public static final SnmpPrivProtocolEnums DEFAULT_PRIV_PROTOCOL = SnmpPrivProtocolEnums.AES_128;

    /**
     * 默认隐私（加密）密码
     */
    public static final String DEFAULT_PRIV_PASSWORD = "privpassword";

    /**
     * 默认安全级别（1: noAuthNoPriv, 2: authNoPriv, 3: authPriv）
     */
    public static final int DEFAULT_SECURITY_LEVEL = 3;

}
