package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.constant.ZeroOrOneConstants;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.domain.docker.InfoDomain;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.common.threadpool.MonitoredThreadPoolExecutor;
import com.gitee.pifeng.monitoring.plug.core.wsclient.WebsocketClientIdGenerator;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDockerDao;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDockerStatsHistoryDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDocker;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerStatsHistory;
import com.gitee.pifeng.monitoring.server.business.server.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * docker信息服务层接口实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/24 14:26
 */
@Slf4j
@Service
public class DockerServiceImpl extends ServiceImpl<IMonitorDockerDao, MonitorDocker> implements IDockerService {

    /**
     * docker容器信息服务接口
     */
    @Autowired
    private IDockerContainerService dockerContainerService;

    /**
     * docker镜像信息服务接口
     */
    @Autowired
    private IDockerImageService dockerImageService;

    /**
     * docker统计信息服务类
     */
    @Autowired
    private IDockerStatsService dockerStatsService;

    /**
     * docker容器统计信息历史记录服务类
     */
    @Autowired
    private IDockerStatsHistoryService dockerStatsHistoryService;

    /**
     * docker事件信息服务类
     */
    @Autowired
    private IDockerEventService dockerEventService;

    /**
     * docker容器统计信息历史记录表数据访问对象
     */
    @Autowired
    private IMonitorDockerStatsHistoryDao dockerStatsHistoryDao;

    /**
     * docker服务监控线程池
     */
    @Autowired
    @Qualifier("dockerMonitorThreadPoolExecutor")
    private MonitoredThreadPoolExecutor dockerMonitorThreadPoolExecutor;

