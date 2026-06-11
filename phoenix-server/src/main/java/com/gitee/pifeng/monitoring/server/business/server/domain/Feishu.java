package com.gitee.pifeng.monitoring.server.business.server.domain;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;

/**
 * <p>
 * 飞书实体对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/5/15 15:26
 */
@Data
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Feishu extends AbstractSuperBean {

    /**
     * 秘钥
     */
    private String secret;

    /**
     * Webhook
     */
    private String webhook;

    /**
     * open_id 或 user_id
     */
    private String[] userIds;

    /**
     * 是否发送所有人
     */
    private Boolean isAtAll;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

}