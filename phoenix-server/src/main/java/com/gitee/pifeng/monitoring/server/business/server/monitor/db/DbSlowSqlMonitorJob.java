package com.gitee.pifeng.monitoring.server.business.server.monitor.db;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.db.Entity;
import com.alibaba.druid.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.gitee.pifeng.monitoring.common.constant.DateTimeStylesEnums;
import com.gitee.pifeng.monitoring.common.constant.DbEnums;
import com.gitee.pifeng.monitoring.common.constant.ZeroOrOneConstants;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmReasonEnums;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorSubTypeEnums;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Alarm;
import com.gitee.pifeng.monitoring.common.dto.AlarmPackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.common.threadpool.MonitoredThreadPoolExecutor;
import com.gitee.pifeng.monitoring.common.util.CollectionUtils;
import com.gitee.pifeng.monitoring.common.util.DateTimeUtils;
import com.gitee.pifeng.monitoring.server.business.server.core.MonitoringConfigPropertiesLoader;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDb;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDbSlowSqlMysql;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDbSlowSqlOracle;
import com.gitee.pifeng.monitoring.server.business.server.service.*;
import com.gitee.pifeng.monitoring.server.constant.ComponentOrderConstants;
import com.gitee.pifeng.monitoring.server.util.db.DbUtils;
import com.gitee.pifeng.monitoring.server.business.server.monitor.enums.MonitorEventTitleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 在项目启动后，定时扫描数据库“MONITOR_DB”表中所有数据库连接对应的数据库慢SQL，发送告警。
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021/1/8 10:43
 */
@Slf4j
@Component
@Order(ComponentOrderConstants.DB + 3)
@DisallowConcurrentExecution
public class DbSlowSqlMonitorJob extends QuartzJobBean {

    /**
     * 监控配置属性加载器
     */
    @Autowired
    private MonitoringConfigPropertiesLoader monitoringConfigPropertiesLoader;

    /**
     * 服务端包构造器
     */
    @Autowired
    private ServerPackageConstructor serverPackageConstructor;

    /**
     * 告警服务接口
     */
    @Autowired
    private IAlarmService alarmService;

    /**
     * 数据库表服务接口
     */
    @Autowired
    private IDbService dbService;

    /**
     * MySQL数据库会话服务接口
     */
    @Autowired
    private IDbSession4MysqlService dbSession4MysqlService;

    /**
     * Oracle数据库会话服务接口
     */
    @Autowired
    private IDbSession4OracleService dbSession4OracleService;

    /**
     * MySQL数据库慢SQL服务接口
     */
    @Autowired
    private IDbSlowSqlMysqlService dbSlowSqlMysqlService;

    /**
     * Oracle数据库慢SQL服务接口
     */
    @Autowired
    private IDbSlowSqlOracleService dbSlowSqlOracleService;

    /**
     * 线程池
     */
    @Autowired
    @Qualifier("dbMonitorThreadPoolExecutor")
    private MonitoredThreadPoolExecutor dbMonitorThreadPoolExecutor;

