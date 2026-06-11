package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDocker;
import com.gitee.pifeng.monitoring.ui.business.web.vo.HomeDockerVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerVo;

import java.util.List;

/**
 * <p>
 * docker服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-04
 */
public interface IMonitorDockerService extends IService<MonitorDocker> {

    /**
     * <p>
     * 获取docker列表
     * </p>
     *
     * @param current         当前页
     * @param size            每页显示条数
     * @param serverIp        服务器IP
     * @param isOnline        状态
     * @param monitorEnv      监控环境
     * @param monitorGroup    监控分组
     * @param dockerSummary   docker摘要
     * @param isEnableMonitor 是否开启监控（0：不开启监控；1：开启监控）
     * @param isEnableAlarm   是否开启告警（0：不开启告警；1：开启告警）
     * @return 简单分页模型
     * @author 皮锋
     * @custom.date 2022/7/5 22:09
     */
    Page<MonitorDockerVo> getMonitorDockerList(Long current, Long size, String serverIp, String isOnline,
                                               String monitorEnv, String monitorGroup, String dockerSummary,
                                               String isEnableMonitor, String isEnableAlarm);

    /**
     * <p>
     * 删除docker
     * </p>
     *
     * @param monitorDockerVos docker服务信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:04
     */
    LayUiAdminResultVo deleteMonitorDocker(List<MonitorDockerVo> monitorDockerVos);

    /**
     * <p>
     * 根据条件获取docker信息
     * </p>
     *
     * @param id docker服务ID
     * @return docker服务信息
     * @author 皮锋
     * @custom.date 2022/7/8 21:46
     */
    MonitorDockerVo getMonitorDockerInfo(Long id);

    /**
     * <p>
     * 编辑docker信息
     * </p>
     *
     * @param monitorDockerVo docker服务信息
     * @return 如果编辑成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:59
     */
    LayUiAdminResultVo editMonitorDocker(MonitorDockerVo monitorDockerVo);

    /**
     * <p>
     * 设置是否开启监控（0：不开启监控；1：开启监控）
     * </p>
     *
     * @param id              主键ID
     * @param isEnableMonitor 是否开启监控（0：不开启监控；1：开启监控）
     * @return 如果设置成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2024/12/10 21:20
     */
    LayUiAdminResultVo setIsEnableMonitor(Long id, String isEnableMonitor);

    /**
     * <p>
     * 设置是否开启告警（0：不开启告警；1：开启告警）
     * </p>
     *
     * @param id            主键ID
     * @param isEnableAlarm 是否开启告警（0：不开启告警；1：开启告警）
     * @return 如果设置成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2024/12/10 21:37
     */
    LayUiAdminResultVo setIsEnableAlarm(Long id, String isEnableAlarm);

    /**
     * <p>
     * 获取home页的docker服务信息
     * </p>
     *
     * @return home页的docker服务表现层对象
     * @author 皮锋
     * @custom.date 2022/9/15 15:40
     */
    HomeDockerVo getHomeDockerInfo();

}
