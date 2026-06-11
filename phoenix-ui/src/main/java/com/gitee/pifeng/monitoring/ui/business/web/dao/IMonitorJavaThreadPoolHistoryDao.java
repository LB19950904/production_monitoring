package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorJavaThreadPoolHistory;
import com.gitee.pifeng.monitoring.ui.business.web.vo.InstanceDetailPageJavaThreadPoolChartVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * java线程池历史记录数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
public interface IMonitorJavaThreadPoolHistoryDao extends BaseMapper<MonitorJavaThreadPoolHistory> {

    /**
     * <p>
     * 获取应用实例详情页面java线程池图表信息
     * </p>
     *
     * @param params 请求参数
     * @return 应用实例详情页面java线程池图表信息表现层对象
     * @author 皮锋
     * @custom.date 2025/1/23 16:14
     */
    List<InstanceDetailPageJavaThreadPoolChartVo> getInstanceDetailPageJavaThreadPoolChartInfo(@Param("params") Map<String, Object> params);

}
