package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerEvent;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerEventVo;

import java.util.List;

/**
 * <p>
 * docker事件信息服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-03
 */
public interface IMonitorDockerEventService extends IService<MonitorDockerEvent> {

    /**
     * <p>
     * 获取docker事件列表
     * </p>
     *
     * @param current      当前页
     * @param size         每页显示条数
     * @param eventId      事件ID
     * @param serverIp     服务器IP
     * @param eventStatus  事件状态
     * @param eventFrom    事件来源
     * @param eventType    事件类型
     * @param eventAction  事件动作
     * @param happenTime   事件发生时间
     * @param monitorEnv   监控环境
     * @param monitorGroup 监控分组
     * @return docker事件列表
     * @author 皮锋
     * @custom.date 2022/8/21 20:47
     */
    Page<MonitorDockerEventVo> getMonitorDockerEventList(Long current, Long size, String eventId, String serverIp,
                                                         String eventStatus, String eventFrom, String eventType,
                                                         String eventAction, String happenTime, String monitorEnv, String monitorGroup);

    /**
     * <p>
     * 删除docker事件
     * </p>
     *
     * @param ids 主键ID集合
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    LayUiAdminResultVo deleteMonitorDockerEvent(List<Long> ids);

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
    MonitorDockerEventVo getMonitorDockerEventInfo(String serverIp, Long id);

}
