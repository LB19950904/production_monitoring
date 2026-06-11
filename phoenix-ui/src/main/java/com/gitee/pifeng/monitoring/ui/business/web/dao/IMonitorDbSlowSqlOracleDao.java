package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlOracle;

/**
 * <p>
 * Oracle数据库慢SQL表数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-08
 */
public interface IMonitorDbSlowSqlOracleDao extends BaseMapper<MonitorDbSlowSqlOracle> {

    /**
     * <p>
     * 清空慢SQL
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/1/16 16:44
     */
    void cleanupSlowSql();

}
