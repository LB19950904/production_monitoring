package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerStatsHistory;
import com.gitee.pifeng.monitoring.ui.business.web.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * docker容器统计信息历史记录表数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-09-09
 */
public interface IMonitorDockerStatsHistoryDao extends BaseMapper<MonitorDockerStatsHistory> {

    /**
     * <p>
     * 获取docker CPU使用率图表数据
     * </p>
     *
     * @param params 请求参数
     * @return docker资源统计详情页面CPU使用率图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerCpuUtilizationRateChartVo> getDockerCpuUtilizationRateChartInfo(@Param("params") Map<String, Object> params);

    /**
     * <p>
     * 获取docker内存使用率图表数据
     * </p>
     *
     * @param params 请求参数
     * @return docker资源统计详情页面内存使用率图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerMenUtilizationRateChartVo> getDockerMenUtilizationRateChartInfo(@Param("params") Map<String, Object> params);

    /**
     * <p>
     * 获取docker当前使用的内存和最大可以使用的内存图表数据
     * </p>
     *
     * @param params 请求参数
     * @return 获取docker当前使用的内存和最大可以使用的内存图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerMenUsageLimitChartVo> getDockerMenUsageLimitChartInfo(@Param("params") Map<String, Object> params);

    /**
     * <p>
     * 获取docker网络图表数据
     * </p>
     *
     * @param params 请求参数
     * @return docker资源统计详情页面网络图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerNetChartVo> getDockerNetChartInfo(@Param("params") Map<String, Object> params);

    /**
     * <p>
     * 获取docker磁盘图表数据
     * </p>
     *
     * @param params 请求参数
     * @return docker资源统计详情页面磁盘图表信息表现层对象
     * @author 皮锋
     * @custom.date 2022/9/16 22:20
     */
    List<DockerStatsDetailPageDockerBlockChartVo> getDockerBlockChartInfo(@Param("params") Map<String, Object> params);

}
