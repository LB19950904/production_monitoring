package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorJavaThreadPool;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * java线程池信息数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
public interface IMonitorJavaThreadPoolDao extends BaseMapper<MonitorJavaThreadPool> {

    /**
     * <p>
     * 获取java线程池名字
     * </p>
     *
     * @param instanceId 应用实例ID
     * @return java线程池名字
     * @author 皮锋
     * @custom.date 2025/1/23 14:15
     */
    List<String> getJavaThreadPoolNames(@Param("instanceId") String instanceId);

}
