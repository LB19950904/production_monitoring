/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(['admin', 'element', 'form', 'layer'], function (e) {
    var admin = layui.admin, $ = layui.$, form = layui.form, layer = layui.layer, device = layui.device();
    // 基于准备好的dom，初始化echarts实例
    var getJavaThreadPoolActiveInfoChart = echarts.init(document.getElementById('get-java-thread-pool-active-info'), 'infographic');
    var getJavaThreadPoolQueueInfoChart = echarts.init(document.getElementById('get-java-thread-pool-queue-info'), 'infographic');
    var getJavaThreadPoolCompletedInfoChart = echarts.init(document.getElementById('get-java-thread-pool-completed-info'), 'infographic');
    var getJavaThreadPoolRejectedInfoChart = echarts.init(document.getElementById('get-java-thread-pool-rejected-info'), 'infographic');
    // 浏览器窗口大小发生改变时
    window.addEventListener("resize", function () {
        getJavaThreadPoolActiveInfoChart.resize();
        getJavaThreadPoolQueueInfoChart.resize();
        getJavaThreadPoolCompletedInfoChart.resize();
        getJavaThreadPoolRejectedInfoChart.resize();
    }, {capture: true});
    // 时间
    var chartTime = 'hour';
    // 线程池名字
    var javaThreadPoolNameVal = $('#javaThreadPoolNameChart option:selected').val();
    var chartPool = javaThreadPoolNameVal === undefined ? '' : javaThreadPoolNameVal;
    // 是否自动刷新
    var autoRefresh = true;
    // 线程池名字条件发生改变
    form.on('select(javaThreadPoolNameChart)', function (data) {
        // 线程池名字
        chartPool = data.value;
        // 发送ajax请求，获取线程池摘要信息
        getJavaThreadPoolAbstractInfo();
        // 发送ajax请求，获取线程池图表数据
        getJavaThreadPoolChartInfo();
    });
    // 时间条件发生改变
    form.on('select(javaThreadPoolTime)', function (data) {
        chartTime = data.value;
        // 发送ajax请求，获取线程池图表数据
        getJavaThreadPoolChartInfo();
    });
    // 自动刷新条件改变
    form.on('switch(autoRefreshJavaThreadPool)', function (data) {
        //是否被选中，true或者false
        autoRefresh = data.elem.checked;
    });
    // 点击Java线程池配置按钮
    $('#javaThreadPoolSetup').on('click', function () {
        // 宽度
        var width;
        // 高度
        var height;
        // 移动端
        if (device.ios || device.android) {
            // 宽度
            width = document.body.clientWidth * 0.8 + 'px';
            // 高度
            height = document.body.clientHeight * 0.55 + 'px';
        } else {
            // 宽度
            width = document.body.clientWidth * 0.4 + 'px';
            // 高度
            height = document.body.clientHeight * 0.5 + 'px';
        }
        // java线程池名字
        var javaThreadPoolName = $('#javaThreadPoolNameChart option:selected').val();
        var javaThreadPoolSetupLayerIndex = layer.open({
            type: 2,
            title: I18nUtils.t('instanceDetail.configDialog.title', {name: javaThreadPoolName}),
            content: ctxPath + 'monitor-instance/set-instance-java-thread-pool-form?endpoint=' + endpoint + '&instanceId=' + instanceId + '&javaThreadPoolName=' + javaThreadPoolName,
            maxmin: true,
            moveOut: true,
            area: [width, height],
            btn: [I18nUtils.t('common.confirm'), I18nUtils.t('common.cancel')],
            yes: function (index, layero) {
                var iframeWindow = window['layui-layer-iframe' + index],
                    submitID = 'LAY-front-submit',
                    submit = layero.find('iframe').contents().find('#' + submitID);
                //监听提交
                iframeWindow.layui.form.on('submit(' + submitID + ')', function (data) {
                    var field = data.field; //获取提交的字段
                    field.allowCoreThreadTimeOut = field.allowCoreThreadTimeOut === 'on';
                    var loading = layer.load(1, {
                        shade: [0.1, '#fff'] //0.1透明度的白色背景
                    });
                    admin.req({
                        type: 'put',
                        url: ctxPath + 'monitor-instance/set-instance-java-thread-pool',
                        data: field,
                        dataType: 'json',
                        headers: {
                            "X-CSRF-TOKEN": tokenValue
                        },
                        success: function (result) {
                            var data = result.data;
                            if (data === webConst.SUCCESS) {
                                layer.close(index); //关闭弹层
                                layer.msg(I18nUtils.t('instanceDetail.configDialog.configSuccess'), {icon: 6});
                                // 重新发送ajax请求，获取线程池摘要信息
                                getJavaThreadPoolAbstractInfo();
                            } else {
                                layer.msg(I18nUtils.t('instanceDetail.configDialog.configFailed'), {icon: 5, shift: 6});
                            }
                            layer.close(loading);
                        },
                        error: function () {
                            layer.msg(I18nUtils.t('instanceDetail.configDialog.systemError'), {icon: 5, shift: 6});
                            layer.close(loading);
                        },
                        complete: function () {
                            layer.close(loading);
                        }
                    });
                });
                submit.trigger('click');
            }
        });
        if (device.ios || device.android) {
            layer.full(javaThreadPoolSetupLayerIndex);
        }
    });

    // 发送ajax请求，获取线程池摘要信息
    function getJavaThreadPoolAbstractInfo() {
        admin.req({
            type: 'get',
            url: layui.setter.base + 'monitor-java-thread-pool/get-java-thread-pool-info',
            dataType: 'json',
            contentType: 'application/json;charset=utf-8',
            headers: {
                "X-CSRF-TOKEN": tokenValue
            },
            data: {
                instanceId: instanceId, // 应用实例ID
                threadPoolName: chartPool // 线程池名字
            },
            success: function (result) {
                var data = result.data;
                var name = isEmpty(data.name) ? '' : data.name;
                var activeCount = isEmpty(data.activeCount) ? '' : data.activeCount;
                var poolSize = isEmpty(data.poolSize) ? '' : data.poolSize;
                var corePoolSize = isEmpty(data.corePoolSize) ? '' : data.corePoolSize;
                var maximumPoolSize = isEmpty(data.maximumPoolSize) ? '' : data.maximumPoolSize;
                var largestPoolSize = isEmpty(data.largestPoolSize) ? '' : data.largestPoolSize;
                var taskCount = isEmpty(data.taskCount) ? '' : data.taskCount;
                var rejectedTaskCount = isEmpty(data.rejectedTaskCount) ? '' : data.rejectedTaskCount;
                var rejectedExecutionHandlerName = isEmpty(data.rejectedExecutionHandlerName) ? '' : data.rejectedExecutionHandlerName;
                var completedTaskCount = isEmpty(data.completedTaskCount) ? '' : data.completedTaskCount;
                var utilizationRate = isEmpty(data.utilizationRate) ? '' : data.utilizationRate;
                var queueSize = isEmpty(data.queueSize) ? '' : data.queueSize;
                var queueRemainingCapacity = isEmpty(data.queueRemainingCapacity) ? '' : data.queueRemainingCapacity;
                var queueType = isEmpty(data.queueType) ? '' : data.queueType;
                var queueCapacity = isEmpty(data.queueCapacity) ? '' : data.queueCapacity;
                var allowCoreThreadTimeOut = isEmpty(data.allowCoreThreadTimeOut) ? '' : (data.allowCoreThreadTimeOut === true ? I18nUtils.t('instanceDetail.threadPool.recycle') : I18nUtils.t('instanceDetail.threadPool.notRecycle'));
                var keepAliveTime = isEmpty(data.keepAliveTime) ? '' : data.keepAliveTime + ' ' + I18nUtils.t('instanceDetail.threadPool.seconds');
                var html = '<!-- 第一行：基础信息 -->' +
                    '<div class="layui-row layui-col-space10">' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.name') + '</div>' +
                    '            <div class="thread-pool-info-value">' + name + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.corePoolSize') + '</div>' +
                    '            <div class="thread-pool-info-value">' + corePoolSize + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.maximumPoolSize') + '</div>' +
                    '            <div class="thread-pool-info-value">' + maximumPoolSize + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.largestPoolSize') + '</div>' +
                    '            <div class="thread-pool-info-value">' + largestPoolSize + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '</div>' +
                    '<!-- 第二行：线程状态 -->' +
                    '<div class="layui-row layui-col-space10">' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.poolSize') + '</div>' +
                    '            <div class="thread-pool-info-value">' + poolSize + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.activeCount') + '</div>' +
                    '            <div class="thread-pool-info-value">' + activeCount + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.taskCount') + '</div>' +
                    '            <div class="thread-pool-info-value">' + taskCount + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.completedTaskCount') + '</div>' +
                    '            <div class="thread-pool-info-value">' + completedTaskCount + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '</div>' +
                    '<!-- 第三行：队列信息 -->' +
                    '<div class="layui-row layui-col-space10">' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.queueType') + '</div>' +
                    '            <div class="thread-pool-info-value">' + queueType + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.queueCapacity') + '</div>' +
                    '            <div class="thread-pool-info-value">' + queueCapacity + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.queueRemainingCapacity') + '</div>' +
                    '            <div class="thread-pool-info-value">' + queueRemainingCapacity + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.queueSize') + '</div>' +
                    '            <div class="thread-pool-info-value">' + queueSize + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '</div>' +
                    '<!-- 第四行：策略与配置 -->' +
                    '<div class="layui-row layui-col-space10">' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.rejectedTaskCount') + '</div>' +
                    '            <div class="thread-pool-info-value">' + rejectedTaskCount + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.rejectedExecutionHandler') + '</div>' +
                    '            <div class="thread-pool-info-value">' + rejectedExecutionHandlerName + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.allowCoreThreadTimeOut') + '</div>' +
                    '            <div class="thread-pool-info-value">' + allowCoreThreadTimeOut + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.keepAliveTime') + '</div>' +
                    '            <div class="thread-pool-info-value">' + keepAliveTime + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '</div>' +
                    '<!-- 第五行：利用率 -->' +
                    '<div class="layui-row layui-col-space10">' +
                    '    <div class="layui-col-md3">' +
                    '        <div class="thread-pool-info-container">' +
                    '            <div class="thread-pool-info-label">' + I18nUtils.t('instanceDetail.threadPool.utilizationRate') + '</div>' +
                    '            <div class="thread-pool-info-value">' + utilizationRate + '</div>' +
                    '        </div>' +
                    '    </div>' +
                    '</div>';
                $('#thread-pool-abstract').empty().append(html);
            }
        });
    }

    // 发送ajax请求，获取线程池图表数据
    function getJavaThreadPoolChartInfo() {
        admin.req({
            type: 'get',
            url: layui.setter.base + 'monitor-java-thread-pool-history/get-instance-detail-page-java-thread-pool-chart-info',
            dataType: 'json',
            contentType: 'application/json;charset=utf-8',
            headers: {
                "X-CSRF-TOKEN": tokenValue
            },
            data: {
                instanceId: instanceId, // 应用实例ID
                time: chartTime, // 时间
                threadPoolName: chartPool // 线程池名字
            },
            success: function (result) {
                var data = result.data;
                // 时间
                var time = data.map(function (item) {
                    return item.insertTime.replace(' ', '\n');
                });
                // 活跃线程数
                var activeCount = data.map(function (item) {
                    return item.activeCount;
                });
                // 已完成的任务数
                var completedTaskCount = data.map(function (item) {
                    return item.completedTaskCount;
                });
                // 当前队列大小
                var queueSize = data.map(function (item) {
                    return item.queueSize;
                });
                // 拒绝的任务数量
                var rejectedTaskCount = data.map(function (item) {
                    return item.rejectedTaskCount;
                });
                // 渲染活跃线程数图表
                playUpJavaThreadPoolActiveInfoChart(time, activeCount);
                // 渲染队列大小图表
                playUpJavaThreadPoolQueueInfoChart(time, queueSize);
                // 渲染已完成任务数图表
                playUpJavaThreadPoolCompletedInfoChart(time, completedTaskCount);
                // 渲染拒绝任务数量图表
                playUpJavaThreadPoolRejectedInfoChart(time, rejectedTaskCount);
            }
        });
    }

    // 渲染活跃线程数图表
    function playUpJavaThreadPoolActiveInfoChart(time, activeCount) {
        var option = {
            title: {
                text: I18nUtils.t('instanceDetail.chartText.activeThreadCount'),
                left: 'center',
                textStyle: {
                    color: '#696969',
                    fontSize: 14
                },
                subtext: 'activeSize',
                subtextStyle: {
                    color: '#BEBEBE'
                }
            }, // 鼠标移到折线上展示数据
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    var result = '';
                    var axisName = '';
                    params.forEach(function (item) {
                        axisName = item.axisValue;
                        var itemValue = item.marker + item.seriesName + ': ' + item.data + '</br>';
                        result += itemValue;
                    });
                    return axisName + '</br>' + result;
                }
            },
            legend: {
                data: [I18nUtils.t('instanceDetail.chartText.count')],
                orient: 'vertical',
                x: '80%' //图例位置，设置right发现图例和文字位置反了，设置一个数值就好了
            },
            /*dataZoom: [{
                type: 'inside'
            }],*/
            toolbox: {
                show: true,
                feature: {
                    dataZoom: {
                        yAxisIndex: "none"
                    },
                    dataView: {
                        readOnly: false
                    },
                    magicType: {
                        type: ["line", "bar"]
                    },
                    restore: {},
                    saveAsImage: {}
                },
                iconStyle: {
                    borderColor: "rgba(105, 98, 98, 1)"
                },
                right: "2%",
                orient: "vertical",
                showTitle: false,
            },
            /*grid: {
                left: '5%',
                right: '5%'
            },*/
            xAxis: {
                type: 'category', // X轴从零刻度开始
                boundaryGap: false,
                data: time,
                axisLabel: {
                    rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                },
            },
            yAxis: {
                type: 'value',
                name: I18nUtils.t('instanceDetail.chartText.count'),
                axisLabel: {
                    formatter: '{value}'
                }
            }, // 数据
            series: [{
                name: I18nUtils.t('instanceDetail.chartText.count'),
                data: activeCount,
                type: 'line',
                smooth: true,
                areaStyle: {
                    type: 'default',
                    // 渐变色实现
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                        // 三种由深及浅的颜色
                        [{
                            offset: 0,
                            color: '#B4EEB4'
                        }, {
                            offset: 0.5,
                            color: '#C1FFC1'
                        }, {
                            offset: 1,
                            color: '#FFFFFF'
                        }])
                },
                itemStyle: {
                    normal: {
                        // 设置颜色
                        color: '#9BCD9B'
                    }
                }
            }]
        };
        getJavaThreadPoolActiveInfoChart.setOption(option);
    }

    // 渲染队列大小图表
    function playUpJavaThreadPoolQueueInfoChart(time, queueSize) {
        var option = {
            title: {
                text: I18nUtils.t('instanceDetail.chartText.queueSize'),
                left: 'center',
                textStyle: {
                    color: '#696969',
                    fontSize: 14
                },
                subtext: 'queueSize',
                subtextStyle: {
                    color: '#BEBEBE'
                }
            }, // 鼠标移到折线上展示数据
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    var result = '';
                    var axisName = '';
                    params.forEach(function (item) {
                        axisName = item.axisValue;
                        var itemValue = item.marker + item.seriesName + ': ' + item.data + '</br>';
                        result += itemValue;
                    });
                    return axisName + '</br>' + result;
                }
            },
            legend: {
                data: [I18nUtils.t('instanceDetail.chartText.count')],
                orient: 'vertical',
                x: '80%' //图例位置，设置right发现图例和文字位置反了，设置一个数值就好了
            },
            /*dataZoom: [{
                type: 'inside'
            }],*/
            toolbox: {
                show: true,
                feature: {
                    dataZoom: {
                        yAxisIndex: "none"
                    },
                    dataView: {
                        readOnly: false
                    },
                    magicType: {
                        type: ["line", "bar"]
                    },
                    restore: {},
                    saveAsImage: {}
                },
                iconStyle: {
                    borderColor: "rgba(105, 98, 98, 1)"
                },
                right: "2%",
                orient: "vertical",
                showTitle: false,
            },
            /*grid: {
                left: '5%',
                right: '5%'
            },*/
            xAxis: {
                type: 'category', // X轴从零刻度开始
                boundaryGap: false,
                data: time,
                axisLabel: {
                    rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                },
            },
            yAxis: {
                type: 'value',
                name: I18nUtils.t('instanceDetail.chartText.count'),
                axisLabel: {
                    formatter: '{value}'
                }
            }, // 数据
            series: [{
                name: I18nUtils.t('instanceDetail.chartText.count'),
                data: queueSize,
                type: 'line',
                smooth: true,
                areaStyle: {
                    type: 'default',
                    // 渐变色实现
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                        // 三种由深及浅的颜色
                        [{
                            offset: 0,
                            color: '#B4EEB4'
                        }, {
                            offset: 0.5,
                            color: '#C1FFC1'
                        }, {
                            offset: 1,
                            color: '#FFFFFF'
                        }])
                },
                itemStyle: {
                    normal: {
                        // 设置颜色
                        color: '#9BCD9B'
                    }
                }
            }]
        };
        getJavaThreadPoolQueueInfoChart.setOption(option);
    }

    // 渲染已完成任务数图表
    function playUpJavaThreadPoolCompletedInfoChart(time, completedTaskCount) {
        var option = {
            title: {
                text: I18nUtils.t('instanceDetail.chartText.completedTaskCount'),
                left: 'center',
                textStyle: {
                    color: '#696969',
                    fontSize: 14
                },
                subtext: 'completedTaskCount',
                subtextStyle: {
                    color: '#BEBEBE'
                }
            }, // 鼠标移到折线上展示数据
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    var result = '';
                    var axisName = '';
                    params.forEach(function (item) {
                        axisName = item.axisValue;
                        var itemValue = item.marker + item.seriesName + ': ' + item.data + '</br>';
                        result += itemValue;
                    });
                    return axisName + '</br>' + result;
                }
            },
            legend: {
                data: [I18nUtils.t('instanceDetail.chartText.count')],
                orient: 'vertical',
                x: '80%' //图例位置，设置right发现图例和文字位置反了，设置一个数值就好了
            },
            /*dataZoom: [{
                type: 'inside'
            }],*/
            toolbox: {
                show: true,
                feature: {
                    dataZoom: {
                        yAxisIndex: "none"
                    },
                    dataView: {
                        readOnly: false
                    },
                    magicType: {
                        type: ["line", "bar"]
                    },
                    restore: {},
                    saveAsImage: {}
                },
                iconStyle: {
                    borderColor: "rgba(105, 98, 98, 1)"
                },
                right: "2%",
                orient: "vertical",
                showTitle: false,
            },
            /*grid: {
                left: '5%',
                right: '5%'
            },*/
            xAxis: {
                type: 'category', // X轴从零刻度开始
                boundaryGap: false,
                data: time,
                axisLabel: {
                    rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                },
            },
            yAxis: {
                type: 'value',
                name: I18nUtils.t('instanceDetail.chartText.count'),
                axisLabel: {
                    formatter: '{value}'
                }
            }, // 数据
            series: [{
                name: I18nUtils.t('instanceDetail.chartText.count'),
                data: completedTaskCount,
                type: 'line',
                smooth: true,
                areaStyle: {
                    type: 'default',
                    // 渐变色实现
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                        // 三种由深及浅的颜色
                        [{
                            offset: 0,
                            color: '#B4EEB4'
                        }, {
                            offset: 0.5,
                            color: '#C1FFC1'
                        }, {
                            offset: 1,
                            color: '#FFFFFF'
                        }])
                },
                itemStyle: {
                    normal: {
                        // 设置颜色
                        color: '#9BCD9B'
                    }
                }
            }]
        };
        getJavaThreadPoolCompletedInfoChart.setOption(option);
    }

    // 渲染拒绝任务数量图表
    function playUpJavaThreadPoolRejectedInfoChart(time, rejectedTaskCount) {
        var option = {
            title: {
                text: I18nUtils.t('instanceDetail.chartText.rejectedTaskCount'),
                left: 'center',
                textStyle: {
                    color: '#696969',
                    fontSize: 14
                },
                subtext: 'rejectedTaskCount',
                subtextStyle: {
                    color: '#BEBEBE'
                }
            }, // 鼠标移到折线上展示数据
            tooltip: {
                trigger: 'axis',
                formatter: function (params) {
                    var result = '';
                    var axisName = '';
                    params.forEach(function (item) {
                        axisName = item.axisValue;
                        var itemValue = item.marker + item.seriesName + ': ' + item.data + '</br>';
                        result += itemValue;
                    });
                    return axisName + '</br>' + result;
                }
            },
            legend: {
                data: [I18nUtils.t('instanceDetail.chartText.count')],
                orient: 'vertical',
                x: '80%' //图例位置，设置right发现图例和文字位置反了，设置一个数值就好了
            },
            /*dataZoom: [{
                type: 'inside'
            }],*/
            toolbox: {
                show: true,
                feature: {
                    dataZoom: {
                        yAxisIndex: "none"
                    },
                    dataView: {
                        readOnly: false
                    },
                    magicType: {
                        type: ["line", "bar"]
                    },
                    restore: {},
                    saveAsImage: {}
                },
                iconStyle: {
                    borderColor: "rgba(105, 98, 98, 1)"
                },
                right: "2%",
                orient: "vertical",
                showTitle: false,
            },
            /*grid: {
                left: '5%',
                right: '5%'
            },*/
            xAxis: {
                type: 'category', // X轴从零刻度开始
                boundaryGap: false,
                data: time,
                axisLabel: {
                    rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                },
            },
            yAxis: {
                type: 'value',
                name: I18nUtils.t('instanceDetail.chartText.count'),
                axisLabel: {
                    formatter: '{value}'
                }
            }, // 数据
            series: [{
                name: I18nUtils.t('instanceDetail.chartText.count'),
                data: rejectedTaskCount,
                type: 'line',
                smooth: true,
                areaStyle: {
                    type: 'default',
                    // 渐变色实现
                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                        // 三种由深及浅的颜色
                        [{
                            offset: 0,
                            color: '#B4EEB4'
                        }, {
                            offset: 0.5,
                            color: '#C1FFC1'
                        }, {
                            offset: 1,
                            color: '#FFFFFF'
                        }])
                },
                itemStyle: {
                    normal: {
                        // 设置颜色
                        color: '#9BCD9B'
                    }
                }
            }]
        };
        getJavaThreadPoolRejectedInfoChart.setOption(option);
    }

    // 执行ajax请求
    function execute() {
        if (autoRefresh) {
            // 发送ajax请求，获取线程池摘要信息
            getJavaThreadPoolAbstractInfo();
            // 发送ajax请求，获取线程池图表数据
            getJavaThreadPoolChartInfo();
        }
    }

    // 页面加载后第一次执行
    execute();
    // 每30秒刷新一次
    window.setInterval(function () {
        execute();
    }, 1000 * 30);

    e('instanceDetailJavaThreadPool', {
        // tab页面切换调用方法
        tabSwitch: function () {
            getJavaThreadPoolActiveInfoChart.resize();
            getJavaThreadPoolQueueInfoChart.resize();
            getJavaThreadPoolCompletedInfoChart.resize();
            getJavaThreadPoolRejectedInfoChart.resize();
        }
    });
});