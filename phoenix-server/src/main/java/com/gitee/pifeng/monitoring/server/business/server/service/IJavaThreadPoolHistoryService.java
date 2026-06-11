package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.dto.JavaThreadPoolPackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorJavaThreadPoolHistory;

/**
 * <p>
 * java线程池历史记录服务层接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
public interface IJavaThreadPoolHistoryService extends IService<MonitorJavaThreadPoolHistory> {

    /**
     * <p>
     * 把java线程池历史信息添加到数据库
     * </p>
     *
     * @param javaThreadPoolPackage Java线程池信息包
     * @author 皮锋
     * @custom.date 2025/1/22 15:58
     */
    void operateMonitorJavaThreadPoolHistory(JavaThreadPoolPackage javaThreadPoolPackage);

}
