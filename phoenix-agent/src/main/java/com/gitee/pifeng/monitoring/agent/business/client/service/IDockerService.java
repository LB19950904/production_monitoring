package com.gitee.pifeng.monitoring.agent.business.client.service;

import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;

/**
 * <p>
 * docker信息服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/26 21:42
 */
@Deprecated
public interface IDockerService {

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
    BaseResponsePackage dealDockerPackage(DockerPackage dockerPackage);

}
