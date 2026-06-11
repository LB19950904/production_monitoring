package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.ZeroOrOneConstants;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.ui.business.web.dao.*;
import com.gitee.pifeng.monitoring.ui.business.web.entity.*;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorRealtimeMonitoringService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.HomeDockerVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerVo;
import com.gitee.pifeng.monitoring.ui.constant.WebResponseConstants;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * docker服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-04
 */
@Service
public class MonitorDockerServiceImpl extends ServiceImpl<IMonitorDockerDao, MonitorDocker> implements IMonitorDockerService {

    /**
     * 实时监控服务类
     */
    @Autowired
    private IMonitorRealtimeMonitoringService monitorRealtimeMonitoringService;

    /**
     * docker容器信息表数据访问对象
     */
    @Autowired
    private IMonitorDockerContainerDao monitorDockerContainerDao;

    /**
     * docker事件信息表数据访问对象
     */
    @Autowired
    private IMonitorDockerEventDao monitorDockerEventDao;

    /**
     * docker镜像信息表数据访问对象
     */
    @Autowired
    private IMonitorDockerImageDao monitorDockerImageDao;

    /**
     * docker容器统计信息表数据访问对象
     */
    @Autowired
    private IMonitorDockerStatsDao monitorDockerStatsDao;

    /**
     * docker容器统计信息历史记录表数据访问对象
     */
    @Autowired
    private IMonitorDockerStatsHistoryDao monitorDockerStatsHistoryDao;

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
    @Override
    public Page<MonitorDockerVo> getMonitorDockerList(Long current, Long size, String serverIp, String isOnline,
                                                      String monitorEnv, String monitorGroup, String dockerSummary,
                                                      String isEnableMonitor, String isEnableAlarm) {
        // 查询数据库
        IPage<MonitorDocker> ipage = new Page<>(current, size);
        // 查询条件
        LambdaQueryWrapper<MonitorDocker> dockerLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(serverIp)) {
            dockerLambdaQueryWrapper.like(MonitorDocker::getServerIp, serverIp);
        }
        if (StringUtils.isNotBlank(isOnline)) {
            if (StringUtils.equals(isOnline, ZeroOrOneConstants.MINUS_ONE)) {
                dockerLambdaQueryWrapper.isNull(MonitorDocker::getIsOnline);
            } else {
                dockerLambdaQueryWrapper.eq(MonitorDocker::getIsOnline, isOnline);
            }
        }
        if (StringUtils.isNotBlank(monitorEnv)) {
            dockerLambdaQueryWrapper.eq(MonitorDocker::getMonitorEnv, monitorEnv);
        }
        if (StringUtils.isNotBlank(monitorGroup)) {
            dockerLambdaQueryWrapper.eq(MonitorDocker::getMonitorGroup, monitorGroup);
        }
        if (StringUtils.isNotBlank(dockerSummary)) {
            dockerLambdaQueryWrapper.like(MonitorDocker::getDockerSummary, dockerSummary);
        }
        if (StringUtils.isNotBlank(isEnableMonitor)) {
            dockerLambdaQueryWrapper.eq(MonitorDocker::getIsEnableMonitor, isEnableMonitor);
        }
        if (StringUtils.isNotBlank(isEnableAlarm)) {
            dockerLambdaQueryWrapper.eq(MonitorDocker::getIsEnableAlarm, isEnableAlarm);
        }
        IPage<MonitorDocker> monitorDockerPage = this.baseMapper.selectPage(ipage, dockerLambdaQueryWrapper);
        List<MonitorDocker> monitorDockers = monitorDockerPage.getRecords();
        // 转换成docker信息表现层对象
        List<MonitorDockerVo> monitorDockerVos = Lists.newLinkedList();
        // 当前时间
        Date currentDateTime = new Date();
        for (MonitorDocker monitorDocker : monitorDockers) {
            MonitorDockerVo monitorDockerVo = MonitorDockerVo.builder().build().convertFor(monitorDocker);
            monitorDockerVo.setMemTotalStr(DataSizeUtil.format(monitorDocker.getMemTotal()));
            Date updateTime = monitorDocker.getUpdateTime();
            // 最后心跳时间
            String finalHeartbeat = updateTime != null ? DateUtil.formatBetween(currentDateTime, updateTime, BetweenFormatter.Level.SECOND) + "前" : "";
            monitorDockerVo.setFinalHeartbeat(finalHeartbeat);
            monitorDockerVos.add(monitorDockerVo);
        }
        // 设置返回对象
        Page<MonitorDockerVo> monitorDockerVoPage = new Page<>();
        monitorDockerVoPage.setRecords(monitorDockerVos);
        monitorDockerVoPage.setTotal(monitorDockerPage.getTotal());
        return monitorDockerVoPage;
    }

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
    @Retryable
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public LayUiAdminResultVo deleteMonitorDocker(List<MonitorDockerVo> monitorDockerVos) {
        List<String> ips = Lists.newArrayList();
        List<String> ids = Lists.newArrayList();
        for (MonitorDockerVo monitorDockerVo : monitorDockerVos) {
            ips.add(monitorDockerVo.getServerIp());
            Long id = monitorDockerVo.getId();
            if (id != null) {
                ids.add(String.valueOf(id));
            }
        }
        // 删除docker服务信息
        LambdaUpdateWrapper<MonitorDocker> monitorDockerLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        monitorDockerLambdaUpdateWrapper.in(MonitorDocker::getServerIp, ips);
        this.baseMapper.delete(monitorDockerLambdaUpdateWrapper);
        // 删除docker容器
        LambdaUpdateWrapper<MonitorDockerContainer> monitorDockerContainerLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        monitorDockerContainerLambdaUpdateWrapper.in(MonitorDockerContainer::getServerIp, ips);
        this.monitorDockerContainerDao.delete(monitorDockerContainerLambdaUpdateWrapper);
        // 删除docker镜像
        LambdaUpdateWrapper<MonitorDockerImage> monitorDockerImageLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        monitorDockerImageLambdaUpdateWrapper.in(MonitorDockerImage::getServerIp, ips);
        this.monitorDockerImageDao.delete(monitorDockerImageLambdaUpdateWrapper);
        // 删除docker事件
        LambdaUpdateWrapper<MonitorDockerEvent> monitorDockerEventLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        monitorDockerEventLambdaUpdateWrapper.in(MonitorDockerEvent::getServerIp, ips);
        this.monitorDockerEventDao.delete(monitorDockerEventLambdaUpdateWrapper);
        // 删除docker统计信息
        LambdaUpdateWrapper<MonitorDockerStats> monitorDockerStatsLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        monitorDockerStatsLambdaUpdateWrapper.in(MonitorDockerStats::getServerIp, ips);
        this.monitorDockerStatsDao.delete(monitorDockerStatsLambdaUpdateWrapper);
        // 删除docker统计历史记录信息
        LambdaUpdateWrapper<MonitorDockerStatsHistory> monitorDockerStatsHistoryLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        monitorDockerStatsHistoryLambdaUpdateWrapper.in(MonitorDockerStatsHistory::getServerIp, ips);
        this.monitorDockerStatsHistoryDao.delete(monitorDockerStatsHistoryLambdaUpdateWrapper);
        // 注意：删除实时监控信息，这个不要忘记了
        this.monitorRealtimeMonitoringService.delete(MonitorTypeEnums.DOCKER, null, ids);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

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
    @Override
    public MonitorDockerVo getMonitorDockerInfo(Long id) {
        MonitorDocker monitorDocker = this.baseMapper.selectById(id);
        MonitorDockerVo monitorDockerVo = MonitorDockerVo.builder().build().convertFor(monitorDocker);
        monitorDockerVo.setMemTotalStr(DataSizeUtil.format(monitorDocker.getMemTotal()));
        // 格式化json字符串
        Object rawValuesObject = JSON.parse(monitorDockerVo.getRawValues());
        String rawValues = JSON.toJSONString(rawValuesObject,
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteNullListAsEmpty);
        monitorDockerVo.setRawValues(rawValues);
        return monitorDockerVo;
    }

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
    @Override
    public LayUiAdminResultVo editMonitorDocker(MonitorDockerVo monitorDockerVo) {
        MonitorDocker monitorDocker = monitorDockerVo.convertTo();
        if (StringUtils.isBlank(monitorDocker.getMonitorEnv())) {
            monitorDocker.setMonitorEnv(null);
        }
        if (StringUtils.isBlank(monitorDocker.getMonitorGroup())) {
            monitorDocker.setMonitorGroup(null);
        }
        this.baseMapper.updateById(monitorDocker);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

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
    @Override
    public LayUiAdminResultVo setIsEnableMonitor(Long id, String isEnableMonitor) {
        LambdaUpdateWrapper<MonitorDocker> dockerLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        // 设置更新条件
        dockerLambdaUpdateWrapper.eq(MonitorDocker::getId, id);
        // 设置更新字段
        dockerLambdaUpdateWrapper.set(MonitorDocker::getIsEnableMonitor, isEnableMonitor);
        // 如果不监控，docker服务状态设置为未知
        if (StringUtils.equals(isEnableMonitor, ZeroOrOneConstants.ZERO)) {
            dockerLambdaUpdateWrapper.set(MonitorDocker::getIsOnline, null);
        }
        this.baseMapper.update(null, dockerLambdaUpdateWrapper);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

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
    @Override
    public LayUiAdminResultVo setIsEnableAlarm(Long id, String isEnableAlarm) {
        LambdaUpdateWrapper<MonitorDocker> dockerLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        // 设置更新条件
        dockerLambdaUpdateWrapper.eq(MonitorDocker::getId, id);
        // 设置更新字段
        dockerLambdaUpdateWrapper.set(MonitorDocker::getIsEnableAlarm, isEnableAlarm);
        this.baseMapper.update(null, dockerLambdaUpdateWrapper);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

    /**
     * <p>
     * 获取home页的docker服务信息
     * </p>
     *
     * @return home页的docker服务表现层对象
     * @author 皮锋
     * @custom.date 2022/9/15 15:40
     */
    @Override
    public HomeDockerVo getHomeDockerInfo() {
        // docker服务在线率统计
        Map<String, Object> map = this.baseMapper.getDockerNormalRateStatistics();
        return HomeDockerVo.builder()
                .dockerSum(NumberUtil.parseInt(map.get("dockerSum").toString()))
                .dockerOnLineSum(NumberUtil.parseInt(map.get("dockerOnLineSum").toString()))
                .dockerOffLineSum(NumberUtil.parseInt(map.get("dockerOffLineSum").toString()))
                .dockerUnknownLineSum(NumberUtil.parseInt(map.get("dockerUnknownLineSum").toString()))
                .dockerOnLineRate(NumberUtil.round(map.get("dockerOnLineRate").toString(), 2).toString())
                .build();
    }

}
