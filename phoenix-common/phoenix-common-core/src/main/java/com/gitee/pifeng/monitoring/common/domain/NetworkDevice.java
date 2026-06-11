package com.gitee.pifeng.monitoring.common.domain;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.ConnectionDomain;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.IfDomain;
import com.gitee.pifeng.monitoring.common.domain.networkdevice.SysDomain;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 网络设备信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/07 20:30
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class NetworkDevice extends AbstractSuperBean {

    /**
     * SNMP连接信息
     */
    private ConnectionDomain connectionDomain;

    /**
     * 系统信息
     */
    private SysDomain sysDomain;

    /**
     * 网络接口信息
     */
    private IfDomain ifDomain;

}