    /**
     * <p>
     * 扫描数据库“MONITOR_DB”表中所有数据库连接对应的数据库慢SQL，发送告警。
     * </p>
     *
     * @param jobExecutionContext 作业执行上下文
     * @author 皮锋
     * @custom.date 2025/1/15 20:55
     */
    @Override
    protected void executeInternal(@NonNull JobExecutionContext jobExecutionContext) throws JobExecutionException {
        // 是否监控数据库
        boolean isEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDbProperties().isEnable();
        if (!isEnable) {
            return;
        }
        // 是否监控数据库慢SQL
        boolean isDbSlowSql = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDbProperties().getDbSlowSqlProperties().isEnable();
        if (!isDbSlowSql) {
            return;
        }
        synchronized (DbTableSpaceMonitorJob.class) {
            try {
                // 查询数据库中在线的数据库信息
                LambdaQueryWrapper<MonitorDb> monitorDbLambdaQueryWrapper = Wrappers.lambdaQuery();
                // 在线
                monitorDbLambdaQueryWrapper.eq(MonitorDb::getIsOnline, ZeroOrOneConstants.ONE);
                // 只查询 MySQL、Oracle
                monitorDbLambdaQueryWrapper.in(MonitorDb::getDbType, DbEnums.MySQL, DbEnums.Oracle);
                List<MonitorDb> monitorDbs = this.dbService.list(monitorDbLambdaQueryWrapper);
                // 打乱
                Collections.shuffle(monitorDbs);
                // 按每个list大小为10拆分成多个list
                List<List<MonitorDb>> subMonitorDbLists = CollectionUtils.split(monitorDbs, 10);
                for (List<MonitorDb> subMonitorDbs : subMonitorDbLists) {
                    // 使用多线程，加快处理速率
                    this.dbMonitorThreadPoolExecutor.execute(() -> {
                        for (MonitorDb monitorDb : subMonitorDbs) {
                            try {
                                // 是否开启监控（0：不开启监控；1：开启监控）
                                String isEnableMonitor = monitorDb.getIsEnableMonitor();
                                // 没有开启监控，直接跳过
                                if (!StringUtils.equals(ZeroOrOneConstants.ONE, isEnableMonitor)) {
                                    continue;
                                }
                                // 发现慢SQL
                                this.discoverSlowSql(monitorDb);
                            } catch (Exception e) {
                                log.error("执行数据库慢SQL监控异常！", e);
                            }
                        }
                    });
                }
            } catch (Exception e) {
                log.error("定时扫描数据库“MONITOR_DB”表中所有数据库连接对应的数据库慢SQL异常！", e);
            }
        }
    }

    /**
     * <p>
     * 发现慢SQL
     * </p>
     *
     * @param monitorDb 数据库信息
     * @throws SQLException SQL异常
     * @author 皮锋
     * @custom.date 2025/1/15 21:03
     */
    private void discoverSlowSql(MonitorDb monitorDb) throws SQLException {
        // 数据库类型
        String dbType = monitorDb.getDbType();
        // Oracle
        if (StringUtils.equalsIgnoreCase(dbType, DbType.oracle.name())) {
            this.dealWithOracle(monitorDb);
        }
        // MySQL
        else if (StringUtils.equalsIgnoreCase(dbType, DbType.mysql.name())) {
            this.dealWithMysql(monitorDb);
        }
    }

