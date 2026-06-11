package com.gitee.pifeng.monitoring.server.business.server.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CiphertextPackage;
import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.service.ILogExceptionService;
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
 * 异常包控制器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/2/28 9:23
 */
@Deprecated
@Slf4j
@RestController
@RequestMapping("/exception")
@Tag(name = "信息包.异常包")
public class ExceptionController {

    /**
     * 异常日志服务层接口
     */
    @Autowired
    private ILogExceptionService logExceptionService;

    /**
     * 服务端包构造器
     */
    @Autowired
    private ServerPackageConstructor serverPackageConstructor;

    /**
     * <p>
     * 监控服务端程序接收监控代理端程序或者监控客户端程序发的异常包，并返回结果
     * </p>
     *
     * @param exceptionPackage 异常包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2024/2/28 9:26
     */
    @Operation(summary = "接收异常包", description = "接收和响应监控代理端程序或者监控客户端程序发的异常包",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CiphertextPackage.class))),
            responses = @ApiResponse(content = {@Content(schema = @Schema(implementation = CiphertextPackage.class))}))
    @PostMapping("/accept-exception-package")
    public BaseResponsePackage acceptExceptionPackage(@RequestBody ExceptionPackage exceptionPackage) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        // 返回值
        Result result = this.logExceptionService.dealExceptionPackage(exceptionPackage);
        BaseResponsePackage baseResponsePackage = this.serverPackageConstructor.structureBaseResponsePackage(result);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理异常包耗时：{}", betweenDay);
        }
        return baseResponsePackage;
    }

}
