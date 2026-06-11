package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorDockerStatsHistoryDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerStatsHistory;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerStatsHistoryService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.*;
import com.gitee.pifeng.monitoring.ui.core.CalculateDateTime;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * docker容器统计信息历史记录服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-09-09
 */
@Service
public class MonitorDockerStatsHistoryServiceImpl extends ServiceImpl<IMonitorDockerStatsHistoryDao, MonitorDockerStatsHistory> implements IMonitorDockerStatsHistoryService {

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
    @Override
    public List<DockerStatsDetailPageDockerCpuUtilizationRateChartVo> getDockerCpuUtilizationRateChartInfo(String serverIp, String containerName, String time) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("serverIp", serverIp);
        params.put("containerName", containerName);
        // 计算时间
        CalculateDateTime calculateDateTime = new CalculateDateTime(time).invoke();
        // 开始时间
        Date startTime = calculateDateTime.getStartTime();
        // 结束时间
        Date endTime = calculateDateTime.getEndTime();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return this.baseMapper.getDockerCpuUtilizationRateChartInfo(params);
    }

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
    @Override
    public List<DockerStatsDetailPageDockerMenUtilizationRateChartVo> getDockerMenUtilizationRateChartInfo(String serverIp, String containerName, String time) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("serverIp", serverIp);
        params.put("containerName", containerName);
        // 计算时间
        CalculateDateTime calculateDateTime = new CalculateDateTime(time).invoke();
        // 开始时间
        Date startTime = calculateDateTime.getStartTime();
        // 结束时间
        Date endTime = calculateDateTime.getEndTime();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return this.baseMapper.getDockerMenUtilizationRateChartInfo(params);
    }

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
    @Override
    public List<DockerStatsDetailPageDockerMenUsageLimitChartVo> getDockerMenUsageLimitChartInfo(String serverIp, String containerName, String time) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("serverIp", serverIp);
        params.put("containerName", containerName);
        // 计算时间
        CalculateDateTime calculateDateTime = new CalculateDateTime(time).invoke();
        // 开始时间
        Date startTime = calculateDateTime.getStartTime();
        // 结束时间
        Date endTime = calculateDateTime.getEndTime();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return this.baseMapper.getDockerMenUsageLimitChartInfo(params);
    }

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
    @Override
    public List<DockerStatsDetailPageDockerNetChartVo> getDockerNetChartInfo(String serverIp, String containerName, String time) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("serverIp", serverIp);
        params.put("containerName", containerName);
        // 计算时间
        CalculateDateTime calculateDateTime = new CalculateDateTime(time).invoke();
        // 开始时间
        Date startTime = calculateDateTime.getStartTime();
        // 结束时间
        Date endTime = calculateDateTime.getEndTime();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return this.baseMapper.getDockerNetChartInfo(params);
    }

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
    @Override
    public List<DockerStatsDetailPageDockerBlockChartVo> getDockerBlockChartInfo(String serverIp, String containerName, String time) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("serverIp", serverIp);
        params.put("containerName", containerName);
        // 计算时间
        CalculateDateTime calculateDateTime = new CalculateDateTime(time).invoke();
        // 开始时间
        Date startTime = calculateDateTime.getStartTime();
        // 结束时间
        Date endTime = calculateDateTime.getEndTime();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return this.baseMapper.getDockerBlockChartInfo(params);
    }

}
