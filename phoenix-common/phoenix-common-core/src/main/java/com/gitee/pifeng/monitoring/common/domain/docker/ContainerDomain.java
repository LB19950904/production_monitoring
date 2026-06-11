package com.gitee.pifeng.monitoring.common.domain.docker;

import cn.hutool.core.collection.CollectionUtil;
import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import com.google.common.collect.Lists;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * docker容器信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/24 11:32
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ContainerDomain extends AbstractSuperBean {

    /**
     * 容器数量
     */
    private Integer containerNum;

    /**
     * 容器信息
     */
    private List<ContainerInfoDomain> containerInfoDomainList;

    @Data
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class ContainerInfoDomain extends AbstractSuperBean {

        /**
         * 容器ID
         */
        private String containerId;

        /**
         * 命令
         */
        private String containerCommand;

        /**
         * 创建时间
         */
        private Date containerCreated;

        /**
         * 状态
         */
        private String containerStatus;

        /**
         * 端口
         */
        private List<ContainerPortDomain> containerPorts;

        /**
         * 容器名字
         */
        private String[] containerNames;

        /**
         * 标识
         */
        private Map<String, String> containerLabels;

        /**
         * 镜像ID
         */
        private String imageId;

        /**
         * 镜像名字
         */
        private String imageName;

        /**
         * <p>
         * 容器端口列表转字符串
         * </p>
         *
         * @return 容器端口字符串
         * @author 皮锋
         * @custom.date 2022/6/25 21:24
         */
        public String containerPorts2String() {
            if (CollectionUtils.isNotEmpty(this.containerPorts)) {
                return this.containerPorts.stream().map(ContainerPortDomain::toString).collect(Collectors.joining(","));
            }
            return null;
        }

        /**
         * <p>
         * 容器名列表转字符串
         * </p>
         *
         * @return 容器名字符串
         * @author 皮锋
         * @custom.date 2022/6/25 21:29
         */
        public String containerNames2String() {
            List<String> containerNameList = Lists.newArrayList();
            for (String containerName : this.containerNames) {
                if (StringUtils.startsWith(containerName, "/")) {
                    containerNameList.add(StringUtils.substring(containerName, 1));
                }
            }
            return CollectionUtil.join(containerNameList, ",");
        }

        /**
         * <p>
         * 容器标识集合转字符串
         * </p>
         *
         * @return 容器标识字符串
         * @author 皮锋
         * @custom.date 2022/6/25 21:32
         */
        public String containerLabels2String() {
            StringBuilder builder = new StringBuilder();
            if (MapUtils.isNotEmpty(this.containerLabels)) {
                for (Map.Entry<String, String> entry : this.containerLabels.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    builder.append(key).append("(").append(value).append(")").append(",");
                }
                builder.deleteCharAt(builder.length() - 1);
                return builder.toString();
            }
            return null;
        }

    }

}
