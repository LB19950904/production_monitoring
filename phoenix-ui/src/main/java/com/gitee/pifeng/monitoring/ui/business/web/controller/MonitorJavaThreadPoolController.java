package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorJavaThreadPoolService;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorJavaThreadPoolVo;
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

/**
 * <p>
 * java线程池信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/1/23 16:01
 */
@Tag(name = "应用程序.java线程池信息")
@Controller
@RequestMapping("/monitor-java-thread-pool")
public class MonitorJavaThreadPoolController extends BaseController {

    /**
     * java线程池信息服务类
     */
    @Autowired
    private IMonitorJavaThreadPoolService monitorJavaThreadPoolService;

    /**
     * <p>
     * 获取java线程池信息
     * </p>
     *
     * @param instanceId     应用实例ID
     * @param threadPoolName 线程池名字
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2025/1/27 07:55
     */
    @Operation(summary = "获取java线程池信息")
    @ResponseBody
    @GetMapping("/get-java-thread-pool-info")
    @Parameters(value = {
            @Parameter(name = "instanceId", description = "应用实例ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "threadPoolName", description = "线程池名字", required = true, in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getJavaThreadPoolInfo(@RequestParam(name = "instanceId") String instanceId,
                                                    @RequestParam(name = "threadPoolName") String threadPoolName) {
        MonitorJavaThreadPoolVo monitorJavaThreadPoolVo = this.monitorJavaThreadPoolService.getJavaThreadPoolInfo(instanceId, threadPoolName);
        return LayUiAdminResultVo.ok(monitorJavaThreadPoolVo);
    }

}