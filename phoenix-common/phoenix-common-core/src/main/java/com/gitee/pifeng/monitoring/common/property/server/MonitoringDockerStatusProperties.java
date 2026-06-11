package com.gitee.pifeng.monitoring.common.property.server;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import lombok.*;

/**
 * <p>
 * docker服务状态配置属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/7 20:29
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MonitoringDockerStatusProperties implements ISuperBean {

    /**
     * 是否监控docker服务状态
     */
    private boolean enable;

    /**
     * 告警是否打开
     */
    private boolean alarmEnable;

}
