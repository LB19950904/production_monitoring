package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerStats;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerEventVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerStatsVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * docker容器统计信息表数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-24
 */
public interface IMonitorDockerStatsDao extends BaseMapper<MonitorDockerStats> {

    /**
     * <p>
     * 获取docker资源统计列表
     * </p>
     *
     * @param page   分页参数
     * @param params 请求条件
     * @return docker资源统计列表
     * @author 皮锋
     * @custom.date 2022/8/21 21:08
     */
    Page<MonitorDockerStatsVo> getMonitorDockerStatsList(Page<MonitorDockerEventVo> page, @Param("params") Map<String, Object> params);

    /**
     * <p>
     * 获取docker资源统计信息
     * </p>
     *
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @return docker资源统计信息表现层对象
     * @author 皮锋
     * @custom.date 2022/8/21 16:42
     */
    MonitorDockerStatsVo getMonitorDockerStatsInfo(@Param("serverIp") String serverIp, @Param("containerName") String containerName);

}
