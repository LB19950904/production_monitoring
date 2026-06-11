package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorDockerStatsDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerStats;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerStatsService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerEventVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerStatsVo;
import com.gitee.pifeng.monitoring.ui.constant.WebResponseConstants;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * docker容器统计信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-24
 */
@Service
public class MonitorDockerStatsServiceImpl extends ServiceImpl<IMonitorDockerStatsDao, MonitorDockerStats> implements IMonitorDockerStatsService {

    /**
     * <p>
     * 获取docker资源统计列表
     * </p>
     *
     * @param current       当前页
     * @param size          每页显示条数
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @param monitorEnv    监控环境
     * @param monitorGroup  监控分组
     * @return docker资源统计列表
     * @author 皮锋
     * @custom.date 2022/8/21 20:47
     */
    @Override
    public Page<MonitorDockerStatsVo> getMonitorDockerStatsList(Long current, Long size, String serverIp, String containerName, String monitorEnv, String monitorGroup) {
        Page<MonitorDockerEventVo> page = new Page<>(current, size);
        Map<String, Object> params = new HashMap<>(16);
        params.put("serverIp", serverIp);
        params.put("containerName", containerName);
        params.put("monitorEnv", monitorEnv);
        params.put("monitorGroup", monitorGroup);
        return this.baseMapper.getMonitorDockerStatsList(page, params);
    }

    /**
     * <p>
     * 删除docker资源统计
     * </p>
     *
     * @param monitorDockerStatsVos docker资源统计信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    @Override
    public LayUiAdminResultVo deleteMonitorDockerStats(List<MonitorDockerStatsVo> monitorDockerStatsVos) {
        List<String> serverIps = Lists.newArrayList();
        List<String> containerNames = Lists.newArrayList();
        for (MonitorDockerStatsVo monitorDockerStatsVo : monitorDockerStatsVos) {
            serverIps.add(monitorDockerStatsVo.getServerIp());
            containerNames.add(monitorDockerStatsVo.getContainerName());
        }
        LambdaUpdateWrapper<MonitorDockerStats> monitorDockerStatsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        monitorDockerStatsLambdaUpdateWrapper.in(MonitorDockerStats::getServerIp, serverIps);
        monitorDockerStatsLambdaUpdateWrapper.in(MonitorDockerStats::getContainerName, containerNames);
        this.baseMapper.delete(monitorDockerStatsLambdaUpdateWrapper);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

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
    @Override
    public MonitorDockerStatsVo getMonitorDockerStatsInfo(String serverIp, String containerName) {
        return this.baseMapper.getMonitorDockerStatsInfo(serverIp, containerName);
    }

}
