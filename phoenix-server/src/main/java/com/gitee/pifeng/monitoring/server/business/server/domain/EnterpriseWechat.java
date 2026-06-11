package com.gitee.pifeng.monitoring.server.business.server.domain;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;

/**
 * <p>
 * 企业微信实体对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/25 20:58
 */
@Data
@Builder
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseWechat extends AbstractSuperBean {

    /**
     * Webhook
     */
    private String webhook;

    /**
     * 接收人手机号码
     */
    private String[] phones;

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
