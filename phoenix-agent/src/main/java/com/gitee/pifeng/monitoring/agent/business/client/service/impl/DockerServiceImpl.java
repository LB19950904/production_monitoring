package com.gitee.pifeng.monitoring.agent.business.client.service.impl;

import com.gitee.pifeng.monitoring.agent.business.client.service.IDockerService;
import com.gitee.pifeng.monitoring.agent.core.MethodExecuteHandler;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import org.springframework.stereotype.Service;

/**
 * <p>
 * docker信息服务实现
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/26 21:45
 */
@Deprecated
@Service
public class DockerServiceImpl implements IDockerService {

    /**
     * <p>
     * 处理docker信息包
     * </p>
     *
     * @param dockerPackage docker信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2022年6月26日 下午21:44:29
     */
    @Override
    public BaseResponsePackage dealDockerPackage(DockerPackage dockerPackage) {
        // 把docker信息包转发到服务端
        return MethodExecuteHandler.sendDockerPackage2Server(dockerPackage);
    }

}
