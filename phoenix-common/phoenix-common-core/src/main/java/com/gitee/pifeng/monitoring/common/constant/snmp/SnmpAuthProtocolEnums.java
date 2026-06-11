package com.gitee.pifeng.monitoring.common.constant.snmp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * SNMP v3 认证协议枚举类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/4/9 11:33
 */
@Getter
@AllArgsConstructor
public enum SnmpAuthProtocolEnums {

    /**
     * 无认证
     */
    NONE(0, "无认证", null),

    /**
     * MD5认证
     */
    MD5(1, "MD5", "MD5"),

    /**
     * SHA认证
     */
    SHA(2, "SHA", "SHA"),

    /**
     * SHA-224认证
     */
    SHA_224(3, "SHA-224", "SHA-224"),

    /**
     * SHA-256认证
     */
    SHA_256(4, "SHA-256", "SHA-256"),

    /**
     * SHA-384认证
     */
    SHA_384(5, "SHA-384", "SHA-384"),

    /**
     * SHA-512认证
     */
    SHA_512(6, "SHA-512", "SHA-512");

    /**
     * 类型ID
     */
    private final int id;

    /**
     * 中文名称
     */
    private final String nameCn;

    /**
     * 协议名称
     */
    private final String protocolName;

}