    /**
     * <p>
     * 处理docker信息包
     * </p>
     * 此处不加事务，因为操作的表太多，数据太多，不加事务能提高并发性能，而且此处对数据的一致性要求并不是很高。
     *
     * @param dockerPackage docker信息包
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2022/6/24 14:27
     */
    //@Transactional(rollbackFor = Throwable.class)
    @Override
    public Result dealDockerPackage(DockerPackage dockerPackage) {
        // 在主线程获取代理（此时 AOP 上下文有效）
        IDockerService selfProxy = (IDockerService) AopContext.currentProxy();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                // 把docker系统信息添加或更新到数据库
                CompletableFuture.runAsync(() -> selfProxy.operateDocker(dockerPackage), this.dockerMonitorThreadPoolExecutor),
                // 把docker容器信息添加或更新到数据库
                CompletableFuture.runAsync(() -> this.dockerContainerService.operateDockerContainer(dockerPackage), this.dockerMonitorThreadPoolExecutor),
                // 把docker镜像信息添加或更新到数据库
                CompletableFuture.runAsync(() -> this.dockerImageService.operateDockerImage(dockerPackage), this.dockerMonitorThreadPoolExecutor),
                // 把docker统计信息添加或更新到数据库
                CompletableFuture.runAsync(() -> this.dockerStatsService.operateDockerStats(dockerPackage), this.dockerMonitorThreadPoolExecutor),
                // 把docker统计历史记录信息添加到数据库
                CompletableFuture.runAsync(() -> this.dockerStatsHistoryService.operateDockerStatsHistory(dockerPackage), this.dockerMonitorThreadPoolExecutor),
                // 把docker事件信息添加到数据库
                CompletableFuture.runAsync(() -> this.dockerEventService.operateDockerEvent(dockerPackage), this.dockerMonitorThreadPoolExecutor)
        );
        try {
            // 设置超时时间
            allFutures.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("并行处理docker信息包被中断：{}", e.getMessage(), e);
            return Result.builder().isSuccess(false).msg("并行处理docker信息包被中断！").build();
        } catch (TimeoutException e) {
            log.error("并行处理docker信息包超时(30s)：{}", e.getMessage(), e);
            // 取消所有子任务（会触发线程中断）
            allFutures.cancel(true);
            return Result.builder().isSuccess(false).msg("并行处理docker信息包超时(30s)！").build();
        } catch (Exception e) {
            log.error("并行处理docker信息包出错：{}", e.getMessage(), e);
            return Result.builder().isSuccess(false).msg("并行处理docker信息包出错！").build();
        }
        // 返回结果
        return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
    }

    /**
     * <p>
     * 把docker系统信息添加或更新到数据库
     * </p>
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/7/4 16:34
     */
    @Retryable
    @Transactional(rollbackFor = Throwable.class)
    @Override
    public void operateDocker(DockerPackage dockerPackage) {
        // IP地址
        String ip = dockerPackage.getIp();
        Date date = new Date();
        InfoDomain infodomain = dockerPackage.getDocker().getInfodomain();
        if (infodomain != null) {
            // 判断数据库中是否有此docker信息
            LambdaQueryWrapper<MonitorDocker> dockerLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dockerLambdaQueryWrapper.eq(MonitorDocker::getServerIp, ip);
            int count = this.count(dockerLambdaQueryWrapper);
            // 实例化数据库对象
            MonitorDocker monitorDocker = new MonitorDocker();
            monitorDocker.setServerIp(ip);
            monitorDocker.setArchitecture(infodomain.getArchitecture());
            monitorDocker.setContainers(infodomain.getContainers());
            monitorDocker.setContainersStopped(infodomain.getContainersStopped());
            monitorDocker.setContainersPaused(infodomain.getContainersPaused());
            monitorDocker.setContainersRunning(infodomain.getContainersRunning());
            monitorDocker.setIsDebug(BooleanUtils.isTrue(infodomain.getDebug()) ? ZeroOrOneConstants.ONE : ZeroOrOneConstants.ZERO);
            monitorDocker.setDockerRootDir(infodomain.getDockerRootDir());
            monitorDocker.setImages(infodomain.getImages());
            monitorDocker.setKernelVersion(infodomain.getKernelVersion());
            monitorDocker.setIsMemoryLimit(BooleanUtils.isTrue(infodomain.getMemoryLimit()) ? ZeroOrOneConstants.ONE : ZeroOrOneConstants.ZERO);
            monitorDocker.setMemTotal(infodomain.getMemTotal());
            monitorDocker.setServerVersion(infodomain.getServerVersion());
            monitorDocker.setCpuNum(infodomain.getCpus());
            monitorDocker.setEventsListenerNum(infodomain.getEventsListeners());
            monitorDocker.setRawValues(infodomain.getRawValues());
            monitorDocker.setConnFrequency((int) dockerPackage.getRate());
            // 没有，新增
            if (count <= 0) {
                monitorDocker.setInsertTime(date);
                monitorDocker.setOfflineCount(0);
                monitorDocker.setAgentCommClientId(WebsocketClientIdGenerator.generate(dockerPackage.getInstanceEndpoint(), dockerPackage.getInstanceId()));
                monitorDocker.setIsEnableMonitor(ZeroOrOneConstants.ONE);
                monitorDocker.setIsEnableAlarm(ZeroOrOneConstants.ONE);
                this.save(monitorDocker);
            }
            // 有，更新
            else {
                monitorDocker.setUpdateTime(date);
                LambdaUpdateWrapper<MonitorDocker> dockerLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                dockerLambdaUpdateWrapper.eq(MonitorDocker::getServerIp, ip);
                this.update(monitorDocker, dockerLambdaUpdateWrapper);
            }
        }
    }

    /**
     * <p>
     * 根据服务器IP获取docker服务信息
     * </p>
     *
     * @param serverIp 服务器IP
     * @return docker服务信息
     * @author 皮锋
     * @custom.date 2025/12/26 21:59
     */
    @Override
    public MonitorDocker getMonitorDockerByServerIp(String serverIp) {
        LambdaQueryWrapper<MonitorDocker> monitorDockerLambdaQueryWrapper = Wrappers.lambdaQuery();
        monitorDockerLambdaQueryWrapper.eq(MonitorDocker::getServerIp, serverIp);
        List<MonitorDocker> monitorDockers = this.list(monitorDockerLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(monitorDockers)) {
            return monitorDockers.get(0);
        }
        return null;
    }

    /**
     * <p>
     * 清理docker历史记录
     * </p>
     *
     * @param historyTime 时间点，清理这个时间点以前的数据
     * @return 清理记录数
     * @author 皮锋
     * @custom.date 2022/9/13 22:13
     */
    // @Transactional(rollbackFor = Throwable.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public int clearHistoryData(Date historyTime) {
        LambdaQueryWrapper<MonitorDockerStatsHistory> dockerStatsHistoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dockerStatsHistoryLambdaQueryWrapper.le(MonitorDockerStatsHistory::getInsertTime, historyTime);
        dockerStatsHistoryLambdaQueryWrapper.orderByAsc(MonitorDockerStatsHistory::getInsertTime);
        dockerStatsHistoryLambdaQueryWrapper.last("limit 5000");
        return this.dockerStatsHistoryDao.delete(dockerStatsHistoryLambdaQueryWrapper);
    }

}
