package com.gitee.pifeng.monitoring.ui.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

@Configuration
public class I18nConfig {
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource =
                new ResourceBundleMessageSource();
        // 只支持中英文两种语言
        messageSource.setBasenames("i18n/messages", "i18n/validation");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setCacheSeconds(3600);
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver();
        localeResolver.setCookieName("language");
        localeResolver.setCookieMaxAge(3600 * 24 * 30); // 30天
        localeResolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE); // 默认中文
        return localeResolver;
    }
}