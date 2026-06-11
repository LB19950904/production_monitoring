package com.gitee.pifeng.monitoring.common.property.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * 与WebSocket通信相关的监控属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/2/3 09:06
 */
@Data
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class MonitoringCommWebSocketProperties {

    /**
     * 监控服务端url
     */
    private String url;

}