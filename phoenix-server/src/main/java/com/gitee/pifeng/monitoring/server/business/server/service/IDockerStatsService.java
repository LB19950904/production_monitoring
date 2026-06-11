package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerStats;

/**
 * <p>
 * docker容器统计信息服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-24
 */
public interface IDockerStatsService extends IService<MonitorDockerStats> {

    /**
     * <p>
     * 把docker统计信息添加或更新到数据库
     * </p>
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/7/24 14:01
     */
    void operateDockerStats(DockerPackage dockerPackage);

}
