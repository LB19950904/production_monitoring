package com.gitee.pifeng.monitoring.agent.business.server.service;

import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetInf;
import com.gitee.pifeng.monitoring.common.web.annotation.TargetMethod;

/**
 * <p>
 * 跟服务端相关的docker信息服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/26 21:57
 */
@TargetInf
public interface IDockerService {

    /**
     * <p>
     * 给服务端发docker信息包
     * </p>
     *
     * @param dockerPackage docker信息包
     * @return {@link BaseResponsePackage}
     * @throws Exception 所有异常
     * @author 皮锋
     * @custom.date 2020年3月7日 下午5:24:47
     */
    @TargetMethod
    BaseResponsePackage sendDockerPackage(DockerPackage dockerPackage) throws Exception;

}
