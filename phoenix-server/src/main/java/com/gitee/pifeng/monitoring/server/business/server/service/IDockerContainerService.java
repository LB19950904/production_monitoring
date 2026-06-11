package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerContainer;

/**
 * <p>
 * docker容器信息服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/25 20:46
 */
public interface IDockerContainerService extends IService<MonitorDockerContainer> {

    /**
     * <p>
     * 把docker容器信息添加或更新到数据库
     * </p>
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/6/25 20:53
     */
    void operateDockerContainer(DockerPackage dockerPackage);

}
