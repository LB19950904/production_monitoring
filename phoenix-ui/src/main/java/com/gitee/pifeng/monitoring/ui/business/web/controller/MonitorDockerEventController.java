package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorEnv;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorGroup;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerEventService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorEnvService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorGroupService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerEventVo;
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
 * docker事件信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-03
 */
@Controller
@Tag(name = "docker.docker事件信息")
@RequestMapping("/monitor-docker-event")
public class MonitorDockerEventController extends BaseController {

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
     * docker事件信息服务类
     */
    @Autowired
    private IMonitorDockerEventService monitorDockerEventService;

    /**
     * <p>
     * 访问docker事件列表页面
     * </p>
     *
     * @return {@link ModelAndView} docker事件列表页面
     * @author 皮锋
     * @custom.date 2022/8/5 22:16
     */
    @Operation(summary = "访问docker事件列表页面")
    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("docker/docker-event");
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
     * 获取docker事件列表
     * </p>
     *
     * @param current      当前页
     * @param size         每页显示条数
     * @param eventId      事件ID
     * @param serverIp     服务器IP
     * @param eventStatus  事件状态
     * @param eventFrom    事件来源
     * @param eventType    事件类型
     * @param eventAction  事件动作
     * @param happenTime   事件发生时间
     * @param monitorEnv   监控环境
     * @param monitorGroup 监控分组
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/8/21 20:47
     */
    @Operation(summary = "获取docker事件列表")
    @Parameters(value = {
            @Parameter(name = "current", description = "当前页", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "每页显示条数", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "eventId", description = "事件ID", in = ParameterIn.QUERY),
            @Parameter(name = "serverIp", description = "服务器IP", in = ParameterIn.QUERY),
            @Parameter(name = "eventStatus", description = "事件状态", in = ParameterIn.QUERY),
            @Parameter(name = "eventFrom", description = "事件来源", in = ParameterIn.QUERY),
            @Parameter(name = "eventType", description = "事件类型", in = ParameterIn.QUERY),
            @Parameter(name = "eventAction", description = "事件动作", in = ParameterIn.QUERY),
            @Parameter(name = "happenTime", description = "事件发生时间", in = ParameterIn.QUERY),
            @Parameter(name = "monitorEnv", description = "监控环境", in = ParameterIn.QUERY),
            @Parameter(name = "monitorGroup", description = "监控分组", in = ParameterIn.QUERY)
    })
    @GetMapping("/get-monitor-docker-event-list")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.QUERY, operDesc = "获取docker事件列表")
    public LayUiAdminResultVo getMonitorDockerEventList(@RequestParam(value = "current") Long current,
                                                        @RequestParam(value = "size") Long size,
                                                        @RequestParam(value = "eventId", required = false) String eventId,
                                                        @RequestParam(value = "serverIp", required = false) String serverIp,
                                                        @RequestParam(value = "eventStatus", required = false) String eventStatus,
                                                        @RequestParam(value = "eventFrom", required = false) String eventFrom,
                                                        @RequestParam(value = "eventType", required = false) String eventType,
                                                        @RequestParam(value = "eventAction", required = false) String eventAction,
                                                        @RequestParam(value = "happenTime", required = false) String happenTime,
                                                        @RequestParam(value = "monitorEnv", required = false) String monitorEnv,
                                                        @RequestParam(value = "monitorGroup", required = false) String monitorGroup) {
        Page<MonitorDockerEventVo> page = this.monitorDockerEventService.getMonitorDockerEventList(current, size, eventId, serverIp,
                eventStatus, eventFrom, eventType, eventAction, happenTime, monitorEnv, monitorGroup);
        return LayUiAdminResultVo.ok(page);
    }

    /**
     * <p>
     * 删除docker事件
     * </p>
     *
     * @param ids 主键ID集合
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    @Operation(summary = "删除docker事件")
    @DeleteMapping("/delete-monitor-docker-event")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.DELETE, operDesc = "删除docker事件")
    public LayUiAdminResultVo deleteMonitorDockerEvent(@RequestBody List<Long> ids) {
        return this.monitorDockerEventService.deleteMonitorDockerEvent(ids);
    }

    /**
     * <p>
     * 访问docker事件详情页面
     * </p>
     *
     * @param serverIp 服务器IP
     * @param id       主键ID
     * @return {@link ModelAndView} docker事件详情页面
     * @author 皮锋
     * @custom.date 2022/8/21 16:33
     */
    @Operation(summary = "访问docker事件详情页面")
    @Parameters(value = {
            @Parameter(name = "serverIp", description = "服务器IP", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "id", description = "主键ID", required = true, in = ParameterIn.QUERY)
    })
    @GetMapping("/docker-event-detail")
    @OperateLog(operModule = UiModuleConstants.DOCKER, operType = OperateTypeConstants.PAGE, operDesc = "访问docker事件详情页面")
    public ModelAndView dockerEventDetail(String serverIp, Long id) {
        ModelAndView mv = new ModelAndView("docker/docker-event-detail");
        mv.addObject("serverIp", serverIp);
        mv.addObject("id", id);
        MonitorDockerEventVo monitorDockerEventVo = this.monitorDockerEventService.getMonitorDockerEventInfo(serverIp, id);
        mv.addObject("eventId", monitorDockerEventVo.getEventId());
        mv.addObject("monitorDockerEventVo", monitorDockerEventVo);
        return mv;
    }

}

