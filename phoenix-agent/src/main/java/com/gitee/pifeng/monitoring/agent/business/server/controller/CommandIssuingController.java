package com.gitee.pifeng.monitoring.agent.business.server.controller;

import com.gitee.pifeng.monitoring.agent.business.server.service.ICommandIssuingService;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CiphertextPackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
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

import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 命令下发控制器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/9/20 16:27
 */
@Deprecated
@Tag(name = "命令下发")
@RestController
@RequestMapping("/command-issuing")
public class CommandIssuingController {

    /**
     * 命令下发服务接口
     */
    @Autowired
    private ICommandIssuingService commandIssuingService;

    /**
     * <p>
     * 监控代理程序接收监控服务端程序发的命令包，并返回结果
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @throws ExecutionException   线程执行异常
     * @throws InterruptedException 线程中断异常
     * @author 皮锋
     * @custom.date 2022年9月21日 下午10:00:54
     */
    @Operation(description = "监控代理程序接收监控服务端程序发的命令包", summary = "接收命令包",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CiphertextPackage.class))),
            responses = @ApiResponse(content = {@Content(schema = @Schema(implementation = CiphertextPackage.class))}))
    @PostMapping("/accept-command-package")
    public BaseResponsePackage acceptCommandPackage(@RequestBody CommandPackage commandPackage) throws ExecutionException, InterruptedException {
        return this.commandIssuingService.dealCommandPackage(commandPackage);
    }

}
