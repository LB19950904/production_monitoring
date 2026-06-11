package com.gitee.pifeng.monitoring.common.domain.docker;

import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.collections.MapUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * docker镜像信息
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/25 22:24
 */
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ImageDomain extends AbstractSuperBean {

    /**
     * 镜像数量
     */
    private Integer imageNum;

    /**
     * 镜像信息
     */
    private List<ImageDomain.ImageInfoDomain> imageInfoDomainList;

    @Data
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @EqualsAndHashCode(callSuper = true)
    public static class ImageInfoDomain extends AbstractSuperBean {

        /**
         * 镜像ID
         */
        private String imageId;

        /**
         * 创建时间
         */
        private Date imageCreated;

        /**
         * 仓库
         */
        private String imageRepository;

        /**
         * 标签
         */
        private String imageTag;

        /**
         * 镜像大小
         */
        private Long imageSize;

        /**
         * 标识
         */
        private Map<String, String> imageLabels;

        /**
         * <p>
         * 镜像标识集合转字符串
         * </p>
         *
         * @return 镜像标识字符串
         * @author 皮锋
         * @custom.date 2022/6/25 21:32
         */
        public String imageLabels2String() {
            StringBuilder builder = new StringBuilder();
            if (MapUtils.isNotEmpty(this.imageLabels)) {
                for (Map.Entry<String, String> entry : this.imageLabels.entrySet()) {
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
