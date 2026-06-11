package com.gitee.pifeng.monitoring.server.business.server.service;

import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.server.business.server.domain.EnterpriseWechat;

/**
 * <p>
 * 企业微信服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/25 21:26
 */
public interface IEnterpriseWechatService {

    /**
     * <p>
     * 发送告警模板企业微信
     * </p>
     *
     * @param enterpriseWechat 企业微信实体对象
     * @return {@link Result} 返回结果
     * @author 皮锋
     * @custom.date 2020/4/13 11:37
     */
    Result sendAlarmTemplateEnterpriseWechat(EnterpriseWechat enterpriseWechat);

}
