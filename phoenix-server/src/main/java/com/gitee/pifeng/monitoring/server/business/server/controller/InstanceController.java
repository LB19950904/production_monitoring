package com.gitee.pifeng.monitoring.server.business.server.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.alibaba.fastjson.JSONObject;
import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.BaseRequestPackage;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CiphertextPackage;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.service.IJavaThreadPoolService;
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
 * 应用程序控制器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/22 13:03
 */
@Slf4j
@RestController
@RequestMapping("/instance")
@Tag(name = "应用程序")
public class InstanceController {

    /**
     * java线程池信息服务层接口
     */
    @Autowired
    private IJavaThreadPoolService javaThreadPoolService;

    /**
     * 服务端包构造器
     */
    @Autowired
    private ServerPackageConstructor serverPackageConstructor;

    /**
     * <p>
     * 配置Java线程池
     * </p>
     *
     * @param baseRequestPackage 基础请求包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2026/3/22 13:13
     */
    @Operation(summary = "配置Java线程池", description = "配置Java线程池",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(schema = @Schema(implementation = CiphertextPackage.class))),
            responses = @ApiResponse(content = {@Content(schema = @Schema(implementation = CiphertextPackage.class))}))
    @PostMapping("/set-instance-java-thread-pool")
    public BaseResponsePackage setInstanceJavaThreadPool(@RequestBody BaseRequestPackage baseRequestPackage) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        JSONObject extraMsg = baseRequestPackage.getExtraMsg();
        JavaThreadPool.ThreadPoolInfoDomain threadPoolInfo = extraMsg.getObject("threadPoolInfo", JavaThreadPool.ThreadPoolInfoDomain.class);
        String endpoint = extraMsg.getString("endpoint");
        String instanceId = extraMsg.getString("instanceId");
        Boolean success = this.javaThreadPoolService.setInstanceJavaThreadPool(endpoint, instanceId, threadPoolInfo);
        Result result = Result.builder().isSuccess(true).msg(String.valueOf(success)).build();
        BaseResponsePackage baseResponsePackage = this.serverPackageConstructor.structureBaseResponsePackage(result);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("配置Java线程池耗时：{}", betweenDay);
        }
        return baseResponsePackage;
    }

}