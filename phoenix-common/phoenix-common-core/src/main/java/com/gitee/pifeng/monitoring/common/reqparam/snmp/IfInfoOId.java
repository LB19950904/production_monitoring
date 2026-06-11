package com.gitee.pifeng.monitoring.common.reqparam.snmp;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 网络接口信息 MIB-II OID 请求参数
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/09 10:25
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class IfInfoOId extends AbstractSuperBean {

    /**
     * 描述：系统上的网络接口数量。
     */
    private String ifNumberOid;

    /**
     * 描述：网络接口的索引号。
     */
    private String ifIndexOid;

    /**
     * 描述：网络接口的描述。
     */
    private String ifDescrOid;

    /**
     * 描述：网络接口的类型。
     */
    private String ifTypeOid;

    /**
     * 描述：网络接口的最大传输单元（MTU）。
     */
    private String ifMtuOid;

    /**
     * 描述：网络接口的速率（以比特/秒为单位）。
     */
    private String ifSpeedOid;

    /**
     * 描述：网络接口的物理地址（MAC地址）。
     */
    private String ifPhysAddressOid;

    /**
     * 描述：网络接口的管理状态（1=up,2=down,3=testing）。
     */
    private String ifAdminStatusOid;

    /**
     * 描述：网络接口的操作状态（1=up,2=down,3=testing,4=unknown,5=dormant,6=notPresent,7=lowerLayerDown）。
     */
    private String ifOperStatusOid;

    /**
     * 描述：网络接口接收到的字节数。
     */
    private String ifInOctetsOid;

    /**
     * 描述：网络接口发送的字节数。
     */
    private String ifOutOctetsOid;

}
