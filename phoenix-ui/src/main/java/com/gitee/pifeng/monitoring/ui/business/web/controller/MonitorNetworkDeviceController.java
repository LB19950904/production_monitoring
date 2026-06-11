package com.gitee.pifeng.monitoring.ui.business.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.controller.BaseController;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.ui.business.web.annotation.OperateLog;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorEnv;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorGroup;
import com.gitee.pifeng.monitoring.ui.business.web.service.*;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceIfVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceSysVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorNetworkDeviceVo;
import com.gitee.pifeng.monitoring.ui.constant.OperateTypeConstants;
import com.gitee.pifeng.monitoring.ui.constant.UiModuleConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hyperic.sigar.SigarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 网络设备
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025-03-18
 */
@RestController
@RequestMapping("/monitor-network-device")
@Tag(name = "网络设备")
public class MonitorNetworkDeviceController extends BaseController {

    /**
     * 网络信息服务类
     */
    @Autowired
    private IMonitorNetService monitorNetService;

    /**
     * 网络设备服务类
     */
    @Autowired
    private IMonitorNetworkDeviceService monitorNetworkDeviceService;

    /**
     * 网络设备系统服务类
     */
    @Autowired
    private IMonitorNetworkDeviceSysService monitorNetworkDeviceSysService;

    /**
     * 网络设备接口服务类
     */
    @Autowired
    private IMonitorNetworkDeviceIfService monitorNetworkDeviceIfService;

    /**
     * 监控环境服务类
     */
    @Autowired
    private IMonitorEnvService monitorEnvService;

    /**
     * 监控分组服务类
     */
    @Autowired
    private IMonitorGroupService monitorGroupService;

