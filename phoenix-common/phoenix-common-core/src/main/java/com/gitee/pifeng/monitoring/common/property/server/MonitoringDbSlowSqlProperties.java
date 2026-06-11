package com.gitee.pifeng.monitoring.common.property.server;

import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmLevelEnums;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import lombok.*;

/**
 * <p>
 * 数据库慢SQL配置属性
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/1/16 12:34
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MonitoringDbSlowSqlProperties implements ISuperBean {

    /**
     * 是否监控数据库慢SQL
     */
    private boolean enable;

    /**
     * 告警是否打开
     */
    private boolean alarmEnable;

    /**
     * 判定为慢SQL的SQL执行时间（秒）
     */
    private long judgeExecTime;

    /**
     * 监控级别，四级：INFO &#60; WARN &#60; ERROR &#60; FATAL
     */
    private AlarmLevelEnums levelEnum;

}