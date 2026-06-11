package com.gitee.pifeng.monitoring.server.business.server.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CiphertextPackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
import com.gitee.pifeng.monitoring.server.business.server.service.ICommandService;
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

import java.io.IOException;

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
     * 监控服务端程序接收监控UI端程序发的命令包，并返回结果
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2022年9月21日 下午10:00:54
     */
    @Operation(description = "监控服务端程序接收监控UI端程序发的命令包", summary = "接收命令包",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CiphertextPackage.class))),
            responses = @ApiResponse(content = {@Content(schema = @Schema(implementation = CiphertextPackage.class))}))
    @PostMapping("/accept-command-package")
    public BaseResponsePackage acceptCommandPackage(@RequestBody CommandPackage commandPackage) throws IOException {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        BaseResponsePackage baseResponsePackage = this.commandService.dealCommandPackage(commandPackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理命令信息包耗时：{}", betweenDay);
        }
        return baseResponsePackage;
    }

}
