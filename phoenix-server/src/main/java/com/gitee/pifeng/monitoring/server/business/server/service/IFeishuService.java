package com.gitee.pifeng.monitoring.server.business.server.service;

import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.server.business.server.domain.Feishu;

/**
 * <p>
 * 飞书服务接口
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/5/15 15:30
 */
public interface IFeishuService {

    /**
     * <p>
     * 发送告警模板飞书
     * </p>
     *
     * @param feishu 飞书实体对象
     * @return {@link Result} 返回结果
     * @author 皮锋
     * @custom.date 2025/5/15 15:33
     */
    Result sendAlarmTemplateFeishu(Feishu feishu);

}