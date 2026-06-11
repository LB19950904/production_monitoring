package com.gitee.pifeng.monitoring.agent.util.docker;

import cn.hutool.core.date.DateUtil;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.domain.docker.ImageDomain;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * docker镜像工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/23 14:09
 */
public class DockerImageUtils {

    /**
     * <p>
     * 获取docker镜像信息
     * </p>
     *
     * @param dockerClient docker客户端
     * @param showAll      是否显示所有镜像
     * @return {@link ImageDomain}
     * @author 皮锋
     * @custom.date 2022/6/25 22:44
     */
    public static ImageDomain getImageInfo(DockerClient dockerClient, boolean showAll) {
        List<Image> images = DockerClientUtils.getImages(dockerClient, showAll);
        List<ImageDomain.ImageInfoDomain> imageInfoDomainList = Lists.newArrayList();
        for (Image image : images) {
            String id = image.getId();
            String[] repoTags = image.getRepoTags();
            Set<String> repositories = Sets.newLinkedHashSet();
            Set<String> tags = Sets.newLinkedHashSet();
            if (repoTags != null) {
                for (String repoTag : repoTags) {
                    if (repoTag.contains(":")) {
                        String[] split = repoTag.split(":", 2);
                        repositories.add(split[0]);
                        tags.add(split[1]);
                    }
                }
            }
            Date created = DateUtil.date(image.getCreated() * 1000);
            Long size = image.getSize();
            Map<String, String> labels = image.getLabels();
            ImageDomain.ImageInfoDomain imageInfoDomain = new ImageDomain.ImageInfoDomain();
            imageInfoDomain.setImageId(id);
            imageInfoDomain.setImageCreated(created);
            imageInfoDomain.setImageRepository(String.join(",", repositories));
            imageInfoDomain.setImageTag(String.join(",", tags));
            imageInfoDomain.setImageSize(size);
            imageInfoDomain.setImageLabels(labels);
            imageInfoDomainList.add(imageInfoDomain);
        }
        return ImageDomain.builder().imageNum(images.size()).imageInfoDomainList(imageInfoDomainList).build();
    }

    /**
     * <p>
     * 删除docker镜像
     * </p>
     *
     * @param dockerClient docker客户端
     * @param imageId      镜像ID
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2022/9/20 17:26
     */
    public static Result removeImage(DockerClient dockerClient, @Nonnull String imageId) {
        return DockerClientUtils.removeImage(dockerClient, imageId);
    }

}