    /**
     * <p>
     * 处理 Oracle 数据库
     * </p>
     *
     * @param monitorDb 数据库信息
     * @throws SQLException SQL异常
     * @author 皮锋
     * @custom.date 2026/1/7 09:59
     */
    private void dealWithOracle(MonitorDb monitorDb) throws SQLException {
        // 判定为慢SQL的SQL执行时间（秒）
        long judgeExecTime = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDbProperties().getDbSlowSqlProperties().getJudgeExecTime();
        // 主键ID
        Long dbId = monitorDb.getId();
        // 数据库URL
        String url = monitorDb.getUrl();
        // 用户名
        String username = monitorDb.getUsername();
        // 密码
        String password = monitorDb.getPassword();
        List<Entity> entityList = this.dbSession4OracleService.getSessionList(url, username, password);
        for (Entity entity : entityList) {
            Long sid = entity.getLong("SID");
            Long serial = entity.getLong("SERIAL#");
            String uname = entity.getStr("USERNAME", StandardCharsets.UTF_8);
            String schemaName = entity.getStr("SCHEMANAME", StandardCharsets.UTF_8);
            String type = entity.getStr("TYPE", StandardCharsets.UTF_8);
            String state = entity.getStr("STATE", StandardCharsets.UTF_8);
            Date logonTime = new Date(entity.getLong("LOGONTIME"));
            String machine = entity.getStr("MACHINE", StandardCharsets.UTF_8);
            String osUser = entity.getStr("OSUSER", StandardCharsets.UTF_8);
            String program = entity.getStr("PROGRAM", StandardCharsets.UTF_8);
            String event = entity.getStr("EVENT", StandardCharsets.UTF_8);
            // 等待事件的时间
            Long waitTime = entity.getLong("WAITTIME") != null ? entity.getLong("WAITTIME") : Long.valueOf(0);
            // SQL已执行的时间
            Long lastCallEt = entity.getLong("LAST_CALL_ET") != null ? entity.getLong("LAST_CALL_ET") : Long.valueOf(0);
            String sql = entity.getStr("SQL", StandardCharsets.UTF_8);
            if (StringUtils.isBlank(sql)) {
                continue;
            }
            if (StringUtils.equalsIgnoreCase(state, "ACTIVE") && lastCallEt >= judgeExecTime && DbUtils.isMonitoredSqlType(sql, DbType.oracle)) {
                // 将 SQL 字符串按照 Oracle 语法规则进行格式标准化
                String normalizeSql = DbUtils.normalizeSql(sql, DbType.oracle);
                // 对 SQL 进行参数化处理（即把具体的值替换成 ?）
                String parameterizeSql = DbUtils.parameterizeSql(normalizeSql, DbType.oracle);
                // 截断 SQL 字符串
                String storedSql = DbUtils.safeTruncateSql(sql, 5000);
                String storedNormalizeSql = DbUtils.safeTruncateSql(normalizeSql, 5000);

                // 生成哈希
                String sqlMd5Hex = DigestUtils.md5Hex(dbId + schemaName + parameterizeSql);

                MonitorDbSlowSqlOracle monitorDbSlowSqlOracle = new MonitorDbSlowSqlOracle();
                monitorDbSlowSqlOracle.setDbId(dbId);
                monitorDbSlowSqlOracle.setSid(sid);
                monitorDbSlowSqlOracle.setSerial(serial);
                monitorDbSlowSqlOracle.setUserName(uname);
                monitorDbSlowSqlOracle.setSchemaName(schemaName);
                monitorDbSlowSqlOracle.setSessionType(type);
                monitorDbSlowSqlOracle.setState(state);
                monitorDbSlowSqlOracle.setLogonTime(logonTime);
                monitorDbSlowSqlOracle.setMachine(machine);
                monitorDbSlowSqlOracle.setOsUser(osUser);
                monitorDbSlowSqlOracle.setProgram(program);
                monitorDbSlowSqlOracle.setEvent(event);
                monitorDbSlowSqlOracle.setWaitTime(waitTime);
                monitorDbSlowSqlOracle.setLastCallEt(lastCallEt);
                monitorDbSlowSqlOracle.setSqlText(storedSql);
                monitorDbSlowSqlOracle.setNormalizeSqlText(storedNormalizeSql);
                monitorDbSlowSqlOracle.setParameterizeSqlText(parameterizeSql);
                monitorDbSlowSqlOracle.setSqlMd5Hex(sqlMd5Hex);
                monitorDbSlowSqlOracle.setDetectTime(new Date());
                monitorDbSlowSqlOracle.setThresholdTime(judgeExecTime);
                // 新增或者更新Oracle数据库慢SQL信息到数据库表
                this.dbSlowSqlOracleService.insertOrUpdate2Db(monitorDbSlowSqlOracle);
                // 发送告警
                String msg = "，<br>会话ID/Session ID：" + sid +
                        "，<br>Serial#：" + serial +
                        "，<br>用户/User：" + uname +
                        "，<br>模式/Schema：" + schemaName +
                        "，<br>会话类型/Session type：" + type +
                        "，<br>状态/State：" + state +
                        "，<br>登录时间/Login time：" + DateTimeUtils.dateToString(logonTime, DateTimeStylesEnums.YYYY_MM_DD_HH_MM_SS_EN) +
                        "，<br>远程主机/Remote host：" + machine +
                        "，<br>远程用户/Remote user：" + osUser +
                        "，<br>远程程序/Remote program：" + program +
                        "，<br>事件/Event：" + event +
                        "，<br>等待时间/Waiting time：" + DateUtil.formatBetween(waitTime * 1000L, BetweenFormatter.Level.SECOND) +
                        "，<br>执行时间/Exec time：" + DateUtil.formatBetween(lastCallEt * 1000L, BetweenFormatter.Level.SECOND) +
                        "，<br>判断阈值/Judgment threshold：" + DateUtil.formatBetween(judgeExecTime * 1000L, BetweenFormatter.Level.SECOND) +
                        "，<br>SQL：" + DbUtils.safeTruncateSql(storedSql, 500);
                this.sendAlarmInfo(sqlMd5Hex, msg, monitorDb);
            }
        }
    }

