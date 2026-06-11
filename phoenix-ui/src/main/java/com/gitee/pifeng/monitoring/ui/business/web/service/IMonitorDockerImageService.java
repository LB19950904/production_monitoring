package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerImage;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerImageVo;
import org.hyperic.sigar.SigarException;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * docker镜像信息服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-26
 */
public interface IMonitorDockerImageService extends IService<MonitorDockerImage> {

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
    Page<MonitorDockerImageVo> getMonitorDockerImageList(Long current, Long size, String serverIp, String imageRepository, String monitorEnv, String monitorGroup);

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
    LayUiAdminResultVo deleteMonitorDockerImage(List<MonitorDockerImageVo> monitorDockerImageVos);

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
    MonitorDockerImageVo getMonitorDockerImageInfo(String serverIp, String imageId);

    /**
     * <p>
     * 删除docker镜像
     * </p>
     *
     * @param dockerId docker服务ID
     * @param imageId  镜像ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 21:10
     */
    LayUiAdminResultVo deleteDockerImage(Long dockerId, String imageId) throws IOException, SigarException;

}
