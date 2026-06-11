package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerContainer;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerContainerVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * docker容器信息表数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-25
 */
public interface IMonitorDockerContainerDao extends BaseMapper<MonitorDockerContainer> {

    /**
     * <p>
     * 获取docker容器列表
     * </p>
     *
     * @param page   分页参数
     * @param params 请求条件
     * @return docker容器列表
     * @author 皮锋
     * @custom.date 2022/8/20 15:14
     */
    Page<MonitorDockerContainerVo> getMonitorDockerContainerList(IPage<MonitorDockerContainerVo> page, @Param("params") Map<String, Object> params);

    /**
     * <p>
     * 获取docker容器信息
     * </p>
     *
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @return docker容器信息表现层对象
     * @author 皮锋
     * @custom.date 2022/8/21 18:17
     */
    MonitorDockerContainerVo getMonitorDockerContainerInfo(@Param("serverIp") String serverIp, @Param("containerName") String containerName);
}
