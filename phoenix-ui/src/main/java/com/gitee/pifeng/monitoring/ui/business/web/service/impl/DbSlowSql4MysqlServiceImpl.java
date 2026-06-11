package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.DateTimeStylesEnums;
import com.gitee.pifeng.monitoring.common.util.DateTimeUtils;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorDbSlowSqlMysqlDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlMysql;
import com.gitee.pifeng.monitoring.ui.business.web.service.IDbSlowSql4MysqlService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.DbSlowSql4MysqlVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.constant.WebResponseConstants;
import com.gitee.pifeng.monitoring.ui.util.HtmlUtils;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * MySQL数据库慢SQL服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-08
 */
@Service
public class DbSlowSql4MysqlServiceImpl extends ServiceImpl<IMonitorDbSlowSqlMysqlDao, MonitorDbSlowSqlMysql> implements IDbSlowSql4MysqlService {

    /**
     * <p>
     * 获取慢SQL列表
     * </p>
     *
     * @param current    当前页
     * @param size       每页显示条数
     * @param dbId       数据库表ID
     * @param dbName     数据库名
     * @param command    命令
     * @param state      状态
     * @param detectTime 检测时间
     * @param sql        SQL
     * @return 简单分页模型
     * @author 皮锋
     * @custom.date 2026/1/15 08:47
     */
    @Override
    public Page<DbSlowSql4MysqlVo> getSlowSqlList(Long current, Long size, Long dbId, String dbName, String command,
                                                  String state, String detectTime, String sql) {
        // 查询数据库
        IPage<MonitorDbSlowSqlMysql> ipage = new Page<>(current, size);
        LambdaQueryWrapper<MonitorDbSlowSqlMysql> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dbId != null, MonitorDbSlowSqlMysql::getDbId, dbId);
        wrapper.like(StringUtils.isNotBlank(dbName), MonitorDbSlowSqlMysql::getDbName, dbName);
        wrapper.like(StringUtils.isNotBlank(command), MonitorDbSlowSqlMysql::getCommand, command);
        wrapper.like(StringUtils.isNotBlank(state), MonitorDbSlowSqlMysql::getState, state);
        if (StringUtils.isNotBlank(detectTime)) {
            String[] split = StringUtils.split(detectTime, "~");
            Date startDateTime = DateTimeUtils.string2Date(split[0].trim(), DateTimeStylesEnums.YYYY_MM_DD);
            Date endDateTime = DateTimeUtils.string2Date(split[1].trim(), DateTimeStylesEnums.YYYY_MM_DD);
            endDateTime = DateUtil.endOfDay(endDateTime).toJdkDate();
            wrapper.between(MonitorDbSlowSqlMysql::getDetectTime, startDateTime, endDateTime);
        }
        wrapper.like(StringUtils.isNotBlank(sql), MonitorDbSlowSqlMysql::getNormalizeSqlText, sql);
        wrapper.orderByDesc(MonitorDbSlowSqlMysql::getDetectTime);
        IPage<MonitorDbSlowSqlMysql> monitorDbSlowSqlMysqlPage = this.baseMapper.selectPage(ipage, wrapper);
        List<MonitorDbSlowSqlMysql> monitorDbSlowSqlMysqls = monitorDbSlowSqlMysqlPage.getRecords();
        // 转换成MySQL数据库慢SQL表现层对象
        List<DbSlowSql4MysqlVo> dbSlowSql4MysqlVos = Lists.newLinkedList();
        for (MonitorDbSlowSqlMysql monitorDbSlowSqlMysql : monitorDbSlowSqlMysqls) {
            // 执行时间（秒）
            Long executionTime = monitorDbSlowSqlMysql.getExecutionTime();
            // 判断阈值（秒）
            Long thresholdTime = monitorDbSlowSqlMysql.getThresholdTime();
            // 格式标准化后的SQL文本
            String normalizeSqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlMysql.getNormalizeSqlText());
            DbSlowSql4MysqlVo dbSlowSql4MysqlVo = DbSlowSql4MysqlVo.builder().build().convertFor(monitorDbSlowSqlMysql);
            dbSlowSql4MysqlVo.setExecutionTimeStr(executionTime != null ? DateUtil.formatBetween(executionTime * 1000L, BetweenFormatter.Level.SECOND) : "");
            dbSlowSql4MysqlVo.setThresholdTimeStr(thresholdTime != null ? DateUtil.formatBetween(thresholdTime * 1000L, BetweenFormatter.Level.SECOND) : "");
            dbSlowSql4MysqlVo.setSqlText(null);
            dbSlowSql4MysqlVo.setNormalizeSqlText(normalizeSqlText);
            dbSlowSql4MysqlVo.setParameterizeSqlText(null);
            dbSlowSql4MysqlVos.add(dbSlowSql4MysqlVo);
        }
        // 设置返回对象
        Page<DbSlowSql4MysqlVo> dbSlowSql4MysqlVoPage = new Page<>();
        dbSlowSql4MysqlVoPage.setRecords(dbSlowSql4MysqlVos);
        dbSlowSql4MysqlVoPage.setTotal(monitorDbSlowSqlMysqlPage.getTotal());
        return dbSlowSql4MysqlVoPage;
    }

    /**
     * <p>
     * 删除慢SQL
     * </p>
     *
     * @param ids 主键ID集合
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2026/1/16 11:32
     */
    @Override
    public LayUiAdminResultVo deleteSlowSql(List<Long> ids) {
        // 批量删除
        this.baseMapper.deleteBatchIds(ids);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

    /**
     * <p>
     * 清空慢SQL
     * </p>
     *
     * @return layUiAdmin响应对象：如果清空成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2026/1/16 15:45
     */
    @Override
    public LayUiAdminResultVo cleanupSlowSql() {
        this.baseMapper.cleanupSlowSql();
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

    /**
     * <p>
     * 根据主键ID获取MySQL数据库慢SQL信息
     * </p>
     *
     * @param id 主键ID
     * @return MySQL数据库慢SQL表现层对象
     * @author 皮锋
     * @custom.date 2026/1/16 14:28
     */
    @Override
    public DbSlowSql4MysqlVo getDbSlowSql4MysqlVoById(Long id) {
        MonitorDbSlowSqlMysql monitorDbSlowSqlMysql = this.baseMapper.selectById(id);
        // 执行时间（秒）
        Long executionTime = monitorDbSlowSqlMysql.getExecutionTime();
        // 判断阈值（秒）
        Long thresholdTime = monitorDbSlowSqlMysql.getThresholdTime();
        // SQL文本
        String sqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlMysql.getSqlText());
        // 格式标准化后的SQL文本
        String normalizeSqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlMysql.getNormalizeSqlText());
        // 参数化处理后的SQL文本
        String parameterizeSqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlMysql.getParameterizeSqlText());
        DbSlowSql4MysqlVo dbSlowSql4MysqlVo = DbSlowSql4MysqlVo.builder().build().convertFor(monitorDbSlowSqlMysql);
        dbSlowSql4MysqlVo.setExecutionTimeStr(executionTime != null ? DateUtil.formatBetween(executionTime * 1000L, BetweenFormatter.Level.SECOND) : "");
        dbSlowSql4MysqlVo.setThresholdTimeStr(thresholdTime != null ? DateUtil.formatBetween(thresholdTime * 1000L, BetweenFormatter.Level.SECOND) : "");
        dbSlowSql4MysqlVo.setSqlText(sqlText);
        dbSlowSql4MysqlVo.setParameterizeSqlText(parameterizeSqlText);
        dbSlowSql4MysqlVo.setNormalizeSqlText(normalizeSqlText);
        return dbSlowSql4MysqlVo;
    }

}
