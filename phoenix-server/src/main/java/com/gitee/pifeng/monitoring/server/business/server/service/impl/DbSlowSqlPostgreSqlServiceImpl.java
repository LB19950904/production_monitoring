package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDbSlowSqlPostgreSqlDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDbSlowSqlPostgreSql;
import com.gitee.pifeng.monitoring.server.business.server.service.IDbSlowSqlPostgreSqlService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * PostgreSQL数据库慢SQL服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-06-13
 */
@Service
public class DbSlowSqlPostgreSqlServiceImpl extends ServiceImpl<IMonitorDbSlowSqlPostgreSqlDao, MonitorDbSlowSqlPostgreSql> implements IDbSlowSqlPostgreSqlService {

    /**
     * <p>
     * 新增或者更新PostgreSQL数据库慢SQL信息到数据库表
     * </p>
     *
     * @param monitorDbSlowSqlPostgreSql PostgreSQL数据库慢SQL信息
     * @author 皮锋
     * @custom.date 2025/6/13 14:50
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void insertOrUpdate2Db(MonitorDbSlowSqlPostgreSql monitorDbSlowSqlPostgreSql) {
        Date currentDate = new Date();
        // SQL MD5值
        String sqlMd5Hex = monitorDbSlowSqlPostgreSql.getSqlMd5Hex();
        // 查询数据库中是否已经存在此 SQL MD5值 的记录
        LambdaQueryWrapper<MonitorDbSlowSqlPostgreSql> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorDbSlowSqlPostgreSql::getSqlMd5Hex, sqlMd5Hex);
        Integer count = this.baseMapper.selectCount(lambdaQueryWrapper);
        // 更新
        if (count > 0) {
            monitorDbSlowSqlPostgreSql.setUpdateTime(currentDate);
            LambdaUpdateWrapper<MonitorDbSlowSqlPostgreSql> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MonitorDbSlowSqlPostgreSql::getSqlMd5Hex, sqlMd5Hex);
            this.baseMapper.update(monitorDbSlowSqlPostgreSql, lambdaUpdateWrapper);
        }
        // 新增
        else {
            monitorDbSlowSqlPostgreSql.setInsertTime(currentDate);
            this.baseMapper.insert(monitorDbSlowSqlPostgreSql);
        }
    }

}
