package com.gitee.pifeng.monitoring.common.dto;

import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * Java线程池信息包
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/13 11:37
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class JavaThreadPoolPackage extends BaseRequestPackage {

    /**
     * Java线程池信息
     */
    private JavaThreadPool javaThreadPool;

    /**
     * 传输频率
     */
    private long rate;

}