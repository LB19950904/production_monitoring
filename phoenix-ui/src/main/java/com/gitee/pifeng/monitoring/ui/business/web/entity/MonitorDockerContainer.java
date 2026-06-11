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
 * docker容器信息表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-25
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_DOCKER_CONTAINER")
@Schema(description = "MonitorDockerContainer对象")
public class MonitorDockerContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @Schema(description = "服务器IP")
    @TableField("SERVER_IP")
    private String serverIp;

    @Schema(description = "容器ID")
    @TableField("CONTAINER_ID")
    private String containerId;

    @Schema(description = "容器名字")
    @TableField("CONTAINER_NAME")
    private String containerName;

    @Schema(description = "容器端口")
    @TableField("CONTAINER_PORTS")
    private String containerPorts;

    @Schema(description = "容器标识")
    @TableField("CONTAINER_LABELS")
    private String containerLabels;

    @Schema(description = "镜像ID")
    @TableField("IMAGE_ID")
    private String imageId;

    @Schema(description = "镜像名字")
    @TableField("IMAGE_NAME")
    private String imageName;

    @Schema(description = "命令")
    @TableField("COMMAND")
    private String command;

    @Schema(description = "容器创建时间")
    @TableField("CREATED")
    private Date created;

    @Schema(description = "容器状态")
    @TableField("STATUS")
    private String status;

    @Schema(description = "添加时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
