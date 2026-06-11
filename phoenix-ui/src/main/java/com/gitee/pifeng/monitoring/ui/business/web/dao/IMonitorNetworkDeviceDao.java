package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDevice;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorServer;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * 网络设备数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
public interface IMonitorNetworkDeviceDao extends BaseMapper<MonitorNetworkDevice> {

    /**
     * <p>
     * 获取网络设备列表
     * </p>
     *
     * @param ipage    分页Page对象接口
     * @param criteria 查询条件
     * @return 简单分页模型
     * @author 皮锋
     * @custom.date 2025-3-18 11:05
     */
    IPage<MonitorNetworkDeviceVo> getMonitorNetworkDeviceList(IPage<MonitorServer> ipage, @Param("criteria") Map<String, Object> criteria);

    /**
     * <p>
     * 网络设备在线率统计
     * </p>
     *
     * @return 网络设备在线率统计信息
     * @author 皮锋
     * @custom.date 2025-4-3 12:54
     */
    Map<String, Object> getNetworkDeviceOnlineRateStatistics();

}
