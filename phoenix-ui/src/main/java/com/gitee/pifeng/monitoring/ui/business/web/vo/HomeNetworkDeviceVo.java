package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * home页的网络设备表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-4-3 12:47
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "home页的网络设备表现层对象")
public class HomeNetworkDeviceVo implements ISuperBean {

    @Schema(description = "网络设备数量")
    private Integer networkDeviceSum;

    @Schema(description = "网络设备在线数量")
    private Integer networkDeviceOnLineSum;

    @Schema(description = "网络设备离线数量")
    private Integer networkDeviceOffLineSum;

    @Schema(description = "网络设备未知状态数量")
    private Integer networkDeviceUnknownLineSum;

    @Schema(description = "网络设备在线率")
    private String networkDeviceOnLineRate;

}