package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerImage;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * docker镜像信息表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/8/21 20:37
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "docker镜像信息表现层对象")
public class MonitorDockerImageVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "服务器IP")
    private String serverIp;

    @Schema(description = "镜像ID")
    private String imageId;

    @Schema(description = "镜像仓库")
    private String imageRepository;

    @Schema(description = "镜像标签")
    private String imageTag;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "镜像大小")
    private Long imageSize;

    @Schema(description = "镜像大小（格式化）")
    private String imageSizeStr;

    @Schema(description = "镜像标识")
    private String imageLabels;

    @Schema(description = "镜像创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date created;

    @Schema(description = "新增时间")
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
     * MonitorDockerImageVo转MonitorDockerImage
     * </p>
     *
     * @return {@link MonitorDockerImage}
     * @author 皮锋
     * @custom.date 2020/9/3 9:20
     */
    public MonitorDockerImage convertTo() {
        MonitorDockerImage monitorDockerImage = MonitorDockerImage.builder().build();
        BeanUtils.copyProperties(this, monitorDockerImage);
        return monitorDockerImage;
    }

    /**
     * <p>
     * MonitorDockerImage转MonitorDockerImageVo
     * </p>
     *
     * @param monitorDockerImage {@link MonitorDockerImage}
     * @return {@link MonitorDockerImageVo}
     * @author 皮锋
     * @custom.date 2020/9/3 9:22
     */
    public MonitorDockerImageVo convertFor(MonitorDockerImage monitorDockerImage) {
        if (null != monitorDockerImage) {
            BeanUtils.copyProperties(monitorDockerImage, this);
        }
        return this;
    }

}
