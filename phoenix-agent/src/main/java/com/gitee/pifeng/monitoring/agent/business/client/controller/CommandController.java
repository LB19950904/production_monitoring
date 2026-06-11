package com.gitee.pifeng.monitoring.agent.business.client.controller;

import com.gitee.pifeng.monitoring.agent.business.client.service.ICommandService;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CiphertextPackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
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
 * 命令下发控制器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/9/20 16:27
 */
@Slf4j
@Tag(name = "信息包.命令信息包")
@RestController
@RequestMapping("/command")
public class CommandController {

    /**
     * 命令下发服务接口
     */
    @Autowired
    private ICommandService commandService;

    /**
     * <p>
     * 监控代理端程序接收监控客户端程序发的命令包，并返回结果
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2022年9月21日 下午10:00:54
     */
    @Operation(description = "接收和响应监控客户端程序发的命令信息包", summary = "接收命令包",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CiphertextPackage.class))),
            responses = @ApiResponse(content = {@Content(schema = @Schema(implementation = CiphertextPackage.class))}))
    @PostMapping("/accept-command-package")
    public BaseResponsePackage acceptCommandPackage(@RequestBody CommandPackage commandPackage) {
        return this.commandService.dealCommandPackage(commandPackage);
    }

}
