package com.gitee.pifeng.monitoring.agent.business.client.service;

import com.gitee.pifeng.monitoring.common.dto.BaseResponsePackage;
import com.gitee.pifeng.monitoring.common.dto.CommandPackage;

/**
 * <p>
 * 命令服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/9/20 16:38
 */
public interface ICommandService {

    /**
     * <p>
     * 处理命令信息包
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2022/9/21 22:09
     */
    BaseResponsePackage dealCommandPackage(CommandPackage commandPackage);

}
