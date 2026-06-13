package com.gitee.pifeng.monitoring.ui.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Locale;

/**
 * 基础控制器类 - 提供国际化功能
 */
@Slf4j
public class BaseController {

    @Autowired
    private MessageSource messageSource;

    /**
     * 在每个Controller方法执行前设置通用模型数据
     */
    @ModelAttribute
    public void setCommonModel(Model model, Locale locale) {
        // 设置当前语言环境
        model.addAttribute("currentLocale", locale);
        // 设置是否为英文
        model.addAttribute("isEnglish", locale.getLanguage().equals("en"));
        // 设置当前语言代码
        model.addAttribute("langCode", locale.toString());
    }

    /**
     * 获取国际化消息
     * @param key 消息key
     * @param params 参数数组
     * @param locale 语言环境
     * @return 国际化后的消息
     */
    protected String getMessage(String key, Object[] params, Locale locale) {
        return messageSource.getMessage(key, params, locale);
    }

    /**
     * 获取国际化消息（无参数）
     * @param key 消息key
     * @param locale 语言环境
     * @return 国际化后的消息
     */
    protected String getMessage(String key, Locale locale) {
        return getMessage(key, null, locale);
    }
}
