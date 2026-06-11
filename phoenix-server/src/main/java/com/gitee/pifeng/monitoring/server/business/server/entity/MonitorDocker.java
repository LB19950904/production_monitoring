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
 * docker表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_DOCKER")
public class MonitorDocker {

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
     * 架构
     */
    @TableField("ARCHITECTURE")
    private String architecture;

    /**
     * 容器数量
     */
    @TableField("CONTAINERS")
    private Integer containers;

    /**
     * 停止的容器数量
     */
    @TableField("CONTAINERS_STOPPED")
    private Integer containersStopped;

    /**
     * 暂停的容器数量
     */
    @TableField("CONTAINERS_PAUSED")
    private Integer containersPaused;

    /**
     * 运行中的容器数量
     */
    @TableField("CONTAINERS_RUNNING")
    private Integer containersRunning;

    /**
     * 是否debug模式（0：否，1：是）
     */
    @TableField("IS_DEBUG")
    private String isDebug;

    /**
     * docker根目录
     */
    @TableField("DOCKER_ROOT_DIR")
    private String dockerRootDir;

    /**
     * 镜像数量
     */
    @TableField("IMAGES")
    private Integer images;

    /**
     * 内核版本
     */
    @TableField("KERNEL_VERSION")
    private String kernelVersion;

    /**
     * 是否限制内存大小（0：否，1：是）
     */
    @TableField("IS_MEMORY_LIMIT")
    private String isMemoryLimit;

    /**
     * 内存总大小
     */
    @TableField("MEM_TOTAL")
    private Long memTotal;

    /**
     * 服务版本
     */
    @TableField("SERVER_VERSION")
    private String serverVersion;

    /**
     * CPU核数
     */
    @TableField("CPU_NUM")
    private Integer cpuNum;

    /**
     * 监听的事件数量
     */
    @TableField("EVENTS_LISTENER_NUM")
    private Integer eventsListenerNum;

    /**
     * 完整信息（Json字符串）
     */
    @TableField("RAW_VALUES")
    private String rawValues;

    /**
     * docker服务状态（0：离线，1：在线）
     */
    @TableField("IS_ONLINE")
    private String isOnline;

    /**
     * 是否开启监控（0：不开启监控；1：开启监控）
     */
    @TableField("IS_ENABLE_MONITOR")
    private String isEnableMonitor;

    /**
     * 是否开启告警（0：不开启告警；1：开启告警）
     */
    @TableField("IS_ENABLE_ALARM")
    private String isEnableAlarm;

    /**
     * 离线次数
     */
    @TableField("OFFLINE_COUNT")
    private Integer offlineCount;

    /**
     * 连接频率
     */
    @TableField("CONN_FREQUENCY")
    private Integer connFrequency;

    /**
     * 监控环境
     */
    @TableField("MONITOR_ENV")
    private String monitorEnv;

    /**
     * 监控分组
     */
    @TableField("MONITOR_GROUP")
    private String monitorGroup;

    /**
     * docker摘要
     */
    @TableField("DOCKER_SUMMARY")
    private String dockerSummary;

    /**
     * 监控代理通信客户端ID
     */
    @TableField("AGENT_COMM_CLIENT_ID")
    private String agentCommClientId;

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
