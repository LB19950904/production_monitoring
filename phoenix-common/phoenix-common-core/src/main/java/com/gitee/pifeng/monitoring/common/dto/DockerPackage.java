package com.gitee.pifeng.monitoring.common.dto;

import com.gitee.pifeng.monitoring.common.domain.Docker;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * docker信息包
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/24 11:46
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class DockerPackage extends BaseRequestPackage {

    /**
     * docker信息
     */
    private Docker docker;

    /**
     * 传输频率
     */
    private long rate;
}
