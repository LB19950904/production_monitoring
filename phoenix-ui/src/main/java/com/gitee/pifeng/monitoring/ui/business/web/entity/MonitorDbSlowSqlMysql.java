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
 * MySQL数据库慢SQL表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-08
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("MONITOR_DB_SLOW_SQL_MYSQL")
@Schema(description = "MonitorDbSlowSqlMysql对象")
public class MonitorDbSlowSqlMysql implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "数据库表ID")
    @TableField("DB_ID")
    private Long dbId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "会话ID")
    @TableField("SESSION_ID")
    private Long sessionId;

    @Schema(description = "用户")
    @TableField("USER_NAME")
    private String userName;

    @Schema(description = "主机")
    @TableField("HOST")
    private String host;

    @Schema(description = "数据库名")
    @TableField("DB_NAME")
    private String dbName;

    @Schema(description = "命令")
    @TableField("COMMAND")
    private String command;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "执行时间（秒）")
    @TableField("EXECUTION_TIME")
    private Long executionTime;

    @Schema(description = "状态")
    @TableField("STATE")
    private String state;

    @Schema(description = "SQL文本")
    @TableField("SQL_TEXT")
    private String sqlText;

    @Schema(description = "格式标准化后的SQL文本")
    @TableField("NORMALIZE_SQL_TEXT")
    private String normalizeSqlText;

    @Schema(description = "参数化处理后的SQL文本")
    @TableField("PARAMETERIZE_SQL_TEXT")
    private String parameterizeSqlText;

    @Schema(description = "SQL MD5值")
    @TableField("SQL_MD5_HEX")
    private String sqlMd5Hex;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "判断阈值（秒）")
    @TableField("THRESHOLD_TIME")
    private Long thresholdTime;

    @Schema(description = "检测时间")
    @TableField("DETECT_TIME")
    private Date detectTime;

    @Schema(description = "插入时间")
    @TableField("INSERT_TIME")
    private Date insertTime;

    @Schema(description = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;

}
