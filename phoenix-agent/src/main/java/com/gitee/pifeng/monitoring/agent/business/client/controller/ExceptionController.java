package com.gitee.pifeng.monitoring.agent.business.client.controller;

import com.gitee.pifeng.monitoring.agent.business.client.service.IExceptionService;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CiphertextPackage;
import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;
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
 * 异常包控制器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/2/28 9:23
 */
@Deprecated
@RestController
@RequestMapping("/exception")
@Tag(name = "信息包.异常包")
public class ExceptionController {

    /**
     * 异常日志服务层接口
     */
    @Autowired
    private IExceptionService exceptionService;

    /**
     * <p>
     * 监控代理程序接收监控客户端程序发的异常包，并返回结果
     * </p>
     *
     * @param exceptionPackage 异常包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2024/2/28 9:26
     */
    @Operation(summary = "接收异常包", description = "接收和响应监控客户端程序发的异常包",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CiphertextPackage.class))),
            responses = @ApiResponse(content = {@Content(schema = @Schema(implementation = CiphertextPackage.class))}))
    @PostMapping("/accept-exception-package")
    public BaseResponsePackage acceptExceptionPackage(@RequestBody ExceptionPackage exceptionPackage) {
        return this.exceptionService.dealExceptionPackage(exceptionPackage);
    }

}
