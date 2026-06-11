package com.gitee.pifeng.monitoring.common.property.server;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import lombok.*;

/**
 * <p>
 * 告警企业微信配置属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/25 21:04
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MonitoringAlarmEnterpriseWechatProperties implements ISuperBean {

    /**
     * Webhook
     */
    private String webhook;

    /**
     * 是否发送所有人
     */
    private Boolean isAtAll;

    /**
     * 手机号码
     */
    private String[] phoneNumbers;

}
