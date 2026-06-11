package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorNetworkDeviceIfService;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceIfVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 网络设备接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-19
 */
@RestController
@RequestMapping("/monitor-network-device-if")
@Tag(name = "网络设备接口")
public class MonitorNetworkDeviceIfController extends BaseController {

    /**
     * 网络设备接口服务类
     */
    @Autowired
    private IMonitorNetworkDeviceIfService monitorNetworkDeviceIfService;

    /**
     * <p>
     * 获取网络设备接口信息
     * </p>
     *
     * @param ip 网络设备IP地址
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2025/3/21 20:24
     */
    @Operation(summary = "获取网络设备接口信息")
    @ResponseBody
    @GetMapping("/get-network-device-if-info")
    @Parameters(value = {@Parameter(name = "ip", description = "网络设备IP地址", required = true, in = ParameterIn.QUERY)})
    public LayUiAdminResultVo getNetworkDeviceIfInfo(@RequestParam(name = "ip") String ip) {
        List<MonitorNetworkDeviceIfVo> monitorNetworkDeviceSysVos = this.monitorNetworkDeviceIfService.getNetworkDeviceIfInfo(ip);
        return LayUiAdminResultVo.ok(monitorNetworkDeviceSysVos);
    }

}
