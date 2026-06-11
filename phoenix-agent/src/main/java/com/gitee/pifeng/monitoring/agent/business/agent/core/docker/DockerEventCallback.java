package com.gitee.pifeng.monitoring.agent.business.agent.core.docker;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.gitee.pifeng.monitoring.agent.core.MethodExecuteHandler;
import com.gitee.pifeng.monitoring.common.domain.Docker;
import com.gitee.pifeng.monitoring.common.domain.docker.EventDomain;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.model.Event;
import com.github.dockerjava.api.model.EventActor;
import com.github.dockerjava.api.model.EventType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.util.Map;

/**
 * <p>
 * docker事件监控回调
 * </p>
 *
 * @author YangRui
 * @custom.date 2022/6/21 17:22
 */
@Slf4j
public class DockerEventCallback implements ResultCallback<Event> {

    /**
     * <p>
     * 异步处理启动时调用
     * </p>
     *
     * @param closeable 可以关闭的数据的源或目标
     * @author 皮锋
     * @custom.date 2022/6/23 20:57
     */
    @Override
    public void onStart(Closeable closeable) {
        log.info("docker事件监听开始！");
    }

    /**
     * <p>
     * 发生异步结果事件时调用
     * </p>
     *
     * @param event docker事件
     * @author 皮锋
     * @custom.date 2022/6/23 20:51
     */
    @Override
    public void onNext(Event event) {
        // 事件类型
        EventType type = event.getType();
        if (type == null) {
            return;
        }
        log.info("监听到docker事件：{}", JSON.toJSONString(event));
        // 事件动作
        String action = event.getAction();
        if (action == null || StringUtils.startsWithIgnoreCase(action, "exec_")) {
            return;
        }
        EventDomain eventDomain = new EventDomain();
        eventDomain.setEventStatus(event.getStatus());
        eventDomain.setEventId(event.getId());
        eventDomain.setEventFrom(event.getFrom());
        eventDomain.setEventType(type.getValue());
        eventDomain.setEventAction(action);
        EventActor actor = event.getActor();
        if (actor != null) {
            Map<String, String> attributes = actor.getAttributes();
            if (attributes != null) {
                eventDomain.setEventAttributes(attributes);
            }
        }
        eventDomain.setEventTime(DateUtil.date(event.getTime() * 1000));
        // 发送docker事件
        this.sendDockerEvent(eventDomain);
    }

    /**
     * <p>
     * 发送docker事件
     * </p>
     *
     * @param eventDomain docker事件信息
     * @author 皮锋
     * @custom.date 2022/6/26 22:15
     */
    private void sendDockerEvent(EventDomain eventDomain) {
        try {
            // 构建docker信息包
            Docker docker = Docker.builder().eventDomain(eventDomain).build();
            // 把 Docker 信息封装成 Docker 数据包，并发送到服务端
            MethodExecuteHandler.send(docker, Docker.class);
        } catch (Exception e) {
            log.error("发送docker事件失败！", e);
        }
    }

    /**
     * <p>
     * 处理过程中发生异常时调用
     * </p>
     *
     * @param throwable 异常
     * @author 皮锋
     * @custom.date 2022/6/23 21:01
     */
    @Override
    public void onError(Throwable throwable) {
        log.error("docker事件监听异常：{}", throwable.getMessage());
    }

    /**
     * <p>
     * 在处理结束或中止时调用
     * </p>
     *
     * @author 皮锋
     * @custom.date 2022/6/23 21:02
     */
    @Override
    public void onComplete() {
        log.warn("docker事件监听终止！");
        // 重新启动docker事件监控回调
        DockerCentralController.getInstance().startDockerEventCallback(this);
        log.warn("重新启动docker事件监听！");
    }

    /**
     * <p>
     * 关闭此流并释放所有关联的系统资源时调用
     * </p>
     *
     * @author 皮锋
     * @custom.date 2022/6/23 21:03
     */
    @Override
    public void close() {
        log.info("释放docker事件监听器资源！");
    }

}
