package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorDockerEventDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerEvent;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerEventService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerEventVo;
import com.gitee.pifeng.monitoring.ui.constant.WebResponseConstants;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * docker事件信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-03
 */
@Service
public class MonitorDockerEventServiceImpl extends ServiceImpl<IMonitorDockerEventDao, MonitorDockerEvent> implements IMonitorDockerEventService {

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
    @Override
    public Page<MonitorDockerEventVo> getMonitorDockerEventList(Long current, Long size, String eventId, String serverIp,
                                                                String eventStatus, String eventFrom, String eventType,
                                                                String eventAction, String happenTime, String monitorEnv, String monitorGroup) {
        Page<MonitorDockerEventVo> page = new Page<>(current, size);
        Map<String, Object> params = new HashMap<>(16);
        params.put("eventId", eventId);
        params.put("serverIp", serverIp);
        params.put("eventStatus", eventStatus);
        params.put("eventFrom", eventFrom);
        params.put("eventType", eventType);
        params.put("eventAction", eventAction);
        params.put("happenTime", happenTime);
        params.put("monitorEnv", monitorEnv);
        params.put("monitorGroup", monitorGroup);
        return this.baseMapper.getMonitorDockerEventList(page, params);
    }

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
    @Override
    public LayUiAdminResultVo deleteMonitorDockerEvent(List<Long> ids) {
        this.baseMapper.deleteBatchIds(ids);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

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
    @Override
    public MonitorDockerEventVo getMonitorDockerEventInfo(String serverIp, Long id) {
        return this.baseMapper.getMonitorDockerEventInfo(serverIp, id);
    }

}
