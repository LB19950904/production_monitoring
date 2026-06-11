package com.gitee.pifeng.monitoring.common.domain.docker;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * docker系统信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/4 14:55
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class InfoDomain extends AbstractSuperBean {

    /**
     * 架构
     */
    private String architecture;

    /**
     * 容器数量
     */
    private Integer containers;

    /**
     * 停止的容器数量
     */
    private Integer containersStopped;

    /**
     * 暂停的容器数量
     */
    private Integer containersPaused;

    /**
     * 运行中的容器数量
     */
    private Integer containersRunning;

    /**
     * 是否debug模式
     */
    private Boolean debug;

    /**
     * docker根目录
     */
    private String dockerRootDir;

    /**
     * 镜像数量
     */
    private Integer images;

    /**
     * 内核版本
     */
    private String kernelVersion;

    /**
     * 是否限制内存大小
     */
    private Boolean memoryLimit;

    /**
     * 内存总大小
     */
    private Long memTotal;

    /**
     * 服务版本
     */
    private String serverVersion;

    /**
     * CPU核数
     */
    private Integer cpus;

    /**
     * 监听的事件数量
     */
    private Integer eventsListeners;

    /**
     * 完整信息（Json字符串）
     */
    private String rawValues;

}
