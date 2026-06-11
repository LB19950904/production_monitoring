package com.gitee.pifeng.monitoring.agent.business.agent.thread;

import com.gitee.pifeng.monitoring.agent.business.agent.core.docker.DockerCentralController;
import com.gitee.pifeng.monitoring.agent.core.MethodExecuteHandler;
import com.gitee.pifeng.monitoring.common.domain.Docker;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 发送Docker信息线程
 * </p>
 *
 * @author YangRui
 * @custom.date 2022/6/23 16:38
 */
@Slf4j
public class DockerThread implements Runnable {

    /**
     * <p>
     * 构建+发送docker信息包
     * </p>
     *
     * @author YangRui
     * @custom.date 2022/6/23 17:30
     */
    @Override
    public void run() {
        try {
            DockerCentralController controller = DockerCentralController.getInstance();
            // 获取docker信息
            Docker docker = controller.getDockerInfo();
            // 把 Docker 信息封装成 Docker 数据包，并发送到服务端
            MethodExecuteHandler.send(docker, Docker.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
