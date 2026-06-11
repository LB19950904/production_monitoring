package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDbSlowSqlOracle;

/**
 * <p>
 * Oracle数据库慢SQL服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-07
 */
public interface IDbSlowSqlOracleService extends IService<MonitorDbSlowSqlOracle> {

    /**
     * <p>
     * 新增或者更新Oracle数据库慢SQL信息到数据库表
     * </p>
     *
     * @param monitorDbSlowSqlOracle Oracle数据库慢SQL信息
     * @author 皮锋
     * @custom.date 2026/1/7 17:11
     */
    void insertOrUpdate2Db(MonitorDbSlowSqlOracle monitorDbSlowSqlOracle);

}
