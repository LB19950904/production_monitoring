package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorEnv;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorGroup;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerImageService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorEnvService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorGroupService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerImageVo;
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
 * docker镜像信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-26
 */
@Controller
@Tag(name = "docker.docker镜像信息")
@RequestMapping("/monitor-docker-image")
public class MonitorDockerImageController extends BaseController {

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
     * docker镜像信息服务类
     */
    @Autowired
    private IMonitorDockerImageService monitorDockerImageService;

    /**
     * <p>
     * 访问docker镜像列表页面
     * </p>
     *
     * @return {@link ModelAndView} docker镜像列表页面
     * @author 皮锋
     * @custom.date 2022/8/5 22:16
     */
    @Operation(summary = "访问docker镜像列表页面")
    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("docker/docker-image");
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
     * 获取docker镜像列表
     * </p>
     *
     * @param current         当前页
     * @param size            每页显示条数
     * @param serverIp        服务器IP
     * @param imageRepository 镜像仓库
     * @param monitorEnv      监控环境
     * @param monitorGroup    监控分组
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/8/21 20:47
     */
    @Operation(summary = "获取docker镜像列表")
    @Parameters(value = {
            @Parameter(name = "current", description = "当前页", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "每页显示条数", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "serverIp", description = "服务器IP", in = ParameterIn.QUERY),
            @Parameter(name = "imageRepository", description = "镜像仓库", in = ParameterIn.QUERY),
            @Parameter(name = "monitorEnv", description = "监控环境", in = ParameterIn.QUERY),
            @Parameter(name = "monitorGroup", description = "监控分组", in = ParameterIn.QUERY)})
    @GetMapping("/get-monitor-docker-image-list")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.QUERY, operDesc = "获取docker镜像列表")
    public LayUiAdminResultVo getMonitorDockerImageList(@RequestParam(value = "current") Long current,
                                                        @RequestParam(value = "size") Long size,
                                                        @RequestParam(value = "serverIp", required = false) String serverIp,
                                                        @RequestParam(value = "imageRepository", required = false) String imageRepository,
                                                        @RequestParam(value = "monitorEnv", required = false) String monitorEnv,
                                                        @RequestParam(value = "monitorGroup", required = false) String monitorGroup) {
        Page<MonitorDockerImageVo> page = this.monitorDockerImageService.getMonitorDockerImageList(current, size, serverIp,
                imageRepository, monitorEnv, monitorGroup);
        return LayUiAdminResultVo.ok(page);
    }

    /**
     * <p>
     * 删除docker镜像
     * </p>
     *
     * @param monitorDockerImageVos docker镜像信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    @Operation(summary = "删除docker镜像")
    @DeleteMapping("/delete-monitor-docker-image")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.DELETE, operDesc = "删除docker镜像")
    public LayUiAdminResultVo deleteMonitorDockerImage(@RequestBody List<MonitorDockerImageVo> monitorDockerImageVos) {
        return this.monitorDockerImageService.deleteMonitorDockerImage(monitorDockerImageVos);
    }

    /**
     * <p>
     * 访问docker镜像详情页面
     * </p>
     *
     * @param serverIp 服务器IP
     * @param imageId  镜像ID
     * @return {@link ModelAndView} docker镜像详情页面
     * @author 皮锋
     * @custom.date 2022/8/21 16:33
     */
    @Operation(summary = "访问docker镜像详情页面")
    @Parameters(value = {
            @Parameter(name = "serverIp", description = "服务器IP", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "imageId", description = "镜像ID", required = true, in = ParameterIn.QUERY)
    })
    @GetMapping("/docker-image-detail")
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.PAGE, operDesc = "访问docker镜像详情页面")
    public ModelAndView dockerImageDetail(String serverIp, String imageId) {
        ModelAndView mv = new ModelAndView("docker/docker-image-detail");
        mv.addObject("serverIp", serverIp);
        mv.addObject("imageId", imageId);
        MonitorDockerImageVo monitorDockerImageVo = this.monitorDockerImageService.getMonitorDockerImageInfo(serverIp, imageId);
        mv.addObject("monitorDockerImageVo", monitorDockerImageVo);
        return mv;
    }

    /**
     * <p>
     * 访问控制docker镜像表单页面
     * </p>
     *
     * @param dockerId docker服务ID
     * @param imageId  docker镜像ID
     * @return {@link ModelAndView} 控制docker镜像表单页面
     * @author 皮锋
     * @custom.date 2022/9/21 22:56
     */
    @Operation(summary = "访问控制docker镜像表单页面")
    @Parameters(value = {
            @Parameter(name = "dockerId", description = "docker服务ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "imageId", description = "docker镜像ID", required = true, in = ParameterIn.QUERY)
    })
    @GetMapping("/docker-image-control")
    public ModelAndView dockerImageControl(Long dockerId, String imageId) {
        ModelAndView mv = new ModelAndView("docker/docker-image-control");
        mv.addObject("dockerId", dockerId);
        mv.addObject("imageId", imageId);
        return mv;
    }

    /**
     * <p>
     * 删除docker镜像（命令下发）
     * </p>
     *
     * @param dockerId docker服务ID
     * @param imageId  镜像ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 21:10
     */
    @Operation(summary = "删除docker镜像（命令下发）")
    @PostMapping("/delete-docker-image")
    @Parameters(value = {
            @Parameter(name = "dockerId", description = "docker服务ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "imageId", description = "镜像ID", required = true, in = ParameterIn.QUERY)
    })
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.CONTROL, operDesc = "删除docker镜像")
    public LayUiAdminResultVo deleteDockerImage(Long dockerId, String imageId) throws IOException, SigarException {
        return this.monitorDockerImageService.deleteDockerImage(dockerId, imageId);
    }

}

