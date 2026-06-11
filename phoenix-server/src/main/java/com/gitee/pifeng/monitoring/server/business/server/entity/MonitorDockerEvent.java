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
 * docker事件信息表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_DOCKER_EVENT")
public class MonitorDockerEvent {

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
     * 事件状态
     */
    @TableField("EVENT_STATUS")
    private String eventStatus;

    /**
     * 事件ID
     */
    @TableField("EVENT_ID")
    private String eventId;

    /**
     * 事件来源
     */
    @TableField("EVENT_FROM")
    private String eventFrom;

    /**
     * 事件类型
     */
    @TableField("EVENT_TYPE")
    private String eventType;

    /**
     * 事件动作
     */
    @TableField("EVENT_ACTION")
    private String eventAction;

    /**
     * 事件属性
     */
    @TableField("EVENT_ATTRIBUTE")
    private String eventAttribute;

    /**
     * 事件发生时间
     */
    @TableField("HAPPEN_TIME")
    private Date happenTime;

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
