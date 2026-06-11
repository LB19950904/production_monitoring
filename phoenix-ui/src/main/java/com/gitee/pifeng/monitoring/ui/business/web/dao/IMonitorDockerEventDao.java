package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerEvent;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerEventVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * docker事件信息表数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-03
 */
public interface IMonitorDockerEventDao extends BaseMapper<MonitorDockerEvent> {

    /**
     * <p>
     * 获取docker事件列表
     * </p>
     *
     * @param page   分页参数
     * @param params 请求条件
     * @return docker事件列表
     * @author 皮锋
     * @custom.date 2022/8/21 21:08
     */
    Page<MonitorDockerEventVo> getMonitorDockerEventList(Page<MonitorDockerEventVo> page, @Param("params") Map<String, Object> params);

    /**
     * <p>
     * 访问docker事件详情页面
     * </p>
     *
     * @param serverIp 服务器IP
     * @param id       主键ID
     * @return docker事件信息表现层对象
     * @author 皮锋
     * @custom.date 2022/8/21 16:33
     */
    MonitorDockerEventVo getMonitorDockerEventInfo(@Param("serverIp") String serverIp, @Param("id") Long id);

}
