package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import com.gitee.pifeng.monitoring.common.dto.JavaThreadPoolPackage;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorJavaThreadPoolHistoryDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorJavaThreadPoolHistory;
import com.gitee.pifeng.monitoring.server.business.server.service.IJavaThreadPoolHistoryService;
import org.assertj.core.util.Lists;
import org.springframework.aop.framework.AopContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * java线程池历史记录服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
@Service
public class JavaThreadPoolHistoryServiceImpl extends ServiceImpl<IMonitorJavaThreadPoolHistoryDao, MonitorJavaThreadPoolHistory> implements IJavaThreadPoolHistoryService {

    /**
     * <p>
     * 把java线程池历史信息添加到数据库
     * </p>
     * 此处不加事务，不加事务能提高并发性能，并且对数据的一致性要求也没那么高
     *
     * @param javaThreadPoolPackage Java线程池信息包
     * @author 皮锋
     * @custom.date 2025/1/22 15:58
     */
    @Retryable
    @Override
    public void operateMonitorJavaThreadPoolHistory(JavaThreadPoolPackage javaThreadPoolPackage) {
        // 应用实例ID
        String instanceId = javaThreadPoolPackage.getInstanceId();
        // 当前时间
        Date currentTime = new Date();
        // 线程池信息
        JavaThreadPool threadPool = javaThreadPoolPackage.getJavaThreadPool();
        if (threadPool != null) {
            List<JavaThreadPool.ThreadPoolInfoDomain> threadPoolInfoDomains = threadPool.getThreadPoolInfoDomains();
            // 要添加的java线程池历史信息
            List<MonitorJavaThreadPoolHistory> saveMonitorJavaThreadPoolHistories = Lists.newArrayList();
            for (JavaThreadPool.ThreadPoolInfoDomain threadPoolInfoDomain : threadPoolInfoDomains) {
                // 封装对象
                MonitorJavaThreadPoolHistory monitorJavaThreadPoolHistory = new MonitorJavaThreadPoolHistory();
                monitorJavaThreadPoolHistory.setInstanceId(instanceId);
                monitorJavaThreadPoolHistory.setName(threadPoolInfoDomain.getName());
                monitorJavaThreadPoolHistory.setActiveCount(threadPoolInfoDomain.getActiveCount());
                monitorJavaThreadPoolHistory.setCompletedTaskCount(threadPoolInfoDomain.getCompletedTaskCount());
                monitorJavaThreadPoolHistory.setTaskCount(threadPoolInfoDomain.getTaskCount());
                monitorJavaThreadPoolHistory.setLargestPoolSize(threadPoolInfoDomain.getLargestPoolSize());
                monitorJavaThreadPoolHistory.setPoolSize(threadPoolInfoDomain.getPoolSize());
                monitorJavaThreadPoolHistory.setCorePoolSize(threadPoolInfoDomain.getCorePoolSize());
                monitorJavaThreadPoolHistory.setMaximumPoolSize(threadPoolInfoDomain.getMaximumPoolSize());
                monitorJavaThreadPoolHistory.setQueueSize(threadPoolInfoDomain.getQueueSize());
                monitorJavaThreadPoolHistory.setRejectedTaskCount(threadPoolInfoDomain.getRejectedTaskCount());
                monitorJavaThreadPoolHistory.setRejectedExecutionHandlerName(threadPoolInfoDomain.getRejectedExecutionHandlerName());
                monitorJavaThreadPoolHistory.setQueueRemainingCapacity(threadPoolInfoDomain.getQueueRemainingCapacity());
                monitorJavaThreadPoolHistory.setQueueType(threadPoolInfoDomain.getQueueType());
                monitorJavaThreadPoolHistory.setQueueCapacity(threadPoolInfoDomain.getQueueCapacity());
                monitorJavaThreadPoolHistory.setAllowCoreThreadTimeOut(threadPoolInfoDomain.getAllowCoreThreadTimeOut());
                monitorJavaThreadPoolHistory.setKeepAliveTime(threadPoolInfoDomain.getKeepAliveTime());
                monitorJavaThreadPoolHistory.setUtilizationRate(threadPoolInfoDomain.getUtilizationRate());
                monitorJavaThreadPoolHistory.setInsertTime(currentTime);
                monitorJavaThreadPoolHistory.setUpdateTime(currentTime);
                saveMonitorJavaThreadPoolHistories.add(monitorJavaThreadPoolHistory);
            }
            // 有要添加的java线程池历史信息
            ((IJavaThreadPoolHistoryService) AopContext.currentProxy()).saveBatch(saveMonitorJavaThreadPoolHistories);
        }
    }

}
