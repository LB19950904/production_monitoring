package com.gitee.pifeng.monitoring.server.business.server.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 实时监控表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021-01-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_REALTIME_MONITORING")
public class MonitorRealtimeMonitoring {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 监控类型
     */
    @TableField("TYPE")
    private String type;

    /**
     * 监控子类型（用于删除实时监控信息，不用于实时监控判断）
     */
    @TableField("SUB_TYPE")
    private String subType;

    /**
     * 监控编号
     */
    @TableField("CODE")
    private String code;

    /**
     * 被告警主体唯一ID（用于删除实时监控信息，不用于实时监控判断）
     */
    @TableField("ALERTED_ENTITY_ID")
    private String alertedEntityId;

    /**
     * 故障告警次数
     */
    @TableField("FAILURE_ALARM_COUNT")
    private Integer failureAlarmCount;

    /**
     * 恢复告警次数
     */
    @TableField("RECOVERY_ALARM_COUNT")
    private Integer recoveryAlarmCount;

    /**
     * 允许的下次故障报警时间
     */
    @TableField(value = "NEXT_ALLOWED_FAILURE_ALARM_TIME", updateStrategy = FieldStrategy.IGNORED)
    private Date nextAllowedFailureAlarmTime;

    /**
     * 允许的下次恢复告警时间
     */
    @TableField(value = "NEXT_ALLOWED_RECOVERY_ALARM_TIME", updateStrategy = FieldStrategy.IGNORED)
    private Date nextAllowedRecoveryAlarmTime;

    /**
     * 插入时间
     */
    @TableField("INSERT_TIME")
    private Date insertTime;

    /**
     * 更新时间
     */
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
