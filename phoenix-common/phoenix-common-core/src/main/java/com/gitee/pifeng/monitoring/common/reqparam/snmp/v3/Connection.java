package com.gitee.pifeng.monitoring.common.reqparam.snmp.v3;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import com.gitee.pifeng.monitoring.common.constant.CommProtocolTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpAuthProtocolEnums;
import com.gitee.pifeng.monitoring.common.constant.snmp.SnmpPrivProtocolEnums;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * SNMP v3 连接请求参数
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/4/9 11:36
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Connection extends AbstractSuperBean {

    /**
     * IP地址
     */
    private String ip;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * {@link CommProtocolTypeEnums} 通信协议类型
     */
    private CommProtocolTypeEnums protocol;

    /**
     * 安全用户名
     */
    private String securityName;

    /**
     * {@link SnmpAuthProtocolEnums} 认证协议
     */
    private SnmpAuthProtocolEnums authProtocol;

    /**
     * 认证密码
     */
    private String authPassword;

    /**
     * {@link SnmpPrivProtocolEnums} 隐私（加密）协议
     */
    private SnmpPrivProtocolEnums privProtocol;

    /**
     * 隐私（加密）密码
     */
    private String privPassword;

    /**
     * 安全级别（可选，默认为认证且加密）
     * 1: noAuthNoPriv - 无认证无加密
     * 2: authNoPriv - 认证无加密
     * 3: authPriv - 认证且加密
     */
    private Integer securityLevel;

    /**
     * 上下文引擎ID（可选）
     */
    private String contextEngineId;

    /**
     * 上下文名称（可选）
     */
    private String contextName;

}
