package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerContainer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * docker容器信息表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/8/20 15:40
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "docker容器信息表现层对象")
public class MonitorDockerContainerVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "服务器IP")
    private String serverIp;

    @Schema(description = "容器ID")
    private String containerId;

    @Schema(description = "容器名字")
    private String containerName;

    @Schema(description = "容器端口")
    private String containerPorts;

    @Schema(description = "容器标识")
    private String containerLabels;

    @Schema(description = "镜像ID")
    private String imageId;

    @Schema(description = "镜像名字")
    private String imageName;

    @Schema(description = "命令")
    private String command;

    @Schema(description = "容器创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date created;

    @Schema(description = "容器状态")
    private String status;

    @Schema(description = "添加时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date insertTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date updateTime;

    @Schema(description = "监控环境")
    private String monitorEnv;

    @Schema(description = "监控分组")
    private String monitorGroup;

    @Schema(description = "docker摘要")
    private String dockerSummary;

    @Schema(description = "docker服务状态（0：离线，1：在线）")
    private String isOnline;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "docker服务ID")
    private Long dockerId;

    /**
     * <p>
     * MonitorDockerContainerVo转MonitorDockerContainer
     * </p>
     *
     * @return {@link MonitorDockerContainer}
     * @author 皮锋
     * @custom.date 2020/9/3 9:20
     */
    public MonitorDockerContainer convertTo() {
        MonitorDockerContainer monitorDockerContainer = MonitorDockerContainer.builder().build();
        BeanUtils.copyProperties(this, monitorDockerContainer);
        return monitorDockerContainer;
    }

    /**
     * <p>
     * MonitorDockerContainer转MonitorDockerContainerVo
     * </p>
     *
     * @param monitorDockerContainer {@link MonitorDockerContainer}
     * @return {@link MonitorDockerContainerVo}
     * @author 皮锋
     * @custom.date 2020/9/3 9:22
     */
    public MonitorDockerContainerVo convertFor(MonitorDockerContainer monitorDockerContainer) {
        if (null != monitorDockerContainer) {
            BeanUtils.copyProperties(monitorDockerContainer, this);
        }
        return this;
    }

}
