package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDbSlowSqlMysqlDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDbSlowSqlMysql;
import com.gitee.pifeng.monitoring.server.business.server.service.IDbSlowSqlMysqlService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * MySQL数据库慢SQL服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-07
 */
@Service
public class DbSlowSqlMysqlServiceImpl extends ServiceImpl<IMonitorDbSlowSqlMysqlDao, MonitorDbSlowSqlMysql> implements IDbSlowSqlMysqlService {

    /**
     * <p>
     * 新增或者更新MySQL数据库慢SQL信息到数据库表
     * </p>
     *
     * @param monitorDbSlowSqlMysql MySQL数据库慢SQL信息
     * @author 皮锋
     * @custom.date 2026/1/8 10:58
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void insertOrUpdate2Db(MonitorDbSlowSqlMysql monitorDbSlowSqlMysql) {
        Date currentDate = new Date();
        // SQL MD5值
        String sqlMd5Hex = monitorDbSlowSqlMysql.getSqlMd5Hex();
        // 查询数据库中是否已经存在此 SQL MD5值 的记录
        LambdaQueryWrapper<MonitorDbSlowSqlMysql> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorDbSlowSqlMysql::getSqlMd5Hex, sqlMd5Hex);
        Integer count = this.baseMapper.selectCount(lambdaQueryWrapper);
        // 更新
        if (count > 0) {
            monitorDbSlowSqlMysql.setUpdateTime(currentDate);
            LambdaUpdateWrapper<MonitorDbSlowSqlMysql> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MonitorDbSlowSqlMysql::getSqlMd5Hex, sqlMd5Hex);
            this.baseMapper.update(monitorDbSlowSqlMysql, lambdaUpdateWrapper);
        }
        // 新增
        else {
            monitorDbSlowSqlMysql.setInsertTime(currentDate);
            this.baseMapper.insert(monitorDbSlowSqlMysql);
        }
    }

}
