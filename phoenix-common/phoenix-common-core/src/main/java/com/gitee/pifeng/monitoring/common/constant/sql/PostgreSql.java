package com.gitee.pifeng.monitoring.common.constant.sql;

/**
 * <p>
 * PostgreSQL数据库sql语句
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/6/13 14:30
 */
public class PostgreSql {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2025/6/13 14:30
     */
    private PostgreSql() {
    }

    /**
     * 检查连接
     */
    public static final String CHECK_CONN = "SELECT 1";

    /**
     * 会话列表
     * 查询PostgreSQL当前活跃的会话信息，用于慢SQL监控
     */
    public static final String SESSION_LIST = "SELECT " +
            "pid AS \"Id\", " +
            "usename AS \"User\", " +
            "client_addr AS \"Host\", " +
            "datname AS \"db\", " +
            "COALESCE(state, '') AS \"Command\", " +
            "COALESCE(EXTRACT(EPOCH FROM (NOW() - query_start)), 0) AS \"Time\", " +
            "COALESCE(state, '') AS \"State\", " +
            "query AS \"Info\" " +
            "FROM " +
            "pg_stat_activity " +
            "WHERE " +
            "pid != pg_backend_pid() " +
            "AND query NOT LIKE '%pg_stat_activity%' " +
            "ORDER BY " +
            "pid ASC";

    /**
     * 结束会话
     * 终止指定的PostgreSQL会话
     */
    public static final String KILL_SESSION = "SELECT pg_terminate_backend(?)";

}
