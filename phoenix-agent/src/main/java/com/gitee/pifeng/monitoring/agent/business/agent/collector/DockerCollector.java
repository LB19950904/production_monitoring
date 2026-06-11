package com.gitee.pifeng.monitoring.agent.business.agent.collector;

import com.gitee.pifeng.monitoring.agent.business.agent.core.docker.DockerCentralController;
import com.gitee.pifeng.monitoring.agent.business.agent.thread.DockerThread;
import com.gitee.pifeng.monitoring.agent.util.docker.DockerClientUtils;
import com.gitee.pifeng.monitoring.common.property.client.MonitoringDockerInfoProperties;
import com.gitee.pifeng.monitoring.common.property.client.MonitoringProperties;
import com.gitee.pifeng.monitoring.common.threadpool.MonitoredScheduledThreadPoolExecutor;
import com.gitee.pifeng.monitoring.plug.core.ConfigLoader;
import com.github.dockerjava.api.DockerClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 在项目启动后，定时收集docker信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/24 16:50
 */
@Slf4j
@Component
public class DockerCollector implements CommandLineRunner {

    /**
     * 监控属性
     */
    @Autowired
    private MonitoringProperties monitoringProperties;

    /**
     * 线程池
     */
    @Lazy
    @Autowired
    @Qualifier("dockerMonitorScheduledThreadPoolExecutor")
    private MonitoredScheduledThreadPoolExecutor dockerMonitorScheduledThreadPoolExecutor;

    /**
     * <p>
     * 如果监控配置文件中配置了发送docker信息，则延迟30秒启动定时任务，定时发送docker信息包，
     * 定时任务的执行频率一般为监控配置文件中配置的docker信息包发送频率，如果监控配置文件中没有配置docker信息包的发送频率，
     * 则由类{@link ConfigLoader}提供默认的发送docker信息频率。
     * </p>
     *
     * @author 皮锋
     * @custom.date 2022/6/23 16:25
     */
    @Override
    public void run(String... args) {
        // docker信息属性
        MonitoringDockerInfoProperties dockerInfoProperties = this.monitoringProperties.getDockerInfo();
        // 是否监控docker信息
        boolean dockerInfoEnable = dockerInfoProperties.getEnable();
        if (dockerInfoEnable) {
            // 第一步：获取docker客户端
            DockerClient dockerClient = getDockerClient(dockerInfoProperties);
            // 第二步：初始化docker中央控制器
            DockerCentralController dockerCentralController = DockerCentralController.getInstance().init(dockerClient);
            // 第三步：启动所有回调
            startAllDockerCallback(dockerCentralController);
            // 第四步：定时收集docker信息，把信息发送到监控服务端
            // 发送docker信息的频率
            long rate = this.monitoringProperties.getDockerInfo().getRate();
            this.dockerMonitorScheduledThreadPoolExecutor.scheduleAtFixedRate(new DockerThread(), 30, rate, TimeUnit.SECONDS);
        }
    }

    /**
     * <p>
     * 获取docker客户端
     * </p>
     *
     * @param dockerInfoProperties docker信息属性
     * @return Docker客户端
     * @author 皮锋
     * @custom.date 2022/6/24 9:33
     */
    private static DockerClient getDockerClient(MonitoringDockerInfoProperties dockerInfoProperties) {
        String host = dockerInfoProperties.getHost();
        boolean tlsVerify = dockerInfoProperties.getTlsVerify();
        String certPath = dockerInfoProperties.getCertPath();
        String config = dockerInfoProperties.getConfig();
        String apiVersion = dockerInfoProperties.getApiVersion();
        String registryUrl = dockerInfoProperties.getRegistryUrl();
        String registryUsername = dockerInfoProperties.getRegistryUsername();
        String registryPassword = dockerInfoProperties.getRegistryPassword();
        String registryEmail = dockerInfoProperties.getRegistryEmail();
        return DockerClientUtils.connectDocker(host, tlsVerify, certPath, config, apiVersion, registryUrl, registryUsername, registryPassword, registryEmail);
    }

    /**
     * <p>
     * 启动所有回调
     * </p>
     *
     * @param dockerCentralController docker中央控制器
     * @author 皮锋
     * @custom.date 2022/6/24 11:03
     */
    private static void startAllDockerCallback(DockerCentralController dockerCentralController) {
        // 启动docker事件监听回调
        dockerCentralController.startDockerEventCallback();
    }

}
