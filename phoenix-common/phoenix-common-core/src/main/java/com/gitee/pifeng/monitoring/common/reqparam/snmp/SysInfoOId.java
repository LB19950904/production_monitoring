package com.gitee.pifeng.monitoring.common.reqparam.snmp;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 系统信息 MIB-II OID 请求参数
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/08 21:16
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SysInfoOId extends AbstractSuperBean {

    /**
     * 描述：系统描述，包含设备的型号、操作系统版本等信息。
     */
    private String sysDescrOid;

    /**
     * 描述：系统自上次重启以来的运行时间（以百分之一秒为单位）。
     */
    private String sysUpTimeOid;

    /**
     * 描述：系统管理员的联系信息。
     */
    private String sysContactOid;

    /**
     * 描述：系统的名称。
     */
    private String sysNameOid;

    /**
     * 描述：系统的物理位置。
     */
    private String sysLocationOid;

    /**
     * 描述：系统提供的服务类型。
     */
    private String sysServicesOid;

}
