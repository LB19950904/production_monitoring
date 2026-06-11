package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * home页的docker服务表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/9/15 15:35
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "home页的docker服务表现层对象")
public class HomeDockerVo implements ISuperBean {

    @Schema(description = "docker服务数量")
    private Integer dockerSum;

    @Schema(description = "docker服务在线数量")
    private Integer dockerOnLineSum;

    @Schema(description = "docker服务离线数量")
    private Integer dockerOffLineSum;

    @Schema(description = "docker服务未知状态数量")
    private Integer dockerUnknownLineSum;

    @Schema(description = "docker服务在线率")
    private String dockerOnLineRate;

}
