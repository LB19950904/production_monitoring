package com.gitee.pifeng.monitoring.common.property.server;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import lombok.*;

/**
 * <p>
 * 告警飞书配置属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/5/15 15:23
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MonitoringAlarmFeishuProperties implements ISuperBean {

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
     * open_id 或 user_id
     */
    private String[] userIds;

}