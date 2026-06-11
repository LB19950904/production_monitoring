package com.gitee.pifeng.monitoring.common.domain.docker;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * docker事件信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/26 20:58
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class EventDomain extends AbstractSuperBean {

    /**
     * 事件状态
     */
    private String eventStatus;

    /**
     * 事件ID
     */
    private String eventId;

    /**
     * 事件来源
     */
    private String eventFrom;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件动作
     */
    private String eventAction;

    /**
     * 事件属性
     */
    private Map<String, String> eventAttributes;

    /**
     * 事件发生时间
     */
    private Date eventTime;

}
