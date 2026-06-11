package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorJavaThreadPoolHistoryService;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.ui.business.web.vo.InstanceDetailPageJavaThreadPoolChartVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
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
 * java线程池历史记录
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/1/23 16:01
 */
@Tag(name = "应用程序.java线程池历史记录")
@Controller
@RequestMapping("/monitor-java-thread-pool-history")
public class MonitorJavaThreadPoolHistoryController extends BaseController {

    /**
     * java线程池历史记录服务类
     */
    @Autowired
    private IMonitorJavaThreadPoolHistoryService monitorJavaThreadPoolHistoryService;

    /**
     * <p>
     * 获取应用实例详情页面java线程池图表信息
     * </p>
     *
     * @param instanceId     应用实例ID
     * @param threadPoolName 线程池名字
     * @param time           时间
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2025/1/23 16:07
     */
    @Operation(summary = "获取应用实例详情页面java线程池图表信息")
    @ResponseBody
    @GetMapping("/get-instance-detail-page-java-thread-pool-chart-info")
    @Parameters(value = {
            @Parameter(name = "instanceId", description = "应用实例ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "threadPoolName", description = "线程池名字", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "time", description = "时间", required = true, in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getInstanceDetailPageJavaThreadPoolChartInfo(@RequestParam(name = "instanceId") String instanceId,
                                                                           @RequestParam(name = "threadPoolName") String threadPoolName,
                                                                           @RequestParam(name = "time") String time) {
        List<InstanceDetailPageJavaThreadPoolChartVo> monitorJavaThreadPoolChartVos = this.monitorJavaThreadPoolHistoryService.getInstanceDetailPageJavaThreadPoolChartInfo(instanceId, threadPoolName, time);
        return LayUiAdminResultVo.ok(monitorJavaThreadPoolChartVos);
    }

}