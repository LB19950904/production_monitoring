/**
 * 英文语言包
 */
const EnMessages = {
    // 通用模块
    common: {
        language: 'Language',
        // projectName: 'Production Communication Guarantee System',
        projectName: 'PCG System',
        ellipsis: '...',
        projectShortName: 'Guarantee System',
        save: 'Save',
        cancel: 'Cancel',
        confirm: 'Confirm',
        delete: 'Delete',
        edit: 'Edit',
        add: 'Add',
        search: 'Search',
        reset: 'Reset',
        export: 'Export',
        import: 'Import',
        refresh: 'Refresh',
        back: 'Back',
        submit: 'Submit',
        close: 'Close',
        id: 'ID',
        account: 'Account',
        username: 'Username',
        email: 'Email',
        role: 'Role',
        description: 'Description',
        remarks: 'Remarks',
        registerTime: 'Register Time',
        updateTime: 'Update Time',
        actions: 'Actions',
        pleaseEnter: 'Please enter',
        clear: 'Clear'
    },

    // 导航菜单
    menu: {
        home: 'Home',
        mainPage: 'Main Page',
        server: 'Server',
        instance: 'Application',
        database: 'Database',
        network: 'Network(PING)',
        tcp: 'Port(TCP)',
        http: 'Interface(HTTP)',
        docker: 'DOCKER',
        networkDevice: 'Network Device',
        topology: 'Topology',
        alarm: 'Alarm',
        config: 'Configuration',
        user: 'User Management',
        log: 'Log',

        // Docker子菜单
        dockerService: 'Service',
        dockerContainer: 'Container',
        dockerImage: 'Image',
        dockerEvent: 'Event',
        dockerResource: 'Resource',

        // 拓扑图子菜单
        serverTopology: 'Server Topology',
        appTopology: 'Application Topology',
        networkTopology: 'Network Topology',
        portTopology: 'Port Topology',
        interfaceTopology: 'Interface Topology',

        // 配置管理子菜单
        envManagement: 'Environment Management',
        groupManagement: 'Group Management',
        monitorConfig: 'Monitor Configuration',
        alarmDefinition: 'Alarm Definition',

        // 用户管理子菜单
        userList: 'Users',
        roleList: 'Roles',

        // 日志子菜单
        operationLog: 'Operation Logs',
        exceptionLog: 'Exception Logs',

        // 其他
        druid: 'Druid',
        knife4j: 'Knife4j',
        logout: 'Logout'
    },
    // 配置管理模块
    config: {
        envManagement: 'Env management',
        groupManagement: 'Group management',
        monitorConfig: 'Monitor config',
        alarmDefinition: 'Alarm definition'
    },

    // 告警模块
    alarm: {
        title: 'Alarm Management',
        rule: {
            list: 'Alarm Rule List',
            name: 'Rule Name',
            type: 'Rule Type',
            threshold: 'Threshold',
            level: 'Alarm Level'
        },
        record: {
            list: 'Alarm Record List'
        },
        level: {
            info: 'Info',
            warn: 'Warning',
            error: 'Error',
            fatal: 'Fatal'
        },
        status: {
            enabled: 'Enabled',
            disabled: 'Disabled'
        }
    },

    // 数据库模块
    db: {
        title: 'Database Management',
        type: {
            mysql: 'MySQL',
            oracle: 'Oracle',
            redis: 'Redis',
            mongo: 'MongoDB'
        },
        connection: {
            status: 'Connection Status',
            active: 'Active',
            inactive: 'Inactive'
        },
        slowSql: {
            list: 'Slow SQL Records'
        },
        session: {
            list: 'Session List'
        },
        table: {
            space: 'Table Space'
        },
        pool: {
            info: 'Connection Pool Info'
        }
    },

    // Docker模块
    docker: {
        title: 'Docker Management',
        services: 'Services',
        containers: 'Containers',
        images: 'Images',
        events: 'Events',
        resources: 'Resources',
        container: {
            list: 'Container List',
            name: 'Container Name',
            status: 'Container Status',
            cpu: 'CPU Usage',
            memory: 'Memory Usage',
            network: 'Network Traffic',
            disk: 'Disk Usage'
        },
        image: {
            list: 'Image List',
            size: 'Image Size'
        }
    },

    // HTTP监控模块
    http: {
        title: 'HTTP Monitoring',
        url: 'Request URL',
        method: 'Request Method',
        avgTime: 'Average Response Time',
        maxTime: 'Max Response Time',
        minTime: 'Min Response Time',
        errorRate: 'Error Rate',
        requestCount: 'Request Count',
        successRate: 'Success Rate'
    },

    // 实例模块
    instance: {
        title: 'Instance Management',
        name: 'Instance Name',
        ip: 'Instance IP',
        port: 'Instance Port',
        status: 'Instance Status',
        threadPool: 'Thread Pool',
        jvm: 'JVM Info',
        memory: 'Memory Usage',
        cpu: 'CPU Usage',
        gc: 'Garbage Collection'
    },

    // 日志模块
    log: {
        title: 'Log Management',
        exception: 'Exception Logs',
        level: 'Log Level',
        time: 'Log Time',
        content: 'Log Content',
        search: 'Log Search',
        filter: 'Log Filter',
        detail: 'Log Detail'
    },

    // 用户模块
    user: {
        title: 'User Management',
        username: 'Username',
        password: 'Password',
        role: 'Role',
        permission: 'Permission',
        login: 'Login',
        logout: 'Logout',
        profile: 'User Profile',
        setting: 'User Settings',
        loginTime: 'Login Time',
        loginIp: 'Login IP',
        modify: {
            password: 'Change password'
        },
        account: 'Account',
        email: 'Email',
        registerTime: 'Register Time',
        updateTime: 'Update Time',
        description: 'Description',
        id: 'ID',
        remarks: 'Remarks',
        // 个人资料页面
        profilePage: {
            title: 'Set My Profile',
            id: 'ID',
            account: 'Account',
            myRole: 'My Role',
            email: 'Email',
            remarks: 'Remarks',
            placeholder: {
                username: 'Please enter username',
                content: 'Please enter content'
            }
        },
        // 密码修改页面
        passwordPage: {
            title: 'Set My Password',
            currentPassword: 'Current Password',
            newPassword: 'New Password',
            confirmPassword: 'Confirm New Password',
            confirmPasswordButton: 'Confirm Change',
            passwordHint: '6 to 16 characters',
            validation: {
                length: 'Password must be 6 to 30 characters and cannot contain spaces',
                mismatch: 'The two new passwords entered are inconsistent, please re-enter!',
                currentIncorrect: 'Current password is incorrect, please re-enter!',
                success: 'Password changed successfully!',
                failed: 'Password change failed!'
            }
        },

        // 用户列表页面
        listPage: {
            title: 'User Management',
            account: 'Account',
            username: 'Username',
            email: 'Email',
            role: 'Role',
            registerTime: 'Register Time',
            updateTime: 'Update Time',
            description: 'Description',
            id: 'ID',
            remarks: 'Remarks',
            actions: 'Actions',
            searchPlaceholder: 'Please enter',
            clearButton: 'Clear',
            addButton: 'Add',
            editButton: 'Edit',
            deleteButton: 'Delete',
            batchDelete: 'Batch Delete'
        }
    },

    // 表单验证
    validation: {
        required: 'This field is required',
        email: 'Please enter a valid email address',
        phone: 'Please enter a valid phone number',
        number: 'Please enter a valid number',
        minLength: 'Length cannot be less than {0} characters',
        maxLength: 'Length cannot be greater than {0} characters',
        passwordNotMatch: 'Passwords do not match',
        username: {
            empty: 'Username cannot be empty',
            specialChars: 'Username cannot have special characters',
            startEndUnderscore: 'Username cannot start or end with underscore \'_\'',
            allNumbers: 'Username cannot be all numbers'
        },
        emailFormat: 'Email format is incorrect'
    },

    // 系统提示
    system: {
        success: 'Operation successful',
        error: 'Operation failed',
        loading: 'Loading...',
        noData: 'No data available',
        networkError: 'Network error, please try again later',
        serverError: 'Server error, please contact administrator',
        timeout: 'Operation timeout, please retry'
    },

    // 操作按钮
    btn: {
        add: 'Add',
        edit: 'Edit',
        delete: 'Delete',
        save: 'Save',
        cancel: 'Cancel',
        search: 'Search',
        reset: 'Reset',
        export: 'Export',
        import: 'Import',
        detail: 'Detail',
        back: 'Back',
        more: 'More'
    },

    // 登录模块
    login: {
        title: 'Login',
        subtitle: 'Production Communication Guarantee System',
        account: 'Account',
        password: 'Password',
        captcha: 'Captcha',
        placeholder: {
            account: 'Please enter your account',
            password: 'Please enter your password',
            captcha: 'Please enter the captcha'
        },
        remember: 'Remember Me',
        submit: 'Sign In',
        error: {
            accountError: 'Invalid account or password, please try again!',
            expire: 'This account has been modified by administrator, you have been forced offline!',
            timeout: 'Login has timed out, please login again!',
            captchaEmpty: 'Captcha cannot be empty!',
            captchaNotexist: 'Captcha does not exist!',
            captchaExpired: 'Captcha has expired!',
            captchaFailed: 'Captcha verification failed!',
            accountEmpty: 'Account cannot be empty!',
            passwordEmpty: 'Password cannot be empty!',
            captchaRequired: 'Captcha cannot be empty!'
        },
        footer: {
            copyright: 'Copyright 2020-2025 PIFENG'
        }
    },

    // 拓扑模块
    topology: {
        server: 'Server Topology',
        instance: 'Application Topology',
        network: 'Network Topology',
        tcp: 'Port Topology',
        http: 'Interface Topology'
    },

    // TCP监控
    tcp: {
        title: 'TCP Monitoring',
        port: 'Port',
        status: 'Connection Status'
    },

    // 日志操作
    log: {
        operation: 'Operation Logs'
    },

    // 标签页操作
    tabs: {
        close: {
            current: 'Close Current Tab',
            others: 'Close Other Tabs',
            all: 'Close All Tabs'
        }
    },

    // 系统操作
    system: {
        operation: {
            confirm: 'Are you sure you want to perform this operation?',
            confirmDelete: 'Are you sure you want to delete?'
        },
        message: {
            saveSuccess: 'Saved successfully!',
            saveFailed: 'Save failed!',
            systemError: 'System error!',
            operationSuccess: 'Operation successful!',
            operationFailed: 'Operation failed!'
        }
    },

    // 个人资料模块
    profile: {
        title: 'Set My Profile',
        saveSuccess: 'Saved successfully!',
        saveFailed: 'Save failed!',
        systemError: 'System error!'
    },

    // 首页模块
    home: {
        // 页面标题
        pageTitle: 'Home',

        // 统计卡片标题
        server: 'Server',
        networkDevice: 'Network Device',
        docker: 'Docker',
        application: 'Application',
        database: 'Database',
        network: 'Network',
        tcp: 'TCP',
        http: 'HTTP',
        alarmNotification: 'Alarm Notice',

        // 状态描述
        online: 'Online',
        offline: 'Offline',
        unknown: 'Unknown',
        normal: 'Normal',
        abnormal: 'Abnormal',
        success: 'Success',
        failure: 'Failure',
        noAlert: 'No Alert',

        // 单位标识
        unit: 'Unit(s)',
        item: 'Item(s)',
        times: 'Count',
        timesUnit: 'Time(s)',

        // 操作系统类型
        windows: 'Windows',
        linux: 'Linux',
        other: 'Other',

        // 图表区域
        alarmStatistics: 'Alarm Statistics',
        latestAlarms: 'Latest Alarms',

        // 百分比
        percentage: 'Percentage',

        // 图表相关
        dataTrend: 'Data Trend',
        resultTrend: 'Result Trend',
        resultRatio: 'Result Ratio',
        successRate: 'Success Rate',
        totalCount: 'Total Count',
        items: 'Items',
        vulnerabilityStatistics: 'Vulnerability Statistics'
    },

    // 环境管理模块
    env: {
        title: 'Environment Management',
        name: 'Environment Name',
        description: 'Environment Description',
        placeholder: {
            name: 'Please enter environment name',
            description: 'Please enter environment description'
        },
        validation: {
            nameRequired: 'Environment name is required',
            descriptionRequired: 'Environment description is required'
        },
        add: 'Add Environment',
        edit: 'Edit Environment'
    },

    // 分组管理模块
    group: {
        title: 'Group Management',
        name: 'Group Name',
        description: 'Group Description',
        placeholder: {
            name: 'Please enter group name',
            description: 'Please enter group description'
        },
        validation: {
            nameRequired: 'Group name is required'
        },
        add: 'Add Group',
        edit: 'Edit Group'
    },

    // 告警定义模块
    alarm: {
        id: 'Alarm ID',
        code: 'Alarm Code',
        type: 'Alarm Type',
        level: 'Alarm Level',
        title: 'Alarm Title',
        content: 'Alarm Content',
        firstClass: 'First Class',
        secondClass: 'Second Class',
        thirdClass: 'Third Class',
        custom: 'Custom',
        message: 'Info',
        warning: 'Warning',
        error: 'Error',
        fatal: 'Fatal',
        placeholder: {
            code: 'Please enter alarm code',
            title: 'Please enter alarm title',
            content: 'Please enter alarm content',
            firstClass: 'Please enter first class',
            secondClass: 'Please enter second class',
            thirdClass: 'Please enter third class'
        },
        validation: {
            codeRequired: 'Alarm code is required',
            titleRequired: 'Alarm title is required',
            contentRequired: 'Alarm content is required',
            firstClassRequired: 'First class is required'
        },
        add: 'Add Alarm Definition',
        edit: 'Edit Alarm Definition'
    },

    // 数据库模块
    database: {
        type: 'Type',
        connName: 'Connection Name',
        url: 'URL',
        username: 'Username',
        password: 'Password',
        monitorEnv: 'Environment',
        monitorGroup: 'Group',
        isEnableMonitor: 'Enable Monitor',
        isEnableAlarm: 'Enable Alarm',
        dbDesc: 'Description',
        databaseId: 'Database ID',
        placeholder: {
            connName: 'Please enter connection name',
            url: 'Please enter URL',
            username: 'Please enter username',
            password: 'Please enter password',
            description: 'Please enter description'
        },
        validation: {
            connNameRequired: 'Connection name is required',
            urlRequired: 'URL is required',
            usernameRequired: 'Username is required',
            passwordRequired: 'Password is required'
        },
        add: 'Add Database',
        edit: 'Edit Database'
    },

    // 通用表单标签
    form: {
        id: 'ID',
        account: 'Account',
        username: 'Username',
        password: 'Password',
        email: 'Email',
        remarks: 'Remarks',
        description: 'Description',
        role: 'Select Role',
        confirmPassword: 'Confirm Password',
        oldPassword: 'Current Password',
        newPassword: 'New Password',
        passwordHint: 'Leave blank to keep unchanged',
        placeholder: {
            account: 'Please enter account',
            username: 'Please enter username',
            password: 'Please enter password',
            email: 'Please enter email',
            remarks: 'Please enter remarks',
            confirmPassword: 'Please confirm password',
            oldPassword: 'Please enter current password',
            newPassword: 'Please enter new password'
        },
        validation: {
            accountRequired: 'Account is required',
            usernameRequired: 'Username is required',
            passwordRequired: 'Password is required',
            passwordLength: 'Password must be 6 to 30 characters and cannot contain spaces',
            emailFormat: 'Email format is incorrect',
            passwordMismatch: 'Passwords do not match',
            usernameSpecialChars: 'Username cannot have special characters',
            usernameUnderscore: 'Username cannot start or end with underscore \'_\'',
            usernameAllNumbers: 'Username cannot be all numbers'
        },
        confirm: 'Confirm',
        confirmPassword: 'Confirm Change'
    },

    // 页面标题
    page: {
        addUser: 'Add User',
        editUser: 'Edit User',
        addEnv: 'Add Monitor Environment',
        editEnv: 'Edit Monitor Environment',
        addGroup: 'Add Group',
        editGroup: 'Edit Group',
        addAlarm: 'Add Alarm Definition',
        editAlarm: 'Edit Alarm Definition',
        addDatabase: 'Add Database',
        editDatabase: 'Edit Database',
        editServer: 'Edit Server Information',
        clearServerData: 'Clear Server Monitoring History Data',
        serverList: 'Server'
    },

    // 服务器管理模块
    server: {
        title: 'Server Management',
        ip: 'IP',
        system: 'System',
        serverName: 'Server Name',
        status: 'Status',
        online: 'Online',
        offline: 'Offline',
        unknown: 'Unknown',
        all: 'All',
        yes: 'Yes',
        no: 'No',
        notSet: 'Not Set',
        description: 'Description',
        placeholder: {
            description: 'Please enter description',
            search: 'Please enter'
        },
        // 数据清理时间选项
        clearData: {
            hour: 'Data 1 hour ago',
            day: 'Data 1 day ago',
            week: 'Data 1 week ago',
            month: 'Data 1 month ago',
            halfYear: 'Data 6 months ago',
            year: 'Data 1 year ago',
            all: 'All data'
        },
        // 状态选项
        statusOptions: {
            online: 'Online',
            offline: 'Offline',
            unknown: 'Unknown'
        },
        // 监控选项
        monitorOptions: {
            yes: 'Yes',
            no: 'No'
        },
        validation: {
            descriptionRequired: 'Description is required'
        }
    }
};

// 导出到全局
window.EnMessages = EnMessages;