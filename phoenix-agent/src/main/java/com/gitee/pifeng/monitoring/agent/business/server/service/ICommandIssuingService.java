package com.gitee.pifeng.monitoring.agent.business.server.service;

import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetInf;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetMethod;

import java.util.concurrent.ExecutionException;

/**
 * <p>
 * 命令下发服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/9/20 16:38
 */
@TargetInf
public interface ICommandIssuingService {

    /**
     * <p>
     * 处理命令信息包
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @throws ExecutionException   线程执行异常
     * @throws InterruptedException 线程中断异常
     * @author 皮锋
     * @custom.date 2022/9/21 22:09
     */
    @TargetMethod
    BaseResponsePackage dealCommandPackage(CommandPackage commandPackage) throws ExecutionException, InterruptedException;

    /**
     * <p>
     * 给服务端发命令信息包
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @throws Exception 所有异常
     * @author 皮锋
     * @custom.date 2023年7月23日 上午9::51:17
     */
    @TargetMethod
    BaseResponsePackage sendCommandPackage(CommandPackage commandPackage) throws Exception;

}
