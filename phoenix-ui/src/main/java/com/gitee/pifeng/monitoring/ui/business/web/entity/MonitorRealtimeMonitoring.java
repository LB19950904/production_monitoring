package com.gitee.pifeng.monitoring.ui.business.web.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 实时监控表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_REALTIME_MONITORING")
@Schema(description = "MonitorRealtimeMonitoring对象")
public class MonitorRealtimeMonitoring implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @Schema(description = "监控类型（SERVER、NET、TCP4SERVICE、HTTP4SERVICE、DOCKER、INSTANCE、DATABASE、CUSTOM）")
    @TableField("TYPE")
    private String type;

    @Schema(description = "监控子类型（用于删除实时监控信息，不用于实时监控判断）")
    @TableField("SUB_TYPE")
    private String subType;

    @Schema(description = "监控编号")
    @TableField("CODE")
    private String code;

    @Schema(description = "被告警主体唯一ID（用于删除实时监控信息，不用于实时监控判断）")
    @TableField("ALERTED_ENTITY_ID")
    private String alertedEntityId;

    @Schema(description = "故障告警次数")
    @TableField("FAILURE_ALARM_COUNT")
    private Integer failureAlarmCount;

    @Schema(description = "恢复告警次数")
    @TableField("RECOVERY_ALARM_COUNT")
    private Integer recoveryAlarmCount;

    @Schema(description = "允许的下次故障报警时间")
    @TableField("NEXT_ALLOWED_FAILURE_ALARM_TIME")
    private Date nextAllowedFailureAlarmTime;

    @Schema(description = "允许的下次恢复告警时间")
    @TableField("NEXT_ALLOWED_RECOVERY_ALARM_TIME")
    private Date nextAllowedRecoveryAlarmTime;

    @Schema(description = "插入时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
