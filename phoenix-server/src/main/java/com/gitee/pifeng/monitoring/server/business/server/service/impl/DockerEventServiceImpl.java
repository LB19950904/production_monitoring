package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmLevelEnums;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmReasonEnums;
import com.gitee.pifeng.monitoring.common.constant.docker.DockerEventActionConstants;
import com.gitee.pifeng.monitoring.common.constant.docker.DockerEventTypeConstants;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Alarm;
import com.gitee.pifeng.monitoring.common.domain.docker.EventDomain;
import com.gitee.pifeng.monitoring.common.dto.AlarmPackage;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.common.util.DateTimeUtils;
import com.gitee.pifeng.monitoring.common.util.MapUtils;
import com.gitee.pifeng.monitoring.server.business.server.core.MonitoringConfigPropertiesLoader;
import com.gitee.pifeng.monitoring.server.business.server.core.ServerPackageConstructor;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDockerEventDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDocker;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerContainer;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerEvent;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerImage;
import com.gitee.pifeng.monitoring.server.business.server.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 * docker事件信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-07-03
 */
@Service
public class DockerEventServiceImpl extends ServiceImpl<IMonitorDockerEventDao, MonitorDockerEvent> implements IDockerEventService {

    /**
     * 监控配置属性加载器
     */
    @Autowired
    private MonitoringConfigPropertiesLoader monitoringConfigPropertiesLoader;

    /**
     * 服务端包构造器
     */
    @Autowired
    private ServerPackageConstructor serverPackageConstructor;

    /**
     * 告警服务接口
     */
    @Autowired
    private IAlarmService alarmService;

    /**
     * docker信息服务层接口
     */
    @Autowired
    private IDockerService dockerService;

    /**
     * docker容器信息服务接口
     */
    @Autowired
    private IDockerContainerService dockerContainerService;

    /**
     * docker镜像信息服务接口
     */
    @Autowired
    private IDockerImageService dockerImageService;

    /**
     * <p>
     * 把docker事件信息添加到数据库，并发送告警
     * </p>
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/7/3 14:45
     */
    @Override
    @Retryable
    public void operateDockerEvent(DockerPackage dockerPackage) {
        // IP地址
        String ip = dockerPackage.getIp();
        Date date = new Date();
        EventDomain eventDomain = dockerPackage.getDocker().getEventDomain();
        if (eventDomain != null) {
            String status = eventDomain.getEventStatus();
            String action = eventDomain.getEventAction();
            String type = eventDomain.getEventType();
            // 封装数据
            MonitorDockerEvent monitorDockerEvent = new MonitorDockerEvent();
            monitorDockerEvent.setServerIp(ip);
            monitorDockerEvent.setEventStatus(status);
            monitorDockerEvent.setEventId(eventDomain.getEventId());
            monitorDockerEvent.setEventFrom(eventDomain.getEventFrom());
            monitorDockerEvent.setEventType(type);
            monitorDockerEvent.setEventAction(action);
            monitorDockerEvent.setEventAttribute(MapUtils.map2JsonString(eventDomain.getEventAttributes()));
            monitorDockerEvent.setHappenTime(eventDomain.getEventTime());
            monitorDockerEvent.setInsertTime(date);
            monitorDockerEvent.setUpdateTime(date);
            this.save(monitorDockerEvent);
            // 发送告警
            if (StringUtils.isNotBlank(status) && StringUtils.isNotBlank(action) && StringUtils.equals(status, action)) {
                // 处理docker容器事件告警
                if (StringUtils.equals(DockerEventTypeConstants.CONTAINER, type)) {
                    this.dealDockerContainerEventAlarm(monitorDockerEvent);
                }
                // 处理docker镜像事件告警
                if (StringUtils.equals(DockerEventTypeConstants.IMAGE, type)) {
                    this.dealDockerImageEventAlarm(monitorDockerEvent);
                }
            }
        }
    }

    /**
     * <p>
     * 处理docker镜像事件告警
     * </p>
     *
     * @param monitorDockerEvent docker事件信息
     * @author 皮锋
     * @custom.date 2022/7/10 14:37
     */
    @Override
    public void dealDockerImageEventAlarm(MonitorDockerEvent monitorDockerEvent) {
        String action = monitorDockerEvent.getEventAction();
        try {
            // 删除镜像
            if (StringUtils.equals(DockerEventActionConstants.DELETE, action)) {
                this.sendAlarmInfo("删除docker镜像", AlarmLevelEnums.FATAL, monitorDockerEvent);
            }
        } catch (Exception e) {
            log.error("docker事件告警异常！", e);
        }
    }

