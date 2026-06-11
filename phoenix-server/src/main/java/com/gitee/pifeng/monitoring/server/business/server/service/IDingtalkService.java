package com.gitee.pifeng.monitoring.server.business.server.service;

import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.server.business.server.domain.Dingtalk;

/**
 * <p>
 * 钉钉服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/24 16:31
 */
public interface IDingtalkService {

    /**
     * <p>
     * 推送钉钉消息
     * </p>
     *
     * @param dingtalk 钉钉实体对象
     * @return {@link Result} 返回结果
     * @author 皮锋
     * @custom.date 2022/7/24 16:37
     */
    Result sendAlarmTemplateDingtalk(Dingtalk dingtalk);

}
