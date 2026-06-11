package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorJavaThreadPoolHistory;
import com.gitee.pifeng.monitoring.ui.business.web.vo.InstanceDetailPageJavaThreadPoolChartVo;

import java.util.List;

/**
 * <p>
 * java线程池历史记录服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
public interface IMonitorJavaThreadPoolHistoryService extends IService<MonitorJavaThreadPoolHistory> {

    /**
     * <p>
     * 获取应用实例详情页面java线程池图表信息
     * </p>
     *
     * @param instanceId     应用实例ID
     * @param threadPoolName 线程池名字
     * @param time           时间
     * @return 应用实例详情页面java线程池图表信息表现层对象
     * @author 皮锋
     * @custom.date 2025/1/23 16:10
     */
    List<InstanceDetailPageJavaThreadPoolChartVo> getInstanceDetailPageJavaThreadPoolChartInfo(String instanceId, String threadPoolName, String time);

}
