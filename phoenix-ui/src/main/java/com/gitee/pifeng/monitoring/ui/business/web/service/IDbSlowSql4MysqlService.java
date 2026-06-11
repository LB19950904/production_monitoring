package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlMysql;
import com.gitee.pifeng.monitoring.ui.business.web.vo.DbSlowSql4MysqlVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;

import java.util.List;

/**
 * <p>
 * MySQL数据库慢SQL服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026-01-08
 */
public interface IDbSlowSql4MysqlService extends IService<MonitorDbSlowSqlMysql> {

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
     * @custom.date 2026/1/15 08:47
     */
    Page<DbSlowSql4MysqlVo> getSlowSqlList(Long current, Long size, Long dbId, String dbName, String command,
                                           String state, String detectTime, String sql);

    /**
     * <p>
     * 删除慢SQL
     * </p>
     *
     * @param ids 主键ID集合
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2026/1/16 11:32
     */
    LayUiAdminResultVo deleteSlowSql(List<Long> ids);

    /**
     * <p>
     * 清空慢SQL
     * </p>
     *
     * @return layUiAdmin响应对象：如果清空成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2026/1/16 15:45
     */
    LayUiAdminResultVo cleanupSlowSql();

    /**
     * <p>
     * 根据主键ID获取MySQL数据库慢SQL信息
     * </p>
     *
     * @param id 主键ID
     * @return MySQL数据库慢SQL表现层对象
     * @author 皮锋
     * @custom.date 2026/1/16 14:28
     */
    DbSlowSql4MysqlVo getDbSlowSql4MysqlVoById(Long id);

}
