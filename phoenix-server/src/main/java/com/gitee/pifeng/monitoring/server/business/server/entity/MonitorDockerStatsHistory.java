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
 * docker容器统计信息历史记录表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-09-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_DOCKER_STATS_HISTORY")
public class MonitorDockerStatsHistory {

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
    private Double cpuUtilizationRate;

    /**
     * 当前使用的内存
     */
    @TableField("MEN_USAGE")
    private Long menUsage;

    /**
     * 最大可以使用的内存
     */
    @TableField("MEN_LIMIT")
    private Long menLimit;

    /**
     * 内存使用情况
     */
    @TableField("MEN_UTILIZATION_RATE")
    private Double menUtilizationRate;

    /**
     * 网络 input 数据
     */
    @TableField("NET_IN")
    private Long netIn;

    /**
     * 网络 output 数据
     */
    @TableField("NET_OUT")
    private Long netOut;

    /**
     * 磁盘 input 数据
     */
    @TableField("BLOCK_IN")
    private Long blockIn;

    /**
     * 磁盘 output 数据
     */
    @TableField("BLOCK_OUT")
    private Long blockOut;

    /**
     * PID号
     */
    @TableField("PIDS")
    private String pids;

    /**
     * 网络 input 数据 速率，单位：B/s
     */
    @TableField("NET_IN_SPEED")
    private Double netInSpeed;

    /**
     * 网络 output 数据 速率，单位：B/s
     */
    @TableField("NET_OUT_SPEED")
    private Double netOutSpeed;

    /**
     * 磁盘 input 数据 速率，单位：B/s
     */
    @TableField("BLOCK_IN_SPEED")
    private Double blockInSpeed;

    /**
     * 磁盘 output 数据 速率，单位：B/s
     */
    @TableField("BLOCK_OUT_SPEED")
    private Double blockOutSpeed;

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
