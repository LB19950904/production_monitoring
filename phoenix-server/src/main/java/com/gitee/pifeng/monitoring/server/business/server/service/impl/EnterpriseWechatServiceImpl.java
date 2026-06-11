package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.server.business.server.domain.EnterpriseWechat;
import com.gitee.pifeng.monitoring.server.business.server.service.IEnterpriseWechatService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 企业微信服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/25 21:28
 */
@Slf4j
@Service
public class EnterpriseWechatServiceImpl implements IEnterpriseWechatService {

    @Autowired
    private RestTemplate restTemplate;

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
    @Override
    public Result sendAlarmTemplateEnterpriseWechat(EnterpriseWechat enterpriseWechat) {
        try {
            log.info("推送企业微信消息开始！");
            // Webhook
            String webhook = enterpriseWechat.getWebhook();
            // 是否发送所有人
            Boolean isAtAll = enterpriseWechat.getIsAtAll();
            // 接收人手机号码
            String[] phones = enterpriseWechat.getPhones();
            // 标题
            String title = enterpriseWechat.getTitle();
            // 内容
            String content = StringUtils.replace(enterpriseWechat.getContent(), "<br>", "\n");
            // 请求参数
            Map<String, Object> params = new HashMap<>();
            params.put("msgtype", "text");
            Map<String, Object> text = new HashMap<>();
            text.put("content", StringUtils.isBlank(title) ? content : ("[" + title + "]\n" + content));
            if (isAtAll) {
                text.put("mentioned_mobile_list", "@all");
            } else {
                text.put("mentioned_mobile_list", phones);
            }
            params.put("text", text);
            String jsonParams = JSON.toJSONString(params);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(jsonParams, headers);
            ResponseEntity<String> responseEntity = this.restTemplate.exchange(webhook, HttpMethod.POST, entity, String.class);
            String body = responseEntity.getBody();
            log.info("推送企业微信消息内容：\r\n{}", jsonParams);
            log.info("推送企业微信消息结束！");
            if (StringUtils.isNotBlank(body)) {
                JSONObject result = JSON.parseObject(body);
                Long errcode = result.getLong("errcode");
                String errmsg = result.getString("errmsg");
                if (errcode == 0 && StringUtils.equalsIgnoreCase(errmsg, "ok")) {
                    return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
                }
            }
            return Result.builder().isSuccess(false).msg("发送企业微信失败！").build();
        } catch (Exception e) {
            log.error("推送企业微信消息异常：{}", e.getMessage());
            return Result.builder().isSuccess(false).msg(e.getMessage()).build();
        }
    }

}
