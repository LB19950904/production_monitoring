package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlMysql;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * MySQL数据库慢SQL表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/1/14 16:25
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "MySQL数据库慢SQL表现层对象")
public class DbSlowSql4MysqlVo implements ISuperBean {

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
     * DbSlowSql4MysqlVo转MonitorDbSlowSqlMysql
     * </p>
     *
     * @return {@link MonitorDbSlowSqlMysql}
     * @author 皮锋
     * @custom.date 2026/1/15 8:54
     */
    public MonitorDbSlowSqlMysql convertTo() {
        MonitorDbSlowSqlMysql monitorDbSlowSqlMysql = MonitorDbSlowSqlMysql.builder().build();
        BeanUtils.copyProperties(this, monitorDbSlowSqlMysql);
        return monitorDbSlowSqlMysql;
    }

    /**
     * <p>
     * MonitorDbSlowSqlMysql转DbSlowSql4MysqlVo
     * </p>
     *
     * @param monitorDbSlowSqlMysql {@link MonitorDbSlowSqlMysql}
     * @return {@link DbSlowSql4MysqlVo}
     * @author 皮锋
     * @custom.date 2026/1/15 8:54
     */
    public DbSlowSql4MysqlVo convertFor(MonitorDbSlowSqlMysql monitorDbSlowSqlMysql) {
        if (null != monitorDbSlowSqlMysql) {
            BeanUtils.copyProperties(monitorDbSlowSqlMysql, this);
        }
        return this;
    }

}