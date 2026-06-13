package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDbSlowSqlPostgreSql;

/**
 * <p>
 * PostgreSQL数据库慢SQL服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-06-13
 */
public interface IDbSlowSqlPostgreSqlService extends IService<MonitorDbSlowSqlPostgreSql> {

    /**
     * <p>
     * 新增或者更新PostgreSQL数据库慢SQL信息到数据库表
     * </p>
     *
     * @param monitorDbSlowSqlPostgreSql PostgreSQL数据库慢SQL信息
     * @author 皮锋
     * @custom.date 2025/6/13 14:45
     */
    void insertOrUpdate2Db(MonitorDbSlowSqlPostgreSql monitorDbSlowSqlPostgreSql);

}
