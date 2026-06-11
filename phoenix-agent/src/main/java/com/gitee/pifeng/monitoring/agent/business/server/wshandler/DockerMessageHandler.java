package com.gitee.pifeng.monitoring.agent.business.server.wshandler;

import com.gitee.pifeng.monitoring.agent.business.server.service.ICommandIssuingService;
import com.gitee.pifeng.monitoring.agent.core.MethodExecuteHandler;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Command;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.common.web.core.Invoker;
import com.gitee.pifeng.monitoring.common.web.core.InvokerHolder;
import com.gitee.pifeng.monitoring.plug.core.wsclient.inf.IWebsocketMessageHandler;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Docker WebSocket 消息处理器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/2/27 17:04
 */
public class DockerMessageHandler implements IWebsocketMessageHandler {

    /**
     * <p>
     * 处理 WebSocket 服务端返回的结构化响应包
     * </p>
     *
     * @param responsePackage WebSocket 响应数据包
     * @author 皮锋
     * @custom.date 2026/2/24 14:58
     */
    @Override
    public void handleMessage(WebSocketPackage responsePackage) {
        String className = responsePackage.getClassName();
        if (!StringUtils.equals(CommandPackage.class.getName(), className)) {
            return;
        }
        Object payload = responsePackage.getPayload();
        CommandPackage commandPackage = (CommandPackage) payload;
        Command command = commandPackage.getCommand();
        MonitorTypeEnums monitorTypeEnum = command.getMonitorTypeEnum();
        if (MonitorTypeEnums.DOCKER != monitorTypeEnum) {
            return;
        }
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(ICommandIssuingService.class, "dealCommandPackage");
        MethodExecuteHandler.execute(invoker, commandPackage);
    }

}