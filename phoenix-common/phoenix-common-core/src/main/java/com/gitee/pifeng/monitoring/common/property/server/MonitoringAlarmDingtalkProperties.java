package com.gitee.pifeng.monitoring.common.property.server;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import lombok.*;

/**
 * <p>
 * 告警钉钉配置属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/24 16:06
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MonitoringAlarmDingtalkProperties implements ISuperBean {

    /**
     * 秘钥
     */
    private String secret;

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
