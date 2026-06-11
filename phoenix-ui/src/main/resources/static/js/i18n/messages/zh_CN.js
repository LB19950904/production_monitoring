/**
 * 中文语言包
 */
const ZhMessages = {
    // 通用模块
    common: {
        language: '语言',
        projectName: '生产通讯保障系统',
        ellipsis: '......',
        projectShortName: '保障系统',
        save: '保存',
        cancel: '取消',
        confirm: '确认',
        delete: '删除',
        edit: '编辑',
        add: '新增',
        search: '搜索',
        reset: '重置',
        export: '导出',
        import: '导入',
        refresh: '刷新',
        back: '返回',
        submit: '提交',
        close: '关闭',
        id: 'ID',
        account: '账号',
        username: '用户名',
        email: '邮箱',
        role: '角色',
        description: '描述',
        remarks: '备注',
        registerTime: '注册时间',
        updateTime: '更新时间',
        actions: '操作',
        pleaseEnter: '请输入',
        clear: '清空'
    },

    // 导航菜单
    menu: {
        home: '主页',
        server: '服务器',
        instance: '应用程序',
        database: '数据库',
        network: '网络(PING)',
        tcp: '端口(TCP)',
        http: '接口(HTTP)',
        docker: 'DOCKER',
        networkDevice: '网络设备',
        topology: '拓扑图',
        alarm: '告警',
        config: '配置管理',
        user: '用户管理',
        log: '日志',
        my: '我的',

        // Docker子菜单
        dockerService: '服务',
        dockerContainer: '容器',
        dockerImage: '镜像',
        dockerEvent: '事件',
        dockerResource: '资源',

        // 拓扑图子菜单
        serverTopology: '服务器拓扑图',
        appTopology: '应用程序拓扑图',
        networkTopology: '网络拓扑图',
        portTopology: '端口拓扑图',
        interfaceTopology: '接口拓扑图',

        // 配置管理子菜单
        envManagement: '环境管理',
        groupManagement: '分组管理',
        monitorConfig: '监控配置',
        alarmDefinition: '告警定义',

        // 用户管理子菜单
        userList: '用户',
        roleList: '角色',

        // 日志子菜单
        operationLog: '操作日志',
        exceptionLog: '异常日志',

        // 其他
        druid: 'Druid',
        knife4j: 'Knife4j',
        logout: '退出'
    },

    // 配置管理模块
    config: {
        envManagement: '环境管理',
        groupManagement: '分组管理',
        monitorConfig: '监控配置',
        alarmDefinition: '告警定义'
    },

    // 告警模块
    alarm: {
        title: '告警管理',
        rule: {
            list: '告警规则列表',
            name: '规则名称',
            type: '规则类型',
            threshold: '阈值',
            level: '告警级别'
        },
        record: {
            list: '告警记录列表'
        },
        level: {
            info: '信息',
            warn: '警告',
            error: '错误',
            fatal: '严重'
        },
        status: {
            enabled: '启用',
            disabled: '禁用'
        }
    },

    // 数据库模块
    db: {
        title: '数据库管理',
        type: {
            mysql: 'MySQL',
            oracle: 'Oracle',
            redis: 'Redis',
            mongo: 'MongoDB'
        },
        connection: {
            status: '连接状态',
            active: '正常',
            inactive: '异常'
        },
        slowSql: {
            list: '慢SQL记录'
        },
        session: {
            list: '会话列表'
        },
        table: {
            space: '表空间'
        },
        pool: {
            info: '连接池信息'
        }
    },

    // Docker模块
    docker: {
        title: '容器管理',
        services: '服务',
        containers: '容器',
        images: '镜像',
        events: '事件',
        resources: '资源',
        container: {
            list: '容器列表',
            name: '容器名称',
            status: '容器状态',
            cpu: 'CPU使用率',
            memory: '内存使用',
            network: '网络流量',
            disk: '磁盘使用'
        },
        image: {
            list: '镜像列表',
            size: '镜像大小'
        }
    },

    // HTTP监控模块
    http: {
        title: 'HTTP监控',
        url: '请求地址',
        method: '请求方法',
        avgTime: '平均响应时间',
        maxTime: '最大响应时间',
        minTime: '最小响应时间',
        errorRate: '错误率',
        requestCount: '请求数量',
        successRate: '成功率'
    },

    // 实例模块
    instance: {
        title: '实例管理',
        name: '实例名称',
        ip: '实例IP',
        port: '实例端口',
        status: '实例状态',
        threadPool: '线程池',
        jvm: 'JVM信息',
        memory: '内存使用',
        cpu: 'CPU使用',
        gc: '垃圾回收'
    },

    // 日志模块
    log: {
        title: '日志管理',
        exception: '异常日志',
        level: '日志级别',
        time: '日志时间',
        content: '日志内容',
        search: '日志搜索',
        filter: '日志筛选',
        detail: '日志详情'
    },

    // 用户模块
    user: {
        title: '用户管理',
        username: '用户名',
        password: '密码',
        role: '角色',
        permission: '权限',
        login: '登录',
        logout: '退出',
        profile: '个人资料',
        setting: '个人设置',
        loginTime: '登录时间',
        loginIp: '登录IP',
        modify: {
            password: '修改密码'
        },
        account: '账号',
        email: '邮箱',
        registerTime: '注册时间',
        updateTime: '更新时间',
        description: '描述',
        id: 'ID',
        remarks: '备注',
        // 个人资料页面
        profilePage: {
            title: '设置我的资料',
            id: 'ID',
            account: '账号',
            myRole: '我的角色',
            email: '邮箱',
            remarks: '备注',
            placeholder: {
                username: '请输入用户名',
                content: '请输入内容'
            }
        },
        // 密码修改页面
        passwordPage: {
            title: '设置我的密码',
            currentPassword: '当前密码',
            newPassword: '新密码',
            confirmPassword: '确认新密码',
            confirmPasswordButton: '确认修改',
            passwordHint: '6到16个字符',
            validation: {
                length: '密码必须6到30位，且不能出现空格',
                mismatch: '两次输入的新密码不一致，请重新输入！',
                currentIncorrect: '当前密码输入不正确，请重新输入！',
                success: '密码修改成功！',
                failed: '密码修改失败！'
            }
        },

        // 用户列表页面
        listPage: {
            title: '用户管理',
            account: '账号',
            username: '用户名',
            email: '邮箱',
            role: '角色',
            registerTime: '注册时间',
            updateTime: '更新时间',
            description: '描述',
            id: 'ID',
            remarks: '备注',
            actions: '操作',
            searchPlaceholder: '请输入',
            clearButton: '清空',
            addButton: '添加',
            editButton: '编辑',
            deleteButton: '删除',
            batchDelete: '批量删除'
        }
    },

    // 表单验证
    validation: {
        required: '此字段为必填项',
        email: '请输入有效的邮箱地址',
        phone: '请输入有效的手机号码',
        number: '请输入有效的数字',
        minLength: '长度不能小于{0}个字符',
        maxLength: '长度不能大于{0}个字符',
        passwordNotMatch: '两次输入的密码不一致',
        username: {
            empty: '用户名不能为空',
            specialChars: '用户名不能有特殊字符',
            startEndUnderscore: '用户名首尾不能出现下划线\'_\'',
            allNumbers: '用户名不能全为数字'
        },
        emailFormat: '邮箱格式不正确'
    },

    // 系统提示
    system: {
        success: '操作成功',
        error: '操作失败',
        loading: '加载中...',
        noData: '暂无数据',
        networkError: '网络异常，请稍后重试',
        serverError: '服务器异常，请联系管理员',
        timeout: '操作超时，请重试'
    },

    // 操作按钮
    btn: {
        add: '新增',
        edit: '编辑',
        delete: '删除',
        save: '保存',
        cancel: '取消',
        search: '查询',
        reset: '重置',
        export: '导出',
        import: '导入',
        detail: '详情',
        back: '返回',
        more: '更多'
    },

    // 登录模块
    login: {
        title: '登录',
        subtitle: '生产通讯保障系统',
        account: '账号',
        password: '密码',
        captcha: '图形验证码',
        placeholder: {
            account: '请输入账号',
            password: '请输入密码',
            captcha: '请输入图形验证码'
        },
        remember: '记住我',
        submit: '登入',
        error: {
            accountError: '账号或密码错误，请重新输入！',
            expire: '此账号已被管理员修改，您被迫下线！',
            timeout: '登录已超时，请重新登录！',
            captchaEmpty: '图形验证码不能为空！',
            captchaNotexist: '图形验证码不存在！',
            captchaExpired: '图形验证码已过期！',
            captchaFailed: '图形验证码校验失败！',
            accountEmpty: '账号不能为空！',
            passwordEmpty: '密码不能为空！',
            captchaRequired: '图形验证码不能为空！'
        },
        footer: {
            copyright: '版权所有 2020-2025 皮锋'
        }
    },
    // 拓扑模块
    topology: {
        server: '服务器拓扑图',
        instance: '应用程序拓扑图',
        network: '网络拓扑图',
        tcp: '端口拓扑图',
        http: '接口拓扑图'
    },

    // TCP监控
    tcp: {
        title: 'TCP监控',
        port: '端口',
        status: '连接状态'
    },

    // 日志操作
    log: {
        operation: '操作日志'
    },

    // 标签页操作
    tabs: {
        close: {
            current: '关闭当前标签页',
            others: '关闭其它标签页',
            all: '关闭全部标签页'
        }
    },

    // 系统操作
    system: {
        operation: {
            confirm: '确定要执行此操作吗？',
            confirmDelete: '确定要删除吗？'
        },
        message: {
            saveSuccess: '保存成功！',
            saveFailed: '保存失败！',
            systemError: '系统错误！',
            operationSuccess: '操作成功！',
            operationFailed: '操作失败！'
        }
    },

    // 个人资料模块
    profile: {
        title: '设置我的资料',
        saveSuccess: '保存成功！',
        saveFailed: '保存失败！',
        systemError: '系统错误！'
    },

    // 首页模块
    home: {
        // 页面标题
        pageTitle: '首页',

        // 统计卡片标题
        server: '服务器',
        networkDevice: '网络设备',
        docker: 'Docker',
        application: '应用程序',
        database: '数据库',
        network: '网络',
        tcp: 'TCP',
        http: 'HTTP',
        alarmNotification: '告警通知',

        // 状态描述
        online: '在线',
        offline: '离线',
        unknown: '未知',
        normal: '正常',
        abnormal: '异常',
        success: '成功',
        failure: '失败',
        noAlert: '不提醒',

        // 单位标识
        unit: '台',
        item: '个',
        times: '次数',
        timesUnit: '次',

        // 操作系统类型
        windows: 'Windows',
        linux: 'Linux',
        other: '其他',

        // 图表区域
        alarmStatistics: '告警统计',
        latestAlarms: '最新告警',

        // 百分比
        percentage: '百分比',

        // 图表相关
        dataTrend: '数据趋势',
        resultTrend: '结果趋势',
        resultRatio: '结果比例',
        successRate: '成功率',
        totalCount: '总数',
        items: '条',
        vulnerabilityStatistics: '漏洞统计'
    },

    // 环境管理模块
    env: {
        title: '环境管理',
        name: '环境名称',
        description: '环境描述',
        placeholder: {
            name: '请输入环境名称',
            description: '请输入环境描述'
        },
        validation: {
            nameRequired: '环境名称不能为空',
            descriptionRequired: '环境描述不能为空'
        },
        add: '添加环境',
        edit: '编辑环境'
    },

    // 分组管理模块
    group: {
        title: '分组管理',
        name: '分组名称',
        description: '分组描述',
        placeholder: {
            name: '请输入分组名称',
            description: '请输入分组描述'
        },
        validation: {
            nameRequired: '分组名称不能为空'
        },
        add: '添加分组',
        edit: '编辑分组'
    },

    // 告警定义模块
    alarm: {
        id: '告警ID',
        code: '告警编码',
        type: '告警类型',
        level: '告警级别',
        title: '告警标题',
        content: '告警内容',
        firstClass: '一级分类',
        secondClass: '二级分类',
        thirdClass: '三级分类',
        custom: '自定义',
        message: '消息',
        warning: '警告',
        error: '错误',
        fatal: '严重',
        placeholder: {
            code: '请输入告警编码',
            title: '请输入告警标题',
            content: '请输入告警内容',
            firstClass: '请输入一级分类',
            secondClass: '请输入二级分类',
            thirdClass: '请输入三级分类'
        },
        validation: {
            codeRequired: '告警编码不能为空',
            titleRequired: '告警标题不能为空',
            contentRequired: '告警内容不能为空',
            firstClassRequired: '一级分类不能为空'
        },
        add: '添加告警定义',
        edit: '编辑告警定义'
    },

    // 数据库模块
    database: {
        type: '类型',
        connName: '连接名',
        url: 'URL',
        username: '用户名',
        password: '密码',
        monitorEnv: '环境',
        monitorGroup: '分组',
        isEnableMonitor: '是否监控',
        isEnableAlarm: '是否告警',
        dbDesc: '描述',
        databaseId: '数据库ID',
        placeholder: {
            connName: '请输入连接名',
            url: '请输入URL',
            username: '请输入用户名',
            password: '请输入密码',
            description: '请输入描述'
        },
        validation: {
            connNameRequired: '连接名不能为空',
            urlRequired: 'URL不能为空',
            usernameRequired: '用户名不能为空',
            passwordRequired: '密码不能为空'
        },
        add: '添加数据库',
        edit: '编辑数据库'
    },

    // 通用表单标签
    form: {
        id: 'ID',
        account: '账号',
        username: '用户名',
        password: '密码',
        email: '邮箱',
        remarks: '备注',
        description: '描述',
        role: '选择角色',
        confirmPassword: '确认密码',
        oldPassword: '当前密码',
        newPassword: '新密码',
        passwordHint: '密码不填则不修改',
        placeholder: {
            account: '请输入账号',
            username: '请输入用户名',
            password: '请输入密码',
            email: '请输入邮箱',
            remarks: '请输入备注',
            confirmPassword: '请确认密码',
            oldPassword: '请输入当前密码',
            newPassword: '请输入新密码'
        },
        validation: {
            accountRequired: '账号不能为空',
            usernameRequired: '用户名不能为空',
            passwordRequired: '密码不能为空',
            passwordLength: '密码必须6到30位，且不能出现空格',
            emailFormat: '邮箱格式不正确',
            passwordMismatch: '两次输入的密码不一致',
            usernameSpecialChars: '用户名不能有特殊字符',
            usernameUnderscore: '用户名首尾不能出现下划线\'_\'',
            usernameAllNumbers: '用户名不能全为数字'
        },
        confirm: '确认',
        confirmPassword: '确认修改'
    },

    // 页面标题
    page: {
        addUser: '添加用户',
        editUser: '编辑用户',
        addEnv: '添加监控环境',
        editEnv: '编辑监控环境',
        addGroup: '添加分组',
        editGroup: '编辑分组',
        addAlarm: '添加告警定义',
        editAlarm: '编辑告警定义',
        addDatabase: '添加数据库',
        editDatabase: '编辑数据库',
        editServer: '编辑服务器信息',
        clearServerData: '清理服务器监控历史数据',
        serverList: '服务器'
    },

    // 服务器管理模块
    server: {
        title: '服务器管理',
        ip: 'IP',
        system: '系统',
        serverName: '服务器名',
        status: '状态',
        online: '在线',
        offline: '离线',
        unknown: '未知',
        all: '所有',
        yes: '是',
        no: '否',
        notSet: '不设置',
        description: '描述',
        placeholder: {
            description: '请输入描述',
            search: '请输入'
        },
        // 数据清理时间选项
        clearData: {
            hour: '一小时前数据',
            day: '一天前数据',
            week: '一星期前数据',
            month: '一个月前数据',
            halfYear: '半年前数据',
            year: '一年前数据',
            all: '所有数据'
        },
        // 状态选项
        statusOptions: {
            online: '在线',
            offline: '离线',
            unknown: '未知'
        },
        // 监控选项
        monitorOptions: {
            yes: '是',
            no: '否'
        },
        validation: {
            descriptionRequired: '描述不能为空'
        }
    }
};

// 导出到全局
window.ZhMessages = ZhMessages;