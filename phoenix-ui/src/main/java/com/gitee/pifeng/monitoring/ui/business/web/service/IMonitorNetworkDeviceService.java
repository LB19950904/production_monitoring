package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDevice;
import com.gitee.pifeng.monitoring.ui.business.web.vo.HomeNetworkDeviceVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceVo;
import org.hyperic.sigar.SigarException;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 网络设备服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
public interface IMonitorNetworkDeviceService extends IService<MonitorNetworkDevice> {

    /**
     * <p>
     * 获取网络设备列表
     * </p>
     *
     * @param current              当前页
     * @param size                 每页显示条数
     * @param ip                   IP地址
     * @param isOnline             设备状态
     * @param insertType           新增方式
     * @param monitorEnv           监控环境
     * @param monitorGroup         监控分组
     * @param networkDeviceType    网络设备类型
     * @param networkDeviceSummary 网络设备摘要
     * @param isEnableMonitor      是否开启监控（0：不开启监控；1：开启监控）
     * @param isEnableAlarm        是否开启告警（0：不开启告警；1：开启告警）
     * @return 简单分页模型
     * @author 皮锋
     * @custom.date 2025-3-18 8:58
     */
    Page<MonitorNetworkDeviceVo> getMonitorNetworkDeviceList(Long current, Long size, String ip, String isOnline,
                                                             String insertType, String monitorEnv, String monitorGroup,
                                                             String networkDeviceType, String networkDeviceSummary,
                                                             String isEnableMonitor, String isEnableAlarm);

    /**
     * <p>
     * 删除网络设备
     * </p>
     *
     * @param monitorNetworkDeviceVos 网络设备信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025-3-18 15:47
     */
    LayUiAdminResultVo deleteMonitorNetworkDevice(List<MonitorNetworkDeviceVo> monitorNetworkDeviceVos);

    /**
     * <p>
     * 设置是否开启监控（0：不开启监控；1：开启监控）
     * </p>
     *
     * @param id              主键ID
     * @param ip              IP地址
     * @param isEnableMonitor 是否开启监控（0：不开启监控；1：开启监控）
     * @return 如果设置成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025-3-19 8:13
     */
    LayUiAdminResultVo setIsEnableMonitor(Long id, String ip, String isEnableMonitor);

    /**
     * <p>
     * 设置是否开启告警（0：不开启告警；1：开启告警）
     * </p>
     *
     * @param id            主键ID
     * @param ip            IP地址
     * @param isEnableAlarm 是否开启告警（0：不开启告警；1：开启告警）
     * @return 如果设置成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025-3-19 8:13
     */
    LayUiAdminResultVo setIsEnableAlarm(Long id, String ip, String isEnableAlarm);

    /**
     * <p>
     * 根据条件获取服务器信息
     * </p>
     *
     * @param id 主键ID
     * @param ip IP地址
     * @return 网络设备表现层对象
     * @author 皮锋
     * @custom.date 2025-3-24 14:28
     */
    MonitorNetworkDeviceVo getMonitorNetworkDeviceInfo(Long id, String ip);

    /**
     * <p>
     * 添加网络设备
     * </p>
     *
     * @param monitorNetworkDeviceVo 网络设备信息
     * @return 如果添加成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025-4-2 11:10
     */
    LayUiAdminResultVo addMonitorNetworkDevice(MonitorNetworkDeviceVo monitorNetworkDeviceVo);

    /**
     * <p>
     * 编辑网络设备信息
     * </p>
     *
     * @param monitorNetworkDeviceVo 网络设备信息
     * @return 如果编辑成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025-3-24 14:49
     */
    LayUiAdminResultVo editMonitorNetworkDevice(MonitorNetworkDeviceVo monitorNetworkDeviceVo);

    /**
     * <p>
     * 清理网络设备监控历史数据
     * </p>
     *
     * @param id   主键ID
     * @param ip   IP地址
     * @param time 时间
     * @return layUiAdmin响应对象：如果清理成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025-3-24 15:44
     */
    LayUiAdminResultVo clearMonitorNetworkDeviceHistory(String id, String ip, String time);

    /**
     * <p>
     * 获取home页的网络设备信息
     * </p>
     *
     * @return home页的网络设备表现层对象
     * @author 皮锋
     * @custom.date 2025-4-3 12:50
     */
    HomeNetworkDeviceVo getHomeNetworkDeviceInfo();

    /**
     * <p>
     * 获取MIB OID配置YAML字符串
     * </p>
     *
     * @param id 主键ID
     * @param ip IP地址
     * @return MIB OID配置YAML字符串
     * @author 皮锋
     * @custom.date 2025-4-10 16:15
     */
    String getOidYamlStr(Long id, String ip);

    /**
     * <p>
     * 测试网络设备连通性
     * </p>
     *
     * @param monitorNetworkDeviceVo 网络设备信息
     * @return layUiAdmin响应对象：网络设备连通性
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2025-4-11 12:42
     */
    LayUiAdminResultVo testMonitorNetworkDevice(MonitorNetworkDeviceVo monitorNetworkDeviceVo) throws SigarException, IOException;

}
