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
 * docker事件信息表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_DOCKER_EVENT")
@Schema(description = "MonitorDockerEvent对象")
public class MonitorDockerEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @Schema(description = "服务器IP")
    @TableField("SERVER_IP")
    private String serverIp;

    @Schema(description = "事件状态")
    @TableField("EVENT_STATUS")
    private String eventStatus;

    @Schema(description = "事件ID")
    @TableField("EVENT_ID")
    private String eventId;

    @Schema(description = "事件来源")
    @TableField("EVENT_FROM")
    private String eventFrom;

    @Schema(description = "事件类型")
    @TableField("EVENT_TYPE")
    private String eventType;

    @Schema(description = "事件动作")
    @TableField("EVENT_ACTION")
    private String eventAction;

    @Schema(description = "事件属性")
    @TableField("EVENT_ATTRIBUTE")
    private String eventAttribute;

    @Schema(description = "事件发生时间")
    @TableField("HAPPEN_TIME")
    private Date happenTime;

    @Schema(description = "新增时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
