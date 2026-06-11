package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import cn.hutool.core.date.BetweenFormatter;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.ZeroOrOneConstants;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.BaseRequestPackage;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.reqparam.snmp.OId;
import com.gitee.pifeng.monitoring.plug.core.Sender;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorNetworkDeviceDao;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorNetworkDeviceIfDao;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorNetworkDeviceSysDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDevice;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDeviceIf;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorNetworkDeviceSys;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorServer;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorNetworkDeviceService;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorRealtimeMonitoringService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.HomeNetworkDeviceVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceVo;
import com.gitee.pifeng.monitoring.ui.constant.UrlConstants;
import com.gitee.pifeng.monitoring.ui.constant.WebResponseConstants;
import com.gitee.pifeng.monitoring.ui.core.UiPackageConstructor;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.util.*;

/**
 * <p>
 * 网络设备服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
@Service
public class MonitorNetworkDeviceServiceImpl extends ServiceImpl<IMonitorNetworkDeviceDao, MonitorNetworkDevice> implements IMonitorNetworkDeviceService {

    /**
     * UI端包构造器
     */
    @Autowired
    private UiPackageConstructor uiPackageConstructor;

    /**
     * 实时监控服务类
     */
    @Autowired
    private IMonitorRealtimeMonitoringService monitorRealtimeMonitoringService;

    /**
     * 网络设备数据访问对象
     */
    @Autowired
    private IMonitorNetworkDeviceDao monitorNetworkDeviceDao;

    /**
     * 网络设备接口数据访问对象
     */
    @Autowired
    private IMonitorNetworkDeviceIfDao monitorNetworkDeviceIfDao;

    /**
     * 网络设备系统数据访问对象
     */
    @Autowired
    private IMonitorNetworkDeviceSysDao monitorNetworkDeviceSysDao;

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
    @Override
    public Page<MonitorNetworkDeviceVo> getMonitorNetworkDeviceList(Long current, Long size, String ip, String isOnline,
                                                                    String insertType, String monitorEnv, String monitorGroup,
                                                                    String networkDeviceType, String networkDeviceSummary,
                                                                    String isEnableMonitor, String isEnableAlarm) {
        // 查询数据库
        IPage<MonitorServer> ipage = new Page<>(current, size);
        // 查询条件
        Map<String, Object> criteria = new HashMap<>(16);
        criteria.put("ip", ip);
        criteria.put("isOnline", isOnline);
        criteria.put("insertType", insertType);
        criteria.put("monitorEnv", monitorEnv);
        criteria.put("monitorGroup", monitorGroup);
        criteria.put("networkDeviceType", networkDeviceType);
        criteria.put("networkDeviceSummary", networkDeviceSummary);
        criteria.put("isEnableMonitor", isEnableMonitor);
        criteria.put("isEnableAlarm", isEnableAlarm);
        IPage<MonitorNetworkDeviceVo> monitorNetworkDevicePage = this.baseMapper.getMonitorNetworkDeviceList(ipage, criteria);
        List<MonitorNetworkDeviceVo> monitorNetworkDeviceVos = monitorNetworkDevicePage.getRecords();
        // 当前时间
        Date currentDateTime = new Date();
        for (MonitorNetworkDeviceVo monitorNetworkDeviceVo : monitorNetworkDeviceVos) {
            Date updateTime = monitorNetworkDeviceVo.getUpdateTime();
            // 最后心跳时间
            String finalHeartbeat = updateTime != null ? DateUtil.formatBetween(currentDateTime, updateTime, BetweenFormatter.Level.SECOND) + "前" : "";
            monitorNetworkDeviceVo.setFinalHeartbeat(finalHeartbeat);
        }
        // 设置返回对象
        Page<MonitorNetworkDeviceVo> monitorNetworkDeviceVoPage = new Page<>();
        monitorNetworkDeviceVoPage.setRecords(monitorNetworkDeviceVos);
        monitorNetworkDeviceVoPage.setTotal(monitorNetworkDevicePage.getTotal());
        return monitorNetworkDeviceVoPage;
    }

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
    @Retryable
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public LayUiAdminResultVo deleteMonitorNetworkDevice(List<MonitorNetworkDeviceVo> monitorNetworkDeviceVos) {
        List<String> ips = Lists.newArrayList();
        List<String> ids = Lists.newArrayList();
        for (MonitorNetworkDeviceVo monitorNetworkDeviceVo : monitorNetworkDeviceVos) {
            ips.add(monitorNetworkDeviceVo.getIpTarget());
            Long id = monitorNetworkDeviceVo.getId();
            if (id != null) {
                ids.add(String.valueOf(id));
            }
        }
        // 网络设备系统
        LambdaUpdateWrapper<MonitorNetworkDeviceSys> networkDeviceSysLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        networkDeviceSysLambdaUpdateWrapper.in(MonitorNetworkDeviceSys::getIp, ips);
        this.monitorNetworkDeviceSysDao.delete(networkDeviceSysLambdaUpdateWrapper);
        // 网络设备接口
        LambdaUpdateWrapper<MonitorNetworkDeviceIf> networkDeviceIfLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        networkDeviceIfLambdaUpdateWrapper.in(MonitorNetworkDeviceIf::getIp, ips);
        this.monitorNetworkDeviceIfDao.delete(networkDeviceIfLambdaUpdateWrapper);
        // 网络设备表
        LambdaUpdateWrapper<MonitorNetworkDevice> networkDeviceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        networkDeviceLambdaUpdateWrapper.in(MonitorNetworkDevice::getIpTarget, ips);
        this.monitorNetworkDeviceDao.delete(networkDeviceLambdaUpdateWrapper);
        // 注意：删除网络设备相关实时监控信息，这个不要忘记了
        this.monitorRealtimeMonitoringService.delete(MonitorTypeEnums.NETWORK_DEVICE, null, ids);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

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
    @Override
    public LayUiAdminResultVo setIsEnableMonitor(Long id, String ip, String isEnableMonitor) {
        LambdaUpdateWrapper<MonitorNetworkDevice> networkDeviceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        // 设置更新条件
        networkDeviceLambdaUpdateWrapper.eq(MonitorNetworkDevice::getId, id);
        networkDeviceLambdaUpdateWrapper.eq(MonitorNetworkDevice::getIpTarget, ip);
        // 设置更新字段
        networkDeviceLambdaUpdateWrapper.set(MonitorNetworkDevice::getIsEnableMonitor, isEnableMonitor);
        // 如果不监控，状态改为未知
        if (StringUtils.equals(isEnableMonitor, ZeroOrOneConstants.ZERO)) {
            networkDeviceLambdaUpdateWrapper.set(MonitorNetworkDevice::getIsOnline, null);
        }
        this.monitorNetworkDeviceDao.update(null, networkDeviceLambdaUpdateWrapper);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

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
    @Override
    public LayUiAdminResultVo setIsEnableAlarm(Long id, String ip, String isEnableAlarm) {
        LambdaUpdateWrapper<MonitorNetworkDevice> networkDeviceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        // 设置更新条件
        networkDeviceLambdaUpdateWrapper.eq(MonitorNetworkDevice::getId, id);
        networkDeviceLambdaUpdateWrapper.eq(MonitorNetworkDevice::getIpTarget, ip);
        // 设置更新字段
        networkDeviceLambdaUpdateWrapper.set(MonitorNetworkDevice::getIsEnableAlarm, isEnableAlarm);
        this.monitorNetworkDeviceDao.update(null, networkDeviceLambdaUpdateWrapper);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

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
    @Override
    public MonitorNetworkDeviceVo getMonitorNetworkDeviceInfo(Long id, String ip) {
        LambdaQueryWrapper<MonitorNetworkDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorNetworkDevice::getId, id);
        lambdaQueryWrapper.eq(MonitorNetworkDevice::getIpTarget, ip);
        MonitorNetworkDevice monitorNetworkDevice = this.baseMapper.selectOne(lambdaQueryWrapper);
        return MonitorNetworkDeviceVo.builder().build().convertFor(monitorNetworkDevice);
    }

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
    @Override
    public LayUiAdminResultVo addMonitorNetworkDevice(MonitorNetworkDeviceVo monitorNetworkDeviceVo) {
        // 根据目标网络设备IP地址，查询数据库中是否已经存在此目标IP地址的记录
        LambdaQueryWrapper<MonitorNetworkDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(MonitorNetworkDevice::getIpTarget, monitorNetworkDeviceVo.getIpTarget());
        MonitorNetworkDevice dbMonitorNetworkDevice = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (dbMonitorNetworkDevice != null) {
            return LayUiAdminResultVo.ok(WebResponseConstants.EXIST);
        }
        MonitorNetworkDevice monitorNetworkDevice = monitorNetworkDeviceVo.convertTo();
        // 手动方式
        monitorNetworkDevice.setInsertType(ZeroOrOneConstants.ONE);
        monitorNetworkDevice.setInsertTime(new Date());
        monitorNetworkDevice.setOfflineCount(0);
        if (StringUtils.isBlank(monitorNetworkDeviceVo.getMonitorEnv())) {
            monitorNetworkDevice.setMonitorEnv(null);
        }
        if (StringUtils.isBlank(monitorNetworkDeviceVo.getMonitorGroup())) {
            monitorNetworkDevice.setMonitorGroup(null);
        }
        if (StringUtils.isBlank(monitorNetworkDeviceVo.getIsEnableMonitor())) {
            monitorNetworkDevice.setIsEnableMonitor(ZeroOrOneConstants.ZERO);
        }
        if (StringUtils.isBlank(monitorNetworkDeviceVo.getIsEnableAlarm())) {
            monitorNetworkDevice.setIsEnableAlarm(ZeroOrOneConstants.ZERO);
        }
        int result = this.baseMapper.insert(monitorNetworkDevice);
        if (result == 1) {
            return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
        }
        return LayUiAdminResultVo.ok(WebResponseConstants.FAIL);
    }

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
    @Override
    public LayUiAdminResultVo editMonitorNetworkDevice(MonitorNetworkDeviceVo monitorNetworkDeviceVo) {
        // 根据目标IP，查询数据库中是否已经存在此目标IP的记录
        LambdaQueryWrapper<MonitorNetworkDevice> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        // 去掉它自己这条记录
        lambdaQueryWrapper.ne(MonitorNetworkDevice::getId, monitorNetworkDeviceVo.getId());
        lambdaQueryWrapper.eq(MonitorNetworkDevice::getIpTarget, monitorNetworkDeviceVo.getIpTarget());
        MonitorNetworkDevice dbMonitorNetworkDevice = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (dbMonitorNetworkDevice != null) {
            return LayUiAdminResultVo.ok(WebResponseConstants.EXIST);
        }
        MonitorNetworkDevice monitorNetworkDevice = monitorNetworkDeviceVo.convertTo();
        monitorNetworkDevice.setUpdateTime(new Date());
        if (StringUtils.isBlank(monitorNetworkDeviceVo.getMonitorEnv())) {
            monitorNetworkDevice.setMonitorEnv(null);
        }
        if (StringUtils.isBlank(monitorNetworkDeviceVo.getMonitorGroup())) {
            monitorNetworkDevice.setMonitorGroup(null);
        }
        if (StringUtils.isBlank(monitorNetworkDeviceVo.getIsEnableMonitor())) {
            monitorNetworkDevice.setIsEnableMonitor(ZeroOrOneConstants.ZERO);
        }
        if (StringUtils.isBlank(monitorNetworkDeviceVo.getIsEnableAlarm())) {
            monitorNetworkDevice.setIsEnableAlarm(ZeroOrOneConstants.ZERO);
        }
        LambdaUpdateWrapper<MonitorNetworkDevice> networkDeviceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        networkDeviceLambdaUpdateWrapper.eq(MonitorNetworkDevice::getId, monitorNetworkDevice.getId());
        networkDeviceLambdaUpdateWrapper.eq(MonitorNetworkDevice::getIpTarget, monitorNetworkDevice.getIpTarget());
        int result = this.baseMapper.update(monitorNetworkDevice, networkDeviceLambdaUpdateWrapper);
        if (result == 1) {
            return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
        }
        return LayUiAdminResultVo.ok(WebResponseConstants.FAIL);
    }

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
    @Override
    public LayUiAdminResultVo clearMonitorNetworkDeviceHistory(String id, String ip, String time) {
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

    /**
     * <p>
     * 获取home页的网络设备信息
     * </p>
     *
     * @return home页的网络设备表现层对象
     * @author 皮锋
     * @custom.date 2025-4-3 12:50
     */
    @Override
    public HomeNetworkDeviceVo getHomeNetworkDeviceInfo() {
        // 网络设备在线率统计
        Map<String, Object> map = this.baseMapper.getNetworkDeviceOnlineRateStatistics();
        return HomeNetworkDeviceVo.builder()
                .networkDeviceSum(NumberUtil.parseInt(map.get("networkDeviceSum").toString()))
                .networkDeviceOnLineSum(NumberUtil.parseInt(map.get("networkDeviceOnLineSum").toString()))
                .networkDeviceOffLineSum(NumberUtil.parseInt(map.get("networkDeviceOffLineSum").toString()))
                .networkDeviceUnknownLineSum(NumberUtil.parseInt(map.get("networkDeviceUnknownLineSum").toString()))
                .networkDeviceOnLineRate(NumberUtil.round(map.get("networkDeviceOnLineRate").toString(), 2).toString())
                .build();
    }

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
    @Override
    public String getOidYamlStr(Long id, String ip) {
        String oidJsonStr = null;
        if (Objects.nonNull(id) && StringUtils.isNotBlank(ip)) {
            MonitorNetworkDeviceVo monitorNetworkDeviceVo = this.getMonitorNetworkDeviceInfo(id, ip);
            oidJsonStr = monitorNetworkDeviceVo.getOid();
        }
        if (StringUtils.isBlank(oidJsonStr)) {
            OId oid = new OId().builderDefaultValues();
            oidJsonStr = oid.toJsonString();
        }
        // 转成 JSON对象
        JSONObject jsonObject = JSON.parseObject(oidJsonStr);
        // 使用 SnakeYAML 转换为 YAML
        DumperOptions options = new DumperOptions();
        // 设置为块格式
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        // 设置超大宽度，避免换行
        options.setWidth(Integer.MAX_VALUE);
        Yaml yaml = new Yaml(options);
        // 将 JSON对象 序列化为 YAML字符串
        return yaml.dump(jsonObject);
    }

    /**
     * <p>
     * 测试网络设备连通性
     * </p>
     *
     * @param monitorNetworkDeviceVo 网络设备信息
     * @return layUiAdmin响应对象：网络设备连通性
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2025-4-11 12:42
     */
    @Override
    public LayUiAdminResultVo testMonitorNetworkDevice(MonitorNetworkDeviceVo monitorNetworkDeviceVo) throws IOException {
        // 封装请求数据
        JSONObject extraMsg = new JSONObject();
        extraMsg.put("ipTarget", monitorNetworkDeviceVo.getIpTarget());
        extraMsg.put("cpPort", monitorNetworkDeviceVo.getCpPort());
        extraMsg.put("cpName", monitorNetworkDeviceVo.getCpName());
        extraMsg.put("cpCommunity", monitorNetworkDeviceVo.getCpCommunity());
        extraMsg.put("oid", StringUtils.isNotBlank(monitorNetworkDeviceVo.getOid()) ? monitorNetworkDeviceVo.getOid() : new OId().builderDefaultValues().toJsonString());
        extraMsg.put("cpVersion", monitorNetworkDeviceVo.getCpVersion());
        BaseRequestPackage baseRequestPackage = this.uiPackageConstructor.structureBaseRequestPackage(extraMsg);
        // 从服务端获取数据
        String resultStr = Sender.send(UrlConstants.TEST_MONITOR_NETWORK_DEVICE_URL, baseRequestPackage.toJsonString());
        BaseResponsePackage baseResponsePackage = JSON.parseObject(resultStr, BaseResponsePackage.class);
        Result result = baseResponsePackage.getResult();
        String msg = result.getMsg();
        boolean isConnected = Boolean.parseBoolean(msg);
        if (isConnected) {
            msg = WebResponseConstants.SUCCESS;
        } else {
            msg = WebResponseConstants.FAIL;
        }
        return LayUiAdminResultVo.ok(msg);
    }

}
