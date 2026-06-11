package com.gitee.pifeng.monitoring.common.domain.docker;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>
 * docker统计信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/22 21:37
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class StatsDomain extends AbstractSuperBean {

    /**
     * 统计信息数量
     */
    private Integer statsNum;

    /**
     * 统计信息
     */
    private List<StatsInfoDomain> statsInfoDomainList;

    @Data
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class StatsInfoDomain extends AbstractSuperBean {

        /**
         * 容器ID
         */
        private String containerId;

        /**
         * 容器名字
         */
        private String containerName;

        /**
         * CPU 的使用情况
         */
        private String cpuUtilizationRate;

        /**
         * 当前使用的内存和最大可以使用的内存
         */
        private String menUsageLimit;

        /**
         * 以百分比的形式显示内存使用情况
         */
        private String menUtilizationRate;

        /**
         * 网络 I/O 数据
         */
        private String netIo;

        /**
         * 磁盘 I/O 数据
         */
        private String blockIo;

        /**
         * PID号
         */
        private String pids;

        /**
         * 网络 I/O 数据 速率，单位：B/s
         */
        private String netIoSpeed;

        /**
         * 磁盘 I/O 数据 速率，单位：B/s
         */
        private String blockIoSpeed;

    }

}
