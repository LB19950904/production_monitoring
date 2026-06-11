package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmLevelEnums;
import com.gitee.pifeng.monitoring.common.constant.alarm.AlarmWayEnums;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.property.server.MonitoringAlarmDingtalkProperties;
import com.gitee.pifeng.monitoring.common.property.server.MonitoringAlarmEnterpriseWechatProperties;
import com.gitee.pifeng.monitoring.common.property.server.MonitoringAlarmFeishuProperties;
import com.gitee.pifeng.monitoring.server.business.server.core.MonitoringConfigPropertiesLoader;
import com.gitee.pifeng.monitoring.server.business.server.domain.*;
import com.gitee.pifeng.monitoring.server.business.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 发送模板消息门面服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/5/24 8:19
 */
@Service
public class TemplateMsgSendFacadeServiceImpl implements ITemplateMsgSendFacadeService {

    /**
     * 监控配置属性加载器
     */
    @Autowired
    private MonitoringConfigPropertiesLoader monitoringConfigPropertiesLoader;

    /**
     * 短信服务接口
     */
    @Autowired
    private ISmsService smsService;

    /**
     * 邮箱服务接口
     */
    @Autowired
    private IMailService mailService;

    /**
     * 钉钉服务接口
     */
    @Autowired
    private IDingtalkService dingtalkService;

    /**
     * 企业微信服务接口
     */
    @Autowired
    private IEnterpriseWechatService enterpriseWechatService;

    /**
     * 飞书服务接口
     */
    @Autowired
    private IFeishuService feishuService;


    /**
     * <p>
     * 发送模板文本消息
     * </p>
     *
     * @param alarmWay     告警方式
     * @param alarmLevel   告警级别
     * @param alarmTitle   告警标题
     * @param alarmContent 告警内容
     * @return {@link Result} 返回结果
     * @author 皮锋
     * @custom.date 2023/5/24 10:39
     */
    @Override
    public Result sendTemplateTextMsg(AlarmWayEnums alarmWay, AlarmLevelEnums alarmLevel, String alarmTitle, String alarmContent) {
        Result result = Result.builder().build();
        switch (alarmWay) {
            case SMS:
                Sms sms = Sms.builder()
                        .phones(this.monitoringConfigPropertiesLoader.getMonitoringProperties().getAlarmProperties().getSmsProperties().getPhoneNumbers())
                        .title(alarmTitle)
                        .content(alarmContent)
                        .level(alarmLevel != null ? alarmLevel.name() : null)
                        .build();
                result = this.smsService.sendAlarmTemplateSms(sms);
                break;
            case MAIL:
                Mail mail = Mail.builder()
                        .email(this.monitoringConfigPropertiesLoader.getMonitoringProperties().getAlarmProperties().getMailProperties().getEmails())
                        .title(alarmTitle)
                        .content(alarmContent)
                        .level(alarmLevel != null ? alarmLevel.name() : null)
                        .build();
                result = this.mailService.sendAlarmTemplateMail(mail);
                break;
            case DINGTALK:
                MonitoringAlarmDingtalkProperties dingtalkProperties = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getAlarmProperties().getDingtalkProperties();
                Dingtalk dingtalk = Dingtalk.builder()
                        .secret(dingtalkProperties.getSecret())
                        .webhook(dingtalkProperties.getWebhook())
                        .isAtAll(dingtalkProperties.getIsAtAll())
                        .phones(dingtalkProperties.getPhoneNumbers())
                        .title(alarmTitle)
                        .content(alarmContent)
                        .build();
                result = this.dingtalkService.sendAlarmTemplateDingtalk(dingtalk);
                break;
            case EPWECHAT:
                MonitoringAlarmEnterpriseWechatProperties enterpriseWechatProperties = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getAlarmProperties().getEnterpriseWechatProperties();
                EnterpriseWechat enterpriseWechat = EnterpriseWechat.builder()
                        .webhook(enterpriseWechatProperties.getWebhook())
                        .isAtAll(enterpriseWechatProperties.getIsAtAll())
                        .phones(enterpriseWechatProperties.getPhoneNumbers())
                        .title(alarmTitle)
                        .content(alarmContent)
                        .build();
                result = this.enterpriseWechatService.sendAlarmTemplateEnterpriseWechat(enterpriseWechat);
                break;
            case FEISHU:
                MonitoringAlarmFeishuProperties feishuProperties = this.monitoringConfigPropertiesLoader.getMonitoringProperties().getAlarmProperties().getFeishuProperties();
                Feishu feishu = Feishu.builder()
                        .secret(feishuProperties.getSecret())
                        .webhook(feishuProperties.getWebhook())
                        .isAtAll(feishuProperties.getIsAtAll())
                        .userIds(feishuProperties.getUserIds())
                        .title(alarmTitle)
                        .content(alarmContent)
                        .build();
                result = this.feishuService.sendAlarmTemplateFeishu(feishu);
            default:
                break;
        }
        return result;
    }

}
