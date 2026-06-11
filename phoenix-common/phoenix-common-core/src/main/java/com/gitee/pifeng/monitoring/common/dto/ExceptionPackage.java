package com.gitee.pifeng.monitoring.common.dto;

import com.gitee.pifeng.monitoring.common.domain.ExceptionInfo;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 监控异常信息包
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/2/27 16:53
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ExceptionPackage extends BaseRequestPackage {

    /**
     * 异常信息
     */
    private ExceptionInfo exceptionInfo;

    /**
     * 是否开启异常信息告警
     */
    private boolean alarmEnable;

}