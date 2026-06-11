package com.gitee.pifeng.monitoring.agent.util.docker;

import cn.hutool.core.date.DateUtil;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.domain.docker.ContainerDomain;
import com.gitee.pifeng.monitoring.common.domain.docker.ContainerPortDomain;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ContainerPort;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * docker容器工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/23 14:05
 */
public class DockerContainerUtils {

    /**
     * <p>
     * 获取docker容器信息
     * </p>
     *
     * @param dockerClient docker客户端
     * @param showAll      是否显示所有容器
     * @return {@link ContainerDomain}
     * @author 皮锋
     * @custom.date 2022/6/24 12:40
     */
    public static ContainerDomain getContainerInfo(DockerClient dockerClient, boolean showAll) {
        List<Container> containers = DockerClientUtils.getContainers(dockerClient, showAll);
        List<ContainerDomain.ContainerInfoDomain> containerInfoDomainList = Lists.newArrayList();
        for (Container container : containers) {
            String id = container.getId();
            String image = container.getImage();
            String imageId = container.getImageId();
            String command = container.getCommand();
            Date created = DateUtil.date(container.getCreated() * 1000);
            String status = container.getStatus();
            String[] names = container.getNames();
            Map<String, String> labels = container.getLabels();
            ContainerPort[] ports = container.getPorts();
            List<ContainerPortDomain> containerPortDomains = Lists.newArrayList();
            for (ContainerPort containerPort : ports) {
                ContainerPortDomain containerPortDomain = new ContainerPortDomain();
                containerPortDomain.setIp(containerPort.getIp());
                containerPortDomain.setPrivatePort(containerPort.getPrivatePort());
                containerPortDomain.setPublicPort(containerPort.getPublicPort());
                containerPortDomain.setType(containerPort.getType());
                containerPortDomains.add(containerPortDomain);
            }
            ContainerDomain.ContainerInfoDomain containerInfoDomain = new ContainerDomain.ContainerInfoDomain();
            containerInfoDomain.setContainerId(id);
            containerInfoDomain.setImageId(imageId);
            containerInfoDomain.setImageName(image);
            containerInfoDomain.setContainerCommand(command);
            containerInfoDomain.setContainerCreated(created);
            containerInfoDomain.setContainerStatus(status);
            containerInfoDomain.setContainerPorts(containerPortDomains);
            containerInfoDomain.setContainerNames(names);
            containerInfoDomain.setContainerLabels(labels);
            containerInfoDomainList.add(containerInfoDomain);
        }
        return ContainerDomain.builder().containerNum(containers.size()).containerInfoDomainList(containerInfoDomainList).build();
    }

    /**
     * <p>
     * 启动docker容器
     * </p>
     *
     * @param dockerClient docker客户端
     * @param containerId  容器ID
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2022/9/20 17:12
     */
    public static Result startContainer(DockerClient dockerClient, @Nonnull String containerId) {
        return DockerClientUtils.startContainer(dockerClient, containerId);
    }

    /**
     * <p>
     * 停止docker容器
     * </p>
     *
     * @param dockerClient docker客户端
     * @param containerId  容器ID
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2022/9/20 17:13
     */
    public static Result stopContainer(DockerClient dockerClient, @Nonnull String containerId) {
        return DockerClientUtils.stopContainer(dockerClient, containerId);
    }

    /**
     * <p>
     * 重启docker容器
     * </p>
     *
     * @param dockerClient docker客户端
     * @param containerId  容器ID
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2022/9/20 17:15
     */
    public static Result restartContainer(DockerClient dockerClient, @Nonnull String containerId) {
        return DockerClientUtils.restartContainer(dockerClient, containerId);
    }

    /**
     * <p>
     * 删除docker容器
     * </p>
     *
     * @param dockerClient docker客户端
     * @param containerId  容器ID
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2022/9/20 17:17
     */
    public static Result removeContainer(DockerClient dockerClient, @Nonnull String containerId) {
        return DockerClientUtils.removeContainer(dockerClient, containerId);
    }

}
