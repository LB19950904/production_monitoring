package com.gitee.pifeng.monitoring.server.business.server.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.DockerPackage;
import com.gitee.pifeng.monitoring.server.business.server.entity.MonitorDocker;

import java.util.Date;

/**
 * <p>
 * docker信息服务层接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/6/24 14:25
 */
public interface IDockerService extends IService<MonitorDocker> {

    /**
     * <p>
     * 处理docker信息包
     * </p>
     *
     * @param dockerPackage docker信息包
     * @return {@link Result}
     * @author 皮锋
     * @custom.date 2022/6/24 14:27
     */
    Result dealDockerPackage(DockerPackage dockerPackage);

    /**
     * <p>
     * 把docker系统信息添加或更新到数据库
     * </p>
     *
     * @param dockerPackage docker信息包
     * @author 皮锋
     * @custom.date 2022/7/4 16:34
     */
    void operateDocker(DockerPackage dockerPackage);

    /**
     * <p>
     * 根据服务器IP获取docker服务信息
     * </p>
     *
     * @param serverIp 服务器IP
     * @return docker服务信息
     * @author 皮锋
     * @custom.date 2025/12/26 21:59
     */
    MonitorDocker getMonitorDockerByServerIp(String serverIp);

    /**
     * <p>
     * 清理docker历史记录
     * </p>
     *
     * @param historyTime 时间点，清理这个时间点以前的数据
     * @return 清理记录数
     * @author 皮锋
     * @custom.date 2022/9/13 22:13
     */
    int clearHistoryData(Date historyTime);

}
