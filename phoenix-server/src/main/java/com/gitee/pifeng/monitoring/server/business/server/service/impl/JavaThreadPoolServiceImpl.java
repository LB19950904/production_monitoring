package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.JavaThreadPoolPackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.common.threadpool.MonitoredThreadPoolExecutor;
import com.gitee.pifeng.monitoring.plug.core.wsclient.WebsocketClientIdGenerator;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorJavaThreadPoolDao;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorJavaThreadPoolHistoryDao;
import com.gitee.pifeng.monitoring.server.business.server.dto.JavaThreadPoolMonitorDto;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorInstance;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorJavaThreadPool;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorJavaThreadPoolHistory;
import com.gitee.pifeng.monitoring.server.business.server.service.IInstanceService;
import com.gitee.pifeng.monitoring.server.business.server.service.IJavaThreadPoolHistoryService;
import com.gitee.pifeng.monitoring.server.business.server.service.IJavaThreadPoolService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.handler.impl.MonitoringFrameHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.assertj.core.util.Lists;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * java线程池信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
@Slf4j
@Service
public class JavaThreadPoolServiceImpl extends ServiceImpl<IMonitorJavaThreadPoolDao, MonitorJavaThreadPool> implements IJavaThreadPoolService {

    /**
     * 应用实例服务接口
     */
    @Autowired
    private IInstanceService instanceService;

    /**
     * java线程池历史记录服务层接口
     */
    @Autowired
    private IJavaThreadPoolHistoryService javaThreadPoolHistoryService;

    /**
     * java线程池历史记录数据访问对象
     */
    @Autowired
    private IMonitorJavaThreadPoolHistoryDao monitorJavaThreadPoolHistoryDao;

    /**
     * 应用实例服务监控线程池
     */
    @Autowired
    @Qualifier("instanceMonitorThreadPoolExecutor")
    private MonitoredThreadPoolExecutor instanceMonitorThreadPoolExecutor;

    /**
     * 监控 WebSocket 数据帧处理器
     */
    @Autowired
    private MonitoringFrameHandler monitoringFrameHandler;

    /**
     * 服务端包构造器
     */
    @Autowired
    private ServerPackageConstructor serverPackageConstructor;

