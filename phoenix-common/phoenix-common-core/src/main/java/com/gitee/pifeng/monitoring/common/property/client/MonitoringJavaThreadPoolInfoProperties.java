package com.gitee.pifeng.monitoring.common.property.client;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * Java线程池信息属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/13 22:37
 */
@Data
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class MonitoringJavaThreadPoolInfoProperties implements ISuperBean {

    /**
     * 是否采集Java线程池信息
     */
    private Boolean enable;

    /**
     * 发送Java线程池信息的频率
     */
    private Long rate;

}