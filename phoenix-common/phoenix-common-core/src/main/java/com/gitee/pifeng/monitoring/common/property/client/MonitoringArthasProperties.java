package com.gitee.pifeng.monitoring.common.property.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * Arthas属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/26 12:12
 */
@Data
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class MonitoringArthasProperties {

    /**
     * 是否开启arthas
     */
    private Boolean enable;

}
