package com.gitee.pifeng.monitoring.ui.business.web.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerContainer;
import com.gitee.pifeng.monitoring.ui.business.web.vo.LayUiAdminResultVo;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerContainerVo;
import org.hyperic.sigar.SigarException;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 * docker容器信息服务类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-25
 */
public interface IMonitorDockerContainerService extends IService<MonitorDockerContainer> {

    /**
     * <p>
     * 获取docker容器列表
     * </p>
     *
     * @param current       当前页
     * @param size          每页显示条数
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @param imageName     镜像名
     * @param status        容器状态
     * @param monitorEnv    监控环境
     * @param monitorGroup  监控分组
     * @return docker容器列表
     * @author 皮锋
     * @custom.date 2022/8/16 17:29
     */
    Page<MonitorDockerContainerVo> getMonitorDockerContainerList(Long current, Long size, String serverIp,
                                                                 String containerName, String imageName, String status,
                                                                 String monitorEnv, String monitorGroup);

    /**
     * <p>
     * 获取docker容器信息
     * </p>
     *
     * @param serverIp      服务器IP
     * @param containerName 容器名
     * @return docker容器信息表现层对象
     * @author 皮锋
     * @custom.date 2022/8/21 16:42
     */
    MonitorDockerContainerVo getMonitorDockerContainerInfo(String serverIp, String containerName);

    /**
     * <p>
     * 删除docker容器
     * </p>
     *
     * @param monitorDockerContainerVos docker容器信息
     * @return layUiAdmin响应对象：如果删除成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @author 皮锋
     * @custom.date 2022/7/8 21:10
     */
    LayUiAdminResultVo deleteMonitorDockerContainer(List<MonitorDockerContainerVo> monitorDockerContainerVos);

    /**
     * <p>
     * 启动docker容器
     * </p>
     *
     * @param id          docker主键ID
     * @param containerId 容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 15:49
     */
    LayUiAdminResultVo startDockerContainer(Long id, String containerId) throws SigarException, IOException;

    /**
     * <p>
     * 停止docker容器
     * </p>
     *
     * @param id          docker主键ID
     * @param containerId 容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 15:49
     */
    LayUiAdminResultVo stopDockerContainer(Long id, String containerId) throws SigarException, IOException;

    /**
     * <p>
     * 重启docker容器
     * </p>
     *
     * @param id          docker主键ID
     * @param containerId 容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 15:49
     */
    LayUiAdminResultVo restartDockerContainer(Long id, String containerId) throws SigarException, IOException;

    /**
     * <p>
     * 销毁docker容器
     * </p>
     *
     * @param id          docker主键ID
     * @param containerId 容器ID
     * @return layUiAdmin响应对象：如果操作成功，LayUiAdminResultVo.data="success"，否则LayUiAdminResultVo.data="fail"。
     * @throws SigarException Sigar异常
     * @throws IOException    IO异常
     * @author 皮锋
     * @custom.date 2022/9/21 15:49
     */
    LayUiAdminResultVo destroyDockerContainer(Long id, String containerId) throws SigarException, IOException;

}
