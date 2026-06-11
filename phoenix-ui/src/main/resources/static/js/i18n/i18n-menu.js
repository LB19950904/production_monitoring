/**
 * 主页面导航菜单国际化处理
 * 用于处理主页面的导航菜单、标签页等的国际化
 */
const I18nMenu = {
    /**
     * 菜单翻译映射表
     */
    menuTranslations: {
        // 主菜单项
        'home': 'menu.home',
        'server': 'menu.server',
        'instance': 'menu.instance',
        'database': 'menu.database',
        'network': 'menu.network',
        'tcp': 'menu.tcp',
        'http': 'menu.http',
        'docker': 'menu.docker',
        'networkDevice': 'menu.networkDevice',
        'topology': 'menu.topology',
        'alarm': 'menu.alarm',
        'set': 'menu.config',
        'user': 'menu.user',
        'log': 'menu.log',
        'myself': 'menu.user',
        'druid': 'Druid',
        'Knife4j': 'Knife4j',

        // 子菜单项 - Docker
        '服务': 'docker.services',
        '容器': 'docker.containers',
        '镜像': 'docker.images',
        '事件': 'docker.events',
        '资源': 'docker.resources',

        // 子菜单项 - 拓扑图
        '服务器拓扑图': 'topology.server',
        '应用程序拓扑图': 'topology.instance',
        '网络拓扑图': 'topology.network',
        '端口拓扑图': 'topology.tcp',
        '接口拓扑图': 'topology.http',

        // 子菜单项 - 配置管理
        '环境管理': 'config.envManagement',
        '分组管理': 'config.groupManagement',
        '监控配置': 'config.monitorConfig',
        '告警定义': 'config.alarmDefinition',

        // 子菜单项 - 用户管理
        '用户': 'menu.userList',
        '角色': 'menu.roleList',

        // 子菜单项 - 日志
        '操作日志': 'menu.operationLog',
        '异常日志': 'menu.exceptionLog',

        // 子菜单项 - 我的
        '基本资料': 'user.profile',
        '修改密码': 'user.modify.password',

        // 标签页操作
        '关闭当前标签页': 'tabs.close.current',
        '关闭其它标签页': 'tabs.close.others',
        '关闭全部标签页': 'tabs.close.all',

        // 主页标签
        '主页': 'menu.home',
        '服务器': 'menu.server',
        '应用程序': 'menu.instance',
        '数据库': 'menu.database',
        '网络(PING)': 'menu.network',
        '端口(TCP)': 'menu.tcp',
        '接口(HTTP)': 'menu.http',
        '网络设备': 'menu.networkDevice',
        '拓扑图': 'menu.topology',
        '告警': 'menu.alarm',
        '配置管理': 'menu.config',
        '用户管理': 'menu.user',
        '日志': 'menu.log',
        '我的': 'menu.my'
    },

    /**
     * 初始化菜单国际化
     */
    init() {
        console.log('Initializing menu internationalization...');

        // 等待DOM加载完成
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => {
                this.translateMenu();
            });
        } else {
            this.translateMenu();
        }

        // 监听语言切换事件
        this.listenLanguageChange();
    },

    /**
     * 翻译导航菜单
     */
    translateMenu() {
        console.log('Translating menu...');

        // 翻译主菜单项
        this.translateMainMenuItems();

        // 翻译子菜单项
        this.translateSubMenuItems();

        // 翻译标签页操作
        this.translateTabOperations();

        // 翻译lay-tips属性
        this.translateLayTips();

        console.log('Menu translation completed');
    },

    /**
     * 翻译主菜单项
     */
    translateMainMenuItems() {
        const menuItems = document.querySelectorAll('#LAY-system-side-menu > .layui-nav-item > a');
        menuItems.forEach(item => {
            const cite = item.querySelector('cite');
            if (cite && cite.textContent.trim()) {
                const originalText = cite.textContent.trim();
                const translated = this.getMenuTranslation(originalText);
                if (translated && translated !== originalText) {
                    cite.textContent = translated;
                }
            }
        });
    },

    /**
     * 翻译子菜单项
     */
    translateSubMenuItems() {
        const subMenuItems = document.querySelectorAll('.layui-nav-child a');
        subMenuItems.forEach(item => {
            const originalText = item.textContent.trim();
            if (originalText) {
                const translated = this.getMenuTranslation(originalText);
                if (translated && translated !== originalText) {
                    item.textContent = translated;
                }
            }
        });
    },

    /**
     * 翻译标签页操作
     */
    translateTabOperations() {
        const tabOperations = document.querySelectorAll('.layadmin-tabs-select a');
        tabOperations.forEach(item => {
            const originalText = item.textContent.trim();
            if (originalText) {
                const translated = this.getMenuTranslation(originalText);
                if (translated && translated !== originalText) {
                    item.textContent = translated;
                }
            }
        });
    },

    /**
     * 翻译lay-tips属性
     */
    translateLayTips() {
        const tipsElements = document.querySelectorAll('#LAY-system-side-menu a[lay-tips]');
        tipsElements.forEach(item => {
            const tips = item.getAttribute('lay-tips');
            if (tips) {
                const translated = this.getMenuTranslation(tips);
                if (translated && translated !== tips) {
                    item.setAttribute('lay-tips', translated);
                }
            }
        });
    },

    /**
     * 获取菜单翻译
     * @param {string} text - 原始文本
     * @returns {string} 翻译后的文本
     */
    getMenuTranslation(text) {
        if (!text) return text;

        // 首先查找直接映射
        if (this.menuTranslations[text]) {
            const key = this.menuTranslations[text];
            return I18nUtils.t(key) || text;
        }

        // 如果没有直接映射，尝试作为国际化key使用
        const translated = I18nUtils.t(text);
        if (translated && translated !== text) {
            return translated;
        }

        return text;
    },

    /**
     * 监听语言切换事件
     */
    listenLanguageChange() {
        // 监听自定义语言切换事件
        document.addEventListener('languageChanged', (event) => {
            console.log('Language changed to:', event.detail.locale);
            // 延迟执行，确保DOM更新完成
            setTimeout(() => {
                this.translateMenu();
            }, 100);
        });
    },

    /**
     * 手动刷新翻译
     */
    refresh() {
        this.translateMenu();
    }
};

// 导出到全局
window.I18nMenu = I18nMenu;

// 自动初始化
if (typeof window !== 'undefined') {
    // 在页面加载后初始化
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            I18nMenu.init();
        });
    } else {
        I18nMenu.init();
    }
}