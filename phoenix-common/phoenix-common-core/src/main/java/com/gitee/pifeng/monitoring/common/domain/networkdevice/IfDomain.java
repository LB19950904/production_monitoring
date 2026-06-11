package com.gitee.pifeng.monitoring.common.domain.networkdevice;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * 网络接口信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/07 20:23
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class IfDomain extends AbstractSuperBean {

    /**
     * 描述：系统上的网络接口数量。
     */
    private Integer ifNumber;

    /**
     * 描述：网络接口信息。
     */
    private List<IfDomain.IfInfoDomain> ifList;

    @Data
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class IfInfoDomain extends AbstractSuperBean {

        /**
         * 描述：网络接口的索引号。
         */
        private Integer ifIndex;

        /**
         * 描述：网络接口的描述。
         */
        private String ifDescr;

        /**
         * 描述：网络接口的类型。
         */
        private String ifType;

        /**
         * 描述：网络接口的最大传输单元（MTU）。
         */
        private Long ifMtu;

        /**
         * 描述：网络接口的速率（以比特/秒为单位）。
         */
        private Long ifSpeed;

        /**
         * 描述：网络接口的物理地址（MAC地址）。
         */
        private String ifPhysAddress;

        /**
         * 描述：网络接口的管理状态（1=up,2=down,3=testing）。
         */
        private String ifAdminStatus;

        /**
         * 描述：网络接口的操作状态（1=up,2=down,3=testing,4=unknown,5=dormant,6=notPresent,7=lowerLayerDown）。
         */
        private String ifOperStatus;

        /**
         * 描述：网络接口接收到的字节数。
         */
        private Long ifInOctets;

        /**
         * 描述：网络接口发送的字节数。
         */
        private Long ifOutOctets;

        /**
         * 描述：网络接口实时接收速率（以比特/秒为单位）。
         */
        private Long ifInRealTimeSpeed;

        /**
         * 描述：网络接口实时发送速率（以比特/秒为单位）。
         */
        private Long ifOutRealTimeSpeed;

    }

}
