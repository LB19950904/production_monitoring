package com.gitee.pifeng.monitoring.server.business.server.service.impl;

import cn.hutool.core.util.CharsetUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.request.OapiRobotSendRequest;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.server.business.server.domain.Dingtalk;
import com.gitee.pifeng.monitoring.server.business.server.service.IDingtalkService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * <p>
 * 钉钉服务实现类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/24 16:32
 */
@Slf4j
@Service
public class DingtalkServiceImpl implements IDingtalkService {

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
    @Override
    public Result sendAlarmTemplateDingtalk(Dingtalk dingtalk) {
        try {
            log.info("推送钉钉消息开始！");
            // 时间戳
            Long timestamp = System.currentTimeMillis();
            // 秘钥
            String secret = dingtalk.getSecret();
            // Webhook
            String webhook = dingtalk.getWebhook();
            // 接收人手机号码
            String[] phones = dingtalk.getPhones();
            // 是否发送所有人
            Boolean isAtAll = dingtalk.getIsAtAll();
            // 标题
            String title = dingtalk.getTitle();
            // 内容
            String content = StringUtils.replace(dingtalk.getContent(), "<br>", "\n");

            String stringToSign = timestamp + "\n" + secret;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] signData = mac.doFinal(stringToSign.getBytes(StandardCharsets.UTF_8));
            String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), CharsetUtil.UTF_8);
            DingTalkClient client = new DefaultDingTalkClient(webhook + "&timestamp=" + timestamp + "&sign=" + sign);
            OapiRobotSendRequest request = new OapiRobotSendRequest();
            OapiRobotSendRequest.At at = new OapiRobotSendRequest.At();
            if (isAtAll) {
                //推送所有人
                at.setIsAtAll(true);
            } else {
                //推送指定用户
                at.setAtMobiles(Arrays.asList(phones));
                at.setIsAtAll(false);
            }
            request.setAt(at);
            //文本消息
            request.setMsgtype("text");
            OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
            text.setContent(StringUtils.isBlank(title) ? content : ("[" + title + "]\n" + content));
            request.setText(text);
            OapiRobotSendResponse response = client.execute(request);
            log.info("推送钉钉消息内容：\r\n{}", JSON.toJSONString(request, SerializerFeature.WriteMapNullValue));
            log.info("推送钉钉消息结束！");
            boolean isSuccess = response.isSuccess();
            if (isSuccess) {
                return Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
            }
            String errMsg = response.getErrmsg();
            return Result.builder().isSuccess(false).msg(errMsg).build();
        } catch (Exception e) {
            log.error("推送钉钉消息异常：{}", e.getMessage());
            return Result.builder().isSuccess(false).msg(e.getMessage()).build();
        }
    }

}
