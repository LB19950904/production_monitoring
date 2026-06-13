package com.gitee.pifeng.monitoring.ui.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Slf4j
@Configuration
public class I18nConfig {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource =
                new ResourceBundleMessageSource();
        // 支持中英文两种语言，明确指定所有可用的basename
        messageSource.setBasenames("i18n/messages", "i18n/validation");
        messageSource.setDefaultEncoding("UTF-8");
        // 减少缓存时间，便于开发调试
        messageSource.setCacheSeconds(1800); // 30分钟
        messageSource.setUseCodeAsDefaultMessage(true);
        // 设置父MessageSource，确保找不到消息时有回退机制
        messageSource.setParentMessageSource(null);
        log.info("MessageSource配置完成：basenames={}, defaultEncoding=UTF-8",
                "i18n/messages, i18n/validation");
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        log.info("初始化自定义CookieLocaleResolver");
        CustomCookieLocaleResolver localeResolver = new CustomCookieLocaleResolver();
        localeResolver.setCookieName("language");
        localeResolver.setCookieMaxAge(3600 * 24 * 30); // 30天
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE); // 默认中文
        localeResolver.setCookieHttpOnly(false); // 允许JavaScript访问Cookie
        // 设置Cookie路径，确保整个应用都能访问
        localeResolver.setCookiePath("/");
        log.info("CookieLocaleResolver配置完成：cookieName={}, defaultLocale={}",
                "language", Locale.SIMPLIFIED_CHINESE);
        return localeResolver;
    }
}