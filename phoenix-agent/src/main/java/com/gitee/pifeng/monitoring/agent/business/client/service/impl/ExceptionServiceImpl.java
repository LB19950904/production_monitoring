package com.gitee.pifeng.monitoring.agent.business.client.service.impl;

import com.gitee.pifeng.monitoring.agent.business.client.service.IExceptionService;
import com.gitee.pifeng.monitoring.agent.core.MethodExecuteHandler;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 异常服务实现
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/2/29 10:19
 */
@Deprecated
@Service
public class ExceptionServiceImpl implements IExceptionService {

    /**
     * <p>
     * 处理异常包
     * </p>
     *
     * @param exceptionPackage 异常信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2024/2/29 10:18
     */
    @Override
    public BaseResponsePackage dealExceptionPackage(ExceptionPackage exceptionPackage) {
        // 把异常包转发到服务端
        return MethodExecuteHandler.sendExceptionPackage2Server(exceptionPackage);
    }

}
