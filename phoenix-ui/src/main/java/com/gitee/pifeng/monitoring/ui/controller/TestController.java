package com.gitee.pifeng.monitoring.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 测试控制器
 */
@Controller
public class TestController extends BaseController {

    /**
     * 国际化测试页面
     * @return 页面路径
     */
    @GetMapping("/test-i18n")
    public String testI18n() {
        return "test-i18n";
    }
}