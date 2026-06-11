package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDbSlowSqlMysql;

/**
 * <p>
 * MySQL数据库慢SQL服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-07
 */
public interface IDbSlowSqlMysqlService extends IService<MonitorDbSlowSqlMysql> {

    /**
     * <p>
     * 新增或者更新MySQL数据库慢SQL信息到数据库表
     * </p>
     *
     * @param monitorDbSlowSqlMysql MySQL数据库慢SQL信息
     * @author 皮锋
     * @custom.date 2026/1/8 10:58
     */
    void insertOrUpdate2Db(MonitorDbSlowSqlMysql monitorDbSlowSqlMysql);

}
