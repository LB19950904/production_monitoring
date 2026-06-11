package com.gitee.pifeng.monitoring.server.business.server.websocket.listener;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.service.IDockerService;
import com.gitee.pifeng.monitoring.server.business.server.websocket.event.DockerEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * <p>
 * docker消息事件监听器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/9 08:31
 */
@Slf4j
@Component
public class DockerListener {

    /**
     * docker信息服务层接口
     */
    @Autowired
    private IDockerService dockerService;

    /**
     * <p>
     * 监听监控代理端程序或者监控客户端程序发的docker信息包
     * </p>
     *
     * @param event docker消息事件
     * @author 皮锋
     * @custom.date 2026/3/9 8:32
     */
    @Async
    @EventListener
    public void handleDockerPackage(DockerEvent event) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        DockerPackage dockerPackage = event.getDockerPackage();
        this.dockerService.dealDockerPackage(dockerPackage);
        // 时间差（毫秒）
        String betweenDay = timer.intervalPretty();
        if (timer.intervalSecond() > 1) {
            log.warn("处理docker信息包耗时：{}", betweenDay);
        }
    }

}