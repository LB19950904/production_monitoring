package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.server.business.server.domain.Feishu;
import com.gitee.pifeng.monitoring.server.business.server.service.IFeishuService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 飞书服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/5/15 15:32
 */
@Slf4j
@Service
public class FeishuServiceImpl implements IFeishuService {

    @Autowired
    private RestTemplate restTemplate;

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
    @Override
    public Result sendAlarmTemplateFeishu(Feishu feishu) {
        try {
            log.info("推送飞书消息开始！");
            // 秘钥
            String secret = feishu.getSecret();
            // Webhook
            String webhook = feishu.getWebhook();
            // 是否发送所有人
            Boolean isAtAll = feishu.getIsAtAll();
            // open_id 或 user_id
            String[] userIds = feishu.getUserIds();
            // 标题
            String title = StringUtils.isBlank(feishu.getTitle()) ? feishu.getTitle() : "[" + feishu.getTitle() + "]";
            // 内容
            String content = StringUtils.replace(feishu.getContent(), "<br>", "\n");
            // 封装飞书请求参数
            Map<String, Object> params = this.wrapRequestParams(title, content, isAtAll, userIds, secret);
            // 转成JSON字符串
            String jsonParams = JSON.toJSONString(params);
            // 发送HTTP请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(jsonParams, headers);
            ResponseEntity<String> responseEntity = this.restTemplate.exchange(webhook, HttpMethod.POST, entity, String.class);
            String body = responseEntity.getBody();
            log.info("推送飞书消息内容：\r\n{}", jsonParams);
            log.info("推送飞书消息结束！");
            if (StringUtils.isNotBlank(body)) {
                JSONObject result = JSON.parseObject(body);
                Long code = result.getLong("code");
                String msg = result.getString("msg");
                if (code == 0 && StringUtils.equalsIgnoreCase(msg, "success")) {
                    return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
                } else {
                    return Result.builder().isSuccess(false).msg(StringUtils.isBlank(msg) ? "发送飞书失败！" : msg).build();
                }
            }
            return Result.builder().isSuccess(false).msg("发送飞书失败！").build();
        } catch (Exception e) {
            log.error("推送飞书消息异常：{}", e.getMessage());
            return Result.builder().isSuccess(false).msg(e.getMessage()).build();
        }
    }

    /**
     * <p>
     * 封装飞书请求参数
     * </p>
     *
     * @param content 消息内容
     * @param isAtAll 是否发送所有人
     * @param userIds open_id 或 user_id
     * @param title   消息标题
     * @param secret  秘钥
     * @return 飞书请求参数
     * @throws NoSuchAlgorithmException 无此算法异常
     * @throws InvalidKeyException      密钥无效异常
     * @author 皮锋
     * @custom.date 2025/5/16 09:06
     */
    private Map<String, Object> wrapRequestParams(String title, String content, Boolean isAtAll, String[] userIds, String secret)
            throws NoSuchAlgorithmException, InvalidKeyException {
        List<Map<String, Object>> contentList = Lists.newArrayList();
        Map<String, Object> textMap = Maps.newHashMap();
        textMap.put("tag", "text");
        textMap.put("text", content);
        contentList.add(textMap);
        if (isAtAll) {
            Map<String, Object> atMap = Maps.newHashMap();
            atMap.put("tag", "at");
            atMap.put("user_id", "all");
            contentList.add(atMap);
        } else {
            for (String userId : userIds) {
                Map<String, Object> atMap = Maps.newHashMap();
                atMap.put("tag", "at");
                atMap.put("user_id", userId);
                contentList.add(atMap);
            }
        }
        Map<String, Object> zhCnMap = Maps.newHashMap();
        zhCnMap.put("title", title);
        zhCnMap.put("content", Collections.singletonList(contentList));
        Map<String, Object> postMap = Maps.newHashMap();
        postMap.put("zh_cn", zhCnMap);
        Map<String, Object> contentMap = Maps.newHashMap();
        contentMap.put("post", postMap);
        // 时间戳
        int timestamp = Math.toIntExact(System.currentTimeMillis() / 1000);
        Map<String, Object> params = Maps.newHashMap();
        params.put("timestamp", timestamp);
        params.put("sign", this.genSign(secret, timestamp));
        params.put("msg_type", "post");
        params.put("content", contentMap);
        return params;
    }

    /**
     * <p>
     * 生成签名
     * </p>
     *
     * @param secret    秘钥
     * @param timestamp 时间戳
     * @return 签名
     * @throws NoSuchAlgorithmException 无此算法异常
     * @throws InvalidKeyException      密钥无效异常
     * @author 皮锋
     * @custom.date 2025/5/15 16:34
     */
    private String genSign(String secret, int timestamp) throws NoSuchAlgorithmException, InvalidKeyException {
        // 把timestamp + "\n" + 密钥当做签名字符串
        String stringToSign = timestamp + "\n" + secret;
        // 使用HmacSHA256算法计算签名
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(stringToSign.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] signData = mac.doFinal(new byte[]{});
        return new String(Base64.encodeBase64(signData));
    }

}