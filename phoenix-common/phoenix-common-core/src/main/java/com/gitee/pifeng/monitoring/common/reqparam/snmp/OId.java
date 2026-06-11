package com.gitee.pifeng.monitoring.common.reqparam.snmp;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import com.gitee.pifeng.monitoring.common.util.snmp.SnmpCommonUtils;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * MIB-II OID 请求参数
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-4-10 14:47
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class OId extends AbstractSuperBean {

    /**
     * 系统信息 MIB-II OID 请求参数
     */
    private SysInfoOId sysInfoOid;

    /**
     * 网络接口信息 MIB-II OID 请求参数
     */
    private IfInfoOId ifInfoOid;

    /**
     * <p>
     * 构建默认 MIB-II OID
     * </p>
     *
     * @return MIB-II OID 请求参数
     * @author 皮锋
     * @custom.date 2025-4-10 15:04
     */
    public OId builderDefaultValues() {
        this.sysInfoOid = SnmpCommonUtils.defaultIfBlank(this.sysInfoOid);
        this.ifInfoOid = SnmpCommonUtils.defaultIfBlank(this.ifInfoOid);
        return this;
    }

}