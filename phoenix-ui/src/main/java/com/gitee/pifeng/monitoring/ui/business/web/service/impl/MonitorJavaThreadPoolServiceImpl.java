package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.BaseRequestPackage;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.plug.core.Sender;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorJavaThreadPoolDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorJavaThreadPool;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorJavaThreadPoolService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorJavaThreadPoolVo;
import com.gitee.pifeng.monitoring.ui.constant.UrlConstants;
import com.gitee.pifeng.monitoring.ui.constant.WebResponseConstants;
import com.gitee.pifeng.monitoring.ui.core.UiPackageConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

/**
 * <p>
 * java线程池信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-01-22
 */
@Service
public class MonitorJavaThreadPoolServiceImpl extends ServiceImpl<IMonitorJavaThreadPoolDao, MonitorJavaThreadPool> implements IMonitorJavaThreadPoolService {

    /**
     * UI端包构造器
     */
    @Autowired
    private UiPackageConstructor uiPackageConstructor;

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
    @Override
    public List<String> getJavaThreadPoolNames(String instanceId) {
        return this.baseMapper.getJavaThreadPoolNames(instanceId);
    }

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
    @Override
    public MonitorJavaThreadPoolVo getJavaThreadPoolInfo(String instanceId, String threadPoolName) {
        LambdaQueryWrapper<MonitorJavaThreadPool> threadPoolLambdaQueryWrapper = Wrappers.lambdaQuery();
        threadPoolLambdaQueryWrapper.eq(MonitorJavaThreadPool::getInstanceId, instanceId);
        threadPoolLambdaQueryWrapper.eq(MonitorJavaThreadPool::getName, threadPoolName);
        List<MonitorJavaThreadPool> monitorJavaThreadPools = this.baseMapper.selectList(threadPoolLambdaQueryWrapper);
        if (CollectionUtils.isNotEmpty(monitorJavaThreadPools)) {
            MonitorJavaThreadPool monitorJavaThreadPool = monitorJavaThreadPools.get(0);
            MonitorJavaThreadPoolVo monitorJavaThreadPoolVo = MonitorJavaThreadPoolVo.builder().build().convertFor(monitorJavaThreadPool);
            DecimalFormat df = new DecimalFormat("0.00%");
            String utilizationRate = df.format(monitorJavaThreadPool.getUtilizationRate());
            monitorJavaThreadPoolVo.setUtilizationRate(utilizationRate);
            return monitorJavaThreadPoolVo;
        }
        return MonitorJavaThreadPoolVo.builder().build();
    }

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
    @Override
    public LayUiAdminResultVo setInstanceJavaThreadPool(MonitorJavaThreadPoolVo monitorJavaThreadPoolVo) throws IOException {
        // 创建线程池信息对象
        JavaThreadPool.ThreadPoolInfoDomain threadPoolInfo = new JavaThreadPool.ThreadPoolInfoDomain();
        threadPoolInfo.setName(monitorJavaThreadPoolVo.getName());
        threadPoolInfo.setCorePoolSize(monitorJavaThreadPoolVo.getCorePoolSize());
        threadPoolInfo.setMaximumPoolSize(monitorJavaThreadPoolVo.getMaximumPoolSize());
        threadPoolInfo.setRejectedExecutionHandlerName(monitorJavaThreadPoolVo.getRejectedExecutionHandlerName());
        threadPoolInfo.setQueueType(monitorJavaThreadPoolVo.getQueueType());
        threadPoolInfo.setQueueCapacity(monitorJavaThreadPoolVo.getQueueCapacity());
        threadPoolInfo.setAllowCoreThreadTimeOut(monitorJavaThreadPoolVo.getAllowCoreThreadTimeOut());
        threadPoolInfo.setKeepAliveTime(monitorJavaThreadPoolVo.getKeepAliveTime());
        // 封装请求数据
        JSONObject extraMsg = new JSONObject();
        extraMsg.put("threadPoolInfo", threadPoolInfo);
        extraMsg.put("endpoint", monitorJavaThreadPoolVo.getEndpoint());
        extraMsg.put("instanceId", monitorJavaThreadPoolVo.getInstanceId());
        BaseRequestPackage baseRequestPackage = this.uiPackageConstructor.structureBaseRequestPackage(extraMsg);
        // 从服务端获取数据
        String resultStr = Sender.send(UrlConstants.SET_INSTANCE_JAVA_THREAD_POOL_URL, baseRequestPackage.toJsonString());
        BaseResponsePackage baseResponsePackage = JSON.parseObject(resultStr, BaseResponsePackage.class);
        Result result = baseResponsePackage.getResult();
        if (result.isSuccess() && Boolean.parseBoolean(result.getMsg())) {
            return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
        } else {
            return LayUiAdminResultVo.ok(WebResponseConstants.FAIL);
        }
    }

}
