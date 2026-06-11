package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerEvent;

/**
 * <p>
 * docker事件信息服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-03
 */
public interface IDockerEventService extends IService<MonitorDockerEvent> {

    /**
     * <p>
     * 把docker事件信息添加到数据库，并发送告警
     * </p>
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/7/3 14:45
     */
    void operateDockerEvent(DockerPackage dockerPackage);

    /**
     * <p>
     * 处理docker容器事件告警
     * </p>
     *
     * @param monitorDockerEvent docker事件信息
     * @author 皮锋
     * @custom.date 2022/7/10 14:30
     */
    void dealDockerContainerEventAlarm(MonitorDockerEvent monitorDockerEvent);

    /**
     * <p>
     * 处理docker镜像事件告警
     * </p>
     *
     * @param monitorDockerEvent docker事件信息
     * @author 皮锋
     * @custom.date 2022/7/10 14:37
     */
    void dealDockerImageEventAlarm(MonitorDockerEvent monitorDockerEvent);

}
