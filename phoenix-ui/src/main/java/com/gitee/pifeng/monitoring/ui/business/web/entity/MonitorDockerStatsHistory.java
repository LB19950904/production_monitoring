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
 * docker容器统计信息历史记录表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-09-09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_DOCKER_STATS_HISTORY")
@Schema(description = "MonitorDockerStatsHistory对象")
public class MonitorDockerStatsHistory implements Serializable {

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

    @Schema(description = "CPU的使用情况")
    @TableField("CPU_UTILIZATION_RATE")
    private Double cpuUtilizationRate;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "当前使用的内存")
    @TableField("MEN_USAGE")
    private Long menUsage;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "最大可以使用的内存")
    @TableField("MEN_LIMIT")
    private Long menLimit;

    @Schema(description = "内存使用情况")
    @TableField("MEN_UTILIZATION_RATE")
    private Double menUtilizationRate;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络 input 数据")
    @TableField("NET_IN")
    private Long netIn;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "网络 output 数据")
    @TableField("NET_OUT")
    private Long netOut;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "磁盘 input 数据")
    @TableField("BLOCK_IN")
    private Long blockIn;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "磁盘 output 数据")
    @TableField("BLOCK_OUT")
    private Long blockOut;

    @Schema(description = "PID号")
    @TableField("PIDS")
    private String pids;

    @Schema(description = "新增时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
