package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorJavaThreadPool;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorJavaThreadPoolVo;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * java线程池信息服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
public interface IMonitorJavaThreadPoolService extends IService<MonitorJavaThreadPool> {

    /**
     * <p>
     * 获取java线程池名字
     * </p>
     *
     * @param instanceId 应用实例ID
     * @return java线程池名字
     * @author 皮锋
     * @custom.date 2025/1/23 14:15
     */
    List<String> getJavaThreadPoolNames(String instanceId);

    /**
     * <p>
     * 获取java线程池信息
     * </p>
     *
     * @param instanceId     应用实例ID
     * @param threadPoolName 线程池名字
     * @return java线程池信息表现层对象
     * @author 皮锋
     * @custom.date 2025/1/27 07:57
     */
    MonitorJavaThreadPoolVo getJavaThreadPoolInfo(String instanceId, String threadPoolName);

    /**
     * <p>
     * 配置Java线程池
     * </p>
     *
     * @param monitorJavaThreadPoolVo java线程池信息
     * @return 如果配置成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2026/3/21 23:42
     */
    LayUiAdminResultVo setInstanceJavaThreadPool(MonitorJavaThreadPoolVo monitorJavaThreadPoolVo) throws IOException;

}
