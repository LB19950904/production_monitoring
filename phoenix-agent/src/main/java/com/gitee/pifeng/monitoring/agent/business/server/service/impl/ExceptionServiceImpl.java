package com.gitee.pifeng.monitoring.agent.business.server.service.impl;

import com.gitee.pifeng.monitoring.agent.business.server.service.IExceptionService;
import com.gitee.pifeng.monitoring.agent.business.server.service.IHttpService;
import com.gitee.pifeng.monitoring.agent.constant.UrlConstants;
import com.gitee.pifeng.monitoring.agent.core.AgentPackageConstructor;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 跟服务端相关的异常服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/2/29 10:33
 */
@Deprecated
@Service
public class ExceptionServiceImpl implements IExceptionService {

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
     * 给服务端发异常包
     * </p>
     *
     * @param exceptionPackage 异常信息包
     * @return {@link BaseResponsePackage}
     * @throws Exception 所有异常
     * @author 皮锋
     * @custom.date 2024/2/29 10:32
     */
    @Override
    public BaseResponsePackage sendExceptionPackage(ExceptionPackage exceptionPackage) throws Exception {
        // 添加链路信息
        exceptionPackage.setChain(this.agentPackageConstructor.getChain(exceptionPackage));
        BaseResponsePackage baseResponsePackage = this.httpService.sendHttpPost(exceptionPackage.toJsonString(), UrlConstants.EXCEPTION_URL);
        // 添加链路信息
        baseResponsePackage.setChain(this.agentPackageConstructor.getChain(baseResponsePackage));
        return baseResponsePackage;
    }

}
