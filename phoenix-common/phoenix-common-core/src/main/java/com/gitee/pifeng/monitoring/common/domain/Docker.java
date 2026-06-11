package com.gitee.pifeng.monitoring.common.domain;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import com.gitee.pifeng.monitoring.common.domain.docker.*;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * docker信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/24 11:29
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class Docker extends AbstractSuperBean {

    /**
     * docker系统信息
     */
    private InfoDomain infodomain;

    /**
     * docker容器信息
     */
    private ContainerDomain containerDomain;

    /**
     * docker镜像信息
     */
    private ImageDomain imageDomain;

    /**
     * docker事件信息
     */
    private EventDomain eventDomain;

    /**
     * docker统计信息
     */
    private StatsDomain statsDomain;

}
