package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.domain.docker.ImageDomain;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.dao.IMonitorDockerImageDao;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDockerImage;
import com.gitee.pifeng.monitoring.server.business.server.service.IDockerImageService;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * docker镜像信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-26
 */
@Service
public class DockerImageServiceImpl extends ServiceImpl<IMonitorDockerImageDao, MonitorDockerImage> implements IDockerImageService {

    /**
     * <p>
     * 把docker镜像信息添加或更新到数据库
     * </p>
     * 此处不加事务，不加事务能提高并发性能，并且对数据的一致性要求也没那么高
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/6/26 15:56
     */
    // @Transactional(rollbackFor = Throwable.class)
    @Override
    @Retryable
    public void operateDockerImage(DockerPackage dockerPackage) {
        // IP地址
        String ip = dockerPackage.getIp();
        Date date = new Date();
        ImageDomain imageDomain = dockerPackage.getDocker().getImageDomain();
        if (imageDomain != null) {
            List<ImageDomain.ImageInfoDomain> imageInfoDomainList = imageDomain.getImageInfoDomainList();
            List<MonitorDockerImage> insertImageList = Lists.newArrayList();
            for (ImageDomain.ImageInfoDomain imageInfoDomain : imageInfoDomainList) {
                MonitorDockerImage monitorDockerImage = new MonitorDockerImage();
                monitorDockerImage.setImageId(StringUtils.replace(imageInfoDomain.getImageId(), "sha256:", ""));
                monitorDockerImage.setImageRepository(imageInfoDomain.getImageRepository());
                monitorDockerImage.setImageTag(imageInfoDomain.getImageTag());
                monitorDockerImage.setImageSize(imageInfoDomain.getImageSize());
                monitorDockerImage.setImageLabels(imageInfoDomain.imageLabels2String());
                monitorDockerImage.setCreated(imageInfoDomain.getImageCreated());
                monitorDockerImage.setServerIp(ip);
                monitorDockerImage.setInsertTime(date);
                monitorDockerImage.setUpdateTime(date);
                insertImageList.add(monitorDockerImage);
            }
            // 先删除，后添加
            LambdaUpdateWrapper<MonitorDockerImage> dockerImageLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            dockerImageLambdaUpdateWrapper.eq(MonitorDockerImage::getServerIp, ip);
            ((IDockerImageService) AopContext.currentProxy()).remove(dockerImageLambdaUpdateWrapper);
            ((IDockerImageService) AopContext.currentProxy()).saveBatch(insertImageList);
        }
    }

}
