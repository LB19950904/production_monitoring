package com.gitee.pifeng.monitoring.server.business.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * PostgreSQL数据库慢SQL表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_DB_SLOW_SQL_POSTGRESQL")
public class MonitorDbSlowSqlPostgreSql implements Serializable {

    /**
     * 主键ID
     */
    @TableId(value = "ID", type = IdType.AUTO)
    private Long id;

    /**
     * 数据库表ID
     */
    @TableField("DB_ID")
    private Long dbId;

    /**
     * 会话ID
     */
    @TableField("SESSION_ID")
    private Long sessionId;

    /**
     * 用户
     */
    @TableField("USER_NAME")
    private String userName;

    /**
     * 主机
     */
    @TableField("HOST")
    private String host;

    /**
     * 数据库名
     */
    @TableField("DB_NAME")
    private String dbName;

    /**
     * 命令
     */
    @TableField("COMMAND")
    private String command;

    /**
     * 执行时间（秒）
     */
    @TableField("EXECUTION_TIME")
    private Long executionTime;

    /**
     * 状态
     */
    @TableField("STATE")
    private String state;

    /**
     * SQL文本
     */
    @TableField("SQL_TEXT")
    private String sqlText;

    /**
     * 格式标准化后的SQL文本
     */
    @TableField("NORMALIZE_SQL_TEXT")
    private String normalizeSqlText;

    /**
     * 参数化处理后的SQL文本
     */
    @TableField("PARAMETERIZE_SQL_TEXT")
    private String parameterizeSqlText;

    /**
     * SQL MD5值
     */
    @TableField("SQL_MD5_HEX")
    private String sqlMd5Hex;

    /**
     * 判断阈值（秒）
     */
    @TableField("THRESHOLD_TIME")
    private Long thresholdTime;

    /**
     * 检测时间
     */
    @TableField("DETECT_TIME")
    private Date detectTime;

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
