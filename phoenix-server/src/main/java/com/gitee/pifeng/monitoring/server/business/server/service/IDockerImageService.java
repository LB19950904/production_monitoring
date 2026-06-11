package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerImage;

/**
 * <p>
 * docker镜像信息服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-26
 */
public interface IDockerImageService extends IService<MonitorDockerImage> {

    /**
     * <p>
     * 把docker镜像信息添加或更新到数据库
     * </p>
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/6/26 15:56
     */
    void operateDockerImage(DockerPackage dockerPackage);

}
