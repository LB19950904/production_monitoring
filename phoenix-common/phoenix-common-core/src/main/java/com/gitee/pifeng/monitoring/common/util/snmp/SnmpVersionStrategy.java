package com.gitee.pifeng.monitoring.common.util.snmp;

import com.gitee.pifeng.monitoring.common.constant.CommProtocolTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.ConnectionDomain;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.util.PDUFactory;

/**
 * <p>
 * SNMP版本策略接口，将v2c和v3的差异抽象为策略，通过组合复用代码。
 * </p>
 *
 * @param <C> 连接参数类型（如 v2c.Connection 或 v3.Connection）
 * @author 皮锋
 * @custom.date 2026/4/9 22:00
 */
public interface SnmpVersionStrategy<C> {

    /**
     * <p>
     * 参数校验，给SNMP连接请求参数设置默认值
     * </p>
     *
     * @param connection SNMP连接请求参数
     * @return 设置了默认值的SNMP连接请求参数
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    C defaultIfBlank(C connection);

    /**
     * <p>
     * 创建SNMP Target
     * </p>
     *
     * @param connection SNMP连接请求参数
     * @return {@link Target}
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    Target createTarget(C connection);

    /**
     * <p>
     * 创建协议数据单元（PDU）
     * </p>
     *
     * @param oids    MIB-II OID 数组
     * @param pduType PDU类型
     * @return {@link PDU} 协议数据单元
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    PDU createPDU(String[] oids, int pduType);

    /**
     * <p>
     * 创建用于TreeUtils批量查询的PDU工厂
     * </p>
     *
     * @return {@link PDUFactory} PDU工厂
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    PDUFactory createTreePDUFactory();

    /**
     * <p>
     * 初始化SNMP会话（v3需要添加USM用户，v2c为空实现）
     * </p>
     *
     * @param snmp       {@link Snmp} SNMP对象
     * @param connection SNMP连接请求参数
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    void initSnmp(Snmp snmp, C connection);

    /**
     * <p>
     * 根据连接参数构建SNMP连接信息域对象
     * </p>
     *
     * @param connection SNMP连接请求参数
     * @return {@link ConnectionDomain} SNMP连接信息
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    ConnectionDomain buildConnectionDomain(C connection);

    /**
     * <p>
     * 获取连接参数中的通信协议类型
     * </p>
     *
     * @param connection SNMP连接请求参数
     * @return {@link CommProtocolTypeEnums} 通信协议类型
     * @author 皮锋
     * @custom.date 2026/4/9 22:00
     */
    CommProtocolTypeEnums getProtocol(C connection);

}
