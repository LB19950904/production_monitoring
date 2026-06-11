package com.gitee.pifeng.monitoring.common.constant.snmp;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>
 * SNMP v3 隐私（加密）协议枚举类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/4/9 11:35
 */
@Getter
@AllArgsConstructor
public enum SnmpPrivProtocolEnums {

    /**
     * 无加密
     */
    NONE(0, "无加密", null),

    /**
     * DES加密
     */
    DES(1, "DES", "DES"),

    /**
     * 3DES（Triple DES）加密
     */
    TRIPLE_DES(2, "3DES", "3DES"),

    /**
     * AES-128加密
     */
    AES_128(3, "AES-128", "AES128"),

    /**
     * AES-192加密
     */
    AES_192(4, "AES-192", "AES192"),

    /**
     * AES-256加密
     */
    AES_256(5, "AES-256", "AES256");

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
