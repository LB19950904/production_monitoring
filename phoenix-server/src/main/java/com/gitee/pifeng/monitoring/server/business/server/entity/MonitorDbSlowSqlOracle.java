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
 * Oracle数据库慢SQL表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_DB_SLOW_SQL_ORACLE")
public class MonitorDbSlowSqlOracle implements Serializable {

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
    @TableField("SID")
    private Long sid;

    /**
     * 序列号
     */
    @TableField("`SERIAL#`")
    private Long serial;

    /**
     * 用户
     */
    @TableField("USER_NAME")
    private String userName;

    /**
     * 模式
     */
    @TableField("SCHEMA_NAME")
    private String schemaName;

    /**
     * 会话类型
     */
    @TableField("SESSION_TYPE")
    private String sessionType;

    /**
     * 状态
     */
    @TableField("STATE")
    private String state;

    /**
     * 登录时间
     */
    @TableField("LOGON_TIME")
    private Date logonTime;

    /**
     * 机器
     */
    @TableField("MACHINE")
    private String machine;

    /**
     * 操作系统用户
     */
    @TableField("OS_USER")
    private String osUser;

    /**
     * 程序
     */
    @TableField("PROGRAM")
    private String program;

    /**
     * 事件
     */
    @TableField("EVENT")
    private String event;

    /**
     * 等待时间（秒）
     */
    @TableField("WAIT_TIME")
    private Long waitTime;

    /**
     * SQL已执行时间（秒）
     */
    @TableField("LAST_CALL_ET")
    private Long lastCallEt;

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
