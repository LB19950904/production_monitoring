package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.JavaThreadPoolPackage;
import com.gitee.pifeng.monitoring.server.business.server.dto.JavaThreadPoolMonitorDto;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorJavaThreadPool;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * java线程池信息服务层接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
public interface IJavaThreadPoolService extends IService<MonitorJavaThreadPool> {

    /**
     * <p>
     * 把java线程池信息添加或更新到数据库
     * </p>
     *
     * @param javaThreadPoolPackage java线程池信息包
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2026/3/13 23:20
     */
    Result dealJavaThreadPoolPackage(JavaThreadPoolPackage javaThreadPoolPackage);

    /**
     * <p>
     * 把java线程池信息添加或更新到数据库
     * </p>
     *
     * @param javaThreadPoolPackage java线程池信息包
     * @author 皮锋
     * @custom.date 2025/1/22 15:58
     */
    void operateMonitorJavaThreadPool(JavaThreadPoolPackage javaThreadPoolPackage);

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
    int clearHistoryData(Date historyTime);

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
    List<JavaThreadPoolMonitorDto> getThreadPoolMonitorList(String instanceId);

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
    Boolean setInstanceJavaThreadPool(String endpoint, String instanceId, JavaThreadPool.ThreadPoolInfoDomain threadPoolInfo);

}
