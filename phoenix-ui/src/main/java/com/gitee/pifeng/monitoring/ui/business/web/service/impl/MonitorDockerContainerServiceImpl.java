package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.docker.DockerEventActionConstants;
import com.gitee.pifeng.monitoring.common.constant.docker.DockerEventTypeConstants;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Command;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
import com.gitee.pifeng.monitoring.plug.core.Sender;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorDockerContainerDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerContainer;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerContainerService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerContainerVo;
import com.gitee.pifeng.monitoring.ui.constant.UrlConstants;
import com.gitee.pifeng.monitoring.ui.constant.WebResponseConstants;
import com.gitee.pifeng.monitoring.ui.core.UiPackageConstructor;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * docker容器信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-25
 */
@Service
public class MonitorDockerContainerServiceImpl extends ServiceImpl<IMonitorDockerContainerDao, MonitorDockerContainer>
        implements IMonitorDockerContainerService {

    /**
     * UI端包构造器
     */
    @Autowired
    private UiPackageConstructor uiPackageConstructor;

    /**
     * <p>
     * 获取docker容器列表
     * </p>
     *
     * @param current       当前页
     * @param size          每页显示条数
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @param imageName     镜像名
     * @param status        容器状态
     * @param monitorEnv    监控环境
     * @param monitorGroup  监控分组
     * @return docker容器列表
     * @author 皮锋
     * @custom.date 2022/8/16 17:29
     */
    @Override
    public Page<MonitorDockerContainerVo> getMonitorDockerContainerList(Long current, Long size, String serverIp,
                                                                        String containerName, String imageName, String status,
                                                                        String monitorEnv, String monitorGroup) {
        Page<MonitorDockerContainerVo> page = new Page<>(current, size);
        Map<String, Object> params = new HashMap<>(16);
        params.put("serverIp", serverIp);
        params.put("status", status);
        params.put("monitorEnv", monitorEnv);
        params.put("monitorGroup", monitorGroup);
        params.put("containerName", containerName);
        params.put("imageName", imageName);
        return this.baseMapper.getMonitorDockerContainerList(page, params);
    }

    /**
     * <p>
     * 获取docker容器信息
     * </p>
     *
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @return docker容器信息表现层对象
     * @author 皮锋
     * @custom.date 2022/8/21 16:42
     */
    @Override
    public MonitorDockerContainerVo getMonitorDockerContainerInfo(String serverIp, String containerName) {
        return this.baseMapper.getMonitorDockerContainerInfo(serverIp, containerName);
    }

    /**
     * <p>
     * 删除docker容器
     * </p>
     *
     * @param monitorDockerContainerVos docker容器信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    @Retryable
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public LayUiAdminResultVo deleteMonitorDockerContainer(List<MonitorDockerContainerVo> monitorDockerContainerVos) {
        List<String> serverIps = Lists.newArrayList();
        List<String> containerNames = Lists.newArrayList();
        for (MonitorDockerContainerVo monitorDockerContainerVo : monitorDockerContainerVos) {
            serverIps.add(monitorDockerContainerVo.getServerIp());
            containerNames.add(monitorDockerContainerVo.getContainerName());
        }
        LambdaUpdateWrapper<MonitorDockerContainer> monitorDockerContainerLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        monitorDockerContainerLambdaUpdateWrapper.in(MonitorDockerContainer::getServerIp, serverIps);
        monitorDockerContainerLambdaUpdateWrapper.in(MonitorDockerContainer::getContainerName, containerNames);
        this.baseMapper.delete(monitorDockerContainerLambdaUpdateWrapper);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

    /**
     * <p>
     * 启动docker容器
     * </p>
     *
     * @param id          docker主键ID
     * @param containerId 容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 15:49
     */
    @Override
    public LayUiAdminResultVo startDockerContainer(Long id, String containerId) throws IOException {
        Command command = Command.builder()
                .monitorTypeEnum(MonitorTypeEnums.DOCKER)
                .commandType(DockerEventTypeConstants.CONTAINER)
                .commandAction(DockerEventActionConstants.START)
                .commandTarget(String.valueOf(id))
                .commandValue(containerId)
                .build();
        CommandPackage commandPackage = this.uiPackageConstructor.structureCommandPackage(command);
        // 发送命令包到服务端
        String resultStr = Sender.send(UrlConstants.COMMAND_URL, commandPackage.toJsonString());
        BaseResponsePackage baseResponsePackage = JSON.parseObject(resultStr, BaseResponsePackage.class);
        Result result = baseResponsePackage.getResult();
        boolean success = result.isSuccess();
        String msg = result.getMsg();
        if (success) {
            return LayUiAdminResultVo.ok(msg);
        }
        return LayUiAdminResultVo.fail(msg);
    }

    /**
     * <p>
     * 停止docker容器
     * </p>
     *
     * @param id          docker主键ID
     * @param containerId 容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 15:49
     */
    @Override
    public LayUiAdminResultVo stopDockerContainer(Long id, String containerId) throws IOException {
        Command command = Command.builder()
                .monitorTypeEnum(MonitorTypeEnums.DOCKER)
                .commandType(DockerEventTypeConstants.CONTAINER)
                .commandAction(DockerEventActionConstants.STOP)
                .commandTarget(String.valueOf(id))
                .commandValue(containerId)
                .build();
        CommandPackage commandPackage = this.uiPackageConstructor.structureCommandPackage(command);
        // 发送命令包到服务端
        String resultStr = Sender.send(UrlConstants.COMMAND_URL, commandPackage.toJsonString());
        BaseResponsePackage baseResponsePackage = JSON.parseObject(resultStr, BaseResponsePackage.class);
        Result result = baseResponsePackage.getResult();
        boolean success = result.isSuccess();
        String msg = result.getMsg();
        if (success) {
            return LayUiAdminResultVo.ok(msg);
        }
        return LayUiAdminResultVo.fail(msg);
    }

    /**
     * <p>
     * 重启docker容器
     * </p>
     *
     * @param id          docker主键ID
     * @param containerId 容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 15:49
     */
    @Override
    public LayUiAdminResultVo restartDockerContainer(Long id, String containerId) throws IOException {
        Command command = Command.builder()
                .monitorTypeEnum(MonitorTypeEnums.DOCKER)
                .commandType(DockerEventTypeConstants.CONTAINER)
                .commandAction(DockerEventActionConstants.RESTART)
                .commandTarget(String.valueOf(id))
                .commandValue(containerId)
                .build();
        CommandPackage commandPackage = this.uiPackageConstructor.structureCommandPackage(command);
        // 发送命令包到服务端
        String resultStr = Sender.send(UrlConstants.COMMAND_URL, commandPackage.toJsonString());
        BaseResponsePackage baseResponsePackage = JSON.parseObject(resultStr, BaseResponsePackage.class);
        Result result = baseResponsePackage.getResult();
        boolean success = result.isSuccess();
        String msg = result.getMsg();
        if (success) {
            return LayUiAdminResultVo.ok(msg);
        }
        return LayUiAdminResultVo.fail(msg);
    }

    /**
     * <p>
     * 销毁docker容器
     * </p>
     *
     * @param id          docker主键ID
     * @param containerId 容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 15:49
     */
    @Override
    public LayUiAdminResultVo destroyDockerContainer(Long id, String containerId) throws IOException {
        Command command = Command.builder()
                .monitorTypeEnum(MonitorTypeEnums.DOCKER)
                .commandType(DockerEventTypeConstants.CONTAINER)
                .commandAction(DockerEventActionConstants.DESTROY)
                .commandTarget(String.valueOf(id))
                .commandValue(containerId)
                .build();
        CommandPackage commandPackage = this.uiPackageConstructor.structureCommandPackage(command);
        // 发送命令包到服务端
        String resultStr = Sender.send(UrlConstants.COMMAND_URL, commandPackage.toJsonString());
        BaseResponsePackage baseResponsePackage = JSON.parseObject(resultStr, BaseResponsePackage.class);
        Result result = baseResponsePackage.getResult();
        boolean success = result.isSuccess();
        String msg = result.getMsg();
        if (success) {
            return LayUiAdminResultVo.ok(msg);
        }
        return LayUiAdminResultVo.fail(msg);
    }

}
