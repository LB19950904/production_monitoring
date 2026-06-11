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
 * docker容器信息表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/25 15:55
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_DOCKER_CONTAINER")
public class MonitorDockerContainer {

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
     * 容器ID
     */
    @TableField("CONTAINER_ID")
    private String containerId;

    /**
     * 容器名字
     */
    @TableField("CONTAINER_NAME")
    private String containerName;

    /**
     * 容器端口
     */
    @TableField("CONTAINER_PORTS")
    private String containerPorts;

    /**
     * 容器标识
     */
    @TableField("CONTAINER_LABELS")
    private String containerLabels;

    /**
     * 镜像ID
     */
    @TableField("IMAGE_ID")
    private String imageId;

    /**
     * 镜像名字
     */
    @TableField("IMAGE_NAME")
    private String imageName;

    /**
     * 命令
     */
    @TableField("COMMAND")
    private String command;

    /**
     * 容器创建时间
     */
    @TableField("CREATED")
    private Date created;

    /**
     * 容器状态
     */
    @TableField("STATUS")
    private String status;

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
