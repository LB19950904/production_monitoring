package com.gitee.pifeng.monitoring.common.domain;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 命令信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/9/20 21:29
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Command extends AbstractSuperBean {

    /**
     * 监控类型
     */
    private MonitorTypeEnums monitorTypeEnum;

    /**
     * 命令类型
     */
    private String commandType;

    /**
     * 命令动作
     */
    private String commandAction;

    /**
     * 命令目标
     */
    private String commandTarget;

    /**
     * 命令值
     */
    private String commandValue;

}
