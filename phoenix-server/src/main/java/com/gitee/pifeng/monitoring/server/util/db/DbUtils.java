package com.gitee.pifeng.monitoring.server.util.db;

import cn.hutool.db.ds.simple.SimpleDataSource;
import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLDeleteStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.ast.statement.SQLUpdateStatement;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.sql.visitor.VisitorFeature;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

/**
 * <p>
 * 数据库工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020/12/29 21:16
 */
@Slf4j
public class DbUtils {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2020/12/29 21:17
     */
    private DbUtils() {
    }

    /**
     * <p>
     * 使用 Hutool 的 {@link SimpleDataSource} 创建一次性连接，密码需为 Base64 编码字符串，方法内部会自动解码为 UTF-8 明文。
     * </p>
     *
     * @param url      数据库连接 URL（例如：jdbc:mysql://localhost:3306/test）
     * @param username 数据库用户名
     * @param password base64编码后的数据库密码
     * @return 可用的 {@link Connection} 数据库连接
     * @throws SQLException SQL异常
     * @author 皮锋
     * @custom.date 2020/12/29 21:17
     */
    public static Connection getConnection(String url, String username, String password) throws SQLException {
        try {
            String pwd = new String(Base64.getDecoder().decode(password), StandardCharsets.UTF_8);
            // 处理p6spy代理URL，将其转换为标准JDBC URL
            // 例如：jdbc:p6spy:mysql://... 转换为 jdbc:mysql://...
            String actualUrl = url;
            if (StringUtils.isNotBlank(url) && url.startsWith("jdbc:p6spy:")) {
                actualUrl = url.replace("jdbc:p6spy:", "jdbc:");
                log.debug("检测到p6spy代理URL，已转换为标准JDBC URL：{}", actualUrl);
            }
            // 根据URL判断数据库类型并指定驱动类名
            String driverClassName = null;
            if (actualUrl.contains("jdbc:mysql:") || actualUrl.contains("jdbc:p6spy:mysql:")) {
                driverClassName = "com.mysql.cj.jdbc.Driver";
            } else if (actualUrl.contains("jdbc:oracle:") || actualUrl.contains("jdbc:p6spy:oracle:")) {
                driverClassName = "oracle.jdbc.OracleDriver";
            } else if (actualUrl.contains("jdbc:postgresql:") || actualUrl.contains("jdbc:p6spy:postgresql:")) {
                driverClassName = "org.postgresql.Driver";
            }
            // 显式加载驱动类，确保JDBC驱动被注册到DriverManager
            if (StringUtils.isNotBlank(driverClassName)) {
                try {
                    Class.forName(driverClassName);
                    log.debug("显式加载JDBC驱动类：{}", driverClassName);
                } catch (ClassNotFoundException e) {
                    log.warn("无法加载JDBC驱动类：{}，将尝试让Hutool自动加载", driverClassName);
                }
            }
            // 数据源
            @Cleanup
            SimpleDataSource ds = new SimpleDataSource(actualUrl, username, pwd);
            return ds.getConnection();
        } catch (SQLException e) {
            log.error("与数据库建立连接异常！", e);
            throw e;
        } catch (IllegalArgumentException e) {
            log.error("Base64 密码解码异常！", e);
            throw e;
        }
    }

    /**
     * <p>
     * 判断 SQL 语句是否属于需要监控的类型（SELECT / INSERT / UPDATE / DELETE）。
     * </p>
     *
     * @param sql    原始 SQL 字符串，可为 {@code null} 或空白
     * @param dbType 数据库类型（来自 Druid 的 {@link DbType} 枚举）
     * @return {@code true} 表示该 SQL 属于需监控的 DML/DQL 类型；{@code false} 表示不属于
     * @author 皮锋
     * @custom.date 2026/1/6 16:40
     */
    public static boolean isMonitoredSqlType(String sql, DbType dbType) {
        if (StringUtils.isBlank(sql)) {
            return false;
        }
        // 使用 JSqlParser 判断 SQL 是否为受监控的语句类型
        boolean b = isMonitoredSqlTypeByJsqlParser(sql);
        if (!b) {
            // 使用 Druid SQL Parser 判断 SQL 是否为受监控的语句类型
            b = isMonitoredSqlTypeByDruidParser(sql, dbType);
        }
        return b;
    }

