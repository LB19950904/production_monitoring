/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['admin', 'carousel', 'jquery', 'element'], function () {
        var admin = layui.admin, $ = layui.$, element = layui.element, carousel = layui.carousel,
            device = layui.device(), layer = layui.layer;

        // 添加自定义进度条样式
        $('head').append('<style>#alarm-record-type-statistics .layui-progress {background-color: #E0E0E0 !important; border-radius: 4px; overflow: hidden;} #alarm-record-type-statistics .layui-progress-bar {background-color: #2196F3 !important;}</style>');

        // 翻译辅助函数
        function t(key) {
            if (typeof I18nUtils !== 'undefined') {
                return I18nUtils.t(key);
            }
            // 如果国际化未初始化，返回默认中文文本
            const defaultTranslations = {
                'home.windows': 'Windows',
                'home.linux': 'Linux',
                'home.other': '其他',
                'home.online': '在线',
                'home.offline': '离线',
                'home.unknown': '未知',
                'home.normal': '正常',
                'home.abnormal': '异常',
                'home.success': '成功',
                'home.failure': '失败',
                'home.noAlert': '不提醒',
                'home.items': '条',
            };
            return defaultTranslations[key] || key;
        }
        // 最近7天告警统计div
        var $Last7DaysAlarmRecordStatistics = $('#last-7-days-alarm-record-statistics');
        // 告警结果统计div
        var $AlarmRecordResultStatistics = $('#alarm-record-result-statistics');
        // 告警统计轮播div
        var $LayadminDataViewMy = $('#layadmin-dataview-my');
        // 摘要轮播图div
        var $Summary = $('#summary');
        // 轮播切换
        if (device.ios || device.android) {
            // 移动端不轮播，直接显示所有卡片
            var css = {
                position: 'relative',
                display: 'block',
                height: '100%'
            };
            $Summary.css(css);
            $('#carousel-item-1-div-1').css(css);
            $('#carousel-item-1-div-2').css(css);
        } else {
            // 摘要轮播
            carousel.render({
                elem: $Summary[0],
                width: '100%',
                height: '170px',
                arrow: 'hover',
                interval: 5000,
                autoplay: true,
                indicator: 'none',
                trigger: device.ios || device.android ? 'click' : 'hover',
                anim: $Summary.data('anim')
            });
        }
        // 告警统计轮播
        carousel.render({
            elem: $LayadminDataViewMy[0],
            width: '100%',
            arrow: 'hover',
            interval: 5000,
            autoplay: true,
            trigger: device.ios || device.android ? 'click' : 'hover',
            anim: $LayadminDataViewMy.data('anim')
        });
        // 设置div高度为统计轮播div的高度，宽度为统计轮播div的宽度
        $Last7DaysAlarmRecordStatistics.width($LayadminDataViewMy.width());
        $Last7DaysAlarmRecordStatistics.height($LayadminDataViewMy.height());
        $AlarmRecordResultStatistics.width($LayadminDataViewMy.width());
        $AlarmRecordResultStatistics.height($LayadminDataViewMy.height());
        // 基于准备好的dom，初始化echarts实例
        var myLast7DaysAlarmRecordStatisticsChart = echarts.init(document.getElementById('last-7-days-alarm-record-statistics'), 'infographic');
        var myAlarmRecordResultStatisticsChart = echarts.init(document.getElementById('alarm-record-result-statistics'), 'infographic');
        // 卡片体元素（flex布局决定其高度）
        var $chartCardBody = $LayadminDataViewMy.parent();
        // 重新计算并设置图表高度
        var resizeTimer;
        var lastChartH = 0;
        function resizeCharts() {
            clearTimeout(resizeTimer);
            resizeTimer = setTimeout(function () {
                // 从卡片体读取内容区高度（clientHeight含padding，需减去）
                var body = $chartCardBody[0];
                var cs = window.getComputedStyle(body);
                var padTop = parseInt(cs.paddingTop) || 0;
                var padBot = parseInt(cs.paddingBottom) || 0;
                var h = body.clientHeight - padTop - padBot;
                if (h > 0 && h !== lastChartH) {
                    lastChartH = h;
                    // 显式设置轮播高度（覆盖carousel.render的280px默认值）
                    $LayadminDataViewMy.css('height', h + 'px');
                    var w = $LayadminDataViewMy.width();
                    $Last7DaysAlarmRecordStatistics.width(w).height(h);
                    $AlarmRecordResultStatistics.width(w).height(h);
                    myLast7DaysAlarmRecordStatisticsChart.resize();
                    myAlarmRecordResultStatisticsChart.resize();
                }
            }, 100);
        }
        // 浏览器窗口大小发生改变时
        window.addEventListener("resize", resizeCharts);
        // flex布局渲染后重新计算图表高度
        setTimeout(resizeCharts, 300);

        // 使用ResizeObserver监听卡片体尺寸变化（全屏、侧边栏收起等场景都能自动响应）
        if (typeof ResizeObserver !== 'undefined') {
            new ResizeObserver(function () {
                lastChartH = 0;
                resizeCharts();
            }).observe($chartCardBody[0]);
        }

        // 全屏切换时强制重绘（监听父页面全屏事件）
        function onFullscreenChange() {
            // 全屏切换时布局需要时间稳定，多次延迟触发
            lastChartH = 0;
            setTimeout(resizeCharts, 300);
            setTimeout(function () { lastChartH = 0; resizeCharts(); }, 600);
        }
        try { window.top.document.addEventListener('fullscreenchange', onFullscreenChange); } catch (e) {}
        try { window.top.document.addEventListener('webkitfullscreenchange', onFullscreenChange); } catch (e) {}
        document.addEventListener('fullscreenchange', onFullscreenChange);

        // 发送ajax请求，获取最近7天告警统计数据
        function getLast7DaysAlarmRecordStatistics() {
            admin.req({
                type: 'post',
                url: layui.setter.base + 'home/get-last-7-days-alarm-record-statistics',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                success: function (result) {
                    var data = result.data;
                    // 日期
                    var date = data.map(function (item) {
                        return item.date;
                    });
                    // 成功
                    var success = data.map(function (item) {
                        return parseInt(item.success);
                    });
                    // 失败
                    var fail = data.map(function (item) {
                        return parseInt(item.fail);
                    });
                    // 不提醒
                    var unsent = data.map(function (item) {
                        return parseInt(item.unsent);
                    });
                    var option = {
                        title: {
                            text: typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.resultTrend') : '结果趋势',
                            left: 'left',
                            textStyle: {
                                color: '#333333',
                                fontSize: 18,
                                fontWeight: '500'
                            }
                        },
                        // 鼠标移到折线上展示数据
                        tooltip: {
                            trigger: 'axis',
                            backgroundColor: 'rgba(85, 85, 85, 0.8)',
                            borderColor: 'rgba(85, 85, 85, 0.8)',
                            borderWidth: 1,
                            textStyle: {
                                color: '#ffffff',
                                fontSize: 12
                            },
                            axisPointer: {
                                type: 'line',
                                lineStyle: {
                                    color: '#cccccc',
                                    type: 'dashed'
                                }
                            }
                        },
                        legend: {
                            top: 'bottom',
                            data: [
                                typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.success') : '成功',
                                typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.failure') : '失败',
                                typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.noAlert') : '不提醒'
                            ],
                            textStyle: {
                                color: '#333333',
                                fontSize: 12
                            },
                            itemGap: 20
                        },
                        grid: {
                            left: '3%',
                            right: '4%',
                            bottom: 50,
                            top: '15%',
                            containLabel: true
                        },
                        xAxis: [{
                            type: 'category',
                            // X轴从零刻度开始
                            boundaryGap: false,
                            data: date,
                            axisLine: {
                                lineStyle: {
                                    color: '#e0e0e0'
                                }
                            },
                            axisLabel: {
                                color: '#666666',
                                fontSize: 11
                            },
                            splitLine: {
                                show: false
                            }
                        }],
                        yAxis: {
                            type: 'value',
                            name: (typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.times') : '次数') + '/' + (typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.timesUnit') : '次'),
                            nameTextStyle: {
                                color: '#666666',
                                fontSize: 12
                            },
                            axisLabel: {
                                formatter: '{value}',
                                color: '#666666',
                                fontSize: 11
                            },
                            axisLine: {
                                lineStyle: {
                                    color: '#e0e0e0'
                                }
                            },
                            splitLine: {
                                lineStyle: {
                                    color: '#E0E0E0',
                                    type: 'solid'
                                }
                            },
                            minInterval: 1
                        },
                        // 数据
                        series: [{
                            name: typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.success') : '成功',
                            data: success,
                            type: 'line',
                            smooth: true,
                            symbol: 'circle',
                            symbolSize: 3,
                            lineStyle: {
                                color: '#4CAF50',
                                width: 2
                            },
                            itemStyle: {
                                color: '#4CAF50',
                                borderColor: '#4CAF50',
                                borderWidth: 1
                            }
                        }, {
                            name: typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.failure') : '失败',
                            data: fail,
                            type: 'line',
                            smooth: true,
                            symbol: 'circle',
                            symbolSize: 3,
                            lineStyle: {
                                color: '#F44336',
                                width: 2
                            },
                            itemStyle: {
                                color: '#F44336',
                                borderColor: '#F44336',
                                borderWidth: 1
                            }
                        }, {
                            name: typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.noAlert') : '不提醒',
                            data: unsent,
                            type: 'line',
                            smooth: true,
                            symbol: 'circle',
                            symbolSize: 3,
                            lineStyle: {
                                color: '#FF9800',
                                width: 2
                            },
                            itemStyle: {
                                color: '#FF9800',
                                borderColor: '#FF9800',
                                borderWidth: 1
                            }
                        }]
                    };
                    myLast7DaysAlarmRecordStatisticsChart.setOption(option);
                },
                error: function () {
                    layer.msg(typeof I18nUtils !== 'undefined' ? I18nUtils.t('system.message.systemError') : '系统错误！', {icon: 5, shift: 6});
                }
            });
        }

        // 告警类型名称映射（中英文）
        var alarmTypeNameMap = {
            'zh_CN': {
                '应用程序': '应用程序',
                '服务器': '服务器',
                '网络': '网络',
                '数据库': '数据库',
                '自定义': '自定义',
                'TCP': 'TCP',
                'HTTP': 'HTTP',
                'DOCKER': 'DOCKER',
                '网络设备': '网络设备'
            },
            'en_US': {
                '应用程序': 'Application',
                '服务器': 'Server',
                '网络': 'Network',
                '数据库': 'Database',
                '自定义': 'Custom',
                'TCP': 'TCP',
                'HTTP': 'HTTP',
                'DOCKER': 'Docker',
                '网络设备': 'Network Device'
            }
        };

        // 发送ajax请求，获取告警类型统计信息
        function getAlarmRecordTypeStatistics() {
            admin.req({
                type: 'post',
                url: layui.setter.base + 'home/get-alarm-record-type-statistics',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                success: function (result) {
                    var data = result.data;
                    var itemsText = t('home.items');

                    // 获取当前语言设置，默认中文
                    var currentLang = (typeof I18nConfig !== 'undefined' && I18nConfig.currentLocale) ? I18nConfig.currentLocale : 'zh_CN';

                    var html = '<div style="background-color: #F5F5F5;border-radius: 8px;">';

                    // 找出最大值用于红色高亮标签
                    var maxRate = 0;
                    var maxTotals = 0;
                    if (data.length > 0) {
                        for (var j = 0; j < data.length; j++) {
                            if (data[j].rate > maxRate) {
                                maxRate = data[j].rate;
                                maxTotals = data[j].totals;
                            }
                        }
                    }

                    // 数据条目列表
                    html += '<div style="background-color: #FFFFFF; padding: 15px; border-radius: 6px; margin-bottom: 15px;">';
                    for (var i = 0; i < data.length; i++) {
                        var obj = data[i];
                        var rate = obj.rate;
                        var totals = obj.totals;
                        var types = obj.types;

                        // 根据当前语言获取对应的类型名称
                        var displayType = types;
                        if (alarmTypeNameMap[currentLang] && alarmTypeNameMap[currentLang][types]) {
                            displayType = alarmTypeNameMap[currentLang][types];
                        }

                        html += '<div style="margin-bottom: 15px;">';

                        // 第一项显示红色高亮标签
                        if (i === 0 && maxTotals > 0) {
                            html += '<div style="text-align: center; margin-bottom: 8px;">';
                            html += '<span style="color: #FF5252; font-size: 12px; font-weight: 700;">' + maxTotals + itemsText + '</span>';
                            html += '</div>';
                        }

                        // 标签和进度条水平布局
                        html += '<div style="display: flex; align-items: center; margin-bottom: 40px;">';
                        html += '<div style="width: 131px; height: 20px; text-align: left; display: flex; align-items: center;">';
                        html += '<span style="color: #000; font-size: 14px; font-weight: 500;">' + displayType + '</span>';
                        html += '<span style="color: #666; font-size: 12px; margin-left: 5px;">（' + totals + itemsText + '）</span>';
                        html += '</div>';

                        // layui进度条组件
                        html += '<div class="layui-progress layui-progress-big" lay-showpercent="true" lay-filter="progress_' + i + '" style="flex: 1; border-radius: 8px; background: #ECEEF2; opacity: 0.8;">';
                        html += '<div class="layui-progress-bar" lay-percent="' + rate + '" style="border-radius: 0px 4px 4px 0px; background: linear-gradient(129deg, #055FE7 0%, #3586FF 100%); box-shadow: 0px 3px 5px 2px rgba(0,24,59,0.06);"></div>';
                        html += '</div>';
                        html += '</div>';
                    }
                    html += '</div></div>';

                    $('#alarm-record-type-statistics').empty().append(html);

                    // 初始化layui进度条组件（使用setTimeout确保DOM完全渲染）
                    setTimeout(function() {
                        // 先渲染进度条组件
                        element.render('progress');
                        // 设置每个进度条的值
                        for (var i = 0; i < data.length; i++) {
                            var obj = data[i];
                            var rate = obj.rate;
                            element.progress('progress_' + i, rate);
                        }
                    }, 100);
                },
                error: function () {
                    layer.msg(typeof I18nUtils !== 'undefined' ? I18nUtils.t('system.message.systemError') : '系统错误！', {icon: 5, shift: 6});
                }
            });
        }

        // 发送ajax，获取告警结果统计信息
        function getAlarmRecordResultStatistics() {
            admin.req({
                type: 'post',
                url: layui.setter.base + 'home/get-alarm-record-result-statistics',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                success: function (result) {
                    var data = result.data;
                    // 告警总次数
                    var alarmRecordSum = data.alarmRecordSum;
                    // 告警成功次数
                    var alarmRecordSuccessSum = data.alarmRecordSuccessSum;
                    // 告警失败次数
                    var alarmRecordFailSum = data.alarmRecordFailSum;
                    // 未发送告警次数
                    var alarmRecordUnsentSum = data.alarmRecordUnsentSum;
                    // 告警成功率
                    var alarmSucRate = data.alarmSucRate;
                    var option = {
                        // 使用graphic组件在图表上方显示摘要信息
                        graphic: [
                            {
                                type: 'text',
                                left: 'center',
                                top: 5,
                                style: {
                                    text: (typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.totalCount') : '总数') + '：' + alarmRecordSum + (typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.times') : '次'),
                                    textAlign: 'center',
                                    fill: '#666666',
                                    fontSize: 14
                                }
                            },
                            {
                                type: 'text',
                                left: 'center',
                                top: 25,
                                style: {
                                    text: (typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.successRate') : '成功率') + '：' + alarmSucRate + '%',
                                    textAlign: 'center',
                                    fill: '#4CAF50',
                                    fontSize: 14,
                                    fontWeight: 'bold'
                                }
                            }
                        ],
                        backgroundColor: 'transparent',
                        tooltip: {
                            trigger: 'item',
                            formatter: '{b}: {c}' + (typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.timesUnit') : '次') + ' ({d}%)'
                        },
                        legend: {
                            orient: 'horizontal',
                            bottom: 10,
                            left: 'center',
                            itemWidth: 12,
                            itemHeight: 12,
                            textStyle: {
                                color: '#666666',
                                fontSize: 12
                            },
                            data: [
                                typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.success') : '成功',
                                typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.failure') : '失败',
                                typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.noAlert') : '不提醒'
                            ]
                        },
                        // 增强饼状图配置，模拟3D效果
                        series: [
                            {
                                type: 'pie',
                                radius: ['35%', '55%'],
                                center: ['50%', '50%'],
                                data: [
                                    {
                                        value: alarmRecordSuccessSum,
                                        name: typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.success') : '成功',
                                        itemStyle: {
                                            color: {
                                                type: 'linear',
                                                x: 0,
                                                y: 0,
                                                x2: 0,
                                                y2: 1,
                                                colorStops: [
                                                    { offset: 0, color: '#6DDA6E' },
                                                    { offset: 1, color: '#4CAF50' }
                                                ]
                                            },
                                            borderColor: '#fff',
                                            borderWidth: 2,
                                            shadowColor: 'rgba(76, 175, 80, 0.5)',
                                            shadowBlur: 10,
                                            shadowOffsetX: 0,
                                            shadowOffsetY: 3
                                        }
                                    },
                                    {
                                        value: alarmRecordFailSum,
                                        name: typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.failure') : '失败',
                                        itemStyle: {
                                            color: {
                                                type: 'linear',
                                                x: 0,
                                                y: 0,
                                                x2: 0,
                                                y2: 1,
                                                colorStops: [
                                                    { offset: 0, color: '#FF6B6B' },
                                                    { offset: 1, color: '#F44336' }
                                                ]
                                            },
                                            borderColor: '#fff',
                                            borderWidth: 2,
                                            shadowColor: 'rgba(244, 67, 54, 0.5)',
                                            shadowBlur: 10,
                                            shadowOffsetX: 0,
                                            shadowOffsetY: 3
                                        }
                                    },
                                    {
                                        value: alarmRecordUnsentSum,
                                        name: typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.noAlert') : '不提醒',
                                        itemStyle: {
                                            color: {
                                                type: 'linear',
                                                x: 0,
                                                y: 0,
                                                x2: 0,
                                                y2: 1,
                                                colorStops: [
                                                    { offset: 0, color: '#FFB74D' },
                                                    { offset: 1, color: '#FF9800' }
                                                ]
                                            },
                                            borderColor: '#fff',
                                            borderWidth: 2,
                                            shadowColor: 'rgba(255, 152, 0, 0.5)',
                                            shadowBlur: 10,
                                            shadowOffsetX: 0,
                                            shadowOffsetY: 3
                                        }
                                    }
                                ],
                                // 模拟3D厚度的环状效果
                                emphasis: {
                                    scale: true,
                                    scaleSize: 10,
                                    itemStyle: {
                                        borderColor: '#fff',
                                        borderWidth: 2,
                                        shadowColor: 'rgba(0, 0, 0, 0.3)',
                                        shadowBlur: 15,
                                        shadowOffsetX: 0,
                                        shadowOffsetY: 5
                                    },
                                    label: {
                                        show: true,
                                        fontSize: 12,
                                        fontWeight: 'bold',
                                        formatter: '{b}: {c}' + (typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.timesUnit') : '次')
                                    }
                                },
                                label: {
                                    show: true,
                                    position: 'outside',
                                    formatter: '{b}: {c}' + (typeof I18nUtils !== 'undefined' ? I18nUtils.t('home.timesUnit') : '次'),
                                    fontSize: 11,
                                    color: '#333333',
                                    distance: 5,
                                    backgroundColor: 'rgba(255, 255, 255, 0.8)',
                                    borderColor: '#ddd',
                                    borderWidth: 1,
                                    borderRadius: 3,
                                    padding: [2, 4]
                                },
                                labelLine: {
                                    show: true,
                                    length: 20,
                                    length2: 15,
                                    smooth: true,
                                    lineStyle: {
                                        color: '#999',
                                        width: 1,
                                        type: 'solid'
                                    }
                                }
                            }
                        ]
                    };
                    myAlarmRecordResultStatisticsChart.setOption(option);
                },
                error: function () {
                    layer.msg(typeof I18nUtils !== 'undefined' ? I18nUtils.t('system.message.systemError') : '系统错误！', {icon: 5, shift: 6});
                }
            });
        }

        // 发送ajax请求，获取最新的5条告警记录
        function getLast5AlarmRecord() {
            admin.req({
                type: 'post',
                url: layui.setter.base + 'home/get-last-5-alarm-record',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                success: function (result) {
                    var data = result.data;
                    var html = '';
                    for (var i = 0; i < data.length; i++) {
                        var obj = data[i];
                        // 从I18nConfig获取当前语言设置，默认为中文
                        var isEnglish = typeof I18nConfig !== 'undefined' && I18nConfig.isEnglish();
                        var title = (isEnglish && obj.titleEn) ? obj.titleEn : obj.title;
                        var ellipsis = typeof I18nUtils !== 'undefined' ? I18nUtils.t('common.ellipsis') : '......';
                        var content = obj.content.length >= 500 ? obj.content.slice(0, 500) + ellipsis : obj.content;
                        let createTime = obj.insertTime;
                        // 将正文按照<br>给分割开
                        let contentSplitArr = content.split('<br>');
                        html += '<li>'
                            + '<div class="alarm-record-item-style">'
                            + '<div class="alarm-record-item-title"><div style="display: flex"><img style="margin-right: 5px" src="/images/new-ui/最新告警_矩形.png" alt=""/><span>' + title + '</span></div>'
                            + '<span style="font-weight: 500;font-size: 12px;color: #999999;line-height: 22px;text-align: left;font-style: normal;">'+ createTime +'</span>'
                            + '</div>'
                            + '<div class="alarm-record-item-content">';
                        // 循环拆分开的正文数组
                        let innerDoms = '';
                        if (contentSplitArr) {
                            for (let j = 0; j < contentSplitArr.length; j++) {
                                let label = contentSplitArr[j].split('：')[0];
                                let value = contentSplitArr[j].split('：')[1];
                                if (label && value) {
                                    let temp = '<div class="alarm-record-item-content-item"><span class="alarm-record-item-label">'+ label +'：</span>' +
                                        '<span class="alarm-record-item-value">'+ value.replace('，','') +'</span></div>';
                                    innerDoms += temp;
                                }
                            }
                            html += innerDoms;
                        }
                        html += '</div>'
                            + '</div>'
                            + '</li>';
                    }
                    $('#get-last-5-alarm-record').empty().append(html);
                },
                error: function () {
                    layer.msg(typeof I18nUtils !== 'undefined' ? I18nUtils.t('system.message.systemError') : '系统错误！', {icon: 5, shift: 6});
                }
            });
        }

        // 发送ajax请求，获取home页的摘要信息
        function getSummaryInfo() {
            admin.req({
                type: 'post',
                url: layui.setter.base + 'home/get-summary-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                success: function (result) {
                    var data = result.data;
                    var homeInstanceVo = data.homeInstanceVo;
                    var homeServerVo = data.homeServerVo;
                    var homeNetVo = data.homeNetVo;
                    var homeAlarmRecordVo = data.homeAlarmRecordVo;
                    var homeDbVo = data.homeDbVo;
                    var homeTcpVo = data.homeTcpVo;
                    var homeHttpVo = data.homeHttpVo;
                    var homeDockerVo = data.homeDockerVo;
                    var homeNetworkDeviceVo = data.homeNetworkDeviceVo;
                    // 服务器类型
                    var htmlServer1 = '<p class="layuiadmin-big-font layuiadmin-big-font-my" style="margin-bottom: 9px">' + homeServerVo.serverSum + '<span class="unit-label">' + t('home.unit') + '</span></p>' +
                        '             <p class="device-card-row-style">' + '<img style="margin-right: 4px" src="/images/new-ui/windows.png">'+ t('home.windows') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeServerVo.windowsSum +
                        '                   </span>' +
                        '             </p>' +
                        '             <p class="device-card-row-style">' + '<img style="margin-right: 4px" src="/images/new-ui/Linux.png">'+ t('home.linux') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeServerVo.linuxSum +
                        '                   </span>' +
                        '             </p>' +
                        '             <p class="device-card-row-style">' + '<img style="margin-right: 4px" src="/images/new-ui/其他.png">'+ t('home.other') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeServerVo.otherSum +
                        '                   </span>' +
                        '             </p>';
                    $('#server-card-list-1').empty().append(htmlServer1);
                    //
                    // 服务器在线率
                    var htmlServer2 = '<div class="card-title-tongji" style="">' +
                        '               <div class="layuiadmin-big-font layuiadmin-big-font-my"><p>'+homeServerVo.serverSum+'</p>' + '<span class="unit-label">' + t('home.unit') + '</span></div>' +
                        '               <div class="card-progress-style">' +
                        '                   <span>'+ homeServerVo.serverOnLineRate +'%</span>'+
                        '                   <div class="layui-progress">' +
                        '                       <div class="layui-progress-bar" lay-percent="'+ homeServerVo.serverOnLineRate +'%"></div>'+
                        '                   </div>'+
                        '               </div>'+
                        '            </div>'+

                        '               <p class="device-card-row-style">' + '<span class="card-status-online-flag"></span>' + t('home.online') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeServerVo.serverOnLineSum +
                        '                   </span>' +
                        '               </p>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-offline-flag"></span>' +  t('home.offline') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeServerVo.serverOffLineSum +
                        '                   </span>' +
                        '               </p>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-error-flag"></span>' +  t('home.unknown') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeServerVo.serverUnknownLineSum +
                        '                   </span>' +
                        '               </p>';
                    $('#server-card-list-2').empty().append(htmlServer2);
                    // 网络设备
                    var htmlNetworkDevice = '<div class="card-title-tongji" style="">' +
                        '               <div class="layuiadmin-big-font layuiadmin-big-font-my"><p>'+homeNetworkDeviceVo.networkDeviceSum+'</p>' + '<span class="unit-label">' + t('home.unit') + '</span></div>' +
                        '               <div class="card-progress-style">' +
                        '                   <span>'+ homeNetworkDeviceVo.networkDeviceOnLineRate +'%</span>'+
                        '                   <div class="layui-progress">' +
                        '                       <div class="layui-progress-bar" lay-percent="'+ homeNetworkDeviceVo.networkDeviceOnLineRate +'%"></div>'+
                        '                   </div>'+
                        '               </div>'+
                        '            </div>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-online-flag"></span>' + t('home.online') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeNetworkDeviceVo.networkDeviceOnLineSum +
                        '                   </span>' +
                        '               </p>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-offline-flag"></span>' + t('home.offline') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeNetworkDeviceVo.networkDeviceOffLineSum +
                        '                   </span>' +
                        '               </p>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-error-flag"></span>' + t('home.unknown') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeNetworkDeviceVo.networkDeviceUnknownLineSum +
                        '                   </span>' +
                        '               </p>';
                    $('#network-device-card-list').empty().append(htmlNetworkDevice);
                    // docker服务
                    var htmlDocker = '<div class="card-title-tongji" style="">' +
                        '               <div class="layuiadmin-big-font layuiadmin-big-font-my"><p>'+homeDockerVo.dockerSum+'</p>' + '<span class="unit-label">' + t('home.item') + '</span></div>' +
                        '               <div class="card-progress-style">' +
                        '                   <span>'+ homeDockerVo.dockerOnLineRate +'%</span>'+
                        '                   <div class="layui-progress">' +
                        '                       <div class="layui-progress-bar" lay-percent="'+ homeDockerVo.dockerOnLineRate +'%"></div>'+
                        '                   </div>'+
                        '               </div>'+
                        '            </div>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-online-flag"></span>' + t('home.online') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeDockerVo.dockerOnLineSum +
                        '                   </span>' +
                        '               </p>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-offline-flag"></span>' + t('home.offline') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeDockerVo.dockerOffLineSum +
                        '                   </span>' +
                        '               </p>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-error-flag"></span>' + t('home.unknown') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeDockerVo.dockerUnknownLineSum +
                        '                   </span>' +
                        '               </p>';
                    $('#docker-card-list').empty().append(htmlDocker);
                    // 应用程序
                    var htmlInstance = '<div class="card-title-tongji" style="">' +
                        '               <div class="layuiadmin-big-font layuiadmin-big-font-my"><p>'+homeInstanceVo.instanceSum+'</p>' + '<span class="unit-label">' + t('home.item') + '</span></div>' +
                        '               <div class="card-progress-style">' +
                        '                   <span>'+ homeInstanceVo.instanceOnLineRate +'%</span>'+
                        '                   <div class="layui-progress">' +
                        '                       <div class="layui-progress-bar" lay-percent="'+ homeInstanceVo.instanceOnLineRate +'%"></div>'+
                        '                   </div>'+
                        '               </div>'+
                        '            </div>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-online-flag"></span>' + t('home.online') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeInstanceVo.instanceOnLineSum +
                        '                   </span>' +
                        '               </p>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-offline-flag"></span>' + t('home.offline') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeInstanceVo.instanceOffLineSum +
                        '                   </span>' +
                        '               </p>' +
                        '               <p class="device-card-row-style">' + '<span class="card-status-error-flag"></span>' + t('home.unknown') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeInstanceVo.instanceUnknownLineSum +
                        '                   </span>' +
                        '               </p>';
                    $('#instance-card-list').empty().append(htmlInstance);
                    // 数据库
                    var htmlDb = '<div class="card-title-tongji" style="">' +
                        '               <div class="layuiadmin-big-font layuiadmin-big-font-my"><p>'+homeDbVo.dbSum+'</p>' + '<span class="unit-label">' + t('home.item') + '</span></div>' +
                        '               <div class="card-progress-style">' +
                        '                   <span>'+ homeDbVo.dbConnectRate +'%</span>'+
                        '                   <div class="layui-progress">' +
                        '                       <div class="layui-progress-bar" lay-percent="'+ homeDbVo.dbConnectRate +'%"></div>'+
                        '                   </div>'+
                        '               </div>'+
                        '            </div>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-online-flag"></span>' + t('home.normal') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeDbVo.dbConnectSum +
                        '               </span>' +
                        '         </p>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-offline-flag"></span>' + t('home.abnormal') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeDbVo.dbDisconnectSum +
                        '               </span>' +
                        '         </p>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-error-flag"></span>' + t('home.unknown') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeDbVo.dbUnsentSum +
                        '               </span>' +
                        '         </p>';
                    $('#db-card-list').empty().append(htmlDb);
                    // 网络
                    var htmlIp = '<div class="card-title-tongji" style="">' +
                        '               <div class="layuiadmin-big-font layuiadmin-big-font-my"><p>'+homeNetVo.netSum+'</p>' + '<span class="unit-label">' + t('home.item') + '</span></div>' +
                        '               <div class="card-progress-style">' +
                        '                   <span>'+ homeNetVo.netConnectRate +'%</span>'+
                        '                   <div class="layui-progress">' +
                        '                       <div class="layui-progress-bar" lay-percent="'+ homeNetVo.netConnectRate +'%"></div>'+
                        '                   </div>'+
                        '               </div>'+
                        '            </div>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-online-flag"></span>' + t('home.normal') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeNetVo.netConnectSum +
                        '               </span>' +
                        '         </p>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-offline-flag"></span>' + t('home.abnormal') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeNetVo.netDisconnectSum +
                        '               </span>' +
                        '         </p>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-error-flag"></span>' + t('home.unknown') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeNetVo.netUnsentSum +
                        '               </span>' +
                        '         </p>';
                    $('#ip-card-list').empty().append(htmlIp);
                    // TCP
                    var htmlTcp = '<div class="card-title-tongji" style="">' +
                        '               <div class="layuiadmin-big-font layuiadmin-big-font-my"><p>'+homeTcpVo.tcpSum+'</p>' + '<span class="unit-label">' + t('home.item') + '</span></div>' +
                        '               <div class="card-progress-style">' +
                        '                   <span>'+ homeTcpVo.tcpConnectRate +'%</span>'+
                        '                   <div class="layui-progress">' +
                        '                       <div class="layui-progress-bar" lay-percent="'+ homeTcpVo.tcpConnectRate +'%"></div>'+
                        '                   </div>'+
                        '               </div>'+
                        '            </div>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-online-flag"></span>' + t('home.normal') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeTcpVo.tcpConnectSum +
                        '               </span>' +
                        '         </p>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-offline-flag"></span>' + t('home.abnormal') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeTcpVo.tcpDisconnectSum +
                        '               </span>' +
                        '         </p>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-error-flag"></span>' + t('home.unknown') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeTcpVo.tcpUnsentSum +
                        '               </span>' +
                        '         </p>';
                    $('#tcp-card-list').empty().append(htmlTcp);
                    // HTTP
                    var htmlHttp = '<div class="card-title-tongji" style="">' +
                        '               <div class="layuiadmin-big-font layuiadmin-big-font-my"><p>'+homeHttpVo.httpSum+'</p>' + '<span class="unit-label">' + t('home.item') + '</span></div>' +
                        '               <div class="card-progress-style">' +
                        '                   <span>'+ homeHttpVo.httpConnectRate +'%</span>'+
                        '                   <div class="layui-progress">' +
                        '                       <div class="layui-progress-bar" lay-percent="'+ homeHttpVo.httpConnectRate +'%"></div>'+
                        '                   </div>'+
                        '               </div>'+
                        '            </div>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-online-flag"></span>' + t('home.normal') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeHttpVo.httpConnectSum +
                        '               </span>' +
                        '         </p>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-offline-flag"></span>' + t('home.abnormal') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeHttpVo.httpDisconnectSum +
                        '               </span>' +
                        '         </p>' +
                        '         <p class="device-card-row-style">' + '<span class="card-status-error-flag"></span>' + t('home.unknown') +
                        '               <span class="layuiadmin-span-color new-card-num">' + homeHttpVo.httpUnsentSum +
                        '               </span>' +
                        '         </p>';
                    $('#http-card-list').empty().append(htmlHttp);
                    // 告警
                    var htmlAlarm = '<div class="card-title-tongji" style="">' +
                        '               <div class="layuiadmin-big-font layuiadmin-big-font-my"><p>'+homeAlarmRecordVo.alarmRecordSum+'</p>' + '<span class="unit-label">' + t('home.timesUnit') + '</span></div>' +
                        '               <div class="card-progress-style">' +
                        '                   <span>'+ homeAlarmRecordVo.alarmSucRate +'%</span>'+
                        '                   <div class="layui-progress">' +
                        '                       <div class="layui-progress-bar" lay-percent="'+ homeAlarmRecordVo.alarmSucRate +'%"></div>'+
                        '                   </div>'+
                        '               </div>'+
                        '            </div>' +
                        '            <p class="device-card-row-style">' + '<span class="card-status-online-flag"></span>' + t('home.success') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeAlarmRecordVo.alarmRecordSuccessSum +
                        '                   </span>' +
                        '            </p>' +
                        '            <p class="device-card-row-style">' + '<span class="card-status-offline-flag"></span>' + t('home.failure') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeAlarmRecordVo.alarmRecordFailSum +
                        '                   </span>' +
                        '            </p>' +
                        '            <p class="device-card-row-style">' + '<span class="card-status-error-flag"></span>' + t('home.noAlert') +
                        '                   <span class="layuiadmin-span-color new-card-num">' + homeAlarmRecordVo.alarmRecordUnsentSum +
                        '                   </span>' +
                        '            </p>';
                    $('#alarm-card-list').empty().append(htmlAlarm);
                },
                error: function () {
                    layer.msg(typeof I18nUtils !== 'undefined' ? I18nUtils.t('system.message.systemError') : '系统错误！', {icon: 5, shift: 6});
                }
            });
        }

        // 发送ajax请求，获取最近7天告警统计数据
        getLast7DaysAlarmRecordStatistics();
        // 发送ajax请求，获取告警类型统计信息
        getAlarmRecordTypeStatistics();
        // 发送ajax，获取告警结果统计信息
        getAlarmRecordResultStatistics();
        // 发送ajax请求，获取最新的5条告警记录
        getLast5AlarmRecord();
        // 发送ajax请求，获取home页的摘要信息（页面加载时也要发送ajax）
        getSummaryInfo();
        // 每30秒刷新一次
        window.setInterval(function () {
            // 发送ajax请求，获取最近7天告警统计数据
            getLast7DaysAlarmRecordStatistics();
            // 发送ajax请求，获取告警类型统计信息
            getAlarmRecordTypeStatistics();
            // 发送ajax，获取告警结果统计信息
            getAlarmRecordResultStatistics();
            // 发送ajax请求，获取最新的5条告警记录
            getLast5AlarmRecord();
            // 发送ajax请求，获取home页的摘要信息
            getSummaryInfo();
        }, 1000 * 30);
    });
    e('home', {});
});