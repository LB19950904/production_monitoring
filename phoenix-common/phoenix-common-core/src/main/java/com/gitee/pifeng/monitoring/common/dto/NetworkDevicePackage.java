package com.gitee.pifeng.monitoring.common.dto;

import com.gitee.pifeng.monitoring.common.domain.NetworkDevice;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 网络设备信息包
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/15 15:26
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class NetworkDevicePackage extends BaseRequestPackage {

    /**
     * 网络设备信息
     */
    private NetworkDevice networkDevice;

    /**
     * 传输频率
     */
    private long rate;

}
