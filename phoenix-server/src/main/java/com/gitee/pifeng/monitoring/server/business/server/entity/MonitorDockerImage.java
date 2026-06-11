package com.gitee.pifeng.monitoring.server.business.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

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
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_DOCKER_IMAGE")
public class MonitorDockerImage {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 服务器IP
     */
    @TableField("SERVER_IP")
    private String serverIp;

    /**
     * 镜像ID
     */
    @TableField("IMAGE_ID")
    private String imageId;

    /**
     * 镜像仓库
     */
    @TableField("IMAGE_REPOSITORY")
    private String imageRepository;

    /**
     * 镜像标签
     */
    @TableField("IMAGE_TAG")
    private String imageTag;

    /**
     * 镜像大小
     */
    @TableField("IMAGE_SIZE")
    private Long imageSize;

    /**
     * 镜像标识
     */
    @TableField("IMAGE_LABELS")
    private String imageLabels;

    /**
     * 镜像创建时间
     */
    @TableField("CREATED")
    private Date created;

    /**
     * 新增时间
     */
    @TableField("INSERT_TIME")
    private Date insertTime;

    /**
     * 更新时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
