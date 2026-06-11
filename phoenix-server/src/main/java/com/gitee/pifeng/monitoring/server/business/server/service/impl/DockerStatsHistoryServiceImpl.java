package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import cn.hutool.core.io.unit.DataSizeUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.domain.docker.StatsDomain;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.common.util.StrUtils;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDockerStatsHistoryDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerStatsHistory;
import com.gitee.pifeng.monitoring.server.business.server.service.IDockerStatsHistoryService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * docker容器统计信息历史记录服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-24
 */
@Service
public class DockerStatsHistoryServiceImpl extends ServiceImpl<IMonitorDockerStatsHistoryDao, MonitorDockerStatsHistory> implements IDockerStatsHistoryService {

    /**
     * <p>
     * 把docker统计历史记录信息添加到数据库
     * </p>
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/9/13 21:22
     */
    @Override
    @Retryable
    public void operateDockerStatsHistory(DockerPackage dockerPackage) {
        // IP地址
        String ip = dockerPackage.getIp();
        Date date = new Date();
        StatsDomain statsDomain = dockerPackage.getDocker().getStatsDomain();
        if (statsDomain != null) {
            List<StatsDomain.StatsInfoDomain> statsInfoDomainList = statsDomain.getStatsInfoDomainList();
            List<MonitorDockerStatsHistory> monitorDockerStatsHistoryList = Lists.newArrayList();
            for (StatsDomain.StatsInfoDomain statsInfoDomain : statsInfoDomainList) {
                String menUsageLimit = statsInfoDomain.getMenUsageLimit();
                String[] menUsageLimitArry = StringUtils.split(menUsageLimit, "/");
                String[] menUsageArry = StrUtils.splitAllEndLetter(StringUtils.trim(StringUtils.removeIgnoreCase(menUsageLimitArry[0], "i")));
                String menUsage = new BigDecimal(menUsageArry[0]).toPlainString() + menUsageArry[1];
                String[] menLimitArry = StrUtils.splitAllEndLetter(StringUtils.trim(StringUtils.removeIgnoreCase(menUsageLimitArry[1], "i")));
                String menLimit = new BigDecimal(menLimitArry[0]).toPlainString() + menLimitArry[1];
                String netIo = statsInfoDomain.getNetIo();
                String[] netIoArry = StringUtils.split(netIo, "/");
                String[] netInArry = StrUtils.splitAllEndLetter(StringUtils.trim(StringUtils.removeIgnoreCase(netIoArry[0], "i")));
                String netIn = new BigDecimal(netInArry[0]).toPlainString() + netInArry[1];
                String[] netOutArry = StrUtils.splitAllEndLetter(StringUtils.trim(StringUtils.removeIgnoreCase(netIoArry[1], "i")));
                String netOut = new BigDecimal(netOutArry[0]).toPlainString() + netOutArry[1];
                String blockIo = statsInfoDomain.getBlockIo();
                String[] blockIoArry = StringUtils.split(blockIo, "/");
                String[] blockInArry = StrUtils.splitAllEndLetter(StringUtils.trim(StringUtils.removeIgnoreCase(blockIoArry[0], "i")));
                String blockIn = new BigDecimal(blockInArry[0]).toPlainString() + blockInArry[1];
                String[] blockOutArry = StrUtils.splitAllEndLetter(StringUtils.trim(StringUtils.removeIgnoreCase(blockIoArry[1], "i")));
                String blockOut = new BigDecimal(blockOutArry[0]).toPlainString() + blockOutArry[1];
                String netIoSpeed = statsInfoDomain.getNetIoSpeed();
                String[] netIoSpeedArry = StringUtils.split(netIoSpeed, "/");
                Double netInSpeed = ArrayUtils.isEmpty(netIoSpeedArry) ? 0 : Double.parseDouble(StringUtils.trim(netIoSpeedArry[0]));
                Double netOutSpeed = ArrayUtils.isEmpty(netIoSpeedArry) ? 0 : Double.parseDouble(StringUtils.trim(netIoSpeedArry[1]));
                String blockIoSpeed = statsInfoDomain.getBlockIoSpeed();
                String[] blockIoSpeedArry = StringUtils.split(blockIoSpeed, "/");
                Double blockInSpeed = ArrayUtils.isEmpty(blockIoSpeedArry) ? 0 : Double.parseDouble(StringUtils.trim(blockIoSpeedArry[0]));
                Double blockOutSpeed = ArrayUtils.isEmpty(blockIoSpeedArry) ? 0 : Double.parseDouble(StringUtils.trim(blockIoSpeedArry[1]));
                MonitorDockerStatsHistory monitorDockerStatsHistory = new MonitorDockerStatsHistory();
                monitorDockerStatsHistory.setServerIp(ip);
                monitorDockerStatsHistory.setContainerId(statsInfoDomain.getContainerId());
                monitorDockerStatsHistory.setContainerName(statsInfoDomain.getContainerName());
                monitorDockerStatsHistory.setCpuUtilizationRate(Double.valueOf(StringUtils.remove(statsInfoDomain.getCpuUtilizationRate(), "%")));
                monitorDockerStatsHistory.setMenUsage(DataSizeUtil.parse(menUsage));
                monitorDockerStatsHistory.setMenLimit(DataSizeUtil.parse(menLimit));
                monitorDockerStatsHistory.setMenUtilizationRate(Double.valueOf(StringUtils.remove(statsInfoDomain.getMenUtilizationRate(), "%")));
                monitorDockerStatsHistory.setNetIn(DataSizeUtil.parse(netIn));
                monitorDockerStatsHistory.setNetOut(DataSizeUtil.parse(netOut));
                monitorDockerStatsHistory.setBlockIn(DataSizeUtil.parse(blockIn));
                monitorDockerStatsHistory.setBlockOut(DataSizeUtil.parse(blockOut));
                monitorDockerStatsHistory.setPids(statsInfoDomain.getPids());
                monitorDockerStatsHistory.setNetInSpeed(netInSpeed);
                monitorDockerStatsHistory.setNetOutSpeed(netOutSpeed);
                monitorDockerStatsHistory.setBlockInSpeed(blockInSpeed);
                monitorDockerStatsHistory.setBlockOutSpeed(blockOutSpeed);
                monitorDockerStatsHistory.setInsertTime(date);
                monitorDockerStatsHistory.setUpdateTime(date);
                monitorDockerStatsHistoryList.add(monitorDockerStatsHistory);
            }
            // 批量插入
            ((IDockerStatsHistoryService) AopContext.currentProxy()).saveBatch(monitorDockerStatsHistoryList);
        }
    }

}
