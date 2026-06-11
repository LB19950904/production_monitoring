package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.ExceptionPackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorLogException;

/**
 * <p>
 * 异常日志服务层接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2021-06-09
 */
public interface ILogExceptionService extends IService<MonitorLogException> {

    /**
     * <p>
     * 处理异常包
     * </p>
     *
     * @param exceptionPackage 异常包
     * @return {@link Result} 返回结果
     * @author 皮锋
     * @custom.date 2024/2/28 11:47
     */
    Result dealExceptionPackage(ExceptionPackage exceptionPackage);

}
