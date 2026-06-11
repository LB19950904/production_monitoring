package com.gitee.pifeng.monitoring.common.property.client;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>
 * docker信息属性
 * </p>
 *
 * @author YangRui
 * @custom.date 2022/6/23 16:01
 */
@Data
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class MonitoringDockerInfoProperties {

    /**
     * 是否采集docker信息
     */
    private Boolean enable;

    /**
     * 发送docker信息的频率
     */
    private Long rate;

    /**
     * docker主机地址
     */
    private String host;

    /**
     * 启用/禁用TLS验证
     */
    private Boolean tlsVerify;

    /**
     * 验证所需证书的路径
     */
    private String certPath;

    /**
     * 其他docker配置文件的路径
     */
    private String config;

    /**
     * API版本
     */
    private String apiVersion;

    /**
     * 注册地址
     */
    private String registryUrl;

    /**
     * 注册用户名
     */
    private String registryUsername;

    /**
     * 注册密码
     */
    private String registryPassword;

    /**
     * 注册电子邮箱
     */
    private String registryEmail;

}
