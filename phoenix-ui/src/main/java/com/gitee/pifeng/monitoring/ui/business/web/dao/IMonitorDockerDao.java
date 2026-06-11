package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDocker;

import java.util.Map;

/**
 * <p>
 * docker表数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-04
 */
public interface IMonitorDockerDao extends BaseMapper<MonitorDocker> {

    /**
     * <p>
     * docker服务在线率统计
     * </p>
     *
     * @return docker服务在线率统计信息
     * @author 皮锋
     * @custom.date 2022/9/15 15:45
     */
    Map<String, Object> getDockerNormalRateStatistics();
}
