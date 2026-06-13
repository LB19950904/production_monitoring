package com.gitee.pifeng.monitoring.ui.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

/**
 * 自定义Cookie语言解析器
 * 增强对Cookie值的解析和容错处理
 */
@Slf4j
public class CustomCookieLocaleResolver extends CookieLocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        // 获取Cookie值
        String cookieValue = getCookieValue(request);
        log.info("CustomCookieLocaleResolver解析Cookie：cookieName={}, cookieValue={}",
                getCookieName(), cookieValue);

        if (StringUtils.hasText(cookieValue)) {
            // 尝试解析Cookie值为Locale
            try {
                Locale locale = parseLocale(cookieValue);
                log.info("成功解析Locale：{}", locale);
                return locale;
            } catch (Exception e) {
                log.warn("解析Cookie值失败：{}，使用默认Locale", cookieValue, e);
            }
        }

        // 如果Cookie无效，使用默认Locale
        Locale defaultLocale = getDefaultLocale();
        log.info("使用默认Locale：{}", defaultLocale);
        return defaultLocale;
    }

    /**
     * 解析Cookie字符串为Locale对象
     * 支持多种格式：zh_CN, zh-CN, zh_CN等
     */
    private Locale parseLocale(String localeValue) {
        if (localeValue == null || localeValue.trim().isEmpty()) {
            return getDefaultLocale();
        }

        // 处理常见格式
        String normalized = localeValue.trim().replace("-", "_");

        // 处理zh_CN, zh_CN等格式
        if (normalized.contains("_")) {
            String[] parts = normalized.split("_");
            if (parts.length == 2) {
                String language = parts[0].toLowerCase();
                String country = parts[1].toUpperCase();
                return new Locale(language, country);
            }
        }

        // 处理简单的语言代码如zh, en
        if (normalized.length() == 2) {
            return new Locale(normalized.toLowerCase());
        }

        // 默认返回
        return getDefaultLocale();
    }

    /**
     * 获取Cookie值
     */
    private String getCookieValue(HttpServletRequest request) {
        javax.servlet.http.Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (javax.servlet.http.Cookie cookie : cookies) {
                if (getCookieName().equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
