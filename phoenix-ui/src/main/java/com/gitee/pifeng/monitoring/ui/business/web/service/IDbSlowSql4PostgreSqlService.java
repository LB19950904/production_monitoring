package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDbSlowSqlPostgreSql;
import com.gitee.pifeng.monitoring.ui.business.web.vo.DbSlowSql4PostgreSqlVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;

import java.util.List;

/**
 * <p>
 * PostgreSQL数据库慢SQL服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-06-13
 */
public interface IDbSlowSql4PostgreSqlService extends IService<MonitorDbSlowSqlPostgreSql> {

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
     * @custom.date 2025/6/13 16:10
     */
    Page<DbSlowSql4PostgreSqlVo> getSlowSqlList(Long current, Long size, Long dbId, String dbName, String command,
                                                 String state, String detectTime, String sql);

    /**
     * <p>
     * 删除慢SQL
     * </p>
     *
     * @param ids 主键ID集合
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025/6/13 16:10
     */
    LayUiAdminResultVo deleteSlowSql(List<Long> ids);

    /**
     * <p>
     * 清空慢SQL
     * </p>
     *
     * @return layUiAdmin响应对象：如果清空成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025/6/13 16:10
     */
    LayUiAdminResultVo cleanupSlowSql();

    /**
     * <p>
     * 根据主键ID获取PostgreSQL数据库慢SQL信息
     * </p>
     *
     * @param id 主键ID
     * @return PostgreSQL数据库慢SQL表现层对象
     * @author 皮锋
     * @custom.date 2025/6/13 16:10
     */
    DbSlowSql4PostgreSqlVo getDbSlowSql4PostgreSqlVoById(Long id);

}
