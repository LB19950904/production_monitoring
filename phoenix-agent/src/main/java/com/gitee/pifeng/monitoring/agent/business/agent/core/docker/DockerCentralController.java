package com.gitee.pifeng.monitoring.agent.business.agent.core.docker;

import com.gitee.pifeng.monitoring.agent.business.agent.thread.DockerThread;
import com.gitee.pifeng.monitoring.agent.util.docker.DockerContainerUtils;
import com.gitee.pifeng.monitoring.agent.util.docker.DockerImageUtils;
import com.gitee.pifeng.monitoring.agent.util.docker.DockerInfoUtils;
import com.gitee.pifeng.monitoring.agent.util.docker.DockerStatsUtils;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.constant.docker.DockerEventActionConstants;
import com.gitee.pifeng.monitoring.common.constant.docker.DockerEventTypeConstants;
import com.gitee.pifeng.monitoring.common.domain.Docker;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.threadpool.MonitoredThreadPoolExecutor;
import com.gitee.pifeng.monitoring.common.threadpool.ThreadPool;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * <p>
 * docker中央控制器
 * </p>
 *
 * @author YangRui
 * @custom.date 2022/6/24 10:29
 */
public class DockerCentralController {

    /**
     * docker客户端
     */
    private DockerClient dockerClient;

    /**
     * 构造方法私有化
     */
    private DockerCentralController() {
    }

    /**
     * 枚举类型是线程安全的，并且只会装载一次
     */
    private enum Singleton {

        /**
         * 实例
         */
        INSTANCE;

        private final DockerCentralController instance;

        Singleton() {
            instance = new DockerCentralController();
        }

        /**
         * <p>
         * 创建实例
         * </p>
         *
         * @return {@link DockerCentralController}
         * @author 皮锋
         * @custom.date 2020/8/22 9:11
         */
        private DockerCentralController getInstance() {
            return instance;
        }
    }

    /**
     * <p>
     * 创建实例
     * </p>
     *
     * @return {@link DockerCentralController}
     * @author 皮锋
     * @custom.date 2020/8/22 9:11
     */
    public static DockerCentralController getInstance() {
        return DockerCentralController.Singleton.INSTANCE.getInstance();
    }

    /**
     * <p>
     * 初始化
     * </p>
     *
     * @param dockerClient docker客户端
     * @return {@link DockerCentralController}
     * @author 皮锋
     * @custom.date 2022/6/24 10:55
     */
    public DockerCentralController init(DockerClient dockerClient) {
        this.dockerClient = dockerClient;
        return this;
    }

    /**
     * <p>
     * 启动docker事件监听回调
     * </p>
     *
     * @author YangRui
     * @custom.date 2022/6/24 10:41
     */
    public void startDockerEventCallback() {
        this.startDockerEventCallback(new DockerEventCallback());
    }

    /**
     * <p>
     * 启动docker事件监听回调
     * </p>
     *
     * @param resultCallback docker事件监听回调
     * @author YangRui
     * @custom.date 2022/6/24 10:41
     */
    public void startDockerEventCallback(ResultCallback<Event> resultCallback) {
        this.dockerClient.eventsCmd().exec(resultCallback);
    }

    /**
     * <p>
     * 获取docker信息
     * </p>
     *
     * @return {@link Docker}
     * @author 皮锋
     * @custom.date 2022/6/24 12:43
     */
    public Docker getDockerInfo() {
        return Docker.builder()
                .infodomain(DockerInfoUtils.getInfo(this.dockerClient))
                .containerDomain(DockerContainerUtils.getContainerInfo(this.dockerClient, true))
                .imageDomain(DockerImageUtils.getImageInfo(this.dockerClient, false))
                .statsDomain(DockerStatsUtils.getIntervalStatsInfo())
                .build();
    }

    /**
     * <p>
     * 执行docker命令
     * </p>
     *
     * @param commandType   命令类型
     * @param commandAction 命令动作
     * @param commandValue  命令值
     * @return {@link Result}
     * @throws ExecutionException   线程执行异常
     * @throws InterruptedException 线程中断异常
     * @author 皮锋
     * @custom.date 2022/9/21 10:47
     */
    public Result executeDockerCommand(String commandType, String commandAction, @Nonnull String commandValue)
            throws ExecutionException, InterruptedException {
        Result result = Result.builder().isSuccess(false).msg(ResultMsgConstants.FAILURE).build();
        // 镜像
        if (StringUtils.equals(commandType, DockerEventTypeConstants.IMAGE)) {
            // 删除docker镜像
            if (StringUtils.equals(commandAction, DockerEventActionConstants.DELETE)) {
                result = DockerImageUtils.removeImage(this.dockerClient, commandValue);
            }
        }
        // 容器
        if (StringUtils.equals(commandType, DockerEventTypeConstants.CONTAINER)) {
            switch (commandAction) {
                // 启动docker容器
                case DockerEventActionConstants.START:
                    result = DockerContainerUtils.startContainer(this.dockerClient, commandValue);
                    break;
                // 停止docker容器
                case DockerEventActionConstants.STOP:
                    result = DockerContainerUtils.stopContainer(this.dockerClient, commandValue);
                    break;
                // 重启docker容器
                case DockerEventActionConstants.RESTART:
                    result = DockerContainerUtils.restartContainer(this.dockerClient, commandValue);
                    break;
                // 删除docker容器
                case DockerEventActionConstants.DESTROY:
                    result = DockerContainerUtils.removeContainer(this.dockerClient, commandValue);
                    break;
                default:
                    break;
            }
        }
        // 执行完docker命令后，马上重新向服务器推送docker信息
        MonitoredThreadPoolExecutor commonIoIntensiveThreadPoolExecutor = ThreadPool.getCommonIoIntensiveThreadPoolExecutor();
        // 等待任务执行完成并获取结果
        Future<Result> submit = commonIoIntensiveThreadPoolExecutor.submit(new DockerThread(), result);
        return submit.get();
    }

}