    /**
     * <p>
     * 访问网络设备列表页面
     * </p>
     *
     * @return {@link ModelAndView} 网络设备列表页面
     * @author 皮锋
     * @custom.date 2025/3/18 10:53
     */
    @Operation(summary = "访问网络设备列表页面")
    @GetMapping("/list")
    public ModelAndView list() {
        ModelAndView mv = new ModelAndView("networkdevice/network-device");
        // 监控环境列表
        List<String> monitorEnvs = this.monitorEnvService.list().stream().map(MonitorEnv::getEnvName).collect(Collectors.toList());
        // 监控分组列表
        List<MonitorGroup> monitorGroupList = this.monitorGroupService.getMonitorGroupList(MonitorTypeEnums.NETWORK_DEVICE);
        List<String> monitorGroups = monitorGroupList.stream().map(MonitorGroup::getGroupName).collect(Collectors.toList());
        mv.addObject("monitorEnvs", monitorEnvs);
        mv.addObject("monitorGroups", monitorGroups);
        return mv;
    }

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
     * @return layUiAdmin响应对象
     * @author 皮锋
     * @custom.date 2025/3/18 10:59
     */
    @Operation(summary = "获取网络设备列表")
    @Parameters(value = {
            @Parameter(name = "current", description = "当前页", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "size", description = "每页显示条数", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "ip", description = "IP", in = ParameterIn.QUERY),
            @Parameter(name = "isOnline", description = "应用状态", in = ParameterIn.QUERY),
            @Parameter(name = "insertType", description = "新增方式", in = ParameterIn.QUERY),
            @Parameter(name = "monitorEnv", description = "监控环境", in = ParameterIn.QUERY),
            @Parameter(name = "monitorGroup", description = "监控分组", in = ParameterIn.QUERY),
            @Parameter(name = "networkDeviceType", description = "网络设备类型", in = ParameterIn.QUERY),
            @Parameter(name = "networkDeviceSummary", description = "网络设备摘要", in = ParameterIn.QUERY),
            @Parameter(name = "isEnableMonitor", description = "是否开启监控（0：不开启监控；1：开启监控）", in = ParameterIn.QUERY),
            @Parameter(name = "isEnableAlarm", description = "是否开启告警（0：不开启告警；1：开启告警）", in = ParameterIn.QUERY)})
    @GetMapping("/get-monitor-network-device-list")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.NETWORK_DEVICE, operType = OperateTypeConstants.QUERY, operDesc = "获取网络设备列表")
    public LayUiAdminResultVo getMonitorNetworkDeviceList(@RequestParam(value = "current") Long current,
                                                          @RequestParam(value = "size") Long size,
                                                          @RequestParam(value = "ip", required = false) String ip,
                                                          @RequestParam(value = "isOnline", required = false) String isOnline,
                                                          @RequestParam(value = "insertType", required = false) String insertType,
                                                          @RequestParam(value = "monitorEnv", required = false) String monitorEnv,
                                                          @RequestParam(value = "monitorGroup", required = false) String monitorGroup,
                                                          @RequestParam(value = "networkDeviceType", required = false) String networkDeviceType,
                                                          @RequestParam(value = "networkDeviceSummary", required = false) String networkDeviceSummary,
                                                          @RequestParam(value = "isEnableMonitor", required = false) String isEnableMonitor,
                                                          @RequestParam(value = "isEnableAlarm", required = false) String isEnableAlarm) {
        Page<MonitorNetworkDeviceVo> page = this.monitorNetworkDeviceService.getMonitorNetworkDeviceList(current, size,
                ip, isOnline, insertType, monitorEnv, monitorGroup, networkDeviceType, networkDeviceSummary,
                isEnableMonitor, isEnableAlarm);
        return LayUiAdminResultVo.ok(page);
    }

    /**
     * <p>
     * 删除网络设备
     * </p>
     *
     * @param monitorNetworkDeviceVos 网络设备信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2025/3/18 16:10
     */
    @Operation(summary = "删除网络设备")
    @DeleteMapping("/delete-monitor-network-device")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.NETWORK_DEVICE, operType = OperateTypeConstants.DELETE, operDesc = "删除网络设备")
    public LayUiAdminResultVo deleteMonitorNetworkDevice(@RequestBody List<MonitorNetworkDeviceVo> monitorNetworkDeviceVos) {
        return this.monitorNetworkDeviceService.deleteMonitorNetworkDevice(monitorNetworkDeviceVos);
    }

    /**
     * <p>
     * 访问网络设备详情页面
     * </p>
     *
     * @param id 网络设备主键ID
     * @param ip 网络设备IP
     * @return {@link ModelAndView} 网络设备详情页面
     * @author 皮锋
     * @custom.date 2025/3/19 15:59
     */
    @Operation(summary = "访问网络设备详情页面")
    @Parameters(value = {
            @Parameter(name = "id", description = "网络设备主键ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "ip", description = "网络设备IP", required = true, in = ParameterIn.QUERY)})
    @GetMapping("/network-device-detail")
    @OperateLog(operModule = UiModuleConstants.NETWORK_DEVICE, operType = OperateTypeConstants.PAGE, operDesc = "访问网络设备详情页面")
    public ModelAndView networkDeviceDetail(Long id, String ip) {
        ModelAndView mv = new ModelAndView("networkdevice/network-device-detail");
        mv.addObject("id", id);
        mv.addObject("ip", ip);
        MonitorNetworkDeviceSysVo networkDeviceSysVo = this.monitorNetworkDeviceSysService.getNetworkDeviceSysInfo(ip);
        mv.addObject("networkDeviceSysVo", networkDeviceSysVo);
        List<MonitorNetworkDeviceIfVo> networkDeviceIfVos = this.monitorNetworkDeviceIfService.getNetworkDeviceIfInfo(ip);
        mv.addObject("networkDeviceIfVos", networkDeviceIfVos);
        return mv;
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
     * @custom.date 2025/3/19 21:20
     */
    @Operation(summary = "设置是否开启监控（0：不开启监控；1：开启监控）")
    @Parameters(value = {
            @Parameter(name = "id", description = "主键ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "ip", description = "IP地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "isEnableMonitor", description = "是否开启监控（0：不开启监控；1：开启监控）", in = ParameterIn.QUERY)})
    @PutMapping("/set-is-enable-monitor")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.NETWORK_DEVICE, operType = OperateTypeConstants.UPDATE, operDesc = "设置是否开启监控（0：不开启监控；1：开启监控）")
    public LayUiAdminResultVo setIsEnableMonitor(@RequestParam(value = "id") Long id,
                                                 @RequestParam(value = "ip") String ip,
                                                 @RequestParam(value = "isEnableMonitor") String isEnableMonitor) {
        return this.monitorNetworkDeviceService.setIsEnableMonitor(id, ip, isEnableMonitor);
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
     * @custom.date 2025/3/19 21:37
     */
    @Operation(summary = "设置是否开启告警（0：不开启告警；1：开启告警）")
    @Parameters(value = {
            @Parameter(name = "id", description = "主键ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "ip", description = "IP地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "isEnableAlarm", description = "是否开启告警（0：不开启告警；1：开启告警）", in = ParameterIn.QUERY)})
    @PutMapping("/set-is-enable-alarm")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.NETWORK_DEVICE, operType = OperateTypeConstants.UPDATE, operDesc = "设置是否开启告警（0：不开启告警；1：开启告警）")
    public LayUiAdminResultVo setIsEnableAlarm(@RequestParam(value = "id") Long id,
                                               @RequestParam(value = "ip") String ip,
                                               @RequestParam(value = "isEnableAlarm") String isEnableAlarm) {
        return this.monitorNetworkDeviceService.setIsEnableAlarm(id, ip, isEnableAlarm);
    }

    /**
     * <p>
     * 访问网络设备新增页面
     * </p>
     *
     * @return {@link ModelAndView} 网络设备新增页面
     * @author 皮锋
     * @custom.date 2025/3/30 13:50
     */
    @Operation(summary = "访问网络设备新增页面")
    @GetMapping("/add-monitor-network-device-form")
    public ModelAndView addMonitorNetworkDeviceForm() {
        ModelAndView mv = new ModelAndView("networkdevice/add-network-device");
        // 监控环境列表
        List<String> monitorEnvs = this.monitorEnvService.list().stream().map(MonitorEnv::getEnvName).collect(Collectors.toList());
        // 监控分组列表
        List<MonitorGroup> monitorGroupList = this.monitorGroupService.getMonitorGroupList(MonitorTypeEnums.NETWORK_DEVICE);
        List<String> monitorGroups = monitorGroupList.stream().map(MonitorGroup::getGroupName).collect(Collectors.toList());
        mv.addObject("monitorEnvs", monitorEnvs);
        mv.addObject("monitorGroups", monitorGroups);
        return mv;
    }

    /**
     * <p>
     * 添加网络设备
     * </p>
     *
     * @param monitorNetworkDeviceVo 网络设备
     * @return layUiAdmin响应对象：如果数据库中已经存在，LayUiAdminResultVo.data="exist"；
     * 如果添加成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2025/3/30 14:01
     */
    @Operation(summary = "添加网络设备")
    @PostMapping("/add-monitor-network-device")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.NETWORK_DEVICE, operType = OperateTypeConstants.ADD, operDesc = "添加网络设备")
    public LayUiAdminResultVo addMonitorNetworkDevice(MonitorNetworkDeviceVo monitorNetworkDeviceVo) throws SigarException, IOException {
        // 获取被监控网络设备源IP地址，获取失败则返回null
        String sourceIp = this.monitorNetService.getSourceIp();
        monitorNetworkDeviceVo.setIpSource(sourceIp);
        LayUiAdminResultVo layUiAdminResultVo = this.monitorNetworkDeviceService.addMonitorNetworkDevice(monitorNetworkDeviceVo);
        // 测试网络设备连通性
        this.monitorNetworkDeviceService.testMonitorNetworkDevice(monitorNetworkDeviceVo);
        return layUiAdminResultVo;
    }


    /**
     * <p>
     * 访问网络设备编辑页面
     * </p>
     *
     * @param id 主键ID
     * @param ip IP地址
     * @return {@link ModelAndView} 网络设备编辑页面
     * @author 皮锋
     * @custom.date 2025/3/24 12:37
     */
    @Operation(summary = "访问网络设备编辑页面")
    @Parameters(value = {
            @Parameter(name = "id", description = "主键ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "ip", description = "IP地址", required = true, in = ParameterIn.QUERY)})
    @GetMapping("/edit-monitor-network-device-form")
    public ModelAndView editMonitorNetworkDeviceForm(Long id, String ip) {
        ModelAndView mv = new ModelAndView("networkdevice/edit-network-device");
        mv.addObject("id", id);
        mv.addObject("ip", ip);
        // 监控环境列表
        List<String> monitorEnvs = this.monitorEnvService.list().stream().map(MonitorEnv::getEnvName).collect(Collectors.toList());
        // 监控分组列表
        List<MonitorGroup> monitorGroupList = this.monitorGroupService.getMonitorGroupList(MonitorTypeEnums.NETWORK_DEVICE);
        List<String> monitorGroups = monitorGroupList.stream().map(MonitorGroup::getGroupName).collect(Collectors.toList());
        mv.addObject("monitorEnvs", monitorEnvs);
        mv.addObject("monitorGroups", monitorGroups);
        // 网络设备信息
        MonitorNetworkDeviceVo monitorNetworkDeviceVo = this.monitorNetworkDeviceService.getMonitorNetworkDeviceInfo(id, ip);
        mv.addObject("monitorNetworkDeviceVo", monitorNetworkDeviceVo);
        mv.addObject("env", monitorNetworkDeviceVo.getMonitorEnv());
        mv.addObject("group", monitorNetworkDeviceVo.getMonitorGroup());
        mv.addObject("isEnableMonitor", monitorNetworkDeviceVo.getIsEnableMonitor());
        mv.addObject("isEnableAlarm", monitorNetworkDeviceVo.getIsEnableAlarm());
        return mv;
    }

    /**
     * <p>
     * 编辑网络设备
     * </p>
     *
     * @param monitorNetworkDeviceVo 网络设备信息
     * @return 如果编辑成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2025/3/24 13:45
     */
    @Operation(summary = "编辑网络设备")
    @PutMapping("/edit-monitor-network-device")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.NETWORK_DEVICE, operType = OperateTypeConstants.UPDATE, operDesc = "编辑网络设备")
    public LayUiAdminResultVo editMonitorNetworkDevice(MonitorNetworkDeviceVo monitorNetworkDeviceVo) throws SigarException, IOException {
        // 获取被监控网络设备源IP地址，获取失败则返回null
        String sourceIp = this.monitorNetService.getSourceIp();
        monitorNetworkDeviceVo.setIpSource(sourceIp);
        LayUiAdminResultVo layUiAdminResultVo = this.monitorNetworkDeviceService.editMonitorNetworkDevice(monitorNetworkDeviceVo);
        // 测试网络设备连通性
        this.monitorNetworkDeviceService.testMonitorNetworkDevice(monitorNetworkDeviceVo);
        return layUiAdminResultVo;
    }

    /**
     * <p>
     * 访问清理网络设备监控历史数据表单页面
     * </p>
     *
     * @param id 主键ID
     * @param ip IP地址
     * @return {@link ModelAndView} 清理网络设备监控历史数据表单页面
     * @author 皮锋
     * @custom.date 2025/3/24 20:56
     */
    @Operation(summary = "访问清理网络设备监控历史数据表单页面")
    @Parameters(value = {
            @Parameter(name = "id", description = "主键ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "ip", description = "IP地址", required = true, in = ParameterIn.QUERY)})
    @GetMapping("/network-device-clear")
    public ModelAndView networkDeviceClear(String id, String ip) {
        ModelAndView mv = new ModelAndView("networkdevice/network-device-clear-form");
        mv.addObject("id", id);
        mv.addObject("ip", ip);
        return mv;
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
     * @custom.date 2025/3/24 20:52
     */
    @Operation(summary = "清理网络设备监控历史数据")
    @Parameters(value = {
            @Parameter(name = "id", description = "主键ID", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "ip", description = "IP地址", required = true, in = ParameterIn.QUERY),
            @Parameter(name = "time", description = "时间", required = true, in = ParameterIn.QUERY)})
    @DeleteMapping("/clear-monitor-network-device-history")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.NETWORK_DEVICE, operType = OperateTypeConstants.DELETE, operDesc = "清理网络设备监控历史数据")
    public LayUiAdminResultVo clearMonitorNetworkDeviceHistory(String id, String ip, String time) {
        return this.monitorNetworkDeviceService.clearMonitorNetworkDeviceHistory(id, ip, time);
    }

    /**
     * <p>
     * 访问MIB OID配置页面
     * </p>
     *
     * @param id 主键ID
     * @param ip IP地址
     * @return {@link ModelAndView} MIB OID配置页面
     * @author 皮锋
     * @custom.date 2025/4/9 16:03
     */
    @Operation(summary = "访问MIB OID配置页面")
    @Parameters(value = {
            @Parameter(name = "id", description = "主键ID", in = ParameterIn.QUERY),
            @Parameter(name = "ip", description = "IP地址", in = ParameterIn.QUERY)})
    @GetMapping("/mib-oid-config")
    public ModelAndView mibOidConfig(@RequestParam(value = "id", required = false) Long id,
                                     @RequestParam(value = "ip", required = false) String ip) {
        ModelAndView mv = new ModelAndView("networkdevice/mib-oid-config");
        // 获取MIB OID配置YAML字符串
        String oidYamlStr = this.monitorNetworkDeviceService.getOidYamlStr(id, ip);
        mv.addObject("oid", oidYamlStr);
        return mv;
    }

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
     * @custom.date 2025/4/11 12:35
     */
    @Operation(summary = "测试网络设备连通性")
    @PostMapping("/test-monitor-network-device")
    @PreAuthorize("hasAuthority('超级管理员')")
    @ResponseBody
    @OperateLog(operModule = UiModuleConstants.NETWORK_DEVICE, operType = OperateTypeConstants.TEST, operDesc = "测试网络设备连通性")
    public LayUiAdminResultVo testMonitorNetworkDevice(MonitorNetworkDeviceVo monitorNetworkDeviceVo) throws SigarException, IOException {
        return this.monitorNetworkDeviceService.testMonitorNetworkDevice(monitorNetworkDeviceVo);
    }

}