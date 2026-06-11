package com.gitee.pifeng.monitoring.agent.business.server.service;

import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetInf;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetMethod;

/**
 * <p>
 * 跟服务端相关的异常服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/2/29 10:29
 */
@Deprecated
@TargetInf
public interface IExceptionService {

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
    @TargetMethod
    BaseResponsePackage sendExceptionPackage(ExceptionPackage exceptionPackage) throws Exception;

}
