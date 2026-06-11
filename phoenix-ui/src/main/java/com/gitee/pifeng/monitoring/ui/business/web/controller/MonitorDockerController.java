package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorEnv;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorGroup;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorEnvService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorGroupService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerVo;
import com.gitee.pifeng.monitoring.ui.constant.OperateTypeConstants;
import com.gitee.pifeng.monitoring.ui.constant.UiModuleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * docker
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-04
 */
@Controller
@Tag(name = "docker")
@RequestMapping("/monitor-docker")
public class MonitorDockerController extends BaseController {

    /**
     * docker服务类
     */
    @Autowired
    private IMonitorDockerService monitorDockerService;

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
     * <p>
     * 访问docker列表页面
     * </p>
     *
     * @return {@link ModelAndView} docker列表页面
     * @author 皮锋
     * @custom.date 2022/7/5 21:16
     */
    @Operation(summary = "访问docker列表页面")
    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("docker/docker");
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
     * 获取docker列表
     * </p>
     *
     * @param current         当前页
     * @param size            每页显示条数
     * @param serverIp        服务器IP
     * @param isOnline        状态
     * @param monitorEnv      监控环境
     * @param monitorGroup    监控分组
     * @param dockerSummary   docker摘要
     * @param isEnableMonitor 是否开启监控（0：不开启监控；1：开启监控）
     * @param isEnableAlarm   是否开启告警（0：不开启告警；1：开启告警）
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/7/5 22:08
     */
    @Operation(summary = "获取docker列表")
    @Parameters(value = {
            @Parameter(name = "current", description = "当前页", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "每页显示条数", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "serverIp", description = "服务器IP", in = ParameterIn.QUERY),
            @Parameter(name = "isOnline", description = "状态", in = ParameterIn.QUERY),
            @Parameter(name = "monitorEnv", description = "监控环境", in = ParameterIn.QUERY),
            @Parameter(name = "monitorGroup", description = "监控分组", in = ParameterIn.QUERY),
            @Parameter(name = "dockerSummary", description = "docker摘要", in = ParameterIn.QUERY),
            @Parameter(name = "isEnableMonitor", description = "是否开启监控（0：不开启监控；1：开启监控）", in = ParameterIn.QUERY),
            @Parameter(name = "isEnableAlarm", description = "是否开启告警（0：不开启告警；1：开启告警）", in = ParameterIn.QUERY)})
    @GetMapping("/get-monitor-docker-list")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.QUERY, operDesc = "获取docker列表")
    public LayUiAdminResultVo getMonitorDockerList(@RequestParam(value = "current") Long current,
                                                   @RequestParam(value = "size") Long size,
                                                   @RequestParam(value = "serverIp", required = false) String serverIp,
                                                   @RequestParam(value = "isOnline", required = false) String isOnline,
                                                   @RequestParam(value = "monitorEnv", required = false) String monitorEnv,
                                                   @RequestParam(value = "monitorGroup", required = false) String monitorGroup,
                                                   @RequestParam(value = "dockerSummary", required = false) String dockerSummary,
                                                   @RequestParam(value = "isEnableMonitor", required = false) String isEnableMonitor,
                                                   @RequestParam(value = "isEnableAlarm", required = false) String isEnableAlarm) {
        Page<MonitorDockerVo> page = this.monitorDockerService.getMonitorDockerList(current, size, serverIp, isOnline,
                monitorEnv, monitorGroup, dockerSummary, isEnableMonitor, isEnableAlarm);
        return LayUiAdminResultVo.ok(page);
    }

    /**
     * <p>
     * 删除docker
     * </p>
     *
     * @param monitorDockerVos docker信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    @Operation(summary = "删除docker")
    @DeleteMapping("/delete-monitor-docker")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.DELETE, operDesc = "删除docker")
    public LayUiAdminResultVo deleteMonitorDocker(@RequestBody List<MonitorDockerVo> monitorDockerVos) {
        return this.monitorDockerService.deleteMonitorDocker(monitorDockerVos);
    }

    /**
     * <p>
     * 访问docker编辑页面
     * </p>
     *
     * @param id docker主键ID
     * @return {@link ModelAndView} docker编辑页面
     * @author 皮锋
     * @custom.date 2022/7/8 21:37
     */
    @Operation(summary = "访问docker编辑页面")
    @Parameters(value = {
            @Parameter(name = "id", description = "docker主键ID", required = true, in = ParameterIn.QUERY)})
    @GetMapping("/edit-monitor-docker-form")
    public ModelAndView editMonitorDockerForm(Long id) {
        ModelAndView mv = new ModelAndView("docker/edit-docker");
        mv.addObject("id", id);
        // 监控环境列表
        List<String> monitorEnvs = this.monitorEnvService.list().stream().map(MonitorEnv::getEnvName).collect(Collectors.toList());
        // 监控分组列表
        List<MonitorGroup> monitorGroupList = this.monitorGroupService.getMonitorGroupList(MonitorTypeEnums.DOCKER);
        List<String> monitorGroups = monitorGroupList.stream().map(MonitorGroup::getGroupName).collect(Collectors.toList());
        mv.addObject("monitorEnvs", monitorEnvs);
        mv.addObject("monitorGroups", monitorGroups);
        // docker信息
        MonitorDockerVo monitorDockerVo = this.monitorDockerService.getMonitorDockerInfo(id);
        mv.addObject("dockerSummary", monitorDockerVo.getDockerSummary());
        mv.addObject("env", monitorDockerVo.getMonitorEnv());
        mv.addObject("group", monitorDockerVo.getMonitorGroup());
        mv.addObject("isEnableMonitor", monitorDockerVo.getIsEnableMonitor());
        mv.addObject("isEnableAlarm", monitorDockerVo.getIsEnableAlarm());
        return mv;
    }

    /**
     * <p>
     * 编辑docker信息
     * </p>
     *
     * @param monitorDockerVo docker信息
     * @return 如果编辑成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:45
     */
    @Operation(summary = "编辑docker信息")
    @PutMapping("/edit-monitor-docker")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.UPDATE, operDesc = "编辑docker信息")
    public LayUiAdminResultVo editMonitorDocker(MonitorDockerVo monitorDockerVo) {
        return this.monitorDockerService.editMonitorDocker(monitorDockerVo);
    }

    /**
     * <p>
     * 设置是否开启监控（0：不开启监控；1：开启监控）
     * </p>
     *
     * @param id              主键ID
     * @param isEnableMonitor 是否开启监控（0：不开启监控；1：开启监控）
     * @return 如果设置成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2024/12/10 21:20
     */
    @Operation(summary = "设置是否开启监控（0：不开启监控；1：开启监控）")
    @Parameters(value = {
            @Parameter(name = "id", description = "主键ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "isEnableMonitor", description = "是否开启监控（0：不开启监控；1：开启监控）", in = ParameterIn.QUERY)})
    @PutMapping("/set-is-enable-monitor")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.UPDATE, operDesc = "设置是否开启监控（0：不开启监控；1：开启监控）")
    public LayUiAdminResultVo setIsEnableMonitor(@RequestParam(value = "id") Long id,
                                                 @RequestParam(value = "isEnableMonitor") String isEnableMonitor) {
        return this.monitorDockerService.setIsEnableMonitor(id, isEnableMonitor);
    }

    /**
     * <p>
     * 设置是否开启告警（0：不开启告警；1：开启告警）
     * </p>
     *
     * @param id            主键ID
     * @param isEnableAlarm 是否开启告警（0：不开启告警；1：开启告警）
     * @return 如果设置成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2024/12/10 21:37
     */
    @Operation(summary = "设置是否开启告警（0：不开启告警；1：开启告警）")
    @Parameters(value = {
            @Parameter(name = "id", description = "主键ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "isEnableAlarm", description = "是否开启告警（0：不开启告警；1：开启告警）", in = ParameterIn.QUERY)})
    @PutMapping("/set-is-enable-alarm")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.UPDATE, operDesc = "设置是否开启告警（0：不开启告警；1：开启告警）")
    public LayUiAdminResultVo setIsEnableAlarm(@RequestParam(value = "id") Long id,
                                               @RequestParam(value = "isEnableAlarm") String isEnableAlarm) {
        return this.monitorDockerService.setIsEnableAlarm(id, isEnableAlarm);
    }

    /**
     * <p>
     * 访问docker详情页面
     * </p>
     *
     * @param id docker主键ID
     * @return {@link ModelAndView} docker详情页面
     * @author 皮锋
     * @custom.date 2022/8/6 22:19
     */
    @Operation(summary = "访问docker详情页面")
    @Parameters(value = {
            @Parameter(name = "id", description = "docker主键ID", required = true, in = ParameterIn.QUERY)})
    @GetMapping("/docker-detail")
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.PAGE, operDesc = "访问docker详情页面")
    public ModelAndView dockereDetail(Long id) {
        ModelAndView mv = new ModelAndView("docker/docker-detail");
        mv.addObject("id", id);
        MonitorDockerVo monitorDockerInfo = this.monitorDockerService.getMonitorDockerInfo(id);
        String rawValues = monitorDockerInfo.getRawValues();
        rawValues = StringUtils.replace(rawValues, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        rawValues = StringUtils.replace(rawValues, "\n", "<br>");
        monitorDockerInfo.setRawValues(rawValues);
        mv.addObject("monitorDockerInfo", monitorDockerInfo);
        return mv;
    }

    /**
     * <p>
     * 根据条件获取docker信息
     * </p>
     *
     * @param id docker主键ID
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/8/6 22:38
     */
    @Operation(summary = "根据条件获取docker信息")
    @Parameters(value = {
            @Parameter(name = "id", description = "docker主键ID", required = true, in = ParameterIn.QUERY)})
    @GetMapping("/get-monitor-docker-info")
    @ResponseBody
    public LayUiAdminResultVo getMonitorDockerInfo(@RequestParam(value = "id") Long id) {
        MonitorDockerVo monitorDockerInfo = this.monitorDockerService.getMonitorDockerInfo(id);
        String rawValues = monitorDockerInfo.getRawValues();
        rawValues = StringUtils.replace(rawValues, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        rawValues = StringUtils.replace(rawValues, "\n", "<br>");
        monitorDockerInfo.setRawValues(rawValues);
        return LayUiAdminResultVo.ok(monitorDockerInfo);
    }

}

