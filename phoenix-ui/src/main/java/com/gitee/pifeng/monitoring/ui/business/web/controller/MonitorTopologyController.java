package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog;
import com.gitee.pifeng.monitoring.ui.business.web.service.*;
import com.gitee.pifeng.monitoring.ui.business.web.vo.*;
import com.gitee.pifeng.monitoring.ui.constant.OperateTypeConstants;
import com.gitee.pifeng.monitoring.ui.constant.UiModuleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 拓扑图
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/11/27 12:57
 */
@Controller
@RequestMapping("/monitor-topology")
@Tag(name = "拓扑图")
public class MonitorTopologyController extends BaseController {

    /**
     * 网络信息服务类
     */
    @Autowired
    private IMonitorNetService monitorNetService;

    /**
     * HTTP信息服务类
     */
    @Autowired
    private IMonitorHttpService monitorHttpService;

    /**
     * TCP信息服务类
     */
    @Autowired
    private IMonitorTcpService monitorTcpService;

    /**
     * 链路 服务类
     */
    @Autowired
    private IMonitorLinkService monitorLinkService;

    /**
     * 应用实例服务类
     */
    @Autowired
    private IMonitorInstanceService monitorInstanceService;

    /**
     * 服务器服务类
     */
    @Autowired
    private IMonitorServerService monitorServerService;

    /**
     * <p>
     * 访问服务器拓扑图页面
     * </p>
     *
     * @return {@link ModelAndView} 服务器拓扑图页面
     * @author 皮锋
     * @custom.date 2022/11/27 13:05
     */
    @Operation(summary = "访问服务器拓扑图页面")
    @GetMapping("/topology-server")
    @OperateLog(operModule = UiModuleConstants.TOPOLOGY, operType = OperateTypeConstants.PAGE, operDesc = "访问服务器拓扑图页面")
    public ModelAndView serverTopology() {
        ModelAndView mv = new ModelAndView("topology/topology-server");
        List<MonitorLinkVo> monitorLinkVos = this.monitorLinkService.getMonitorLinkInfo(MonitorTypeEnums.SERVER);
        mv.addObject("monitorLinkVos", monitorLinkVos);
        Map<String, MonitorServerVo> monitorServer2Map = this.monitorServerService.getMonitorServer2Map();
        mv.addObject("monitorServerVoMap", monitorServer2Map);
        return mv;
    }

    /**
     * <p>
     * 获取服务器拓扑图信息
     * </p>
     *
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/11/27 19:31
     */
    @Operation(summary = "获取服务器拓扑图信息")
    @PostMapping("/get-server-topology-info")
    @ResponseBody
    public LayUiAdminResultVo getServerTopologyInfo() {
        List<MonitorLinkVo> monitorLinkVos = this.monitorLinkService.getMonitorLinkInfo(MonitorTypeEnums.SERVER);
        return LayUiAdminResultVo.ok(monitorLinkVos);
    }

    /**
     * <p>
     * 访问网络拓扑图页面
     * </p>
     *
     * @return {@link ModelAndView} 网络拓扑图页面
     * @author 皮锋
     * @custom.date 2022/11/27 13:05
     */
    @Operation(summary = "访问网络拓扑图页面")
    @GetMapping("/topology-network")
    @OperateLog(operModule = UiModuleConstants.TOPOLOGY, operType = OperateTypeConstants.PAGE, operDesc = "访问网络拓扑图页面")
    public ModelAndView networkTopology() {
        ModelAndView mv = new ModelAndView("topology/topology-network");
        List<MonitorNetVo> monitorNetVos = this.monitorNetService.getMonitorNetInfo();
        mv.addObject("monitorNetVos", monitorNetVos);
        return mv;
    }

    /**
     * <p>
     * 获取网络拓扑图信息
     * </p>
     *
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/11/27 19:31
     */
    @Operation(summary = "获取网络拓扑图信息")
    @PostMapping("/get-network-topology-info")
    @ResponseBody
    public LayUiAdminResultVo getNetworkTopologyInfo() {
        List<MonitorNetVo> monitorNetVos = this.monitorNetService.getMonitorNetInfo();
        return LayUiAdminResultVo.ok(monitorNetVos);
    }

