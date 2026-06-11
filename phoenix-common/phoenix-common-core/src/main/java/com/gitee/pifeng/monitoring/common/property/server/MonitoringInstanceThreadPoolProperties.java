package com.gitee.pifeng.monitoring.common.property.server;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import lombok.*;

/**
 * <p>
 * 应用实例线程池配置属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-2-18 11:34
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MonitoringInstanceThreadPoolProperties implements ISuperBean {

    /**
     * 是否监控应用实例线程池
     */
    private boolean enable;

    /**
     * 告警是否打开
     */
    private boolean alarmEnable;

}