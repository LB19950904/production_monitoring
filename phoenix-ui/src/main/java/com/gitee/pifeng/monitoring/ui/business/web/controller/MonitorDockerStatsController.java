package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorEnv;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorGroup;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerStatsService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorEnvService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorGroupService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerStatsVo;
import com.gitee.pifeng.monitoring.ui.constant.OperateTypeConstants;
import com.gitee.pifeng.monitoring.ui.constant.UiModuleConstants;
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
import java.util.stream.Collectors;

/**
 * <p>
 * docker容器统计信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-24
 */
@Controller
@Tag(name = "docker.docker统计信息")
@RequestMapping("/monitor-docker-stats")
public class MonitorDockerStatsController extends BaseController {

    /**
     * 监控环境服务类
     */
    @Autowired
    private IMonitorEnvService monitorEnvService;

    /**
     * 监控分组服务类
     */
    @Autowired
    private IMonitorGroupService monitorGroupService;

    /**
     * docker容器统计信息服务类
     */
    @Autowired
    private IMonitorDockerStatsService monitorDockerStatsService;

    /**
     * <p>
     * 访问docker资源统计列表页面
     * </p>
     *
     * @return {@link ModelAndView} docker资源统计列表页面
     * @author 皮锋
     * @custom.date 2022/8/5 22:16
     */
    @Operation(summary = "访问docker资源统计列表页面")
    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("docker/docker-stats");
        // 监控环境列表
        List<String> monitorEnvs = this.monitorEnvService.list().stream().map(MonitorEnv::getEnvName).collect(Collectors.toList());
        // 监控分组列表
        List<MonitorGroup> monitorGroupList = this.monitorGroupService.getMonitorGroupList(MonitorTypeEnums.DOCKER);
        List<String> monitorGroups = monitorGroupList.stream().map(MonitorGroup::getGroupName).collect(Collectors.toList());
        mv.addObject("monitorEnvs", monitorEnvs);
        mv.addObject("monitorGroups", monitorGroups);
        return mv;
    }

    /**
     * <p>
     * 获取docker资源统计列表
     * </p>
     *
     * @param current       当前页
     * @param size          每页显示条数
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @param monitorEnv    监控环境
     * @param monitorGroup  监控分组
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/8/21 20:47
     */
    @Operation(summary = "获取docker资源统计列表")
    @Parameters(value = {
            @Parameter(name = "current", description = "当前页", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "每页显示条数", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "serverIp", description = "服务器IP", in = ParameterIn.QUERY),
            @Parameter(name = "containerName", description = "容器名", in = ParameterIn.QUERY),
            @Parameter(name = "monitorEnv", description = "监控环境", in = ParameterIn.QUERY),
            @Parameter(name = "monitorGroup", description = "监控分组", in = ParameterIn.QUERY)})
    @GetMapping("/get-monitor-docker-stats-list")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.QUERY, operDesc = "获取docker资源统计列表")
    public LayUiAdminResultVo getMonitorDockerStatsList(@RequestParam(value = "current") Long current,
                                                        @RequestParam(value = "size") Long size,
                                                        @RequestParam(value = "serverIp", required = false) String serverIp,
                                                        @RequestParam(value = "containerName", required = false) String containerName,
                                                        @RequestParam(value = "monitorEnv", required = false) String monitorEnv,
                                                        @RequestParam(value = "monitorGroup", required = false) String monitorGroup) {
        Page<MonitorDockerStatsVo> page = this.monitorDockerStatsService.getMonitorDockerStatsList(current, size, serverIp, containerName, monitorEnv, monitorGroup);
        return LayUiAdminResultVo.ok(page);
    }

    /**
     * <p>
     * 删除docker资源统计
     * </p>
     *
     * @param monitorDockerStatsVos docker资源统计信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    @Operation(summary = "删除docker资源统计")
    @DeleteMapping("/delete-monitor-docker-stats")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.DELETE, operDesc = "删除docker资源统计")
    public LayUiAdminResultVo deleteMonitorDockerStats(@RequestBody List<MonitorDockerStatsVo> monitorDockerStatsVos) {
        return this.monitorDockerStatsService.deleteMonitorDockerStats(monitorDockerStatsVos);
    }

    /**
     * <p>
     * 访问docker资源统计详情页面
     * </p>
     *
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @return {@link ModelAndView} docker资源统计详情页面
     * @author 皮锋
     * @custom.date 2022/8/21 16:33
     */
    @Operation(summary = "访问docker资源统计详情页面")
    @Parameters(value = {
            @Parameter(name = "serverIp", description = "服务器IP", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerName", description = "容器名", required = true, in = ParameterIn.QUERY)
    })
    @GetMapping("/docker-stats-detail")
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.PAGE, operDesc = "访问docker资源统计详情页面")
    public ModelAndView dockerStatsDetail(String serverIp, String containerName) {
        ModelAndView mv = new ModelAndView("docker/docker-stats-detail");
        mv.addObject("serverIp", serverIp);
        mv.addObject("containerName", containerName);
        MonitorDockerStatsVo monitorDockerStatsVo = this.monitorDockerStatsService.getMonitorDockerStatsInfo(serverIp, containerName);
        mv.addObject("monitorDockerStatsVo", monitorDockerStatsVo);
        return mv;
    }

}

