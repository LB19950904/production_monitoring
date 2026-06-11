package com.gitee.pifeng.monitoring.ui.business.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * docker镜像信息表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_DOCKER_IMAGE")
@Schema(description = "MonitorDockerImage对象")
public class MonitorDockerImage implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @Schema(description = "服务器IP")
    @TableField("SERVER_IP")
    private String serverIp;

    @Schema(description = "镜像ID")
    @TableField("IMAGE_ID")
    private String imageId;

    @Schema(description = "镜像仓库")
    @TableField("IMAGE_REPOSITORY")
    private String imageRepository;

    @Schema(description = "镜像标签")
    @TableField("IMAGE_TAG")
    private String imageTag;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "镜像大小")
    @TableField("IMAGE_SIZE")
    private Long imageSize;

    @Schema(description = "镜像标识")
    @TableField("IMAGE_LABELS")
    private String imageLabels;

    @Schema(description = "镜像创建时间")
    @TableField("CREATED")
    private Date created;

    @Schema(description = "新增时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
