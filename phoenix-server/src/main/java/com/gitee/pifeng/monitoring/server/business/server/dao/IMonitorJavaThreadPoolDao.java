package com.gitee.pifeng.monitoring.server.business.server.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.pifeng.monitoring.server.business.server.dto.JavaThreadPoolMonitorDto;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorJavaThreadPool;
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
     * 获取java线程池监控传输层对象列表
     * </p>
     *
     * @param instanceId 应用实例ID
     * @return java线程池监控传输层对象列表
     * @author 皮锋
     * @custom.date 2025-2-18 9:48
     */
    List<JavaThreadPoolMonitorDto> getThreadPoolMonitorList(@Param("instanceId") String instanceId);

}
