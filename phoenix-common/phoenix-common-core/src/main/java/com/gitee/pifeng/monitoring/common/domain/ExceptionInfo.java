package com.gitee.pifeng.monitoring.common.domain;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 异常信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/2/27 16:55
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public final class ExceptionInfo extends AbstractSuperBean {

    /**
     * 异常名称
     */
    private String excName;

    /**
     * 异常信息
     */
    private String excMessage;

    /**
     * 操作方法，类名全路径#方法名，这样就能很方便的找到这个方法。<br>
     * 例如：com.gitee.pifeng.monitoring.server.business.server.controller.ExceptionController#acceptExceptionPackage
     */
    private String operationMethod;

    /**
     * 操作用户ID，无相关信息可以不设置（是谁操作系统出现了异常）
     */
    private Long userId;

    /**
     * 操作用户名，无相关信息可以不设置（是谁操作系统出现了异常）
     */
    private String username;

    /**
     * 请求参数，无相关信息可以不设置（针对接口请求异常）
     */
    private String reqParam;

    /**
     * 请求URI，无相关信息可以不设置（针对接口请求异常）
     */
    private String reqUri;

    /**
     * 请求IP，无相关信息可以不设置（针对接口请求异常）
     */
    private String reqIp;

}