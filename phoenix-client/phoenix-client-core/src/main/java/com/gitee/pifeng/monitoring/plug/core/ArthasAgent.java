package com.gitee.pifeng.monitoring.plug.core;

import com.gitee.pifeng.monitoring.common.init.InitBanner;
import com.gitee.pifeng.monitoring.common.property.client.MonitoringArthasProperties;
import com.gitee.pifeng.monitoring.common.property.client.MonitoringProperties;
import com.gitee.pifeng.monitoring.plug.constant.WebSocketBusinessTypeConstants;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Map;

/**
 * <p>
 * Arthas代理器
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/3/26 15:56
 */
@Slf4j
public class ArthasAgent {

    /**
     * <p>
     * 启用 Arthas
     * </p>
     *
     * @author 皮锋
     * @custom.date 2023/3/26 16:03
     */
    public static void attach() {
        // 监控属性
        MonitoringProperties monitoringProperties = ConfigLoader.getMonitoringProperties();
        // Arthas属性
        MonitoringArthasProperties arthasProperties = monitoringProperties.getArthas();
        // 是否开启arthas
        boolean enable = arthasProperties.getEnable();
        if (!enable) {
            return;
        }
        // 监控服务端url
        String url = monitoringProperties.getComm().getWebsocket().getUrl();
        if (StringUtils.isBlank(url)) {
            log.warn("监控程序找不到监控服务端(代理端)WS(S) URL配置，无法开启 Arthas ！");
            return;
        }
        // 打印banner
        InitBanner.printBanner("banner-arthas.txt");
        // 封装 Arthas 配置
        Map<String, String> arthasConfigs = wrapArthasConfigs(monitoringProperties);
        // 启用
        com.taobao.arthas.agent.attach.ArthasAgent.attach(arthasConfigs);
    }

    /**
     * <p>
     * 封装 Arthas 配置
     * </p>
     *
     * @param monitoringProperties {@link MonitoringProperties} 监控属性
     * @return Arthas 配置集合
     * @author 皮锋
     * @custom.date 2026/2/2 10:30
     */
    private static Map<String, String> wrapArthasConfigs(MonitoringProperties monitoringProperties) {
        // arthas服务端地址
        String arthasUrl = monitoringProperties.getComm().getWebsocket().getUrl()
                // 中继代理端点
                + "/websocket/relay";
        // 应用实例ID
        String instanceId = InstanceGenerator.getInstanceId();
        // 应用实例名字
        String instanceName = monitoringProperties.getInstance().getName();
        // 实例端点类型（服务端、代理端、客户端、UI端）
        String endpoint = StringUtils.lowerCase(monitoringProperties.getInstance().getEndpoint());
        // Arthas 配置项
        Map<String, String> arthasConfigMap = Maps.newHashMap();
        // 不监听telnet、http端口，通过tunnel server来使用arthas
        arthasConfigMap.put("arthas.telnetPort", "-1");
        arthasConfigMap.put("arthas.httpPort", "-1");
        arthasConfigMap.put("arthas.localConnectionNonAuth", "true");
        // 当配置密码时，使用本地连接，也不需要鉴权。默认配置值是 true，方便本地连接使用。只有远程连接时，才需要鉴权
        arthasConfigMap.put("arthas.username", "arthas");
        // 密码与agentId相同
        arthasConfigMap.put("arthas.password", instanceId);
        // 与arthas服务端通信的配置
        arthasConfigMap.put("arthas.agentId", "arthas_" + instanceId);
        arthasConfigMap.put("arthas.appName", instanceName);
        arthasConfigMap.put("arthas.tunnelServer", arthasUrl + "/" + WebSocketBusinessTypeConstants.ARTHAS);
        // 默认情况下，禁掉stop命令
        arthasConfigMap.put("arthas.disabledCommands", "stop");
        arthasConfigMap.put("arthas.outputPath", "liblog4phoenix" + File.separator + "logs" + File.separator + "phoenix-" + endpoint + File.separator + "arthas-output");
        return arthasConfigMap;
    }

}