    /**
     * <p>
     * 访问HTTP接口拓扑图页面
     * </p>
     *
     * @return {@link ModelAndView} HTTP接口拓扑图页面
     * @author 皮锋
     * @custom.date 2022/11/27 13:05
     */
    @Operation(summary = "访问HTTP接口拓扑图页面")
    @GetMapping("/topology-http")
    @OperateLog(operModule = UiModuleConstants.TOPOLOGY, operType = OperateTypeConstants.PAGE, operDesc = "访问HTTP接口拓扑图页面")
    public ModelAndView httpTopology() {
        ModelAndView mv = new ModelAndView("topology/topology-http");
        List<MonitorHttpVo> monitorHttpVos = this.monitorHttpService.getMonitorHttpInfo();
        mv.addObject("monitorHttpVos", monitorHttpVos);
        return mv;
    }

    /**
     * <p>
     * 获取HTTP接口拓扑图信息
     * </p>
     *
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/11/27 19:31
     */
    @Operation(summary = "获取HTTP接口拓扑图信息")
    @PostMapping("/get-http-topology-info")
    @ResponseBody
    public LayUiAdminResultVo getHttpTopologyInfo() {
        List<MonitorHttpVo> monitorHttpVos = this.monitorHttpService.getMonitorHttpInfo();
        return LayUiAdminResultVo.ok(monitorHttpVos);
    }

    /**
     * <p>
     * 访问Tcp端口拓扑图页面
     * </p>
     *
     * @return {@link ModelAndView} Tcp端口拓扑图页面
     * @author 皮锋
     * @custom.date 2022/11/27 13:05
     */
    @Operation(summary = "访问Tcp端口拓扑图页面")
    @GetMapping("/topology-tcp")
    @OperateLog(operModule = UiModuleConstants.TOPOLOGY, operType = OperateTypeConstants.PAGE, operDesc = "访问Tcp端口拓扑图页面")
    public ModelAndView tcpTopology() {
        ModelAndView mv = new ModelAndView("topology/topology-tcp");
        List<MonitorTcpVo> monitorTcpVos = this.monitorTcpService.getMonitorTcpInfo();
        mv.addObject("monitorTcpVos", monitorTcpVos);
        return mv;
    }

    /**
     * <p>
     * 获取Tcp端口拓扑图信息
     * </p>
     *
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/11/27 19:31
     */
    @Operation(summary = "获取Tcp端口拓扑图信息")
    @PostMapping("/get-tcp-topology-info")
    @ResponseBody
    public LayUiAdminResultVo getTcpTopologyInfo() {
        List<MonitorTcpVo> monitorTcpVos = this.monitorTcpService.getMonitorTcpInfo();
        return LayUiAdminResultVo.ok(monitorTcpVos);
    }

    /**
     * <p>
     * 访问应用程序拓扑图页面
     * </p>
     *
     * @return {@link ModelAndView} 应用程序拓扑图页面
     * @author 皮锋
     * @custom.date 2022/12/19 13:05
     */
    @Operation(summary = "访问应用程序拓扑图页面")
    @GetMapping("/topology-instance")
    @OperateLog(operModule = UiModuleConstants.TOPOLOGY, operType = OperateTypeConstants.PAGE, operDesc = "访问应用程序拓扑图页面")
    public ModelAndView instanceTopology() {
        ModelAndView mv = new ModelAndView("topology/topology-instance");
        List<MonitorLinkVo> monitorLinkVos = this.monitorLinkService.getMonitorLinkInfo(MonitorTypeEnums.INSTANCE);
        mv.addObject("monitorLinkVos", monitorLinkVos);
        Map<String, MonitorInstanceVo> monitorInstance2Map = this.monitorInstanceService.getMonitorInstance2Map();
        mv.addObject("monitorInstanceVoMap", monitorInstance2Map);
        return mv;
    }

    /**
     * <p>
     * 访问应用程序拓扑图信息
     * </p>
     *
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2022/12/19 19:31
     */
    @Operation(summary = "访问应用程序拓扑图信息")
    @PostMapping("/get-instance-topology-info")
    @ResponseBody
    public LayUiAdminResultVo getInstanceTopologyInfo() {
        List<MonitorLinkVo> monitorLinkVos = this.monitorLinkService.getMonitorLinkInfo(MonitorTypeEnums.INSTANCE);
        return LayUiAdminResultVo.ok(monitorLinkVos);
    }

}
