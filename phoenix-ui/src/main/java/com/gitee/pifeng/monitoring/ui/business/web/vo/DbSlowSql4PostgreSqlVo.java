package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlPostgreSql;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * PostgreSQL数据库慢SQL表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-06-13
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "PostgreSQL数据库慢SQL表现层对象")
public class DbSlowSql4PostgreSqlVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "数据库表ID")
    private Long dbId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "会话ID")
    private Long sessionId;

    @Schema(description = "用户")
    @TableField("USER_NAME")
    private String userName;

    @Schema(description = "主机")
    private String host;

    @Schema(description = "数据库名")
    private String dbName;

    @Schema(description = "命令")
    private String command;

    @Schema(description = "执行时间（秒）")
    private String executionTimeStr;

    @Schema(description = "状态")
    private String state;

    @Schema(description = "SQL文本")
    private String sqlText;

    @Schema(description = "格式标准化后的SQL文本")
    private String normalizeSqlText;

    @Schema(description = "参数化处理后的SQL文本")
    private String parameterizeSqlText;

    @Schema(description = "SQL MD5值")
    private String sqlMd5Hex;

    @Schema(description = "判断阈值（秒）")
    private String thresholdTimeStr;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "检测时间")
    private Date detectTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "插入时间")
    private Date insertTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "更新时间")
    private Date updateTime;

    /**
     * <p>
     * DbSlowSql4PostgreSqlVo转MonitorDbSlowSqlPostgreSql
     * </p>
     *
     * @return {@link MonitorDbSlowSqlPostgreSql}
     * @author 皮锋
     * @custom.date 2025/6/13 16:00
     */
    public MonitorDbSlowSqlPostgreSql convertTo() {
        MonitorDbSlowSqlPostgreSql monitorDbSlowSqlPostgreSql = MonitorDbSlowSqlPostgreSql.builder().build();
        BeanUtils.copyProperties(this, monitorDbSlowSqlPostgreSql);
        return monitorDbSlowSqlPostgreSql;
    }

    /**
     * <p>
     * MonitorDbSlowSqlPostgreSql转DbSlowSql4PostgreSqlVo
     * </p>
     *
     * @param monitorDbSlowSqlPostgreSql {@link MonitorDbSlowSqlPostgreSql}
     * @return {@link DbSlowSql4PostgreSqlVo}
     * @author 皮锋
     * @custom.date 2025/6/13 16:00
     */
    public DbSlowSql4PostgreSqlVo convertFor(MonitorDbSlowSqlPostgreSql monitorDbSlowSqlPostgreSql) {
        if (null != monitorDbSlowSqlPostgreSql) {
            BeanUtils.copyProperties(monitorDbSlowSqlPostgreSql, this);
        }
        return this;
    }

}
