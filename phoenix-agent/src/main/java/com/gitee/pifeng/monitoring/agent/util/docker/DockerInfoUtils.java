package com.gitee.pifeng.monitoring.agent.util.docker;

import com.gitee.pifeng.monitoring.common.domain.docker.InfoDomain;
import com.gitee.pifeng.monitoring.common.util.MapUtils;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Info;

/**
 * <p>
 * docker系统信息工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/23 14:12
 */
public class DockerInfoUtils {

    /**
     * <p>
     * 获取docker系统信息
     * </p>
     *
     * @param dockerClient docker客户端
     * @return {@link InfoDomain}
     * @author 皮锋
     * @custom.date 2022/7/4 15:07
     */
    public static InfoDomain getInfo(DockerClient dockerClient) {
        Info info = DockerClientUtils.getInfo(dockerClient);
        InfoDomain infoDomain = new InfoDomain();
        infoDomain.setArchitecture(info.getArchitecture());
        infoDomain.setContainers(info.getContainers());
        infoDomain.setContainersStopped(info.getContainersStopped());
        infoDomain.setContainersPaused(info.getContainersPaused());
        infoDomain.setContainersRunning(info.getContainersRunning());
        infoDomain.setDebug(info.getDebug());
        infoDomain.setDockerRootDir(info.getDockerRootDir());
        infoDomain.setImages(info.getImages());
        infoDomain.setKernelVersion(info.getKernelVersion());
        infoDomain.setMemoryLimit(info.getMemoryLimit());
        infoDomain.setMemTotal(info.getMemTotal());
        infoDomain.setServerVersion(info.getServerVersion());
        infoDomain.setCpus(info.getNCPU());
        infoDomain.setEventsListeners(info.getNEventsListener());
        infoDomain.setRawValues(MapUtils.map2JsonString(info.getRawValues()));
        return infoDomain;
    }

}
