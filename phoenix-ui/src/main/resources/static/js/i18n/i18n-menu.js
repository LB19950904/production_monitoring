/**
 * 主页面导航菜单国际化处理
 * 用于处理主页面的导航菜单、标签页等的国际化
 */
const I18nMenu = {
    /**
     * 菜单翻译映射表
     */
    menuTranslations: {
        // 主菜单项（完整的英文菜单文本）
        'Home': 'menu.home',
        'home': 'menu.home',
        '主页': 'menu.home',

        'Server': 'menu.server',
        'Server Management': 'menu.server',
        'server': 'menu.server',
        '服务器': 'menu.server',

        'Application': 'menu.instance',
        'Instance Management': 'menu.instance',
        'instance': 'menu.instance',
        '应用程序': 'menu.instance',

        'Database': 'menu.database',
        'database': 'menu.database',
        '数据库': 'menu.database',

        'Network(PING)': 'menu.network',
        'Network': 'menu.network',
        'network': 'menu.network',
        '网络': 'menu.network',

        'Port(TCP)': 'menu.tcp',
        'TCP': 'menu.tcp',
        'tcp': 'menu.tcp',
        '端口': 'menu.tcp',

        'Interface(HTTP)': 'menu.http',
        'HTTP Monitoring': 'menu.http',
        'HTTP': 'menu.http',
        'http': 'menu.http',
        '接口': 'menu.http',

        'Docker': 'menu.docker',
        'Docker Management': 'menu.docker',
        'docker': 'menu.docker',
        '容器管理': 'menu.docker',

        'Network Device': 'menu.networkDevice',
        'Network Devices': 'menu.networkDevice',
        'networkDevice': 'menu.networkDevice',
        '网络设备': 'menu.networkDevice',

        'Topology': 'menu.topology',
        'Topological Graph': 'menu.topology',
        'topology': 'menu.topology',
        '拓扑图': 'menu.topology',

        'Alarm': 'menu.alarm',
        'Alarm Management': 'menu.alarm',
        'alarm': 'menu.alarm',
        '告警': 'menu.alarm',

        'Configuration': 'menu.config',
        'Config Management': 'menu.config',
        'set': 'menu.config',
        '配置管理': 'menu.config',

        'User Management': 'menu.user',
        'user': 'menu.user',
        '用户管理': 'menu.user',

        'Log': 'menu.log',
        'log': 'menu.log',
        '日志': 'menu.log',

        'My': 'menu.my',
        'myself': 'menu.my',
        '我的': 'menu.my',

        'User Profile': 'user.profile',
        '基本资料': 'user.profile',

        'Change Password': 'user.modify.password',
        '修改密码': 'user.modify.password',

        'Logout': 'menu.logout',
        '退出': 'menu.logout',

        'druid': 'Druid',
        'Knife4j': 'Knife4j',

        // 子菜单项 - Docker
        'Services': 'docker.services',
        '服务': 'docker.services',

        'Containers': 'docker.containers',
        '容器': 'docker.containers',

        'Images': 'docker.images',
        '镜像': 'docker.images',

        'Events': 'docker.events',
        '事件': 'docker.events',

        'Resources': 'docker.resources',
        '资源': 'docker.resources',

        // 子菜单项 - 拓扑图
        'Server topology': 'topology.server',
        '服务器拓扑图': 'topology.server',

        'Application topology': 'topology.instance',
        '应用程序拓扑图': 'topology.instance',

        'Network topology': 'topology.network',
        '网络拓扑图': 'topology.network',

        'Port topology': 'topology.tcp',
        '端口拓扑图': 'topology.tcp',

        'Interface topology': 'topology.http',
        '接口拓扑图': 'topology.http',

        // 子菜单项 - 配置管理
        '环境管理': 'config.envManagement',
        'Environment Management': 'config.envManagement',

        '分组管理': 'config.groupManagement',
        'Group Management': 'config.groupManagement',

        '监控配置': 'config.monitorConfig',
        'Monitor Configuration': 'config.monitorConfig',

        '告警定义': 'config.alarmDefinition',
        'Alarm Definition': 'config.alarmDefinition',

        // 子菜单项 - 用户管理
        'Users': 'menu.userList',
        '用户': 'menu.userList',

        'Roles': 'menu.roleList',
        '角色': 'menu.roleList',

        // 子菜单项 - 日志
        'Operation Logs': 'menu.operationLog',
        '操作日志': 'menu.operationLog',

        'Exception Logs': 'menu.exceptionLog',
        '异常日志': 'menu.exceptionLog',

        // 标签页操作
        '关闭当前标签页': 'tabs.close.current',
        '关闭其它标签页': 'tabs.close.others',
        '关闭全部标签页': 'tabs.close.all',
        'Close Current Tab': 'tabs.close.current',
        'Close Other Tabs': 'tabs.close.others',
        'Close All Tabs': 'tabs.close.all',

        // 主页标签
        '主页': 'menu.home',
        '服务器': 'menu.server',
        '应用程序': 'menu.instance',
        '数据库': 'menu.database',
        '网络': 'menu.network',
        '端口': 'menu.tcp',
        '接口': 'menu.http',
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
            // DOM已经加载，但可能菜单还未完全渲染，添加延迟
            setTimeout(() => {
                this.translateMenu();
            }, 200);
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
        console.log('Found main menu items:', menuItems.length);
        menuItems.forEach((item, index) => {
            const cite = item.querySelector('cite');
            if (cite && cite.textContent.trim()) {
                const originalText = cite.textContent.trim();
                const translated = this.getMenuTranslation(originalText);
                console.log(`Main menu ${index}: "${originalText}" -> "${translated}"`);
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
            let tips = item.getAttribute('lay-tips');
            // 如果lay-tips为空，从cite元素获取文本作为提示内容
            if (!tips) {
                const cite = item.querySelector('cite');
                if (cite && cite.textContent.trim()) {
                    tips = cite.textContent.trim();
                }
            }
            if (tips) {
                const translated = this.getMenuTranslation(tips);
                if (translated && translated !== tips) {
                    item.setAttribute('lay-tips', translated);
                } else {
                    item.setAttribute('lay-tips', tips);
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

        console.log('Translating menu item:', text);
        console.log('Current locale:', typeof I18nConfig !== 'undefined' ? I18nConfig.getCurrentLocale() : 'undefined');

        // 首先查找直接映射
        if (this.menuTranslations[text]) {
            const key = this.menuTranslations[text];
            const translated = I18nUtils.t(key) || text;
            console.log('Direct mapping found:', text, '->', key, '->', translated);
            return translated;
        }

        // 如果没有直接映射，尝试作为国际化key使用
        const translated = I18nUtils.t(text);
        console.log('I18nUtils.t result:', text, '->', translated);
        if (translated && translated !== text) {
            return translated;
        }

        console.log('No translation found for:', text, ', returning original');
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