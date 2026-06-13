package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlPostgreSql;

/**
 * <p>
 * PostgreSQL数据库慢SQL表数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-06-13
 */
public interface IMonitorDbSlowSqlPostgreSqlDao extends BaseMapper<MonitorDbSlowSqlPostgreSql> {

    /**
     * <p>
     * 清空慢SQL
     * </p>
     *
     * @author 皮锋
     * @custom.date 2025/6/13 16:30
     */
    void cleanupSlowSql();

}