    /**
     * <p>
     * 处理 MySQL 数据库
     * </p>
     *
     * @param monitorDb 数据库信息
     * @throws SQLException SQL异常
     * @author 皮锋
     * @custom.date 2026/1/7 09:59
     */
    private void dealWithMysql(MonitorDb monitorDb) throws SQLException {
        // 判定为慢SQL的SQL执行时间（秒）
        long judgeExecTime = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDbProperties().getDbSlowSqlProperties().getJudgeExecTime();
        // 主键ID
        Long dbId = monitorDb.getId();
        // 数据库URL
        String url = monitorDb.getUrl();
        // 用户名
        String username = monitorDb.getUsername();
        // 密码
        String password = monitorDb.getPassword();
        List<Entity> entityList = this.dbSession4MysqlService.getSessionList(url, username, password);
        for (Entity entity : entityList) {
            Long sessionId = entity.getLong("Id");
            String user = entity.getStr("User", StandardCharsets.UTF_8);
            String host = entity.getStr("Host", StandardCharsets.UTF_8);
            String db = entity.getStr("db", StandardCharsets.UTF_8);
            String command = entity.getStr("Command", StandardCharsets.UTF_8);
            Long time = entity.getLong("Time");
            String state = entity.getStr("State", StandardCharsets.UTF_8);
            String info = entity.getStr("Info", StandardCharsets.UTF_8);
            if (StringUtils.isBlank(info)) {
                continue;
            }
            if (!StringUtils.equalsIgnoreCase(command, "Sleep") && time >= judgeExecTime && DbUtils.isMonitoredSqlType(info, DbType.mysql)) {
                // 将 SQL 字符串按照 MySQL 语法规则进行格式标准化
                String normalizeSql = DbUtils.normalizeSql(info, DbType.mysql);
                // 对 SQL 进行参数化处理（即把具体的值替换成 ?）
                String parameterizeSql = DbUtils.parameterizeSql(normalizeSql, DbType.mysql);
                // 截断 SQL 字符串
                String storedInfo = DbUtils.safeTruncateSql(info, 5000);
                String storedNormalizeSql = DbUtils.safeTruncateSql(normalizeSql, 5000);

                // 生成哈希
                String sqlMd5Hex = DigestUtils.md5Hex(dbId + db + parameterizeSql);

                MonitorDbSlowSqlMysql monitorDbSlowSqlMysql = new MonitorDbSlowSqlMysql();
                monitorDbSlowSqlMysql.setDbId(dbId);
                monitorDbSlowSqlMysql.setSessionId(sessionId);
                monitorDbSlowSqlMysql.setUserName(user);
                monitorDbSlowSqlMysql.setHost(host);
                monitorDbSlowSqlMysql.setDbName(db);
                monitorDbSlowSqlMysql.setCommand(command);
                monitorDbSlowSqlMysql.setExecutionTime(time);
                monitorDbSlowSqlMysql.setState(state);
                monitorDbSlowSqlMysql.setSqlText(storedInfo);
                monitorDbSlowSqlMysql.setNormalizeSqlText(storedNormalizeSql);
                monitorDbSlowSqlMysql.setParameterizeSqlText(parameterizeSql);
                monitorDbSlowSqlMysql.setSqlMd5Hex(sqlMd5Hex);
                monitorDbSlowSqlMysql.setThresholdTime(judgeExecTime);
                monitorDbSlowSqlMysql.setDetectTime(new Date());
                // 新增或者更新MySQL数据库慢SQL信息到数据库表
                this.dbSlowSqlMysqlService.insertOrUpdate2Db(monitorDbSlowSqlMysql);
                // 发送告警
                String msg = "，<br>会话ID/Session ID：" + sessionId +
                        "，<br>用户/User：" + user +
                        "，<br>主机/Host：" + host +
                        "，<br>数据库/Database：" + db +
                        "，<br>命令/Command：" + command +
                        "，<br>执行时间/Exec time：" + DateUtil.formatBetween(time * 1000L, BetweenFormatter.Level.SECOND) +
                        "，<br>判断阈值/Judgment threshold：" + DateUtil.formatBetween(judgeExecTime * 1000L, BetweenFormatter.Level.SECOND) +
                        "，<br>状态/State：" + state +
                        "，<br>SQL：" + DbUtils.safeTruncateSql(storedInfo, 500);
                this.sendAlarmInfo(sqlMd5Hex, msg, monitorDb);
            }
        }
    }