    /**
     * <p>
     * 把java线程池信息添加或更新到数据库
     * </p>
     * 此处不加事务，因为操作的表太多，数据太多，不加事务能提高并发性能，而且此处对数据的一致性要求并不是很高。
     *
     * @param javaThreadPoolPackage java线程池信息包
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2026/3/13 23:20
     */
    //@Transactional(rollbackFor = Throwable.class)
    @Override
    public Result dealJavaThreadPoolPackage(JavaThreadPoolPackage javaThreadPoolPackage) {
        // 先判断有没有此应用实例
        LambdaQueryWrapper<MonitorInstance> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorInstance::getInstanceId, javaThreadPoolPackage.getInstanceId());
        int count = this.instanceService.count(lambdaQueryWrapper);
        if (count == 0) {
            return Result.builder().isSuccess(false).msg(ResultMsgConstants.FAILURE).build();
        }
        // 在主线程获取代理（此时 AOP 上下文有效）
        IJavaThreadPoolService selfProxy = (IJavaThreadPoolService) AopContext.currentProxy();
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                // 把java线程池运行时信息添加或更新到数据库
                CompletableFuture.runAsync(() -> selfProxy.operateMonitorJavaThreadPool(javaThreadPoolPackage), this.instanceMonitorThreadPoolExecutor),
                // 把java线程池类加载信息添加或更新到数据库
                CompletableFuture.runAsync(() -> this.javaThreadPoolHistoryService.operateMonitorJavaThreadPoolHistory(javaThreadPoolPackage), this.instanceMonitorThreadPoolExecutor)
        );
        try {
            // 设置超时时间
            allFutures.get(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("并行处理java线程池信息包被中断：{}", e.getMessage(), e);
            return Result.builder().isSuccess(false).msg("并行处理java线程池信息包被中断！").build();
        } catch (TimeoutException e) {
            log.error("并行处理java线程池信息包超时(30s)：{}", e.getMessage(), e);
            // 取消所有子任务（会触发线程中断）
            allFutures.cancel(true);
            return Result.builder().isSuccess(false).msg("并行处理java线程池信息包超时(30s)！").build();
        } catch (Exception e) {
            log.error("并行处理java线程池信息包出错：{}", e.getMessage(), e);
            return Result.builder().isSuccess(false).msg("并行处理java线程池信息包出错！").build();
        }
        // 返回结果
        return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
    }

    /**
     * <p>
     * 把java线程池信息添加或更新到数据库
     * </p>
     * 此处不加事务，不加事务能提高并发性能，并且对数据的一致性要求也没那么高
     *
     * @param javaThreadPoolPackage Java线程池信息包
     * @author 皮锋
     * @custom.date 2025/1/22 15:58
     */
    @Override
    @Retryable
    public void operateMonitorJavaThreadPool(JavaThreadPoolPackage javaThreadPoolPackage) {
        // 应用实例ID
        String instanceId = javaThreadPoolPackage.getInstanceId();
        // 当前时间
        Date currentTime = new Date();
        // 线程池信息
        JavaThreadPool threadPool = javaThreadPoolPackage.getJavaThreadPool();
        if (threadPool != null) {
            List<JavaThreadPool.ThreadPoolInfoDomain> threadPoolInfoDomains = threadPool.getThreadPoolInfoDomains();
            // 要添加的java线程池信息
            List<MonitorJavaThreadPool> saveMonitorJavaThreadPools = Lists.newArrayList();
            for (JavaThreadPool.ThreadPoolInfoDomain threadPoolInfoDomain : threadPoolInfoDomains) {
                // 线程池名字
                String name = threadPoolInfoDomain.getName();
                // 查询数据库中有没有当前java线程池信息
                LambdaQueryWrapper<MonitorJavaThreadPool> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(MonitorJavaThreadPool::getInstanceId, instanceId);
                lambdaQueryWrapper.eq(MonitorJavaThreadPool::getName, name);
                int selectCountDb = this.count(lambdaQueryWrapper);
                // 封装对象
                MonitorJavaThreadPool monitorJavaThreadPool = new MonitorJavaThreadPool();
                monitorJavaThreadPool.setInstanceId(instanceId);
                monitorJavaThreadPool.setName(name);
                monitorJavaThreadPool.setActiveCount(threadPoolInfoDomain.getActiveCount());
                monitorJavaThreadPool.setCompletedTaskCount(threadPoolInfoDomain.getCompletedTaskCount());
                monitorJavaThreadPool.setTaskCount(threadPoolInfoDomain.getTaskCount());
                monitorJavaThreadPool.setLargestPoolSize(threadPoolInfoDomain.getLargestPoolSize());
                monitorJavaThreadPool.setPoolSize(threadPoolInfoDomain.getPoolSize());
                monitorJavaThreadPool.setCorePoolSize(threadPoolInfoDomain.getCorePoolSize());
                monitorJavaThreadPool.setMaximumPoolSize(threadPoolInfoDomain.getMaximumPoolSize());
                monitorJavaThreadPool.setQueueSize(threadPoolInfoDomain.getQueueSize());
                monitorJavaThreadPool.setRejectedTaskCount(threadPoolInfoDomain.getRejectedTaskCount());
                monitorJavaThreadPool.setRejectedExecutionHandlerName(threadPoolInfoDomain.getRejectedExecutionHandlerName());
                monitorJavaThreadPool.setQueueRemainingCapacity(threadPoolInfoDomain.getQueueRemainingCapacity());
                monitorJavaThreadPool.setQueueType(threadPoolInfoDomain.getQueueType());
                monitorJavaThreadPool.setQueueCapacity(threadPoolInfoDomain.getQueueCapacity());
                monitorJavaThreadPool.setAllowCoreThreadTimeOut(threadPoolInfoDomain.getAllowCoreThreadTimeOut());
                monitorJavaThreadPool.setKeepAliveTime(threadPoolInfoDomain.getKeepAliveTime());
                monitorJavaThreadPool.setUtilizationRate(threadPoolInfoDomain.getUtilizationRate());
                // 新增java线程池信息
                if (selectCountDb == 0) {
                    monitorJavaThreadPool.setInsertTime(currentTime);
                    saveMonitorJavaThreadPools.add(monitorJavaThreadPool);
                }
                // 更新java线程池信息
                else {
                    monitorJavaThreadPool.setUpdateTime(currentTime);
                    LambdaUpdateWrapper<MonitorJavaThreadPool> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                    lambdaUpdateWrapper.eq(MonitorJavaThreadPool::getInstanceId, instanceId);
                    lambdaUpdateWrapper.eq(MonitorJavaThreadPool::getName, name);
                    this.update(monitorJavaThreadPool, lambdaUpdateWrapper);
                }
            }
            // 有要添加的java线程池信息
            if (CollectionUtils.isNotEmpty(saveMonitorJavaThreadPools)) {
                ((IJavaThreadPoolService) AopContext.currentProxy()).saveBatch(saveMonitorJavaThreadPools);
            }
        }
    }

    /**
     * <p>
     * 清理java线程池历史记录
     * </p>
     *
     * @param historyTime 时间点，清理这个时间点以前的数据
     * @return 清理记录数
     * @author 皮锋
     * @custom.date 2021/12/9 20:46
     */
    @Override
    public int clearHistoryData(Date historyTime) {
        LambdaQueryWrapper<MonitorJavaThreadPoolHistory> javaThreadPoolHistoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        javaThreadPoolHistoryLambdaQueryWrapper.le(MonitorJavaThreadPoolHistory::getInsertTime, historyTime);
        javaThreadPoolHistoryLambdaQueryWrapper.orderByAsc(MonitorJavaThreadPoolHistory::getInsertTime);
        javaThreadPoolHistoryLambdaQueryWrapper.last("limit 5000");
        return this.monitorJavaThreadPoolHistoryDao.delete(javaThreadPoolHistoryLambdaQueryWrapper);
    }

    /**
     * <p>
     * 获取java线程池监控传输层对象列表
     * </p>
     *
     * @param instanceId 应用实例ID
     * @return java线程池监控传输层对象列表
     * @author 皮锋
     * @custom.date 2025-2-18 9:47
     */
    @Override
    public List<JavaThreadPoolMonitorDto> getThreadPoolMonitorList(String instanceId) {
        return this.baseMapper.getThreadPoolMonitorList(instanceId);
    }

    /**
     * <p>
     * 配置Java线程池
     * </p>
     *
     * @param endpoint       应用端点
     * @param instanceId     应用实例ID
     * @param threadPoolInfo java线程池信息
     * @return 如果配置成功，返回true，否则返回false
     * @author 皮锋
     * @custom.date 2026/3/22 13:17
     */
    @Override
    public Boolean setInstanceJavaThreadPool(String endpoint, String instanceId, JavaThreadPool.ThreadPoolInfoDomain threadPoolInfo) {
        // 下发命令
        try {
            JavaThreadPool javaThreadPool = JavaThreadPool.builder().threadPoolInfoDomains(Lists.newArrayList(threadPoolInfo)).build();
            // 构建Java线程池信息包
            JavaThreadPoolPackage javaThreadPoolPackage = this.serverPackageConstructor.structureJavaThreadPoolPackage(javaThreadPool);
            WebSocketPackage requestPackage = new WebSocketPackage();
            requestPackage.setClassName(JavaThreadPoolPackage.class.getName());
            requestPackage.setPayload(javaThreadPoolPackage);
            // 生成 Websocket 客户端ID
            String websocketClientId = WebsocketClientIdGenerator.generate(endpoint, instanceId);
            this.monitoringFrameHandler.sendMsgToClientSync(websocketClientId, requestPackage, 10, TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
