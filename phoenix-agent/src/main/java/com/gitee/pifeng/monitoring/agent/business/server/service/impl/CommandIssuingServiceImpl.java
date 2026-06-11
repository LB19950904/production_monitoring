package com.gitee.pifeng.monitoring.agent.business.server.service.impl;

import com.gitee.pifeng.monitoring.agent.business.agent.core.docker.DockerCentralController;
import com.gitee.pifeng.monitoring.agent.business.server.service.ICommandIssuingService;
import com.gitee.pifeng.monitoring.agent.business.server.service.IHttpService;
import com.gitee.pifeng.monitoring.agent.constant.UrlConstants;
import com.gitee.pifeng.monitoring.agent.core.AgentPackageConstructor;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Command;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 命令下发服务接口实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/9/20 16:39
 */
@Service
public class CommandIssuingServiceImpl implements ICommandIssuingService {

    /**
     * 包构造器接口
     */
    @Autowired
    private AgentPackageConstructor agentPackageConstructor;

    /**
     * 跟服务端相关的HTTP服务接口
     */
    @Autowired
    private IHttpService httpService;

    /**
     * <p>
     * 处理命令信息包
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @throws ExecutionException   线程执行异常
     * @throws InterruptedException 线程中断异常
     * @author 皮锋
     * @custom.date 2022/9/21 22:09
     */
    @Override
    public BaseResponsePackage dealCommandPackage(CommandPackage commandPackage) throws ExecutionException, InterruptedException {
        Command command = commandPackage.getCommand();
        MonitorTypeEnums monitorTypeEnum = Objects.requireNonNull(command.getMonitorTypeEnum());
        String commandType = Objects.requireNonNull(command.getCommandType());
        String commandAction = Objects.requireNonNull(command.getCommandAction());
        String commandValue = Objects.requireNonNull(command.getCommandValue());
        Result result = Result.builder().build();
        // docker
        if (MonitorTypeEnums.DOCKER.equals(monitorTypeEnum)) {
            result = DockerCentralController.getInstance().executeDockerCommand(commandType, commandAction, commandValue);
        }
        return this.agentPackageConstructor.structureBaseResponsePackage(result);
    }

    /**
     * <p>
     * 给服务端发命令信息包
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @throws Exception 所有异常
     * @author 皮锋
     * @custom.date 2023年7月23日 上午9::51:17
     */
    @Override
    public BaseResponsePackage sendCommandPackage(CommandPackage commandPackage) throws Exception {
        // 添加链路信息
        commandPackage.setChain(this.agentPackageConstructor.getChain(commandPackage));
        BaseResponsePackage baseResponsePackage = this.httpService.sendHttpPost(commandPackage.toJsonString(), UrlConstants.COMMAND_URL);
        // 添加链路信息
        baseResponsePackage.setChain(this.agentPackageConstructor.getChain(baseResponsePackage));
        return baseResponsePackage;
    }

}
