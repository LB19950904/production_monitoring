package com.gitee.pifeng.monitoring.common.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.pifeng.monitoring.common.abs.AbstractSuperBean;
import com.gitee.pifeng.monitoring.common.constant.monitortype.MonitorTypeEnums;
import com.gitee.pifeng.monitoring.common.exception.WebSocketException;
import com.gitee.pifeng.monitoring.common.util.MsgPayloadUtils;
import lombok.*;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Set;

/**
 * <p>
 * WebSocket 数据包
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/2/28 11:44
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class WebSocketPackage extends AbstractSuperBean {

    /**
     * 负载数据对应的 Java 类全限定名，用于接收方将 {@link #payload} 反序列化为具体对象
     */
    private String className;

    /**
     * 实际数据
     */
    private Object payload;

    // 字段名常量
    private static final String FIELD_CLASS_NAME = "className";
    private static final String FIELD_PAYLOAD = "payload";

    /**
     * <p>
     * 将原始 “密文 WebSocket 消息JSON字符串” 解析并转换为 “{@link WebSocketPackage} 数据包”
     * </p>
     *
     * @param jsonMessage       原始 WebSocket 接收到的密文 JSON 字符串消息
     * @param allowedClassNames 允许反序列化的 payload 类全限定名白名单
     * @return 成功解析后的 {@link WebSocketPackage} 数据包
     * @throws ClassNotFoundException {@code className} 指定的类在当前 {@link ClassLoader} 中不存在
     * @throws WebSocketException     自定义的 Websocket 异常
     * @author 皮锋
     * @custom.date 2026/3/3 15:48
     * @see MsgPayloadUtils#decryptPayload(String)
     * @see WebSocketPackage
     * @see MonitorTypeEnums
     */
    public static WebSocketPackage convert(String jsonMessage, Set<String> allowedClassNames) throws ClassNotFoundException {
        if (StringUtils.isBlank(jsonMessage)) {
            throw new WebSocketException("WebSocket消息内容不能为空！");
        }
        if (CollectionUtils.isEmpty(allowedClassNames)) {
            throw new WebSocketException("允许的负载数据类全限定名白名单不能为空！");
        }
        // 将 密文JSON字符串 转换成 明文JSON字符串
        String decryptStr = MsgPayloadUtils.decryptPayload(jsonMessage);
        // 转化成WebSocket数据包
        JSONObject root = JSON.parseObject(decryptStr);
        String className = root.getString(WebSocketPackage.FIELD_CLASS_NAME);
        if (!allowedClassNames.contains(className)) {
            throw new WebSocketException("拒绝反序列化未授权的类：" + className);
        }
        WebSocketPackage pkg = new WebSocketPackage();
        pkg.setClassName(className);
        pkg.setPayload(root.getObject(WebSocketPackage.FIELD_PAYLOAD, Class.forName(className)));
        return pkg;
    }

}