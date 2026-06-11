package com.gitee.pifeng.monitoring.agent.business.client.controller;

import com.gitee.pifeng.monitoring.agent.business.client.service.IDockerService;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CiphertextPackage;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * docker控制器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/26 21:40
 */
@Deprecated
@Tag(name = "信息包.docker信息包")
@RestController
@RequestMapping("/docker")
public class DockerController {

    /**
     * docker信息服务接口
     */
    @Autowired
    private IDockerService dockerService;

    /**
     * <p>
     * 监控代理程序接收监控客户端程序发的docker信息包，并返回结果
     * </p>
     *
     * @param dockerPackage docker信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2022年6月26日 下午21:49:54
     */
    @Operation(description = "接收和响应监控客户端程序发的docker信息包", summary = "接收docker信息包",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CiphertextPackage.class))),
            responses = @ApiResponse(content = {@Content(schema = @Schema(implementation = CiphertextPackage.class))}))
    @PostMapping("/accept-docker-package")
    public BaseResponsePackage acceptDockerPackage(@RequestBody DockerPackage dockerPackage) {
        return this.dockerService.dealDockerPackage(dockerPackage);
    }

}