    /**
     * <p>
     * 使用 JSqlParser 判断 SQL 是否为受监控的语句类型。
     * JSqlParser 能自动忽略注释、处理大小写、跳过前导空白，但对 Oracle 特有语法（如 :bind、NEXTVAL）支持有限。
     * 若解析失败（如遇到非标准 SQL），直接返回 {@code false}，由上层调用方决定是否使用其他解析器兜底。
     * </p>
     *
     * @param sql 原始 SQL 字符串，可为 {@code null} 或空白
     * @return {@code true} 表示成功解析；否则返回 {@code false}
     * @author 皮锋
     * @custom.date 2026/1/7 09:42
     */
    private static boolean isMonitoredSqlTypeByJsqlParser(String sql) {
        if (StringUtils.isBlank(sql)) {
            return false;
        }
        try {
            // JSqlParser 会自动忽略注释、处理大小写、跳过前导空白
            Statement stmt = CCJSqlParserUtil.parse(sql);
            // 只关心以下四类语句
            return stmt instanceof Select
                    || stmt instanceof Insert
                    || stmt instanceof Update
                    || stmt instanceof Delete;
        } catch (JSQLParserException e) {
            // 记录无法解析的 SQL（通常是非 DML/DQL 语句）
            log.debug("SQL 无法被 JSQLParser 解析：{}", sql, e);
            return false;
        }
    }

    /**
     * <p>
     * 使用 Druid SQL Parser 判断 SQL 是否为受监控的语句类型。
     * 根据传入的 {@code dbType} 自动选择对应的数据库方言进行解析，对 Oracle、MySQL 等主流数据库兼容性更好。
     * 仅检查解析结果中的第一条语句（适用于慢 SQL 监控等单语句场景）。
     * </p>
     *
     * @param sql    原始 SQL 字符串，可为 {@code null} 或空白
     * @param dbType 数据库类型（来自 Druid 的 {@link DbType} 枚举）
     * @return {@code true} 表示成功解析；否则返回 {@code false}
     * @author 皮锋
     * @custom.date 2026/1/7 09:44
     */
    private static boolean isMonitoredSqlTypeByDruidParser(String sql, DbType dbType) {
        if (StringUtils.isBlank(sql)) {
            return false;
        }
        try {
            List<SQLStatement> statements = SQLUtils.parseStatements(sql, dbType);
            if (statements.isEmpty()) {
                return false;
            }
            // 仅检查第一条语句（适用于慢 SQL / 当前执行语句监控场景）
            SQLStatement stmt = statements.get(0);
            // 判断语句类型
            return stmt instanceof SQLSelectStatement
                    || stmt instanceof SQLInsertStatement
                    || stmt instanceof SQLUpdateStatement
                    || stmt instanceof SQLDeleteStatement;
        } catch (Exception e) {
            // 记录无法解析的 SQL（通常是非 DML/DQL 语句）
            log.debug("SQL 无法被 Druid 解析：{}", sql, e);
            return false;
        }
    }

    /**
     * <p>
     * 将 SQL 字符串进行格式标准化处理，失败则返回原始 SQL 字符串
     * </p>
     *
     * @param sql    原始 SQL 字符串，可为 {@code null} 或空白
     * @param dbType 数据库类型（来自 Druid 的 {@link DbType} 枚举）
     * @return 格式标准化处理后的 SQL 字符串
     * @author 皮锋
     * @custom.date 2026/1/8 10:08
     */
    public static String normalizeSql(String sql, DbType dbType) {
        try {
            String normalize = SQLUtils.normalize(sql, dbType);
            normalize = SQLUtils.format(normalize, dbType);
            return normalize;
        } catch (Exception e) {
            log.debug("无法将 SQL 字符串按照 {} 语法规则进行格式标准化处理：{}", dbType.name(), sql, e);
            return sql;
        }
    }

    /**
     * <p>
     * 将 SQL 字符串进行参数化处理，失败则返回原始 SQL 字符串
     * </p>
     *
     * @param sql    原始 SQL 字符串，可为 {@code null} 或空白
     * @param dbType 数据库类型（来自 Druid 的 {@link DbType} 枚举）
     * @return 参数化处理后的 SQL 字符串
     * @author 皮锋
     * @custom.date 2026/1/8 10:13
     */
    public static String parameterizeSql(String sql, DbType dbType) {
        try {
            return ParameterizedOutputVisitorUtils.parameterize(sql, dbType, VisitorFeature.OutputPrettyFormat);
        } catch (Exception e) {
            log.debug("无法将 SQL 字符串按照 {} 语法规则进行参数化处理：{}", dbType.name(), sql, e);
            return sql;
        }
    }

    /**
     * <p>
     * 安全截断 SQL 字符串，避免因 SQL 过长导致数据库存储异常、内存溢出或告警信息冗余。
     * </p>
     *
     * @param sql       原始 SQL 字符串，可能为 {@code null}
     * @param maxLength 允许的最大字符长度（必须大于 0）
     * @return 截断后的 SQL 字符串，或原始字符串（未超长时），或 {@code null}（输入为 {@code null} 时）
     * @author 皮锋
     * @custom.date 2026/1/20 17:16
     */
    public static String safeTruncateSql(String sql, int maxLength) {
        if (sql == null) {
            return null;
        }
        if (sql.length() <= maxLength) {
            return sql;
        }
        return sql.substring(0, maxLength) + " ... [TRUNCATED DUE TO LENGTH]";
    }

}
