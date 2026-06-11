package com.gitee.pifeng.monitoring.common.dto;

import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 心跳包
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月4日 下午12:20:06
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class HeartbeatPackage extends BaseRequestPackage {

    /**
     * 心跳频率
     */
    private long rate;

    /**
     * 是否启用Arthas（默认值为true，因为要兼容低版本，低版本没有此属性）
     *
     * @since 2.0.5.RELEASE
     */
    private boolean isEnableArthas = false;

    /**
     * 是否收集VM指标（默认值为true，因为要兼容低版本，低版本没有此属性）
     *
     * @since 2.0.5.RELEASE
     */
    private boolean isCollectVmMetrics = true;

    /**
     * 是否收集线程池指标（默认值为true，因为要兼容低版本，低版本没有此属性）
     *
     * @since 2.0.5.RELEASE
     */
    private boolean isCollectThreadPoolMetrics = false;

}