    /**
     * <p>
     * 处理docker容器事件告警
     * </p>
     *
     * @param monitorDockerEvent docker事件信息
     * @author 皮锋
     * @custom.date 2022/7/10 14:30
     */
    @Override
    public void dealDockerContainerEventAlarm(MonitorDockerEvent monitorDockerEvent) {
        String action = monitorDockerEvent.getEventAction();
        try {
            // 创建容器
            if (StringUtils.equals(DockerEventActionConstants.CREATE, action)) {
                this.sendAlarmInfo("创建docker容器", AlarmLevelEnums.INFO, monitorDockerEvent);
            }
            // 销毁容器
            if (StringUtils.equals(DockerEventActionConstants.DESTROY, action)) {
                this.sendAlarmInfo("销毁docker容器", AlarmLevelEnums.INFO, monitorDockerEvent);
            }
            // 启动容器
            if (StringUtils.equals(DockerEventActionConstants.START, action)) {
                this.sendAlarmInfo("启动docker容器", AlarmLevelEnums.INFO, monitorDockerEvent);
            }
            // 停止容器
            if (StringUtils.equals(DockerEventActionConstants.STOP, action)) {
                this.sendAlarmInfo("停止docker容器", AlarmLevelEnums.FATAL, monitorDockerEvent);
            }
            // 重启容器
            if (StringUtils.equals(DockerEventActionConstants.RESTART, action)) {
                this.sendAlarmInfo("重启docker容器", AlarmLevelEnums.INFO, monitorDockerEvent);
            }
        } catch (Exception e) {
            log.error("docker事件告警异常！", e);
        }
    }

    /**
     * <p>
     * 发送告警信息
     * </p>
     *
     * @param title          告警标题
     * @param alarmLevelEnum 告警级别
     * @param event          docker事件信息
     * @throws NetException 自定义获取网络信息异常
     * @author 皮锋
     * @custom.date 2022/7/9 22:13
     */
    private void sendAlarmInfo(String title, AlarmLevelEnums alarmLevelEnum, MonitorDockerEvent event)
            throws NetException {
        // 是否监控docker服务
        boolean isEnable = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getDockerProperties().isEnable();
        // 不需要监控docker服务
        if (!isEnable) {
            return;
        }
        // 服务器IP
        String serverIp = event.getServerIp();
        // 事件ID
        String eventId = event.getEventId();
        // 事件类型
        String eventType = event.getEventType();
        // 事件属性
        String eventAttribute = event.getEventAttribute();
        // 监控环境
        String monitorEnv = null;
        // 监控分组
        String monitorGroup = null;
        // 根据服务器IP获取docker服务信息
        MonitorDocker monitorDocker = this.dockerService.getMonitorDockerByServerIp(serverIp);
        if (monitorDocker != null) {
            monitorEnv = monitorDocker.getMonitorEnv();
            monitorGroup = monitorDocker.getMonitorGroup();
        }
        StringBuilder builder = new StringBuilder();
        builder.append("服务器IP：").append(serverIp);
        // 容器事件
        if (StringUtils.equals(DockerEventTypeConstants.CONTAINER, eventType)) {
            // 获取容器信息
            LambdaQueryWrapper<MonitorDockerContainer> dockerContainerLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dockerContainerLambdaQueryWrapper.eq(MonitorDockerContainer::getContainerId, eventId);
            dockerContainerLambdaQueryWrapper.eq(MonitorDockerContainer::getServerIp, serverIp);
            MonitorDockerContainer dockerContainer = this.dockerContainerService.getOne(dockerContainerLambdaQueryWrapper);
            builder.append("，<br>容器ID：").append(eventId);
            if (dockerContainer != null) {
                builder.append("，<br>容器名：").append(dockerContainer.getContainerName());
            }
        }
        // 镜像事件
        if (StringUtils.equals(DockerEventTypeConstants.IMAGE, eventType)) {
            eventId = StringUtils.replace(eventId, "sha256:", "");
            // 获取镜像信息
            LambdaQueryWrapper<MonitorDockerImage> dockerImageLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dockerImageLambdaQueryWrapper.eq(MonitorDockerImage::getImageId, eventId);
            dockerImageLambdaQueryWrapper.eq(MonitorDockerImage::getServerIp, serverIp);
            MonitorDockerImage dockerImage = this.dockerImageService.getOne(dockerImageLambdaQueryWrapper);
            builder.append("，<br>镜像ID：").append(eventId);
            if (dockerImage != null) {
                builder.append("，<br>镜像名：").append(dockerImage.getImageRepository()).append(":").append(dockerImage.getImageTag());
            }
        }
        if (StringUtils.isNotBlank(eventAttribute)) {
            builder.append("，<br>属性：").append(JSON.parse(eventAttribute));
        }
        if (StringUtils.isNotBlank(monitorEnv)) {
            builder.append("，<br>环境：").append(monitorEnv);
        }
        if (StringUtils.isNotBlank(monitorGroup)) {
            builder.append("，<br>分组：").append(monitorGroup);
        }
        builder.append("，<br>时间：").append(DateTimeUtils.dateToString(event.getHappenTime()));
        Alarm alarm = Alarm.builder()
                .title(title)
                .msg(builder.toString())
                .alarmLevel(alarmLevelEnum)
                .alarmReason(AlarmReasonEnums.IGNORE)
                .monitorType(MonitorTypeEnums.DOCKER)
                .build();
        AlarmPackage alarmPackage = this.serverPackageConstructor.structureAlarmPackage(alarm);
        this.alarmService.dealAlarmPackage(alarmPackage);
    }

}
