package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlOracle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * Oracle数据库慢SQL表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/1/16 16:33
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Oracle数据库慢SQL表现层对象")
public class DbSlowSql4OracleVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "数据库表ID")
    private Long dbId;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "会话ID")
    private Long sid;

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "序列号")
    private Long serial;

    @Schema(description = "用户")
    private String userName;

    @Schema(description = "模式")
    private String schemaName;

    @Schema(description = "会话类型")
    private String sessionType;

    @Schema(description = "状态")
    private String state;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    @Schema(description = "登录时间")
    private Date logonTime;

    @Schema(description = "机器")
    private String machine;

    @Schema(description = "操作系统用户")
    private String osUser;

    @Schema(description = "程序")
    private String program;

    @Schema(description = "事件")
    private String event;

    @Schema(description = "等待时间（秒）")
    private String waitTimeStr;

    @Schema(description = "SQL已执行时间（秒）")
    private String lastCallEtStr;

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
     * DbSlowSql4OracleVo转MonitorDbSlowSqlOracle
     * </p>
     *
     * @return {@link MonitorDbSlowSqlOracle}
     * @author 皮锋
     * @custom.date 2026/1/16 8:54
     */
    public MonitorDbSlowSqlOracle convertTo() {
        MonitorDbSlowSqlOracle monitorDbSlowSqlOracle = MonitorDbSlowSqlOracle.builder().build();
        BeanUtils.copyProperties(this, monitorDbSlowSqlOracle);
        return monitorDbSlowSqlOracle;
    }

    /**
     * <p>
     * MonitorDbSlowSqlOracle转DbSlowSql4OracleVo
     * </p>
     *
     * @param monitorDbSlowSqlOracle {@link MonitorDbSlowSqlOracle}
     * @return {@link DbSlowSql4OracleVo}
     * @author 皮锋
     * @custom.date 2026/1/16 8:54
     */
    public DbSlowSql4OracleVo convertFor(MonitorDbSlowSqlOracle monitorDbSlowSqlOracle) {
        if (null != monitorDbSlowSqlOracle) {
            BeanUtils.copyProperties(monitorDbSlowSqlOracle, this);
        }
        return this;
    }

}