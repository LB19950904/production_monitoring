/**
 * 国际化配置文件
 * 用于管理中英文语言切换
 * 支持浏览器语言自动检测和切换
 */
const I18nConfig = {
    // 默认语言（当浏览器语言不是中文或英文时使用）
    defaultLocale: 'en_US',

    // 当前语言
    currentLocale: 'en_US',

    // 支持的语言列表
    supportedLocales: ['zh_CN', 'en_US'],

    // Cookie名称
    cookieName: 'language',

    // 语言包缓存
    messages: {},

    // 是否已初始化
    initialized: false,

    // 支持的中文语言变体映射
    chineseLocales: ['zh', 'zh-CN', 'zh-TW', 'zh-HK', 'zh-SG', 'zh-MO'],

    // 支持的英文语言变体映射
    englishLocales: ['en', 'en-US', 'en-GB', 'en-AU', 'en-CA', 'en-IN', 'en-NZ', 'en-ZA', 'en-IE'],

    /**
     * 检测浏览器语言
     * @returns {string} 检测到的语言代码
     */
    detectBrowserLanguage() {
        // 获取浏览器语言设置
        const browserLang = (navigator.language || navigator.userLanguage || 'en').toLowerCase();

        console.log('Detected browser language:', browserLang);

        // 检查是否为中文语言变体
        if (this.isChineseLocale(browserLang)) {
            console.log('Detected Chinese locale, using zh_CN');
            return 'zh_CN';
        }

        // 检查是否为英文语言变体
        if (this.isEnglishLocale(browserLang)) {
            console.log('Detected English locale, using en_US');
            return 'en_US';
        }

        // 如果不是中文也不是英文，使用默认的英文
        console.log('Detected unsupported locale, using default en_US');
        return this.defaultLocale;
    },

    /**
     * 判断是否为中文语言变体
     * @param {string} locale - 语言代码
     * @returns {boolean} 是否为中文语言变体
     */
    isChineseLocale(locale) {
        const lowerLocale = locale.toLowerCase();
        return this.chineseLocales.some(chineseLocale =>
            lowerLocale === chineseLocale.toLowerCase() ||
            lowerLocale.startsWith(chineseLocale.toLowerCase())
        );
    },

    /**
     * 判断是否为英文语言变体
     * @param {string} locale - 语言代码
     * @returns {boolean} 是否为英文语言变体
     */
    isEnglishLocale(locale) {
        const lowerLocale = locale.toLowerCase();
        return this.englishLocales.some(englishLocale =>
            lowerLocale === englishLocale.toLowerCase() ||
            lowerLocale.startsWith(englishLocale.toLowerCase())
        );
    },

    /**
     * 初始化国际化配置
     */
    init() {
        if (this.initialized) {
            return;
        }

        // 从Cookie获取语言设置
        const savedLang = this.getCookie(this.cookieName);
        if (savedLang && this.supportedLocales.includes(savedLang)) {
            // 如果Cookie中有有效的语言设置，使用Cookie中的设置
            this.currentLocale = savedLang;
            console.log('Using saved language from cookie:', savedLang);
        } else {
            // 否则检测浏览器语言
            this.currentLocale = this.detectBrowserLanguage();

            // 如果检测到了有效的语言，保存到Cookie中
            if (this.currentLocale) {
                this.setCookie(this.cookieName, this.currentLocale, 30);
                console.log('Detected and saved browser language:', this.currentLocale);
            }
        }

        // 设置HTML语言属性
        document.documentElement.lang = this.currentLocale;

        // 加载对应语言包
        this.loadMessages(this.currentLocale);

        this.initialized = true;

        console.log('I18n initialized with locale:', this.currentLocale);
    },

    /**
     * 获取Cookie值
     * @param {string} name - Cookie名称
     * @returns {string|null} Cookie值
     */
    getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) {
            return parts.pop().split(';').shift();
        }
        return null;
    },

    /**
     * 设置Cookie
     * @param {string} name - Cookie名称
     * @param {string} value - Cookie值
     * @param {number} days - 有效天数
     */
    setCookie(name, value, days) {
        const expires = new Date();
        expires.setTime(expires.getTime() + days * 24 * 60 * 60 * 1000);
        document.cookie = `${name}=${value};expires=${expires.toUTCString()};path=/`;
    },

    /**
     * 加载语言包
     * @param {string} locale - 语言代码
     */
    loadMessages(locale) {
        // 使用全局语言包变量
        if (locale === 'zh_CN') {
            this.messages = window.ZhMessages || {};
        } else if (locale === 'en_US') {
            this.messages = window.EnMessages || {};
        }
    },

    /**
     * 切换语言
     * @param {string} locale - 目标语言代码
     */
    changeLanguage(locale) {
        if (!this.supportedLocales.includes(locale)) {
            console.error('Unsupported locale:', locale);
            return;
        }

        const oldLocale = this.currentLocale;

        console.log('changeLanguage: 从', oldLocale, '切换到', locale);

        // 设置Cookie
        this.setCookie(this.cookieName, locale, 30);

        console.log('Cookie已设置为:', locale, '，验证Cookie:', this.getCookie(this.cookieName));

        // 更新当前语言
        this.currentLocale = locale;

        // 更新HTML语言属性
        document.documentElement.lang = locale;

        // 重新加载语言包
        this.loadMessages(locale);

        // 触发语言切换事件
        this.dispatchLanguageChangedEvent(locale, oldLocale);

        // 重新加载页面以应用所有更改
        window.location.reload();
    },

    /**
     * 触发语言切换事件
     * @param {string} newLocale - 新语言代码
     * @param {string} oldLocale - 旧语言代码
     */
    dispatchLanguageChangedEvent(newLocale, oldLocale) {
        const event = new CustomEvent('languageChanged', {
            detail: {
                newLocale: newLocale,
                oldLocale: oldLocale,
                locale: newLocale // 为了兼容性
            }
        });
        document.dispatchEvent(event);
        console.log('Language changed event dispatched:', newLocale);
    },

    /**
     * 获取当前语言
     * @returns {string} 当前语言代码
     */
    getCurrentLocale() {
        return this.currentLocale;
    },

    /**
     * 是否为英文
     * @returns {boolean} 是否为英文
     */
    isEnglish() {
        return this.currentLocale === 'en_US';
    },

    /**
     * 是否为中文
     * @returns {boolean} 是否为中文
     */
    isChinese() {
        return this.currentLocale === 'zh_CN';
    },

    /**
     * 获取支持的语言列表
     * @returns {Array} 语言列表
     */
    getSupportedLocales() {
        return this.supportedLocales;
    },

    /**
     * 获取语言检测信息（用于调试）
     * @returns {Object} 语言检测信息
     */
    getDetectionInfo() {
        const browserLang = (navigator.language || navigator.userLanguage || 'unknown').toLowerCase();
        const detectedLocale = this.detectBrowserLanguage();

        return {
            browserLanguage: browserLang,
            detectedLocale: detectedLocale,
            currentLocale: this.currentLocale,
            cookieLanguage: this.getCookie(this.cookieName),
            isChineseLocale: this.isChineseLocale(browserLang),
            isEnglishLocale: this.isEnglishLocale(browserLang),
            supportedLocales: this.supportedLocales,
            defaultLocale: this.defaultLocale
        };
    },

    /**
     * 手动触发语言重新检测
     * 用于测试和调试
     */
    redetectLanguage() {
        // 删除现有的语言Cookie
        this.setCookie(this.cookieName, '', -1);

        // 重新检测浏览器语言
        const newLocale = this.detectBrowserLanguage();

        // 如果检测到不同的语言，进行切换
        if (newLocale !== this.currentLocale) {
            console.log('Language redetection triggered, switching from', this.currentLocale, 'to', newLocale);
            this.changeLanguage(newLocale);
        } else {
            console.log('Language redetection: no change needed, current locale is', this.currentLocale);
        }
    },

    /**
     * 切换语言（在中英文之间切换）
     */
    toggleLanguage() {
        // 从I18nConfig获取当前语言，如果没有则使用默认中文
        const currentLang = this.currentLocale || 'zh_CN';
        const targetLocale = currentLang === 'zh_CN' ? 'en_US' : 'zh_CN';

        console.log('当前语言:', currentLang, '，目标语言:', targetLocale);
        console.log('切换前的Cookie:', this.getCookie(this.cookieName));

        // 调用changeLanguage来切换语言并保存到Cookie
        this.changeLanguage(targetLocale);
    },

    /**
     * 获取当前语言的显示名称
     * @returns {string} 语言显示名称
     */
    getCurrentLocaleDisplayName() {
        const displayNames = {
            'zh_CN': '中文',
            'en_US': 'English'
        };
        return displayNames[this.currentLocale] || this.currentLocale;
    }
};

// 页面加载完成后初始化
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', () => {
        I18nConfig.init();
    });
} else {
    I18nConfig.init();
}

// 导出到全局
window.I18nConfig = I18nConfig;

// 全局语言切换函数（供 HTML onclick 调用）
window.toggleLanguage = function() {
    I18nConfig.toggleLanguage();
};

// 全局语言切换函数（保持向后兼容）
window.changeLanguage = function(locale) {
    I18nConfig.changeLanguage(locale);
};
