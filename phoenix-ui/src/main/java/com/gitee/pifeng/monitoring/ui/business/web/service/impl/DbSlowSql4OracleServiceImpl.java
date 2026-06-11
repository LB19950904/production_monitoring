package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.DateTimeStylesEnums;
import com.gitee.pifeng.monitoring.common.util.DateTimeUtils;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorDbSlowSqlOracleDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlOracle;
import com.gitee.pifeng.monitoring.ui.business.web.service.IDbSlowSql4OracleService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.DbSlowSql4OracleVo;
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
 * Oracle数据库慢SQL服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-08
 */
@Service
public class DbSlowSql4OracleServiceImpl extends ServiceImpl<IMonitorDbSlowSqlOracleDao, MonitorDbSlowSqlOracle> implements IDbSlowSql4OracleService {

    /**
     * <p>
     * 获取慢SQL列表
     * </p>
     *
     * @param current     当前页
     * @param size        每页显示条数
     * @param dbId        数据库表ID
     * @param schemaName  模式
     * @param event       事件
     * @param sessionType 会话类型
     * @param state       状态
     * @param detectTime  检测时间
     * @param sql         SQL
     * @return 简单分页模型
     * @author 皮锋
     * @custom.date 2026/1/16 17:06
     */
    @Override
    public Page<DbSlowSql4OracleVo> getSlowSqlList(Long current, Long size, Long dbId, String schemaName, String event,
                                                   String sessionType, String state, String detectTime, String sql) {
        // 查询数据库
        IPage<MonitorDbSlowSqlOracle> ipage = new Page<>(current, size);
        LambdaQueryWrapper<MonitorDbSlowSqlOracle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dbId != null, MonitorDbSlowSqlOracle::getDbId, dbId);
        wrapper.like(StringUtils.isNotBlank(schemaName), MonitorDbSlowSqlOracle::getSchemaName, schemaName);
        wrapper.like(StringUtils.isNotBlank(event), MonitorDbSlowSqlOracle::getEvent, event);
        wrapper.like(StringUtils.isNotBlank(sessionType), MonitorDbSlowSqlOracle::getSessionType, sessionType);
        wrapper.like(StringUtils.isNotBlank(state), MonitorDbSlowSqlOracle::getState, state);
        if (StringUtils.isNotBlank(detectTime)) {
            String[] split = StringUtils.split(detectTime, "~");
            Date startDateTime = DateTimeUtils.string2Date(split[0].trim(), DateTimeStylesEnums.YYYY_MM_DD);
            Date endDateTime = DateTimeUtils.string2Date(split[1].trim(), DateTimeStylesEnums.YYYY_MM_DD);
            endDateTime = DateUtil.endOfDay(endDateTime).toJdkDate();
            wrapper.between(MonitorDbSlowSqlOracle::getDetectTime, startDateTime, endDateTime);
        }
        wrapper.like(StringUtils.isNotBlank(sql), MonitorDbSlowSqlOracle::getNormalizeSqlText, sql);
        wrapper.orderByDesc(MonitorDbSlowSqlOracle::getDetectTime);
        IPage<MonitorDbSlowSqlOracle> monitorDbSlowSqlOraclePage = this.baseMapper.selectPage(ipage, wrapper);
        List<MonitorDbSlowSqlOracle> monitorDbSlowSqlOracles = monitorDbSlowSqlOraclePage.getRecords();
        // 转换成Oracle数据库慢SQL表现层对象
        List<DbSlowSql4OracleVo> dbSlowSql4OracleVos = Lists.newLinkedList();
        for (MonitorDbSlowSqlOracle monitorDbSlowSqlOracle : monitorDbSlowSqlOracles) {
            // SQL已执行时间（秒）
            Long lastCallEt = monitorDbSlowSqlOracle.getLastCallEt();
            // 判断阈值（秒）
            Long thresholdTime = monitorDbSlowSqlOracle.getThresholdTime();
            // 格式标准化后的SQL文本
            String normalizeSqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlOracle.getNormalizeSqlText());
            DbSlowSql4OracleVo dbSlowSql4OracleVo = DbSlowSql4OracleVo.builder().build().convertFor(monitorDbSlowSqlOracle);
            dbSlowSql4OracleVo.setLastCallEtStr(lastCallEt != null ? DateUtil.formatBetween(lastCallEt * 1000L, BetweenFormatter.Level.SECOND) : "");
            dbSlowSql4OracleVo.setThresholdTimeStr(thresholdTime != null ? DateUtil.formatBetween(thresholdTime * 1000L, BetweenFormatter.Level.SECOND) : "");
            dbSlowSql4OracleVo.setSqlText(null);
            dbSlowSql4OracleVo.setNormalizeSqlText(normalizeSqlText);
            dbSlowSql4OracleVo.setParameterizeSqlText(null);
            dbSlowSql4OracleVos.add(dbSlowSql4OracleVo);
        }
        // 设置返回对象
        Page<DbSlowSql4OracleVo> dbSlowSql4OracleVoPage = new Page<>();
        dbSlowSql4OracleVoPage.setRecords(dbSlowSql4OracleVos);
        dbSlowSql4OracleVoPage.setTotal(monitorDbSlowSqlOraclePage.getTotal());
        return dbSlowSql4OracleVoPage;
    }

    /**
     * <p>
     * 删除慢SQL
     * </p>
     *
     * @param ids 主键ID集合
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2026/1/16 16:21
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
     * @custom.date 2026/1/16 16:24
     */
    @Override
    public LayUiAdminResultVo cleanupSlowSql() {
        this.baseMapper.cleanupSlowSql();
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

    /**
     * <p>
     * 根据主键ID获取Oracle数据库慢SQL信息
     * </p>
     *
     * @param id 主键ID
     * @return Oracle数据库慢SQL表现层对象
     * @author 皮锋
     * @custom.date 2026/1/16 16:41
     */
    @Override
    public DbSlowSql4OracleVo getDbSlowSql4OracleVoById(Long id) {
        MonitorDbSlowSqlOracle monitorDbSlowSqlOracle = this.baseMapper.selectById(id);
        // 等待时间（秒）
        Long waitTime = monitorDbSlowSqlOracle.getWaitTime();
        // SQL已执行时间（秒）
        Long lastCallEt = monitorDbSlowSqlOracle.getLastCallEt();
        // 判断阈值（秒）
        Long thresholdTime = monitorDbSlowSqlOracle.getThresholdTime();
        // SQL文本
        String sqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlOracle.getSqlText());
        // 格式标准化后的SQL文本
        String normalizeSqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlOracle.getNormalizeSqlText());
        // 参数化处理后的SQL文本
        String parameterizeSqlText = HtmlUtils.escapeHtmlButAllowBr(monitorDbSlowSqlOracle.getParameterizeSqlText());
        DbSlowSql4OracleVo dbSlowSql4OracleVo = DbSlowSql4OracleVo.builder().build().convertFor(monitorDbSlowSqlOracle);
        dbSlowSql4OracleVo.setWaitTimeStr(waitTime != null ? DateUtil.formatBetween(waitTime * 1000L, BetweenFormatter.Level.SECOND) : "");
        dbSlowSql4OracleVo.setLastCallEtStr(lastCallEt != null ? DateUtil.formatBetween(lastCallEt * 1000L, BetweenFormatter.Level.SECOND) : "");
        dbSlowSql4OracleVo.setThresholdTimeStr(thresholdTime != null ? DateUtil.formatBetween(thresholdTime * 1000L, BetweenFormatter.Level.SECOND) : "");
        dbSlowSql4OracleVo.setSqlText(sqlText);
        dbSlowSql4OracleVo.setParameterizeSqlText(parameterizeSqlText);
        dbSlowSql4OracleVo.setNormalizeSqlText(normalizeSqlText);
        return dbSlowSql4OracleVo;
    }

}
