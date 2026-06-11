package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerStatsHistory;

/**
 * <p>
 * docker容器统计信息历史记录服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-09-09
 */
public interface IDockerStatsHistoryService extends IService<MonitorDockerStatsHistory> {

    /**
     * <p>
     * 把docker统计历史记录信息添加到数据库
     * </p>
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/9/13 21:22
     */
    void operateDockerStatsHistory(DockerPackage dockerPackage);

}
