package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Command;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDocker;
import com.gitee.pifeng.monitoring.server.business.server.service.ICommandService;
import com.gitee.pifeng.monitoring.server.business.server.service.IDockerService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.handler.impl.MonitoringFrameHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 命令信息包服务接口实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/9/20 16:39
 */
@Service
public class CommandServiceImpl implements ICommandService {

    /**
     * 服务端包构造器
     */
    @Autowired
    private ServerPackageConstructor serverPackageConstructor;

    /**
     * docker信息服务层接口
     */
    @Autowired
    private IDockerService dockerService;

    /**
     * 监控 WebSocket 数据帧处理器
     */
    @Autowired
    private MonitoringFrameHandler monitoringFrameHandler;

    /**
     * <p>
     * 处理命令信息包
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2022/9/21 22:09
     */
    @Override
    public BaseResponsePackage dealCommandPackage(CommandPackage commandPackage) {
        // 返回值
        BaseResponsePackage baseResponsePackage = null;
        Command command = commandPackage.getCommand();
        MonitorTypeEnums monitorTypeEnum = command.getMonitorTypeEnum();
        String commandTarget = command.getCommandTarget();
        // docker
        if (MonitorTypeEnums.DOCKER.equals(monitorTypeEnum)) {
            MonitorDocker monitorDocker = this.dockerService.getById(Long.valueOf(commandTarget));
            String agentAddr = monitorDocker.getAgentCommClientId();
            // 下发命令
            try {
                WebSocketPackage requestPackage = new WebSocketPackage();
                requestPackage.setClassName(CommandPackage.class.getName());
                requestPackage.setPayload(commandPackage);
                this.monitoringFrameHandler.sendMsgToClientSync(agentAddr, requestPackage, 10, TimeUnit.SECONDS);
                baseResponsePackage = this.serverPackageConstructor.structureBaseResponsePackage(Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build());
            } catch (Exception e) {
                baseResponsePackage = this.serverPackageConstructor.structureBaseResponsePackage(Result.builder().isSuccess(false).msg(e.getMessage()).build());
            }
        }
        return baseResponsePackage;
    }

}
