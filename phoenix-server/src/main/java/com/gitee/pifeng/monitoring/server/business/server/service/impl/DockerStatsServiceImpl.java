package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.domain.docker.StatsDomain;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDockerStatsDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerStats;
import com.gitee.pifeng.monitoring.server.business.server.service.IDockerStatsService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * docker容器统计信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-24
 */
@Service
public class DockerStatsServiceImpl extends ServiceImpl<IMonitorDockerStatsDao, MonitorDockerStats> implements IDockerStatsService {

    /**
     * <p>
     * 把docker统计信息添加或更新到数据库
     * </p>
     * 此处不加事务，不加事务能提高并发性能，并且对数据的一致性要求也没那么高
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/7/24 14:02
     */
    // @Transactional(rollbackFor = Throwable.class)
    @Override
    @Retryable
    public void operateDockerStats(DockerPackage dockerPackage) {
        // IP地址
        String ip = dockerPackage.getIp();
        Date date = new Date();
        StatsDomain statsDomain = dockerPackage.getDocker().getStatsDomain();
        if (statsDomain != null) {
            List<StatsDomain.StatsInfoDomain> statsInfoDomainList = statsDomain.getStatsInfoDomainList();
            List<MonitorDockerStats> monitorDockerStatsList = Lists.newArrayList();
            for (StatsDomain.StatsInfoDomain statsInfoDomain : statsInfoDomainList) {
                MonitorDockerStats monitorDockerStats = new MonitorDockerStats();
                monitorDockerStats.setServerIp(ip);
                monitorDockerStats.setContainerId(statsInfoDomain.getContainerId());
                monitorDockerStats.setContainerName(statsInfoDomain.getContainerName());
                monitorDockerStats.setCpuUtilizationRate(statsInfoDomain.getCpuUtilizationRate());
                monitorDockerStats.setMenUsageLimit(StringUtils.removeIgnoreCase(statsInfoDomain.getMenUsageLimit(), "i"));
                monitorDockerStats.setMenUtilizationRate(statsInfoDomain.getMenUtilizationRate());
                monitorDockerStats.setNetIo(StringUtils.removeIgnoreCase(statsInfoDomain.getNetIo(), "i"));
                monitorDockerStats.setBlockIo(StringUtils.removeIgnoreCase(statsInfoDomain.getBlockIo(), "i"));
                monitorDockerStats.setPids(statsInfoDomain.getPids());
                monitorDockerStats.setInsertTime(date);
                monitorDockerStats.setUpdateTime(date);
                monitorDockerStatsList.add(monitorDockerStats);
            }
            // 先删除，后添加
            LambdaUpdateWrapper<MonitorDockerStats> monitorDockerStatsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            monitorDockerStatsLambdaUpdateWrapper.eq(MonitorDockerStats::getServerIp, ip);
            ((IDockerStatsService) AopContext.currentProxy()).remove(monitorDockerStatsLambdaUpdateWrapper);
            ((IDockerStatsService) AopContext.currentProxy()).saveBatch(monitorDockerStatsList);
        }
    }

}
