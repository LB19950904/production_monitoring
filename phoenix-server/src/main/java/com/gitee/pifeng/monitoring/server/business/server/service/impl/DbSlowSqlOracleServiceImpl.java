package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDbSlowSqlOracleDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDbSlowSqlOracle;
import com.gitee.pifeng.monitoring.server.business.server.service.IDbSlowSqlOracleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <p>
 * Oracle数据库慢SQL服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-07
 */
@Service
public class DbSlowSqlOracleServiceImpl extends ServiceImpl<IMonitorDbSlowSqlOracleDao, MonitorDbSlowSqlOracle> implements IDbSlowSqlOracleService {

    /**
     * <p>
     * 新增或者更新Oracle数据库慢SQL信息到数据库表
     * </p>
     *
     * @param monitorDbSlowSqlOracle Oracle数据库慢SQL信息
     * @author 皮锋
     * @custom.date 2026/1/7 17:11
     */
    @Override
    @Transactional(rollbackFor = Throwable.class)
    public void insertOrUpdate2Db(MonitorDbSlowSqlOracle monitorDbSlowSqlOracle) {
        Date currentDate = new Date();
        // SQL MD5值
        String sqlMd5Hex = monitorDbSlowSqlOracle.getSqlMd5Hex();
        // 查询数据库中是否已经存在此 SQL MD5值 的记录
        LambdaQueryWrapper<MonitorDbSlowSqlOracle> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorDbSlowSqlOracle::getSqlMd5Hex, sqlMd5Hex);
        Integer count = this.baseMapper.selectCount(lambdaQueryWrapper);
        // 更新
        if (count > 0) {
            monitorDbSlowSqlOracle.setUpdateTime(currentDate);
            LambdaUpdateWrapper<MonitorDbSlowSqlOracle> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            lambdaUpdateWrapper.eq(MonitorDbSlowSqlOracle::getSqlMd5Hex, sqlMd5Hex);
            this.baseMapper.update(monitorDbSlowSqlOracle, lambdaUpdateWrapper);
        }
        // 新增
        else {
            monitorDbSlowSqlOracle.setInsertTime(currentDate);
            this.baseMapper.insert(monitorDbSlowSqlOracle);
        }
    }

}
