package com.gitee.pifeng.monitoring.common.domain.networkdevice;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * SNMP连接信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-3-7 15:05
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ConnectionDomain extends AbstractSuperBean {

    /**
     * IP地址
     */
    protected String ip;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 通信协议
     */
    private String protocol;

    /**
     * SNMP协议版本
     */
    private String snmpVersion;

    /**
     * 社区字符串（SNMP v2c）
     */
    private String community;

    /**
     * 安全用户名（SNMP v3）
     */
    private String securityName;

    /**
     * 认证协议（SNMP v3）
     */
    private String authProtocol;

    /**
     * 隐私（加密）协议（SNMP v3）
     */
    private String privProtocol;

}