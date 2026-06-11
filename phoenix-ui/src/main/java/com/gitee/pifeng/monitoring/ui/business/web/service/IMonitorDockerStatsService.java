package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerStats;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerStatsVo;

import java.util.List;

/**
 * <p>
 * docker容器统计信息服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-24
 */
public interface IMonitorDockerStatsService extends IService<MonitorDockerStats> {

    /**
     * <p>
     * 获取docker资源统计列表
     * </p>
     *
     * @param current       当前页
     * @param size          每页显示条数
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @param monitorEnv    监控环境
     * @param monitorGroup  监控分组
     * @return docker资源统计列表
     * @author 皮锋
     * @custom.date 2022/8/21 20:47
     */
    Page<MonitorDockerStatsVo> getMonitorDockerStatsList(Long current, Long size, String serverIp, String containerName, String monitorEnv, String monitorGroup);

    /**
     * <p>
     * 删除docker资源统计
     * </p>
     *
     * @param monitorDockerStatsVos docker资源统计信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    LayUiAdminResultVo deleteMonitorDockerStats(List<MonitorDockerStatsVo> monitorDockerStatsVos);

    /**
     * <p>
     * 获取docker资源统计信息
     * </p>
     *
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @return docker资源统计信息表现层对象
     * @author 皮锋
     * @custom.date 2022/8/21 16:42
     */
    MonitorDockerStatsVo getMonitorDockerStatsInfo(String serverIp, String containerName);

}
