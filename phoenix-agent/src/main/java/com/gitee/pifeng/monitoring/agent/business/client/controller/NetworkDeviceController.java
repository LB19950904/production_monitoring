package com.gitee.pifeng.monitoring.agent.business.client.controller;

import com.gitee.pifeng.monitoring.agent.business.client.service.IBaseRequestPackageService;
import com.gitee.pifeng.monitoring.agent.business.client.service.INetworkDeviceService;
import com.gitee.pifeng.monitoring.agent.constant.UrlConstants;
import com.gitee.pifeng.monitoring.common.dto.BaseRequestPackage;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CiphertextPackage;
import com.gitee.pifeng.monitoring.common.dto.NetworkDevicePackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 网络设备信息控制器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/11/19 20:19
 */
@Slf4j
@RestController
@RequestMapping("/network-device")
@Tag(name = "信息包.网络设备信息包")
public class NetworkDeviceController {

    /**
     * 网络设备信息服务接口
     */
    @Autowired
    private INetworkDeviceService networkDeviceService;

    /**
     * 基础请求包服务接口
     */
    @Autowired
    private IBaseRequestPackageService baseRequestPackageService;

    /**
     * <p>
     * 监控代理程序接收监控客户端程序发的网络设备信息包，并返回结果
     * </p>
     *
     * @param networkDevicePackage 网络设备信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2020年11月20日 下午20:11:23
     */
    @Deprecated
    @Operation(description = "接收和响应监控客户端程序发的网络设备信息包", summary = "接收网络设备信息包",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CiphertextPackage.class))),
            responses = @ApiResponse(content = {@Content(schema = @Schema(implementation = CiphertextPackage.class))}))
    @PostMapping("/accept-network-device-package")
    public BaseResponsePackage acceptNetworkDevicePackage(@RequestBody NetworkDevicePackage networkDevicePackage) {
        return this.networkDeviceService.dealNetworkDevicePackage(networkDevicePackage);
    }

    /**
     * <p>
     * 测试网络设备连通性
     * </p>
     *
     * @param baseRequestPackage 基础请求包
     * @return {@link BaseResponsePackage}
     * @throws NetException 自定义获取网络信息异常
     * @author 皮锋
     * @custom.date 2022/10/10 22:04
     */
    @Operation(summary = "测试网络设备连通性", description = "测试网络设备连通性",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CiphertextPackage.class))),
            responses = @ApiResponse(content = {@Content(schema = @Schema(implementation = CiphertextPackage.class))}))
    @PostMapping("/test-monitor-network-device")
    public BaseResponsePackage testMonitorNetworkDevice(@RequestBody BaseRequestPackage baseRequestPackage) throws NetException {
        return this.baseRequestPackageService.dealBaseRequestPackage(baseRequestPackage, UrlConstants.TEST_MONITOR_NETWORK_DEVICE_URL);
    }

}
