/**
 * 中文语言包
 */
if (typeof ZhMessages === 'undefined') {
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
        clear: '清空',
        // 表格工具栏（筛选、导出、打印）
        toolbar: {
            filterColumns: '筛选列',
            export: '导出',
            print: '打印',
            exportCsv: '导出到 Csv 文件',
            exportXls: '导出到 Excel 文件'
        },
        // 分页组件
        pagination: {
            total: '共 {count} 条',
            perPage: '{count} 条/页',
            skipTo: '到第',
            page: '页',
            go: '确定'
        }
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
        more: '更多',
        clear: '清理'
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
            captcha: '请输入图形验证码',
            language: '请选择语言'
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
        offlineCount: '离线次数',
        cpu: 'CPU(%)',
        memory: '内存(%)',
        loadAverage: '负载',
        downloadSpeed: '下行速率(↓)',
        uploadSpeed: '上行速率(↑)',
        finalHeartbeat: '最后心跳',
        monitorEnv: '环境',
        monitorGroup: '分组',
        insertTime: '新增时间',
        isEnableMonitor: '是否监控',
        isEnableAlarm: '是否告警',
        actions: '操作',
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
    },

    // 应用程序模块
    instance: {
        title: '应用程序',
        instanceId: '应用ID',
        instanceName: '应用名称',
        endpoint: '端点',
        ip: 'IP',
        status: '应用状态',
        offlineCount: '离线次数',
        appServerType: '应用服务器',
        language: '程序语言',
        finalHeartbeat: '最后心跳',
        monitorEnv: '环境',
        monitorGroup: '分组',
        description: '描述',
        insertTime: '新增时间',
        isEnableMonitor: '是否监控',
        isEnableAlarm: '是否告警',
        actions: '操作',
        // 端点选项
        endpointOptions: {
            client: '客户端',
            agent: '代理端',
            server: '服务端',
            ui: 'UI端'
        },
        // 状态选项
        statusOptions: {
            online: '在线',
            offline: '离线',
            unknown: '未知'
        },
        // 操作菜单
        menu: {
            edit: '编辑',
            delete: '删除',
            clear: '清理'
        },
        // 弹窗标题
        dialog: {
            detail: '应用详情({instanceId})',
            editDesc: '编辑应用实例描述',
            clearData: '请选择要清理的历史数据'
        },
        // 消息
        msg: {
            selectData: '请选择数据',
            confirmDelete: '确定删除吗？',
            deleteSuccess: '删除成功！',
            deleteFailed: '删除失败！',
            editSuccess: '编辑成功！',
            editFailed: '编辑失败！',
            clearSuccess: '清理成功！',
            clearFailed: '清理失败！',
            requiredNull: '必选项不能为空！',
            setSuccess: '设置成功！',
            setFailed: '设置失败！',
            systemError: '系统错误！'
        }
    },

    // 应用详情模块
    instanceDetail: {
        // Tab标签
        tab: {
            memory: '内存',
            jvm: 'JVM',
            threadPool: '线程池',
            arthas: 'Arthas'
        },
        // 通用表单
        timeRange: '时间范围(T)',
        autoRefresh: '自动刷新',
        chart: '图表(C)',
        config: '配置',
        switchOn: '开启',
        switchOff: '关闭',
        // 时间选项
        timeOption: {
            hour: '1小时',
            day: '1天',
            week: '1周',
            month: '1月'
        },
        // 线程池表单
        threadPoolName: '线程池名字',
        // JVM 折叠面板
        jvmSection: {
            classLoading: '类',
            gc: 'GC',
            thread: '线程',
            vm: 'VM'
        },
        // 线程池信息标签
        threadPool: {
            name: '线程池名字<br>(name)',
            corePoolSize: '核心线程数<br>(corePoolSize)',
            maximumPoolSize: '最大线程数<br>(maximumPoolSize)',
            largestPoolSize: '历史最大线程数<br>(largestPoolSize)',
            poolSize: '当前线程数<br>(poolSize)',
            activeCount: '活跃线程数<br>(activeCount)',
            taskCount: '总任务数<br>(taskCount)',
            completedTaskCount: '已完成任务数<br>(completedTaskCount)',
            queueType: '队列类型<br>(queueType)',
            queueCapacity: '队列容量<br>(queueCapacity)',
            queueRemainingCapacity: '队列剩余容量<br>(queueRemainingCapacity)',
            queueSize: '当前队列大小<br>(queueSize)',
            rejectedTaskCount: '拒绝任务数<br>(rejectedTaskCount)',
            rejectedExecutionHandler: '拒绝策略<br>(rejectedExecutionHandler)',
            allowCoreThreadTimeOut: '核心线程空闲回收<br>(allowCoreThreadTimeOut)',
            keepAliveTime: '空闲回收时间<br>(keepAliveTime)',
            utilizationRate: '利用率<br>(utilizationRate)',
            recycle: '回收',
            notRecycle: '不回收',
            seconds: '秒'
        },
        // JVM详情标签
        jvmDetail: {
            activeThreadCount: '活动线程数：',
            peakThreadCount: '线程峰值：',
            daemonThreadCount: '守护线程数：',
            totalThreadCount: '线程总数：',
            threadDetail: '线程详情：',
            gcName: '名称：',
            gcCount: 'GC总次数：',
            gcTime: 'GC总时间(毫秒)：',
            totalLoadedClassCount: '累计加载类数量：',
            loadedClassCount: '已加载类数量：',
            unloadedClassCount: '已卸载类总数：',
            verbose: '是否启用详细模式：',
            vmStartTime: '虚拟机开始时间：',
            vmUptime: '虚拟机正常运行时间(毫秒)：',
            vmName: '虚拟机名称：',
            vmImplName: '虚拟机实现名称：',
            vmImplVendor: '虚拟机实现供应商：',
            vmImplVersion: '虚拟机实现版本：',
            vmSpecName: '虚拟机规范名称：',
            vmSpecVendor: '虚拟机规范供应商：',
            vmSpecVersion: '虚拟机规范版本：',
            managementSpecVersion: '管理接口规范版本：',
            vmArgs: '虚拟机入参：',
            classPath: 'Java类路径：',
            libraryPath: 'Java库路径：',
            bootClassPathSupported: '虚拟机是否支持引导类路径：',
            bootClassPath: '引导类路径：',
            yes: '是',
            no: '否'
        },
        // Arthas
        arthas: {
            ip: 'IP',
            port: '端口号',
            connect: '连接',
            disconnect: '断开',
            connected: '连接已经建立！',
            errorIpEmpty: '连接错误，IP不能为空！',
            errorPortEmpty: '连接错误，端口号不能为空！',
            errorConnect: '连接错误！',
            disconnectSuccess: '连接关闭成功！',
            notConnected: '请先建立连接！'
        },
        // 图表文本
        chartText: {
            used: '使用量',
            committed: '提交量',
            count: '数量',
            noData: '无数据',
            undefined: '未定义',
            initMemory: '初始内存：',
            maxMemory: '，最大内存：',
            memoryUsage: '内存使用量',
            activeThreadCount: '活跃线程数',
            queueSize: '当前队列大小',
            completedTaskCount: '已完成的任务数',
            rejectedTaskCount: '拒绝的任务数'
        },
        // 线程池配置弹窗
        configDialog: {
            title: 'Java线程池配置({name})',
            configSuccess: '配置成功！',
            configFailed: '配置失败！',
            systemError: '系统错误！'
        }
    },

    // 服务器详情模块
    serverDetail: {
        // 弹窗标题
        title: '服务器详情（IP：{ip}）',
        // Tab
        chartTab: '图表',
        summary: '概要',
        // 表单
        timeRange: '时间范围(T)',
        autoRefresh: '自动刷新',
        switchOn: '开启',
        switchOff: '关闭',
        timeHour: '1小时',
        timeDay: '1天',
        timeWeek: '1周',
        timeMonth: '1月',
        // 卡片标题
        diskTitle: '磁盘',
        batteryTitle: '电池',
        cpuTemperature: 'CPU温度',
        netcardAddress: '网卡地址',
        // 折叠面板标题
        sectionOS: '操作系统',
        sectionCPU: 'CPU',
        sectionGPU: 'GPU',
        sectionNetwork: '网络',
        sectionBattery: '电池',
        sectionSensor: '传感器',
        sectionProcess: '进程(内存占用倒序前20个)',
        // 通用状态
        unknown: '未知',
        noData: '没数据',
        unit: '个',
        core: '核',
        charging: '充电',
        discharging: '放电',
        tempHigh: '温度偏高',
        tempNormal: '温度正常',
        tempLow: '温度偏低',
        // 操作系统字段
        os: {
            ipAddress: 'IP地址',
            serverName: '服务器名',
            timeZone: '系统时区',
            osName: '系统名称',
            osArch: '系统架构',
            osVersion: '系统版本',
            userName: '系统用户',
            userHome: '用户目录'
        },
        // CPU字段
        cpu: {
            frequency: '频率',
            vendor: '供应商',
            model: '类型',
            totalUsage: '总使用率',
            idle: '剩余率',
            userUsage: '用户使用率',
            systemUsage: '系统使用率',
            wait: '等待率',
            nice: '错误率'
        },
        // GPU字段
        gpu: {
            deviceId: '设备ID',
            name: '名称',
            vendor: '供应商',
            versionInfo: '版本信息',
            vramTotal: '显存总量'
        },
        // 网卡字段
        netcard: {
            name: '网卡名字',
            type: '网卡类型',
            address: '网卡地址',
            mask: '子网掩码',
            broadcast: '广播地址',
            mac: 'MAC地址',
            description: '网卡信息描述',
            rxTotal: '接收的总数据大小',
            rxPackets: '接收的总包数',
            rxErrors: '接收到的错误包数',
            rxDropped: '接收时丢弃的包数',
            txTotal: '发送的总数据大小',
            txPackets: '发送的总包数',
            txErrors: '发送时的错误包数',
            txDropped: '发送时丢弃的包数',
            downloadSpeed: '下行速率(↓)',
            uploadSpeed: '上行速率(↑)'
        },
        // 进程字段
        process: {
            pid: '进程ID',
            name: '进程名',
            cpuUsage: 'CPU使用率',
            memoryRss: '占用内存(RSS)',
            state: '状态',
            bitness: '位数',
            startTime: '开始时间',
            upTime: '运行时长',
            user: '用户名',
            ports: '占用端口',
            commandLine: '命令行',
            path: '路径',
            workingDir: '工作目录'
        },
        // 电池字段
        battery: {
            name: '电池名称',
            serialNumber: '序列号',
            type: '电池类型',
            manufacturer: '供应商',
            manufactureDate: '生产日期',
            designCapacity: '原始容量',
            maxCapacity: '最大容量',
            currentCapacity: '剩余容量',
            remainingPercent: '剩余百分比',
            timeRemainingEstimated: '剩余使用时间(系统报告)',
            timeRemainingInstant: '剩余时间(电池报告)',
            voltage: '电压',
            amperage: '电流',
            powerUsageRate: '功率',
            temperature: '温度'
        },
        // 传感器字段
        sensor: {
            cpuTemperature: 'CPU温度',
            cpuVoltage: 'CPU电压',
            fanSpeed: '风扇转速'
        },
        // 磁盘字段
        disk: {
            devName: '盘符名称',
            dirName: '盘符路径',
            type: '磁盘类型',
            capacity: '容量',
            available: '可用',
            total: '共'
        },
        // 图表
        chart: {
            memSwapTitle: '内存/交换区',
            memSwapSubtitle: '物理内存：{memTotal}，物理内存使用率：{memUsedPercent}，交换区：{swapTotal}，交换区使用率：{swapUsedPercent}',
            processTitle: '进程',
            processSubtitle: '最大进程数：{max}，最小进程数：{min}',
            cpuSubtitle: '总使用率：{current}，最大使用率：{max}，最小使用率：{min}',
            netSpeedTitle: '{address}({name}) 上行/下行 速率',
            netSpeedSubtitle: '↑ 上行速率：{upload}，↓ 下行速率：{download}',
            loadAverageTitle: '平均负载',
            loadAverageSubtitle: '1分钟：{one}，5分钟：{five}，15分钟：{fifteen}',
            cpuTempTitle: 'CPU温度',
            batteryGaugeName: '电池电量',
            batteryGaugeDetail: '电量',
            yAxisUsage: '利用率',
            yAxisSpeed: '网速',
            yAxisLoad: '负载',
            yAxisCount: '个'
        },
        // 图例/系列名
        series: {
            memUsed: '内存使用量',
            swapUsed: '交换区使用量',
            runningProcess: '运行中进程数',
            idle: '剩余率',
            totalUsage: '总使用率',
            userUsage: '用户使用率',
            systemUsage: '系统使用率',
            wait: '等待率',
            nice: '错误率',
            downloadSpeed: '下行速率',
            uploadSpeed: '上行速率',
            oneMin: '1分钟',
            fiveMin: '5分钟',
            fifteenMin: '15分钟',
            avg: '(均值)',
            avgIdle: '(剩余率均值)',
            avgTotal: '(总使用率均值)',
            ideal: '(理想)',
            overload: '(过载)',
            critical: '(严重)',
            alarm: '(告警值)'
        }
    }
};

// 导出到全局
window.ZhMessages = ZhMessages;
}