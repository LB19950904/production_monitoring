package com.gitee.pifeng.monitoring.agent.business.client.service;

import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;

/**
 * <p>
 * 异常服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2024/2/29 10:15
 */
@Deprecated
public interface IExceptionService {

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
    BaseResponsePackage dealExceptionPackage(ExceptionPackage exceptionPackage);

}
