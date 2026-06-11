/**
 * 国际化工具函数库
 * 提供各种国际化相关的工具方法
 */
const I18nUtils = {
    /**
     * 获取国际化文本
     * @param {string} key - 消息key，支持点号分隔的嵌套key
     * @param {Object} params - 参数对象
     * @returns {string} 国际化后的文本
     */
    t(key, params) {
        if (!key) {
            return '';
        }

        let message = I18nConfig.messages;

        // 处理嵌套key
        const keys = key.split('.');
        for (const k of keys) {
            if (message && typeof message === 'object' && k in message) {
                message = message[k];
            } else {
                // 找不到翻译时返回key
                console.warn(`Translation not found for key: ${key}`);
                return key;
            }
        }

        // 如果最终结果不是字符串，返回key
        if (typeof message !== 'string') {
            console.warn(`Translation is not a string for key: ${key}`);
            return key;
        }

        // 处理参数替换 {param} 格式
        if (params && typeof message === 'string') {
            Object.keys(params).forEach(param => {
                const regex = new RegExp(`\\{${param}\\}`, 'g');
                message = message.replace(regex, params[param]);
            });
        }

        return message;
    },

    /**
     * 格式化日期
     * @param {Date|string|number} date - 日期对象、字符串或时间戳
     * @param {string} format - 格式化模板
     * @returns {string} 格式化后的日期字符串
     */
    formatDate(date, format) {
        if (!date) {
            return '';
        }

        const dateObj = date instanceof Date ? date : new Date(date);

        if (isNaN(dateObj.getTime())) {
            return '';
        }

        const isEnglish = I18nConfig.isEnglish();

        // 如果没有指定格式，使用地区默认格式
        if (!format) {
            return dateObj.toLocaleDateString(isEnglish ? 'en-US' : 'zh-CN');
        }

        // 自定义格式化
        const year = dateObj.getFullYear();
        const month = String(dateObj.getMonth() + 1).padStart(2, '0');
        const day = String(dateObj.getDate()).padStart(2, '0');
        const hours = String(dateObj.getHours()).padStart(2, '0');
        const minutes = String(dateObj.getMinutes()).padStart(2, '0');
        const seconds = String(dateObj.getSeconds()).padStart(2, '0');

        return format
            .replace('YYYY', year)
            .replace('MM', month)
            .replace('DD', day)
            .replace('HH', hours)
            .replace('mm', minutes)
            .replace('ss', seconds);
    },

    /**
     * 格式化时间
     * @param {Date|string|number} date - 日期对象、字符串或时间戳
     * @returns {string} 格式化后的时间字符串
     */
    formatTime(date) {
        return this.formatDate(date, 'HH:mm:ss');
    },

    /**
     * 格式化日期时间
     * @param {Date|string|number} date - 日期对象、字符串或时间戳
     * @returns {string} 格式化后的日期时间字符串
     */
    formatDateTime(date) {
        return this.formatDate(date, 'YYYY-MM-DD HH:mm:ss');
    },

    /**
     * 格式化数字
     * @param {number} number - 数字
     * @param {number} decimals - 小数位数
     * @returns {string} 格式化后的数字字符串
     */
    formatNumber(number, decimals = 2) {
        if (isNaN(number)) {
            return '0';
        }

        const isEnglish = I18nConfig.isEnglish();
        return Number(number).toLocaleString(isEnglish ? 'en-US' : 'zh-CN', {
            minimumFractionDigits: decimals,
            maximumFractionDigits: decimals
        });
    },

    /**
     * 格式化百分比
     * @param {number} value - 数值（0-1之间）
     * @param {number} decimals - 小数位数
     * @returns {string} 格式化后的百分比字符串
     */
    formatPercent(value, decimals = 2) {
        if (isNaN(value)) {
            return '0%';
        }

        const isEnglish = I18nConfig.isEnglish();
        return (value * 100).toLocaleString(isEnglish ? 'en-US' : 'zh-CN', {
            minimumFractionDigits: decimals,
            maximumFractionDigits: decimals
        }) + '%';
    },

    /**
     * 格式化文件大小
     * @param {number} bytes - 字节数
     * @returns {string} 格式化后的文件大小字符串
     */
    formatFileSize(bytes) {
        if (isNaN(bytes) || bytes === 0) {
            return '0 B';
        }

        const units = ['B', 'KB', 'MB', 'GB', 'TB'];
        const index = Math.floor(Math.log(bytes) / Math.log(1024));
        const size = bytes / Math.pow(1024, index);

        return `${this.formatNumber(size, 2)} ${units[index]}`;
    },

    /**
     * 格式化持续时间
     * @param {number} seconds - 秒数
     * @returns {string} 格式化后的持续时间字符串
     */
    formatDuration(seconds) {
        if (isNaN(seconds) || seconds < 0) {
            return '0s';
        }

        const hours = Math.floor(seconds / 3600);
        const minutes = Math.floor((seconds % 3600) / 60);
        const secs = Math.floor(seconds % 60);

        const parts = [];
        if (hours > 0) {
            parts.push(`${hours}${this.t('time.hour')}`);
        }
        if (minutes > 0) {
            parts.push(`${minutes}${this.t('time.minute')}`);
        }
        if (secs > 0 || parts.length === 0) {
            parts.push(`${secs}${this.t('time.second')}`);
        }

        return parts.join(' ');
    },

    /**
     * 切换到指定语言
     * @param {string} locale - 目标语言代码
     */
    switchTo(locale) {
        I18nConfig.changeLanguage(locale);
    },

    /**
     * 切换到中文
     */
    switchToChinese() {
        this.switchTo('zh_CN');
    },

    /**
     * 切换到英文
     */
    switchToEnglish() {
        this.switchTo('en_US');
    },

    /**
     * 获取当前语言
     * @returns {string} 当前语言代码
     */
    getCurrentLocale() {
        return I18nConfig.getCurrentLocale();
    },

    /**
     * 是否为英文
     * @returns {boolean} 是否为英文
     */
    isEnglish() {
        return I18nConfig.isEnglish();
    },

    /**
     * 是否为中文
     * @returns {boolean} 是否为中文
     */
    isChinese() {
        return I18nConfig.isChinese();
    }
};

// 导出到全局
window.I18nUtils = I18nUtils;
