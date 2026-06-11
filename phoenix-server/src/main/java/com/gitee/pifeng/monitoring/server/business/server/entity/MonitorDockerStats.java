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
 * docker容器统计信息表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_DOCKER_STATS")
public class MonitorDockerStats {

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
     * CPU的使用情况
     */
    @TableField("CPU_UTILIZATION_RATE")
    private String cpuUtilizationRate;

    /**
     * 当前使用的内存和最大可以使用的内存
     */
    @TableField("MEN_USAGE_LIMIT")
    private String menUsageLimit;

    /**
     * 内存使用情况
     */
    @TableField("MEN_UTILIZATION_RATE")
    private String menUtilizationRate;

    /**
     * 网络 I/O 数据
     */
    @TableField("NET_IO")
    private String netIo;

    /**
     * 磁盘 I/O 数据
     */
    @TableField("BLOCK_IO")
    private String blockIo;

    /**
     * PID号
     */
    @TableField("PIDS")
    private String pids;

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
