package com.gitee.pifeng.monitoring.agent.business.server.service.impl;

import com.gitee.pifeng.monitoring.agent.business.server.service.IDockerService;
import com.gitee.pifeng.monitoring.agent.core.AgentPackageConstructor;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.plug.core.DataExchanger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 跟服务端相关的docker信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/26 22:01
 */
@Service
public class DockerServiceImpl implements IDockerService {

    /**
     * 包构造器接口
     */
    @Autowired
    private AgentPackageConstructor agentPackageConstructor;

    /**
     * <p>
     * 给服务端发docker信息包
     * </p>
     *
     * @param dockerPackage docker信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2020年3月7日 下午5:24:47
     */
    @Override
    public BaseResponsePackage sendDockerPackage(DockerPackage dockerPackage) {
        if (!DataExchanger.isReady()) {
            Result result = Result.builder().isSuccess(false).msg("数据交换器未准备好，请稍后再试！").build();
            return this.agentPackageConstructor.structureBaseResponsePackage(result);
        }
        // 添加链路信息
        dockerPackage.setChain(this.agentPackageConstructor.getChain(dockerPackage));
        // BaseResponsePackage baseResponsePackage = this.httpService.sendHttpPost(dockerPackage.toJsonString(), UrlConstants.DOCKER_URL);
        // 添加链路信息
        // baseResponsePackage.setChain(this.agentPackageConstructor.getChain(baseResponsePackage));
        // 发送请求
        WebSocketPackage requestPackage = new WebSocketPackage();
        requestPackage.setClassName(DockerPackage.class.getName());
        requestPackage.setPayload(dockerPackage);
        DataExchanger.sendMessage(requestPackage);
        Result result = Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
        return this.agentPackageConstructor.structureBaseResponsePackage(result);
    }

}
