package com.gitee.pifeng.monitoring.common.reqparam.snmp.v2c;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import com.gitee.pifeng.monitoring.common.constant.CommProtocolTypeEnums;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * SNMP连接请求参数
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/08 20:53
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
     * 社区字符串
     */
    private String community;

}