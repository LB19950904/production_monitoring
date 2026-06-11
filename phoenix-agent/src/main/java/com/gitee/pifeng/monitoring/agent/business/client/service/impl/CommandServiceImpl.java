package com.gitee.pifeng.monitoring.agent.business.client.service.impl;

import com.gitee.pifeng.monitoring.agent.business.client.service.ICommandService;
import com.gitee.pifeng.monitoring.agent.core.MethodExecuteHandler;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 命令服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023年7月23日 上午9:42:30
 */
@Service
public class CommandServiceImpl implements ICommandService {

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
        // 把命令信息包转发到服务端
        return MethodExecuteHandler.sendCommandPackage2Server(commandPackage);
    }

}
