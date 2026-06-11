package com.gitee.pifeng.monitoring.common.constant.snmp;

/**
 * <p>
 * SNMP协议 标准 MIB-II OID 常量
 * </p>
 *
 * <a href="https://www.alvestrand.no/objectid/top.html?spm=5176.28103460.0.0.65225d27kPqrh3">
 * 参考文档：https://www.alvestrand.no/objectid/top.html?spm=5176.28103460.0.0.65225d27kPqrh3
 * </a>
 *
 * @author 皮锋
 * @custom.date 2024/11/05 22:03
 */
public class SnmpOIdConstants {

    /**
     * 描述：系统描述，包含设备的型号、操作系统版本等信息。
     */
    public static final String SYS_DESCR_OID = "1.3.6.1.2.1.1.1.0";

    /**
     * 描述：系统自上次重启以来的运行时间（以百分之一秒为单位）。
     */
    public static final String SYS_UP_TIME_OID = "1.3.6.1.2.1.1.3.0";

    /**
     * 描述：系统管理员的联系信息。
     */
    public static final String SYS_CONTACT_OID = "1.3.6.1.2.1.1.4.0";

    /**
     * 描述：系统的名称。
     */
    public static final String SYS_NAME_OID = "1.3.6.1.2.1.1.5.0";

    /**
     * 描述：系统的物理位置。
     */
    public static final String SYS_LOCATION_OID = "1.3.6.1.2.1.1.6.0";

    /**
     * 描述：系统提供的服务类型。
     */
    public static final String SYS_SERVICES_OID = "1.3.6.1.2.1.1.7.0";

    /**
     * 描述：系统上的网络接口数量。
     */
    public static final String IF_NUMBER_OID = "1.3.6.1.2.1.2.1.0";

    /**
     * 描述：网络接口的索引号。
     */
    public static final String IF_INDEX_OID = "1.3.6.1.2.1.2.2.1.1";

    /**
     * 描述：网络接口的描述。
     */
    public static final String IF_DESCR_OID = "1.3.6.1.2.1.2.2.1.2";

    /**
     * 描述：网络接口的类型。
     */
    public static final String IF_TYPE_OID = "1.3.6.1.2.1.2.2.1.3";

    /**
     * 描述：网络接口的最大传输单元（MTU）。
     */
    public static final String IF_MTU_OID = "1.3.6.1.2.1.2.2.1.4";

    /**
     * 描述：网络接口的速率（以比特/秒为单位）。
     */
    public static final String IF_SPEED_OID = "1.3.6.1.2.1.2.2.1.5";

    /**
     * 描述：网络接口的物理地址（MAC地址）。
     */
    public static final String IF_PHYS_ADDRESS_OID = "1.3.6.1.2.1.2.2.1.6";

    /**
     * 描述：网络接口的管理状态（1=up,2=down,3=testing）。
     */
    public static final String IF_ADMIN_STATUS_OID = "1.3.6.1.2.1.2.2.1.7";

    /**
     * 描述：网络接口的操作状态（1=up,2=down,3=testing,4=unknown,5=dormant,6=notPresent,7=lowerLayerDown）。
     */
    public static final String IF_OPER_STATUS_OID = "1.3.6.1.2.1.2.2.1.8";

    /**
     * 描述：网络接口接收到的字节数。
     */
    public static final String IF_IN_OCTETS_OID = "1.3.6.1.2.1.2.2.1.10";

    /**
     * 描述：网络接口发送的字节数。
     */
    public static final String IF_OUT_OCTETS_OID = "1.3.6.1.2.1.2.2.1.16";

}
