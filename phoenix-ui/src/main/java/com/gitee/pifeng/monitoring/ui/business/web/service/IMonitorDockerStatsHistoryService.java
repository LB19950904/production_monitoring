package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerStatsHistory;
import com.gitee.pifeng.monitoring.ui.business.web.vo.*;

import java.util.List;

/**
 * <p>
 * docker容器统计信息历史记录服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-09-09
 */
public interface IMonitorDockerStatsHistoryService extends IService<MonitorDockerStatsHistory> {

    /**
     * <p>
     * 获取docker CPU使用率图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return docker资源统计详情页面CPU使用率图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerCpuUtilizationRateChartVo> getDockerCpuUtilizationRateChartInfo(String serverIp, String containerName, String time);

    /**
     * <p>
     * 获取docker内存使用率图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return docker资源统计详情页面内存使用率图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerMenUtilizationRateChartVo> getDockerMenUtilizationRateChartInfo(String serverIp, String containerName, String time);

    /**
     * <p>
     * 获取docker当前使用的内存和最大可以使用的内存图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return 获取docker当前使用的内存和最大可以使用的内存图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerMenUsageLimitChartVo> getDockerMenUsageLimitChartInfo(String serverIp, String containerName, String time);

    /**
     * <p>
     * 获取docker网络图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return docker资源统计详情页面网络图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerNetChartVo> getDockerNetChartInfo(String serverIp, String containerName, String time);

    /**
     * <p>
     * 获取docker磁盘图表数据
     * </p>
     *
     * @param serverIp      服务器IP地址
     * @param containerName 容器名
     * @param time          时间
     * @return docker资源统计详情页面磁盘图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerBlockChartVo> getDockerBlockChartInfo(String serverIp, String containerName, String time);
}