    /**
     * <p>
     * 发送告警信息
     * </p>
     *
     * @param alarmCode 告警编码
     * @param msg       告警内容
     * @param monitorDb 数据库信息
     * @throws NetException 获取数据库信息异常
     * @author 皮锋
     * @custom.date 2025/1/16 21:16
     */
    private void sendAlarmInfo(String alarmCode, String msg, MonitorDb monitorDb)
            throws NetException {
        // 告警是否打开
        boolean alarmEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDbProperties().getDbSlowSqlProperties().isAlarmEnable();
        if (!alarmEnable) {
            return;
        }
        // 是否开启告警（0：不开启告警；1：开启告警）
        String isEnableAlarm = monitorDb.getIsEnableAlarm();
        // 没有开启告警，直接结束
        if (!StringUtils.equals(ZeroOrOneConstants.ONE, isEnableAlarm)) {
            return;
        }
        StringBuilder builder = new StringBuilder();
        builder.append("连接名/Connection name：").append(monitorDb.getConnName())
                .append("，<br>Url：").append(monitorDb.getUrl())
                .append("，<br>类型/Type：").append(monitorDb.getDbType());
        if (StringUtils.isNotBlank(monitorDb.getDbDesc())) {
            builder.append("，<br>描述/Desc：").append(monitorDb.getDbDesc());
        }
        builder.append(msg);
        if (StringUtils.isNotBlank(monitorDb.getMonitorEnv())) {
            builder.append("，<br>环境/Env：").append(monitorDb.getMonitorEnv());
        }
        if (StringUtils.isNotBlank(monitorDb.getMonitorGroup())) {
            builder.append("，<br>分组/Group：").append(monitorDb.getMonitorGroup());
        }
        builder.append("，<br>时间/Time：").append(DateTimeUtils.dateToString(new Date()));
        Alarm alarm = Alarm.builder()
                // 保证code的唯一性
                .code(alarmCode)
                .title("数据库慢SQL")
                .titleEn(MonitorEventTitleEnum.getEnglishTitle("数据库慢SQL"))
                .msg(builder.toString())
                .alarmLevel(this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDbProperties().getDbSlowSqlProperties().getLevelEnum())
                .alarmReason(AlarmReasonEnums.NORMAL_2_ABNORMAL)
                .monitorType(MonitorTypeEnums.DATABASE)
                .monitorSubType(MonitorSubTypeEnums.DATABASE__SLOW_SQL)
                .alertedEntityId(String.valueOf(monitorDb.getId()))
                .build();
        AlarmPackage alarmPackage = this.serverPackageConstructor.structureAlarmPackage(alarm);
        this.alarmService.dealAlarmPackage(alarmPackage);
    }

}
