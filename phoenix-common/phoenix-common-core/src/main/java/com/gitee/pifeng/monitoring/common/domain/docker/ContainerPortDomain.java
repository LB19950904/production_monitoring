package com.gitee.pifeng.monitoring.common.domain.docker;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * docker容器端口信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/24 21:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ContainerPortDomain extends AbstractSuperBean {

    /**
     * ip
     */
    private String ip;

    /**
     * 私有端口，也就是容器内部端口
     */
    private Integer privatePort;

    /**
     * 公共端口，也就是容器所在主机可供外部访问的接口
     */
    private Integer publicPort;

    /**
     * 类型
     */
    private String type;

    /**
     * <p>
     * 转docker容器端口信息字符串
     * </p>
     *
     * @author 皮锋
     * @custom.date 2022/6/25 21:19
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (ip != null) {
            sb.append(ip).append(":");
        }
        sb.append(privatePort);
        if (publicPort != null) {
            sb.append("->");
            sb.append(publicPort);
        }
        sb.append("/").append(type);
        return sb.toString();
    }

}
