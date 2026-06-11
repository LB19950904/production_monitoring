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
 * Oracle数据库慢SQL表
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
@TableName("MONITOR_DB_SLOW_SQL_ORACLE")
@Schema(description = "MonitorDbSlowSqlOracle对象")
public class MonitorDbSlowSqlOracle implements Serializable {

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
    @TableField("SID")
    private Long sid;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "序列号")
    @TableField("`SERIAL#`")
    private Long serial;

    @Schema(description = "用户")
    @TableField("USER_NAME")
    private String userName;

    @Schema(description = "模式")
    @TableField("SCHEMA_NAME")
    private String schemaName;

    @Schema(description = "会话类型")
    @TableField("SESSION_TYPE")
    private String sessionType;

    @Schema(description = "状态")
    @TableField("STATE")
    private String state;

    @Schema(description = "登录时间")
    @TableField("LOGON_TIME")
    private Date logonTime;

    @Schema(description = "机器")
    @TableField("MACHINE")
    private String machine;

    @Schema(description = "操作系统用户")
    @TableField("OS_USER")
    private String osUser;

    @Schema(description = "程序")
    @TableField("PROGRAM")
    private String program;

    @Schema(description = "事件")
    @TableField("EVENT")
    private String event;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "等待时间（秒）")
    @TableField("WAIT_TIME")
    private Long waitTime;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "SQL已执行时间（秒）")
    @TableField("LAST_CALL_ET")
    private Long lastCallEt;

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
