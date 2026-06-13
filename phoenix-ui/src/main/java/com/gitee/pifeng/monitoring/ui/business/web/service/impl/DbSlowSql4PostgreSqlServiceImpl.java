package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.DateTimeStylesEnums;
import com.gitee.pifeng.monitoring.common.util.DateTimeUtils;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorDbSlowSqlPostgreSqlDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlPostgreSql;
import com.gitee.pifeng.monitoring.ui.business.web.service.IDbSlowSql4PostgreSqlService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.DbSlowSql4PostgreSqlVo;
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
 * PostgreSQL数据库慢SQL服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-06-13
 */
@Service
public class DbSlowSql4PostgreSqlServiceImpl extends ServiceImpl<IMonitorDbSlowSqlPostgreSqlDao, MonitorDbSlowSqlPostgreSql> implements IDbSlowSql4PostgreSqlService {

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
     * @custom.date 2025/6/13 16:15
     */
    @Override
    public Page<DbSlowSql4PostgreSqlVo> getSlowSqlList(Long current, Long size, Long dbId, String dbName, String command,
                                                        String state, String detectTime, String sql) {
        // 查询数据库
        IPage<MonitorDbSlowSqlPostgreSql> ipage = new Page<>(current, size);
        LambdaQueryWrapper<MonitorDbSlowSqlPostgreSql> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dbId != null, MonitorDbSlowSqlPostgreSql::getDbId, dbId);
        wrapper.like(StringUtils.isNotBlank(dbName), MonitorDbSlowSqlPostgreSql::getDbName, dbName);
        wrapper.like(StringUtils.isNotBlank(command), MonitorDbSlowSqlPostgreSql::getCommand, command);
        wrapper.like(StringUtils.isNotBlank(state), MonitorDbSlowSqlPostgreSql::getState, state);
        if (StringUtils.isNotBlank(detectTime)) {
            String[] split = StringUtils.split(detectTime, "~");
            Date startDateTime = DateTimeUtils.string2Date(split[0].trim(), DateTimeStylesEnums.YYYY_MM_DD);
            Date endDateTime = DateTimeUtils.string2Date(split[1].trim(), DateTimeStylesEnums.YYYY_MM_DD);
            endDateTime = DateUtil.endOfDay(endDateTime).toJdkDate();
            wrapper.between(MonitorDbSlowSqlPostgreSql::getDetectTime, startDateTime, endDateTime);
        }
        wrapper.like(StringUtils.isNotBlank(sql), MonitorDbSlowSqlPostgreSql::getNormalizeSqlText, sql);
        wrapper.orderByDesc(MonitorDbSlowSqlPostgreSql::getDetectTime);
        IPage<MonitorDbSlowSqlPostgreSql> monitorDbSlowSqlPostgreSqlPage = this.baseMapper.selectPage(ipage, wrapper);
        List<MonitorDbSlowSqlPostgreSql> monitorDbSlowSqlPostgreSqls = monitorDbSlowSqlPostgreSqlPage.getRecords();
        // 转换成PostgreSQL数据库慢SQL表现层对象
        List<DbSlowSql4PostgreSqlVo> dbSlowSql4PostgreSqlVos = Lists.newLinkedList();
        for (MonitorDbSlowSqlPostgreSql monitorDbSlowSqlPostgreSql : monitorDbSlowSqlPostgreSqls) {
            // 执行时间（秒）
            Long executionTime = monitorDbSlowSqlPostgreSql.getExecutionTime();
            // 判断阈值（秒）
            Long thresholdTime = monitorDbSlowSqlPostgreSql.getThresholdTime();
            // 格式标准化后的SQL文本
            String normalizeSqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlPostgreSql.getNormalizeSqlText());
            DbSlowSql4PostgreSqlVo dbSlowSql4PostgreSqlVo = DbSlowSql4PostgreSqlVo.builder().build().convertFor(monitorDbSlowSqlPostgreSql);
            dbSlowSql4PostgreSqlVo.setExecutionTimeStr(executionTime != null ? DateUtil.formatBetween(executionTime * 1000L, BetweenFormatter.Level.SECOND) : "");
            dbSlowSql4PostgreSqlVo.setThresholdTimeStr(thresholdTime != null ? DateUtil.formatBetween(thresholdTime * 1000L, BetweenFormatter.Level.SECOND) : "");
            dbSlowSql4PostgreSqlVo.setSqlText(null);
            dbSlowSql4PostgreSqlVo.setNormalizeSqlText(normalizeSqlText);
            dbSlowSql4PostgreSqlVo.setParameterizeSqlText(null);
            dbSlowSql4PostgreSqlVos.add(dbSlowSql4PostgreSqlVo);
        }
        // 设置返回对象
        Page<DbSlowSql4PostgreSqlVo> dbSlowSql4PostgreSqlVoPage = new Page<>();
        dbSlowSql4PostgreSqlVoPage.setRecords(dbSlowSql4PostgreSqlVos);
        dbSlowSql4PostgreSqlVoPage.setTotal(monitorDbSlowSqlPostgreSqlPage.getTotal());
        return dbSlowSql4PostgreSqlVoPage;
    }

    /**
     * <p>
     * 删除慢SQL
     * </p>
     *
     * @param ids 主键ID集合
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025/6/13 16:15
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
     * @custom.date 2025/6/13 16:15
     */
    @Override
    public LayUiAdminResultVo cleanupSlowSql() {
        this.baseMapper.cleanupSlowSql();
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

    /**
     * <p>
     * 根据主键ID获取PostgreSQL数据库慢SQL信息
     * </p>
     *
     * @param id 主键ID
     * @return PostgreSQL数据库慢SQL表现层对象
     * @author 皮锋
     * @custom.date 2025/6/13 16:15
     */
    @Override
    public DbSlowSql4PostgreSqlVo getDbSlowSql4PostgreSqlVoById(Long id) {
        MonitorDbSlowSqlPostgreSql monitorDbSlowSqlPostgreSql = this.baseMapper.selectById(id);
        // 执行时间（秒）
        Long executionTime = monitorDbSlowSqlPostgreSql.getExecutionTime();
        // 判断阈值（秒）
        Long thresholdTime = monitorDbSlowSqlPostgreSql.getThresholdTime();
        // SQL文本
        String sqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlPostgreSql.getSqlText());
        // 格式标准化后的SQL文本
        String normalizeSqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlPostgreSql.getNormalizeSqlText());
        // 参数化处理后的SQL文本
        String parameterizeSqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlPostgreSql.getParameterizeSqlText());
        DbSlowSql4PostgreSqlVo dbSlowSql4PostgreSqlVo = DbSlowSql4PostgreSqlVo.builder().build().convertFor(monitorDbSlowSqlPostgreSql);
        dbSlowSql4PostgreSqlVo.setExecutionTimeStr(executionTime != null ? DateUtil.formatBetween(executionTime * 1000L, BetweenFormatter.Level.SECOND) : "");
        dbSlowSql4PostgreSqlVo.setThresholdTimeStr(thresholdTime != null ? DateUtil.formatBetween(thresholdTime * 1000L, BetweenFormatter.Level.SECOND) : "");
        dbSlowSql4PostgreSqlVo.setSqlText(sqlText);
        dbSlowSql4PostgreSqlVo.setParameterizeSqlText(parameterizeSqlText);
        dbSlowSql4PostgreSqlVo.setNormalizeSqlText(normalizeSqlText);
        return dbSlowSql4PostgreSqlVo;
    }

}
