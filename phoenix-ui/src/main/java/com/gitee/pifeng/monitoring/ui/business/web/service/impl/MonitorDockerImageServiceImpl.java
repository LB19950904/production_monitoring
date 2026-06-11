package com.gitee.pifeng.monitoring.ui.business.web.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.pifeng.monitoring.common.constant.docker.DockerEventActionConstants;
import com.gitee.pifeng.monitoring.common.constant.docker.DockerEventTypeConstants;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.domain.Command;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;
import com.gitee.pifeng.monitoring.common.util.DataSizeUtils;
import com.gitee.pifeng.monitoring.plug.core.Sender;
import com.gitee.pifeng.monitoring.ui.business.web.dao.IMonitorDockerImageDao;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerImage;
import com.gitee.pifeng.monitoring.ui.business.web.service.IMonitorDockerImageService;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerImageVo;
import com.gitee.pifeng.monitoring.ui.constant.UrlConstants;
import com.gitee.pifeng.monitoring.ui.constant.WebResponseConstants;
import com.gitee.pifeng.monitoring.ui.core.UiPackageConstructor;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * docker镜像信息服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-26
 */
@Service
public class MonitorDockerImageServiceImpl extends ServiceImpl<IMonitorDockerImageDao, MonitorDockerImage> implements IMonitorDockerImageService {

    /**
     * UI端包构造器
     */
    @Autowired
    private UiPackageConstructor uiPackageConstructor;

    /**
     * <p>
     * 获取docker镜像列表
     * </p>
     *
     * @param current         当前页
     * @param size            每页显示条数
     * @param serverIp        服务器IP
     * @param imageRepository 镜像仓库
     * @param monitorEnv      监控环境
     * @param monitorGroup    监控分组
     * @return docker镜像列表
     * @author 皮锋
     * @custom.date 2022/8/21 20:51
     */
    @Override
    public Page<MonitorDockerImageVo> getMonitorDockerImageList(Long current, Long size, String serverIp,
                                                                String imageRepository, String monitorEnv,
                                                                String monitorGroup) {
        Page<MonitorDockerImageVo> page = new Page<>(current, size);
        Map<String, Object> params = new HashMap<>(16);
        params.put("serverIp", serverIp);
        params.put("imageRepository", imageRepository);
        params.put("monitorEnv", monitorEnv);
        params.put("monitorGroup", monitorGroup);
        Page<MonitorDockerImageVo> monitorDockerImageList = this.baseMapper.getMonitorDockerImageList(page, params);
        List<MonitorDockerImageVo> records = monitorDockerImageList.getRecords();
        for (MonitorDockerImageVo record : records) {
            record.setImageSizeStr(DataSizeUtils.format((double) record.getImageSize()));
            record.setImageRepository(StringUtils.replace(StringUtils.replace(record.getImageRepository(), "<", "&lt;"), ">", "&gt;"));
            record.setImageTag(StringUtils.replace(StringUtils.replace(record.getImageTag(), "<", "&lt;"), ">", "&gt;"));
        }
        return monitorDockerImageList;
    }

    /**
     * <p>
     * 删除docker镜像
     * </p>
     *
     * @param monitorDockerImageVos docker镜像信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    @Retryable
    @Transactional(rollbackFor = Throwable.class, isolation = Isolation.READ_COMMITTED)
    @Override
    public LayUiAdminResultVo deleteMonitorDockerImage(List<MonitorDockerImageVo> monitorDockerImageVos) {
        List<String> serverIps = Lists.newArrayList();
        List<String> imageIds = Lists.newArrayList();
        for (MonitorDockerImageVo monitorDockerImageVo : monitorDockerImageVos) {
            serverIps.add(monitorDockerImageVo.getServerIp());
            imageIds.add(monitorDockerImageVo.getImageId());
        }
        LambdaUpdateWrapper<MonitorDockerImage> monitorDockerImageLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        monitorDockerImageLambdaUpdateWrapper.in(MonitorDockerImage::getServerIp, serverIps);
        monitorDockerImageLambdaUpdateWrapper.in(MonitorDockerImage::getImageId, imageIds);
        this.baseMapper.delete(monitorDockerImageLambdaUpdateWrapper);
        return LayUiAdminResultVo.ok(WebResponseConstants.SUCCESS);
    }

    /**
     * <p>
     * 获取docker镜像信息
     * </p>
     *
     * @param serverIp 服务器IP
     * @param imageId  镜像ID
     * @return docker镜像信息表现层对象
     * @author 皮锋
     * @custom.date 2022/8/21 16:42
     */
    @Override
    public MonitorDockerImageVo getMonitorDockerImageInfo(String serverIp, String imageId) {
        MonitorDockerImageVo monitorDockerImageVo = this.baseMapper.getMonitorDockerImageInfo(serverIp, imageId);
        monitorDockerImageVo.setImageSizeStr(DataSizeUtils.format((double) monitorDockerImageVo.getImageSize()));
        return monitorDockerImageVo;
    }

    /**
     * <p>
     * 删除docker镜像
     * </p>
     *
     * @param dockerId docker服务ID
     * @param imageId  镜像ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws IOException IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 21:10
     */
    @Override
    public LayUiAdminResultVo deleteDockerImage(Long dockerId, String imageId) throws IOException {
        Command command = Command.builder()
                .monitorTypeEnum(MonitorTypeEnums.DOCKER)
                .commandType(DockerEventTypeConstants.IMAGE)
                .commandAction(DockerEventActionConstants.DELETE)
                .commandTarget(String.valueOf(dockerId))
                .commandValue(imageId)
                .build();
        CommandPackage commandPackage = this.uiPackageConstructor.structureCommandPackage(command);
        // 发送命令包到服务端
        String resultStr = Sender.send(UrlConstants.COMMAND_URL, commandPackage.toJsonString());
        BaseResponsePackage baseResponsePackage = JSON.parseObject(resultStr, BaseResponsePackage.class);
        Result result = baseResponsePackage.getResult();
        boolean success = result.isSuccess();
        String msg = result.getMsg();
        if (success) {
            return LayUiAdminResultVo.ok(msg);
        }
        return LayUiAdminResultVo.fail(msg);
    }

}
