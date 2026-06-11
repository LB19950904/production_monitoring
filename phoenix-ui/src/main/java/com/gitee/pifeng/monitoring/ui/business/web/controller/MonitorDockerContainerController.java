package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorEnv;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorGroup;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerContainerService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorEnvService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorGroupService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerContainerVo;
import com.gitee.pifeng.monitoring.ui.constant.OperateTypeConstants;
import com.gitee.pifeng.monitoring.ui.constant.UiModuleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * docker容器信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-25
 */
@Controller
@Tag(name = "docker.docker容器信息")
@RequestMapping("/monitor-docker-container")
public class MonitorDockerContainerController extends BaseController {

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
     * docker容器信息服务类
     */
    @Autowired
    private IMonitorDockerContainerService monitorDockerContainerService;

    /**
     * <p>
     * 访问docker容器列表页面
     * </p>
     *
     * @return {@link ModelAndView} docker容器列表页面
     * @author 皮锋
     * @custom.date 2022/8/5 22:16
     */
    @Operation(summary = "访问docker容器列表页面")
    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("docker/docker-container");
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
     * 获取docker容器列表
     * </p>
     *
     * @param current       当前页
     * @param size          每页显示条数
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @param imageName     镜像名
     * @param status        容器状态
     * @param monitorEnv    监控环境
     * @param monitorGroup  监控分组
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/8/16 17:28
     */
    @Operation(summary = "获取docker容器列表")
    @Parameters(value = {
            @Parameter(name = "current", description = "当前页", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "每页显示条数", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "serverIp", description = "服务器IP", in = ParameterIn.QUERY),
            @Parameter(name = "containerName", description = "容器名", in = ParameterIn.QUERY),
            @Parameter(name = "imageName", description = "镜像名", in = ParameterIn.QUERY),
            @Parameter(name = "status", description = "容器状态", in = ParameterIn.QUERY),
            @Parameter(name = "monitorEnv", description = "监控环境", in = ParameterIn.QUERY),
            @Parameter(name = "monitorGroup", description = "监控分组", in = ParameterIn.QUERY)})
    @GetMapping("/get-monitor-docker-container-list")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.QUERY, operDesc = "获取docker容器列表")
    public LayUiAdminResultVo getMonitorDockerContainerList(@RequestParam(value = "current") Long current,
                                                            @RequestParam(value = "size") Long size,
                                                            @RequestParam(value = "serverIp", required = false) String serverIp,
                                                            @RequestParam(value = "containerName", required = false) String containerName,
                                                            @RequestParam(value = "imageName", required = false) String imageName,
                                                            @RequestParam(value = "status", required = false) String status,
                                                            @RequestParam(value = "monitorEnv", required = false) String monitorEnv,
                                                            @RequestParam(value = "monitorGroup", required = false) String monitorGroup) {
        Page<MonitorDockerContainerVo> page = this.monitorDockerContainerService.getMonitorDockerContainerList(current, size, serverIp,
                containerName, imageName, status, monitorEnv, monitorGroup);
        return LayUiAdminResultVo.ok(page);
    }

    /**
     * <p>
     * 访问docker容器详情页面
     * </p>
     *
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @return {@link ModelAndView} docker容器详情页面
     * @author 皮锋
     * @custom.date 2022/8/21 16:33
     */
    @Operation(summary = "访问docker容器详情页面")
    @Parameters(value = {
            @Parameter(name = "serverIp", description = "服务器IP", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerName", description = "容器名", required = true, in = ParameterIn.QUERY)
    })
    @GetMapping("/docker-container-detail")
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.PAGE, operDesc = "访问docker容器详情页面")
    public ModelAndView dockerContainerDetail(String serverIp, String containerName) {
        ModelAndView mv = new ModelAndView("docker/docker-container-detail");
        mv.addObject("serverIp", serverIp);
        mv.addObject("containerName", containerName);
        MonitorDockerContainerVo monitorDockerContainerVo = this.monitorDockerContainerService.getMonitorDockerContainerInfo(serverIp, containerName);
        mv.addObject("monitorDockerContainerVo", monitorDockerContainerVo);
        return mv;
    }

    /**
     * <p>
     * 删除docker容器
     * </p>
     *
     * @param monitorDockerContainerVos docker容器信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    @Operation(summary = "删除docker容器")
    @DeleteMapping("/delete-monitor-docker-container")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.DELETE, operDesc = "删除docker容器")
    public LayUiAdminResultVo deleteMonitorDockerContainer(@RequestBody List<MonitorDockerContainerVo> monitorDockerContainerVos) {
        return this.monitorDockerContainerService.deleteMonitorDockerContainer(monitorDockerContainerVos);
    }

    /**
     * <p>
     * 访问控制docker容器表单页面
     * </p>
     *
     * @param dockerId    docker服务ID
     * @param containerId docker容器ID
     * @return {@link ModelAndView} 控制docker容器表单页面
     * @author 皮锋
     * @custom.date 2022/9/21 22:56
     */
    @Operation(summary = "访问控制docker容器表单页面")
    @Parameters(value = {
            @Parameter(name = "dockerId", description = "docker服务ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerId", description = "docker容器ID", required = true, in = ParameterIn.QUERY)
    })
    @GetMapping("/docker-container-control")
    public ModelAndView dockerContainerControl(Long dockerId, String containerId) {
        ModelAndView mv = new ModelAndView("docker/docker-container-control");
        mv.addObject("dockerId", dockerId);
        mv.addObject("containerId", containerId);
        return mv;
    }

    /**
     * <p>
     * 启动docker容器（命令下发）
     * </p>
     *
     * @param dockerId    docker服务ID
     * @param containerId docker容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 21:10
     */
    @Operation(summary = "启动docker容器（命令下发）")
    @PostMapping("/start-docker-container")
    @Parameters(value = {
            @Parameter(name = "dockerId", description = "docker服务ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerId", description = "docker容器ID", required = true, in = ParameterIn.QUERY)
    })
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.CONTROL, operDesc = "启动docker容器")
    public LayUiAdminResultVo startDockerContainer(Long dockerId, String containerId) throws IOException, SigarException {
        return this.monitorDockerContainerService.startDockerContainer(dockerId, containerId);
    }

    /**
     * <p>
     * 停止docker容器（命令下发）
     * </p>
     *
     * @param dockerId    docker服务ID
     * @param containerId docker容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 21:10
     */
    @Operation(summary = "停止docker容器（命令下发）")
    @PostMapping("/stop-docker-container")
    @Parameters(value = {
            @Parameter(name = "dockerId", description = "docker服务ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerId", description = "docker容器ID", required = true, in = ParameterIn.QUERY)
    })
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.CONTROL, operDesc = "停止docker容器")
    public LayUiAdminResultVo stopDockerContainer(Long dockerId, String containerId) throws IOException, SigarException {
        return this.monitorDockerContainerService.stopDockerContainer(dockerId, containerId);
    }

    /**
     * <p>
     * 重启docker容器（命令下发）
     * </p>
     *
     * @param dockerId    docker服务ID
     * @param containerId docker容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 21:10
     */
    @Operation(summary = "重启docker容器（命令下发）")
    @PostMapping("/restart-docker-container")
    @Parameters(value = {
            @Parameter(name = "dockerId", description = "docker服务ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerId", description = "docker容器ID", required = true, in = ParameterIn.QUERY)
    })
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.CONTROL, operDesc = "重启docker容器")
    public LayUiAdminResultVo restartDockerContainer(Long dockerId, String containerId) throws IOException, SigarException {
        return this.monitorDockerContainerService.restartDockerContainer(dockerId, containerId);
    }

    /**
     * <p>
     * 销毁docker容器（命令下发）
     * </p>
     *
     * @param dockerId    docker服务ID
     * @param containerId docker容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 21:10
     */
    @Operation(summary = "销毁docker容器（命令下发）")
    @PostMapping("/destroy-docker-container")
    @Parameters(value = {
            @Parameter(name = "dockerId", description = "docker服务ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerId", description = "docker容器ID", required = true, in = ParameterIn.QUERY)
    })
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.CONTROL, operDesc = "销毁docker容器")
    public LayUiAdminResultVo destroyDockerContainer(Long dockerId, String containerId) throws IOException, SigarException {
        return this.monitorDockerContainerService.destroyDockerContainer(dockerId, containerId);
    }

}

