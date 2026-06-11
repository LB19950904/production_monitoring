package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.domain.docker.ContainerDomain;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDockerContainerDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerContainer;
import com.gitee.pifeng.monitoring.server.business.server.service.IDockerContainerService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * docker容器信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/25 20:49
 */
@Service
public class DockerContainerServiceImpl extends ServiceImpl<IMonitorDockerContainerDao, MonitorDockerContainer> implements IDockerContainerService {

    /**
     * <p>
     * 把docker容器信息添加或更新到数据库
     * </p>
     * 此处不加事务，不加事务能提高并发性能，并且对数据的一致性要求也没那么高
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/6/25 20:53
     */
    // @Transactional(rollbackFor = Throwable.class)
    @Override
    @Retryable
    public void operateDockerContainer(DockerPackage dockerPackage) {
        // IP地址
        String ip = dockerPackage.getIp();
        Date date = new Date();
        ContainerDomain containerDomain = dockerPackage.getDocker().getContainerDomain();
        if (containerDomain != null) {
            List<ContainerDomain.ContainerInfoDomain> containerInfoDomainList = containerDomain.getContainerInfoDomainList();
            List<MonitorDockerContainer> insertContainerList = Lists.newArrayList();
            for (ContainerDomain.ContainerInfoDomain containerInfoDomain : containerInfoDomainList) {
                MonitorDockerContainer monitorDockerContainer = new MonitorDockerContainer();
                monitorDockerContainer.setContainerId(containerInfoDomain.getContainerId());
                monitorDockerContainer.setContainerName(containerInfoDomain.containerNames2String());
                monitorDockerContainer.setContainerPorts(containerInfoDomain.containerPorts2String());
                monitorDockerContainer.setContainerLabels(containerInfoDomain.containerLabels2String());
                monitorDockerContainer.setImageId(StringUtils.replace(containerInfoDomain.getImageId(), "sha256:", ""));
                monitorDockerContainer.setImageName(containerInfoDomain.getImageName());
                monitorDockerContainer.setCommand(containerInfoDomain.getContainerCommand());
                monitorDockerContainer.setCreated(containerInfoDomain.getContainerCreated());
                monitorDockerContainer.setStatus(containerInfoDomain.getContainerStatus());
                monitorDockerContainer.setServerIp(ip);
                monitorDockerContainer.setInsertTime(date);
                monitorDockerContainer.setUpdateTime(date);
                insertContainerList.add(monitorDockerContainer);
            }
            // 先删除，后添加
            LambdaUpdateWrapper<MonitorDockerContainer> dockerContainerLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            dockerContainerLambdaUpdateWrapper.eq(MonitorDockerContainer::getServerIp, ip);
            ((IDockerContainerService) AopContext.currentProxy()).remove(dockerContainerLambdaUpdateWrapper);
            ((IDockerContainerService) AopContext.currentProxy()).saveBatch(insertContainerList);
        }
    }

}
