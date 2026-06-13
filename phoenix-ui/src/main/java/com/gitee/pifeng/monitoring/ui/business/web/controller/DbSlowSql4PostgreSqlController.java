package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog;
import com.gitee.pifeng.monitoring.ui.business.web.service.IDbSlowSql4PostgreSqlService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.DbSlowSql4PostgreSqlVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.constant.OperateTypeConstants;
import com.gitee.pifeng.monitoring.ui.constant.UiModuleConstants;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * <p>
 * PostgreSQL数据库慢SQL
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-06-13
 */
@Controller
@Tag(name = "数据库慢SQL.PostgreSQL")
@RequestMapping("/db-slowsql4postgresql")
public class DbSlowSql4PostgreSqlController extends BaseController {

    /**
     * PostgreSQL数据库慢SQL服务类
     */
    @Autowired
    private IDbSlowSql4PostgreSqlService dbSlowSql4PostgreSqlService;

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
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2025/6/13 16:20
     */
    @Operation(summary = "获取慢SQL列表")
    @Parameters(value = {
            @Parameter(name = "current", description = "当前页", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "每页显示条数", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "dbId", description = "数据库表ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "dbName", description = "数据库名", in = ParameterIn.QUERY),
            @Parameter(name = "command", description = "命令", in = ParameterIn.QUERY),
            @Parameter(name = "state", description = "状态", in = ParameterIn.QUERY),
            @Parameter(name = "detectTime", description = "检测时间", in = ParameterIn.QUERY),
            @Parameter(name = "sql", description = "SQL", in = ParameterIn.QUERY)})
    @GetMapping("/get-slowsql-list")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DATABASE + "#PostgreSQL慢SQL", operType = OperateTypeConstants.QUERY, operDesc = "获取慢SQL列表")
    public LayUiAdminResultVo getSlowSqlList(@RequestParam(value = "current") Long current,
                                             @RequestParam(value = "size") Long size,
                                             @RequestParam(value = "dbId") Long dbId,
                                             @RequestParam(value = "dbName", required = false) String dbName,
                                             @RequestParam(value = "command", required = false) String command,
                                             @RequestParam(value = "state", required = false) String state,
                                             @RequestParam(value = "detectTime", required = false) String detectTime,
                                             @RequestParam(value = "sql", required = false) String sql) {
        Page<DbSlowSql4PostgreSqlVo> page = this.dbSlowSql4PostgreSqlService.getSlowSqlList(current, size, dbId, dbName, command, state, detectTime, sql);
        return LayUiAdminResultVo.ok(page);
    }

    /**
     * <p>
     * 删除慢SQL
     * </p>
     *
     * @param ids 主键ID集合
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025/6/13 16:20
     */
    @Operation(summary = "删除慢SQL")
    @DeleteMapping("/delete-slowsql")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DATABASE + "#PostgreSQL慢SQL", operType = OperateTypeConstants.DELETE, operDesc = "删除慢SQL")
    public LayUiAdminResultVo deleteSlowSql(@RequestBody List<Long> ids) {
        return this.dbSlowSql4PostgreSqlService.deleteSlowSql(ids);
    }

    /**
     * <p>
     * 清空慢SQL
     * </p>
     *
     * @return layUiAdmin响应对象：如果清空成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025/6/13 16:20
     */
    @Operation(summary = "清空慢SQL")
    @DeleteMapping("/cleanup-slowsql")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DATABASE + "#PostgreSQL慢SQL", operType = OperateTypeConstants.DELETE, operDesc = "清空慢SQL")
    public LayUiAdminResultVo cleanupSlowSql() {
        return this.dbSlowSql4PostgreSqlService.cleanupSlowSql();
    }

    /**
     * <p>
     * 访问慢SQL详情页面
     * </p>
     *
     * @param id 主键ID
     * @return {@link ModelAndView} 慢SQL详情页面
     * @author 皮锋
     * @custom.date 2025/6/13 16:20
     */
    @Operation(summary = "访问慢SQL详情页面")
    @Parameters(value = {
            @Parameter(name = "id", description = "主键ID", required = true, in = ParameterIn.QUERY)})
    @GetMapping("/slowsql-detail")
    @OperateLog(operModule = UiModuleConstants.DATABASE + "#PostgreSQL慢SQL", operType = OperateTypeConstants.PAGE, operDesc = "访问慢SQL详情页面")
    public ModelAndView slowSqlDetail(Long id) {
        ModelAndView mv = new ModelAndView("db/db-postgresql-slowsql-detail");
        DbSlowSql4PostgreSqlVo slowSql4PostgreSqlVo = this.dbSlowSql4PostgreSqlService.getDbSlowSql4PostgreSqlVoById(id);
        mv.addObject("slowSql4PostgreSqlVo", slowSql4PostgreSqlVo);
        return mv;
    }

}
