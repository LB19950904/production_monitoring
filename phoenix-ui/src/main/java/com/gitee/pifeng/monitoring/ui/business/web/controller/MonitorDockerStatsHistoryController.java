package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerStatsHistoryService;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.ui.business.web.vo.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * <p>
 * docker容器统计信息历史记录
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-09-09
 */
@Controller
@Tag(name = "docker.docker统计信息历史记录")
@RequestMapping("/monitor-docker-stats-history")
public class MonitorDockerStatsHistoryController extends BaseController {

    /**
     * docker容器统计信息历史记录服务类
     */
    @Autowired
    private IMonitorDockerStatsHistoryService monitorDockerStatsHistoryService;

    /**
     * <p>
     * 获取docker CPU使用率图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    @Operation(summary = "获取docker CPU使用率图表数据")
    @ResponseBody
    @GetMapping("/get-docker-cpu-utilization-rate-chart-info")
    @Parameters(value = {
            @Parameter(name = "serverIp", description = "服务器IP地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerName", description = "容器名", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "time", description = "时间", in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getDockerCpuUtilizationRateChartInfo(@RequestParam(name = "serverIp") String serverIp,
                                                                   @RequestParam(name = "containerName") String containerName,
                                                                   @RequestParam(name = "time", required = false) String time) {
        List<DockerStatsDetailPageDockerCpuUtilizationRateChartVo> dockerStatsDetailPageDockerCpuUtilizationRateChartVos
                = this.monitorDockerStatsHistoryService.getDockerCpuUtilizationRateChartInfo(serverIp, containerName, time);
        return LayUiAdminResultVo.ok(dockerStatsDetailPageDockerCpuUtilizationRateChartVos);
    }

    /**
     * <p>
     * 获取docker内存使用率图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    @Operation(summary = "获取docker内存使用率图表数据")
    @ResponseBody
    @GetMapping("/get-docker-men-utilization-rate-chart-info")
    @Parameters(value = {
            @Parameter(name = "serverIp", description = "服务器IP地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerName", description = "容器名", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "time", description = "时间", in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getDockerMenUtilizationRateChartInfo(@RequestParam(name = "serverIp") String serverIp,
                                                                   @RequestParam(name = "containerName") String containerName,
                                                                   @RequestParam(name = "time", required = false) String time) {
        List<DockerStatsDetailPageDockerMenUtilizationRateChartVo> dockerStatsDetailPageDockerMenUtilizationRateChartVos
                = this.monitorDockerStatsHistoryService.getDockerMenUtilizationRateChartInfo(serverIp, containerName, time);
        return LayUiAdminResultVo.ok(dockerStatsDetailPageDockerMenUtilizationRateChartVos);
    }

    /**
     * <p>
     * 获取docker当前使用的内存和最大可以使用的内存图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    @Operation(summary = "获取docker当前使用的内存和最大可以使用的内存图表数据")
    @ResponseBody
    @GetMapping("/get-docker-men-usage-limit-chart-info")
    @Parameters(value = {
            @Parameter(name = "serverIp", description = "服务器IP地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerName", description = "容器名", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "time", description = "时间", in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getDockerMenUsageLimitChartInfo(@RequestParam(name = "serverIp") String serverIp,
                                                              @RequestParam(name = "containerName") String containerName,
                                                              @RequestParam(name = "time", required = false) String time) {
        List<DockerStatsDetailPageDockerMenUsageLimitChartVo> dockerStatsDetailPageDockerMenUsageLimitChartVos
                = this.monitorDockerStatsHistoryService.getDockerMenUsageLimitChartInfo(serverIp, containerName, time);
        return LayUiAdminResultVo.ok(dockerStatsDetailPageDockerMenUsageLimitChartVos);
    }

    /**
     * <p>
     * 获取docker网络图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    @Operation(summary = "获取docker网络图表数据")
    @ResponseBody
    @GetMapping("/get-docker-net-chart-info")
    @Parameters(value = {
            @Parameter(name = "serverIp", description = "服务器IP地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerName", description = "容器名", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "time", description = "时间", in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getDockerNetChartInfo(@RequestParam(name = "serverIp") String serverIp,
                                                    @RequestParam(name = "containerName") String containerName,
                                                    @RequestParam(name = "time", required = false) String time) {
        List<DockerStatsDetailPageDockerNetChartVo> dockerStatsDetailPageDockerNetChartVos
                = this.monitorDockerStatsHistoryService.getDockerNetChartInfo(serverIp, containerName, time);
        return LayUiAdminResultVo.ok(dockerStatsDetailPageDockerNetChartVos);
    }

    /**
     * <p>
     * 获取docker磁盘图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    @Operation(summary = "获取docker磁盘图表数据")
    @ResponseBody
    @GetMapping("/get-docker-block-chart-info")
    @Parameters(value = {
            @Parameter(name = "serverIp", description = "服务器IP地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "containerName", description = "容器名", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "time", description = "时间", in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getDockerBlockChartInfo(@RequestParam(name = "serverIp") String serverIp,
                                                      @RequestParam(name = "containerName") String containerName,
                                                      @RequestParam(name = "time", required = false) String time) {
        List<DockerStatsDetailPageDockerBlockChartVo> dockerStatsDetailPageDockerNetChartVos
                = this.monitorDockerStatsHistoryService.getDockerBlockChartInfo(serverIp, containerName, time);
        return LayUiAdminResultVo.ok(dockerStatsDetailPageDockerNetChartVos);
    }

}

