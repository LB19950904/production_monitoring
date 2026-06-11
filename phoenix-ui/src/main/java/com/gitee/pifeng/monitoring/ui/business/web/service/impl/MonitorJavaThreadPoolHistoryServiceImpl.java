package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorJavaThreadPoolHistoryDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorJavaThreadPoolHistory;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorJavaThreadPoolHistoryService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.InstanceDetailPageJavaThreadPoolChartVo;
import com.gitee.pifeng.monitoring.ui.core.CalculateDateTime;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * java线程池历史记录服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
@Service
public class MonitorJavaThreadPoolHistoryServiceImpl extends ServiceImpl<IMonitorJavaThreadPoolHistoryDao, MonitorJavaThreadPoolHistory> implements IMonitorJavaThreadPoolHistoryService {

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
    @Override
    public List<InstanceDetailPageJavaThreadPoolChartVo> getInstanceDetailPageJavaThreadPoolChartInfo(String instanceId, String threadPoolName, String time) {
        Map<String, Object> params = new HashMap<>(16);
        params.put("instanceId", instanceId);
        params.put("threadPoolName", threadPoolName);
        // 计算时间
        CalculateDateTime calculateDateTime = new CalculateDateTime(time).invoke();
        // 开始时间
        Date startTime = calculateDateTime.getStartTime();
        // 结束时间
        Date endTime = calculateDateTime.getEndTime();
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        return this.baseMapper.getInstanceDetailPageJavaThreadPoolChartInfo(params);
    }

}
