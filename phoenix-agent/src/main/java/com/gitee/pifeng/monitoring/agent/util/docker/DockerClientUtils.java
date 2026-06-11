package com.gitee.pifeng.monitoring.agent.util.docker;

import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.List;

/**
 * <p>
 * docker客户端工具类
 * </p>
 *
 * @author YangRui
 * @custom.date 2022/6/23 17:00
 */
public class DockerClientUtils {

    /**
     * <p>
     * 连接docker客户端
     * </p>
     *
     * @param host             docker主机
     * @param tlsVerify        启用/禁用TLS验证
     * @param certPath         验证所需证书的路径
     * @param config           其他docker配置文件的路径
     * @param apiVersion       API版本
     * @param registryUrl      注册地址
     * @param registryUsername 注册用户名
     * @param registryPassword 注册密码
     * @param registryEmail    注册电子邮箱
     * @return docker客户端-{@link DockerClient}
     * @author YangRui
     * @custom.date 2022/6/23 17:05
     */
    public static DockerClient connectDocker(String host, boolean tlsVerify, String certPath, String config,
                                             String apiVersion, String registryUrl, String registryUsername,
                                             String registryPassword, String registryEmail) {
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(host)
                .withDockerTlsVerify(tlsVerify);
        if (StringUtils.isNotBlank(certPath)) {
            builder.withDockerCertPath(certPath);
        }
        if (StringUtils.isNotBlank(config)) {
            builder.withDockerConfig(config);
        }
        if (StringUtils.isNotBlank(apiVersion)) {
            builder.withApiVersion(apiVersion);
        }
        if (StringUtils.isNotBlank(registryUrl)) {
            builder.withRegistryUrl(registryUrl);
        }
        if (StringUtils.isNotBlank(registryUsername)) {
            builder.withRegistryUsername(registryUsername);
        }
        if (StringUtils.isNotBlank(registryPassword)) {
            builder.withRegistryPassword(registryPassword);
        }
        if (StringUtils.isNotBlank(registryEmail)) {
            builder.withRegistryEmail(registryEmail);
        }
        DefaultDockerClientConfig dockerClientConfig = builder.build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .maxConnections(100)
                .connectionTimeout(Duration.ofSeconds(300))
                .responseTimeout(Duration.ofSeconds(450))
                .build();
        return DockerClientImpl.getInstance(dockerClientConfig, httpClient);
    }

    /**
     * <p>
     * 获取docker容器列表
     * </p>
     *
     * @param dockerClient docker客户端
     * @param showAll      是否显示所有容器
     * @return docker容器列表
     * @author YangRui
     * @custom.date 2022/6/23 17:07
     */
    public static List<Container> getContainers(DockerClient dockerClient, boolean showAll) {
        return dockerClient.listContainersCmd().withShowAll(showAll).exec();
    }

    /**
     * <p>
     * 获取docker镜像列表
     * </p>
     *
     * @param dockerClient docker客户端
     * @param showAll      是否显示所有镜像
     * @return docker镜像列表
     * @author 皮锋
     * @custom.date 2022/6/24 22:43
     */
    public static List<Image> getImages(DockerClient dockerClient, boolean showAll) {
        return dockerClient.listImagesCmd().withShowAll(showAll).exec();
    }

    /**
     * <p>
     * 获取docker系统信息
     * </p>
     *
     * @param dockerClient docker客户端
     * @return docker系统信息
     * @author 皮锋
     * @custom.date 2022/7/4 14:49
     */
    public static Info getInfo(DockerClient dockerClient) {
        return dockerClient.infoCmd().exec();
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
        try {
            dockerClient.startContainerCmd(containerId).exec();
            return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
        } catch (Exception e) {
            return Result.builder().isSuccess(false).msg(e.getMessage()).build();
        }
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
        try {
            dockerClient.stopContainerCmd(containerId).exec();
            return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
        } catch (Exception e) {
            return Result.builder().isSuccess(false).msg(e.getMessage()).build();
        }
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
        try {
            dockerClient.restartContainerCmd(containerId).exec();
            return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
        } catch (Exception e) {
            return Result.builder().isSuccess(false).msg(e.getMessage()).build();
        }
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
        try {
            dockerClient.removeContainerCmd(containerId).exec();
            return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
        } catch (Exception e) {
            return Result.builder().isSuccess(false).msg(e.getMessage()).build();
        }
    }

    /**
     * <p>
     * 删除docker镜像
     * </p>
     *
     * @param dockerClient docker客户端
     * @param imageId      镜像ID
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2022/9/20 17:26
     */
    public static Result removeImage(DockerClient dockerClient, @Nonnull String imageId) {
        try {
            dockerClient.removeImageCmd(imageId).exec();
            return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
        } catch (Exception e) {
            return Result.builder().isSuccess(false).msg(e.getMessage()).build();
        }
    }

}