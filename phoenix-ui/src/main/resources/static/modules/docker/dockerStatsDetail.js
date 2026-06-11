/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['admin', 'form'], function () {
        var admin = layui.admin, form = layui.form;
        // 基于准备好的dom，初始化echarts实例
        var getDockerCpuUtilizationRateInfoChart = echarts.init(document.getElementById('get-docker-cpu-utilization-rate-info'), 'infographic');
        var getDockerMenUtilizationRateInfoChart = echarts.init(document.getElementById('get-docker-men-utilization-rate-info'), 'infographic');
        var getDockerMenUsageLimitInfoChart = echarts.init(document.getElementById('get-docker-men-usage-limit-info'), 'infographic');
        var getDockerNetInfoChart = echarts.init(document.getElementById('get-docker-net-info'), 'infographic');
        var getDockerBlockInfoChart = echarts.init(document.getElementById('get-docker-block-info'), 'infographic');

        // 浏览器窗口大小发生改变时
        window.addEventListener("resize", function () {
            getDockerCpuUtilizationRateInfoChart.resize();
            getDockerMenUtilizationRateInfoChart.resize();
            getDockerMenUsageLimitInfoChart.resize();
            getDockerNetInfoChart.resize();
            getDockerBlockInfoChart.resize();
        });
        // 时间
        var time = 'hour';
        // 是否自动刷新
        var autoRefresh = true;
        // 时间条件发生改变
        form.on('select(time)', function (data) {
            time = data.value;
            // 发送ajax请求，获取docker CPU使用率图表数据
            getDockerCpuUtilizationRateChartInfo(time);
            // 发送ajax请求，获取docker内存使用率图表数据
            getDockerMenUtilizationRateChartInfo(time);
            // 发送ajax请求，获取docker当前使用的内存和最大可以使用的内存图表数据
            getDockerMenUsageLimitChartInfo(time);
            // 发送ajax请求，获取docker网络图表数据
            getDockerNetChartInfo(time);
            // 发送ajax请求，获取docker磁盘图表数据
            getDockerBlockChartInfo(time);
        });
        // 自动刷新条件改变
        form.on('switch(autoRefresh)', function (data) {
            //是否被选中，true或者false
            autoRefresh = data.elem.checked;
        });

        // 发送ajax请求，获取docker CPU使用率图表数据
        function getDockerCpuUtilizationRateChartInfo(time) {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-docker-stats-history/get-docker-cpu-utilization-rate-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    serverIp: serverIp, // 服务器IP
                    containerName: containerName,// 容器名
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // CPU使用率
                    var cpuUtilizationRate = data.map(function (item) {
                        return item.cpuUtilizationRate;
                    });
                    // 最大CPU使用率
                    var maxCpuUtilizationRate = Math.ceil(Math.max.apply(null, cpuUtilizationRate));
                    // 最新CPU使用率
                    var lastCpuUtilizationRate = data.length !== 0 ? data[data.length - 1].cpuUtilizationRate + '%' : '没数据';
                    // 新增时间
                    var insertTime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    var option = {
                        title: {
                            text: 'CPU使用率',
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: '容器名：' + containerName + '，使用率：' + lastCpuUtilizationRate,
                            subtextStyle: {
                                color: '#BEBEBE'
                            }
                        },
                        // 鼠标移到折线上展示数据
                        tooltip: {
                            trigger: 'axis',
                            formatter: function (params) {
                                var result = '';
                                var axisName = '';
                                params.forEach(function (item) {
                                    axisName = item.axisValue;
                                    var itemValue = item.marker + item.seriesName + ': ' + item.data + '%</br>';
                                    result += itemValue;
                                });
                                return axisName + '</br>' + result;
                            }
                        },
                        legend: {
                            data: ['使用率'],
                            x: 'center',
                            y: '12%',
                            orient: 'horizontal'
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
                        grid: {
                            left: '5%',
                            right: '5%'
                        },
                        xAxis: {
                            type: 'category',
                            // X轴从零刻度开始
                            boundaryGap: false,
                            data: insertTime,
                            axisLabel: {
                                rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                            }
                        },
                        yAxis: {
                            type: 'value',
                            name: '使用率',
                            min: 0,  //一定要设置最小刻度
                            max: maxCpuUtilizationRate > 100 ? maxCpuUtilizationRate : 100,
                            minInterval: 20, //这个可自己设置刻度间隔
                            axisLabel: {
                                formatter: '{value}%'
                            }
                        },
                        // 数据
                        series: [{
                            name: '使用率',
                            data: cpuUtilizationRate,
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
                    getDockerCpuUtilizationRateInfoChart.setOption(option);
                },
                error: function () {
                }
            });
        }

        // 发送ajax请求，获取docker内存使用率图表数据
        function getDockerMenUtilizationRateChartInfo(time) {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-docker-stats-history/get-docker-men-utilization-rate-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    serverIp: serverIp, // 服务器IP
                    containerName: containerName,// 容器名
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // 内存使用率
                    var menUtilizationRate = data.map(function (item) {
                        return item.menUtilizationRate;
                    });
                    // 最大内存使用率
                    var maxMenUtilizationRate = Math.ceil(Math.max.apply(null, menUtilizationRate));
                    // 最新内存使用率
                    var lastMenUtilizationRate = data.length !== 0 ? data[data.length - 1].menUtilizationRate + '%' : '没数据';
                    // 新增时间
                    var insertTime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    var option = {
                        title: {
                            text: '内存使用率',
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: '容器名：' + containerName + '，使用率：' + lastMenUtilizationRate,
                            subtextStyle: {
                                color: '#BEBEBE'
                            }
                        },
                        // 鼠标移到折线上展示数据
                        tooltip: {
                            trigger: 'axis',
                            formatter: function (params) {
                                var result = '';
                                var axisName = '';
                                params.forEach(function (item) {
                                    axisName = item.axisValue;
                                    var itemValue = item.marker + item.seriesName + ': ' + item.data + '%</br>';
                                    result += itemValue;
                                });
                                return axisName + '</br>' + result;
                            }
                        },
                        legend: {
                            data: ['使用率'],
                            x: 'center',
                            y: '12%',
                            orient: 'horizontal'
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
                        grid: {
                            left: '5%',
                            right: '5%'
                        },
                        xAxis: {
                            type: 'category',
                            // X轴从零刻度开始
                            boundaryGap: false,
                            data: insertTime,
                            axisLabel: {
                                rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                            }
                        },
                        yAxis: {
                            type: 'value',
                            name: '使用率',
                            min: 0,  //一定要设置最小刻度
                            max: maxMenUtilizationRate > 100 ? maxMenUtilizationRate : 100,
                            minInterval: 20, //这个可自己设置刻度间隔
                            axisLabel: {
                                formatter: '{value}%'
                            }
                        },
                        // 数据
                        series: [{
                            name: '使用率',
                            data: menUtilizationRate,
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
                    getDockerMenUtilizationRateInfoChart.setOption(option);
                },
                error: function () {
                }
            });
        }

        // 发送ajax请求，获取docker当前使用的内存和最大可以使用的内存图表数据
        function getDockerMenUsageLimitChartInfo(time) {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-docker-stats-history/get-docker-men-usage-limit-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    serverIp: serverIp, // 服务器IP
                    containerName: containerName,// 容器名
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // 当前使用的内存
                    var menUsage = data.map(function (item) {
                        return item.menUsage;
                    });
                    // 最新当前使用的内存
                    var lastMenUsage = data.length !== 0 ? convertSize(data[data.length - 1].menUsage) : '没数据';
                    // 最大可以使用的内存
                    var lastMenLimitNum = data.length !== 0 ? data[data.length - 1].menLimit : 0;
                    var lastMenLimit = data.length !== 0 ? convertSize(data[data.length - 1].menLimit) : '没数据';
                    // 新增时间
                    var insertTime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    var option = {
                        title: {
                            text: '内存',
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: '容器名：' + containerName + '，最大：' + lastMenLimit + '，使用量：' + lastMenUsage,
                            subtextStyle: {
                                color: '#BEBEBE'
                            }
                        },
                        // 鼠标移到折线上展示数据
                        tooltip: {
                            trigger: 'axis',
                            formatter: function (params) {
                                var result = '';
                                var axisName = '';
                                params.forEach(function (item) {
                                    axisName = item.axisValue;
                                    var itemValue = item.marker + item.seriesName + ': ' + convertSize(item.data) + '</br>';
                                    result += itemValue;
                                });
                                return axisName + '</br>' + result;
                            }
                        },
                        legend: {
                            data: ['使用量'],
                            x: 'center',
                            y: '12%',
                            orient: 'horizontal'
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
                        grid: {
                            left: '5%',
                            right: '5%'
                        },
                        xAxis: {
                            type: 'category',
                            // X轴从零刻度开始
                            boundaryGap: false,
                            data: insertTime,
                            axisLabel: {
                                rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                            }
                        },
                        yAxis: {
                            type: 'value',
                            name: '使用量',
                            min: 0,  //一定要设置最小刻度
                            max: lastMenLimitNum,
                            axisLabel: {
                                formatter: function (value, index) {
                                    return convertSize(value);
                                }
                            }
                        },
                        // 数据
                        series: [{
                            name: '使用量',
                            data: menUsage,
                            type: 'line',
                            smooth: true,
                            // markLine: {
                            //     data: [
                            //         {
                            //             yAxis: lastMenLimitNum,
                            //             itemStyle: {
                            //                 color: '#E13C00'
                            //             }
                            //         }]
                            // },
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
                    getDockerMenUsageLimitInfoChart.setOption(option);
                },
                error: function () {
                }
            });
        }

        // 发送ajax请求，获取docker网络图表数据
        function getDockerNetChartInfo(time) {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-docker-stats-history/get-docker-net-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    serverIp: serverIp, // 服务器IP
                    containerName: containerName,// 容器名
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // 网络 input 数据
                    var netIn = data.map(function (item) {
                        return item.netIn;
                    });
                    // 网络 output 数据
                    var netOut = data.map(function (item) {
                        return item.netOut;
                    });
                    // 网络 input 数据 速率
                    var netInSpeed = data.map(function (item) {
                        return item.netInSpeed;
                    });
                    // 网络 output 数据 速率
                    var netOutSpeed = data.map(function (item) {
                        return item.netOutSpeed;
                    });
                    // 最大网络 input 数据
                    var maxNetIn = Math.ceil(Math.max.apply(null, data.map(function (item) {
                        return item.netIn;
                    })));
                    // 最大网络 output 数据
                    var maxNetOut = Math.ceil(Math.max.apply(null, data.map(function (item) {
                        return item.netOut;
                    })));
                    // 最大网络 input 数据 速率
                    var maxNetInSpeed = Math.ceil(Math.max.apply(null, data.map(function (item) {
                        return item.netInSpeed;
                    })));
                    // 最大网络 output 数据 速率
                    var maxNetOutSpeed = Math.ceil(Math.max.apply(null, data.map(function (item) {
                        return item.netOutSpeed;
                    })));
                    // 最新网络 input 数据
                    var lastNetIn = data.length !== 0 ? convertSize(data[data.length - 1].netIn) : '没数据';
                    // 最新网络 output 数据
                    var lastNetOut = data.length !== 0 ? convertSize(data[data.length - 1].netOut) : '没数据';
                    // 最新网络 input 数据 速率
                    var lastNetInSpeed = data.length !== 0 ? convertSize(data[data.length - 1].netInSpeed) + '/s' : '没数据';
                    // 最新网络 output 数据 速率
                    var lastNetOutSpeed = data.length !== 0 ? convertSize(data[data.length - 1].netOutSpeed) + '/s' : '没数据';
                    // 新增时间
                    var insertTime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    var option = {
                        title: {
                            text: '网络',
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: '容器名：' + containerName + '，接收(总量)：' + lastNetIn + '，发送(总量)：' + lastNetOut + '，接收(速率)：' + lastNetInSpeed + '，发送(速率)：' + lastNetOutSpeed,
                            subtextStyle: {
                                color: '#BEBEBE'
                            }
                        },
                        // 鼠标移到折线上展示数据
                        tooltip: {
                            trigger: 'axis',
                            formatter: function (params) {
                                var result = '';
                                var axisName = '';
                                var itemValue = '';
                                params.forEach(function (item) {
                                    axisName = item.axisValue;
                                    if (item.seriesName.includes('速率')) {
                                        itemValue = item.marker + item.seriesName + ': ' + convertSize(item.data) + '/s</br>';
                                    } else {
                                        itemValue = item.marker + item.seriesName + ': ' + convertSize(item.data) + '</br>';
                                    }
                                    result += itemValue;
                                });
                                return axisName + '</br>' + result;
                            }
                        },
                        legend: {
                            data: ['接收(总量)', '发送(总量)', '接收(速率)', '发送(速率)'],
                            x: 'center',
                            y: '12%',
                            orient: 'horizontal'
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
                        grid: {
                            left: '5%',
                            right: '8%'
                        },
                        xAxis: {
                            type: 'category',
                            // X轴从零刻度开始
                            boundaryGap: false,
                            data: insertTime,
                            axisLabel: {
                                rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                            }
                        },
                        yAxis: [{
                            type: 'value',
                            name: '总量',
                            min: 0,  //一定要设置最小刻度
                            max: maxNetIn > maxNetOut ? maxNetIn : maxNetOut,
                            axisLabel: {
                                formatter: function (value, index) {
                                    return convertSize(value);
                                }
                            }
                        }, {
                            type: 'value',
                            name: '速率',
                            min: 0,  //一定要设置最小刻度
                            max: maxNetInSpeed > maxNetOutSpeed ? maxNetInSpeed : maxNetOutSpeed,
                            axisLabel: {
                                formatter: function (value, index) {
                                    return convertSize(value) + '/s';
                                }
                            }
                        }],
                        // 数据
                        series: [{
                            name: '接收(总量)',
                            data: netIn,
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 0,
                            // markLine: {
                            //     data: [
                            //         {
                            //             yAxis: lastMenLimitNum,
                            //             itemStyle: {
                            //                 color: '#E13C00'
                            //             }
                            //         }]
                            // },
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
                        }, {
                            name: '发送(总量)',
                            data: netOut,
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 0,
                            // markLine: {
                            //     data: [
                            //         {
                            //             yAxis: lastMenLimitNum,
                            //             itemStyle: {
                            //                 color: '#E13C00'
                            //             }
                            //         }]
                            // },
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#00E5EE'
                                    }, {
                                        offset: 0.5,
                                        color: '#00F5FF'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#00C5CD'
                                }
                            }
                        }, {
                            name: '接收(速率)',
                            data: netInSpeed,
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 1,  // 关键：绑定到第二个 Y 轴（速率）
                            // markLine: {
                            //     data: [
                            //         {
                            //             yAxis: lastMenLimitNum,
                            //             itemStyle: {
                            //                 color: '#E13C00'
                            //             }
                            //         }]
                            // },
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#7B68EE'
                                    }, {
                                        offset: 0.5,
                                        color: '#8470FF'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#6A5ACD'
                                }
                            }
                        }, {
                            name: '发送(速率)',
                            data: netOutSpeed,
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 1,  // 关键：绑定到第二个 Y 轴（速率）
                            // markLine: {
                            //     data: [
                            //         {
                            //             yAxis: lastMenLimitNum,
                            //             itemStyle: {
                            //                 color: '#E13C00'
                            //             }
                            //         }]
                            // },
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#D15FEE'
                                    }, {
                                        offset: 0.5,
                                        color: '#E066FF'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#B452CD'
                                }
                            }
                        }]
                    };
                    getDockerNetInfoChart.setOption(option);
                },
                error: function () {
                }
            });
        }

        // 发送ajax请求，获取docker磁盘图表数据
        function getDockerBlockChartInfo(time) {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-docker-stats-history/get-docker-block-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    serverIp: serverIp, // 服务器IP
                    containerName: containerName,// 容器名
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // 磁盘 input 数据
                    var blockIn = data.map(function (item) {
                        return item.blockIn;
                    });
                    // 磁盘 output 数据
                    var blockOut = data.map(function (item) {
                        return item.blockOut;
                    });
                    // 磁盘 input 数据 速率
                    var blockInSpeed = data.map(function (item) {
                        return item.blockInSpeed;
                    });
                    // 磁盘 output 数据 速率
                    var blockOutSpeed = data.map(function (item) {
                        return item.blockOutSpeed;
                    });
                    // 最大磁盘 input 数据
                    var maxBlockIn = Math.ceil(Math.max.apply(null, data.map(function (item) {
                        return item.blockIn;
                    })));
                    // 最大磁盘 output 数据
                    var maxBlockOut = Math.ceil(Math.max.apply(null, data.map(function (item) {
                        return item.blockOut;
                    })));
                    // 最大磁盘 input 数据 速率
                    var maxBlockInSpeed = Math.ceil(Math.max.apply(null, data.map(function (item) {
                        return item.blockInSpeed;
                    })));
                    // 最大磁盘 output 数据 速率
                    var maxBlockOutSpeed = Math.ceil(Math.max.apply(null, data.map(function (item) {
                        return item.blockOutSpeed;
                    })));
                    // 最新磁盘 input 数据
                    var lastBlockIn = data.length !== 0 ? convertSize(data[data.length - 1].blockIn) : '没数据';
                    // 最新磁盘 output 数据
                    var lastBlockOut = data.length !== 0 ? convertSize(data[data.length - 1].blockOut) : '没数据';
                    // 最新磁盘 input 数据 速率
                    var lastBlockInSpeed = data.length !== 0 ? convertSize(data[data.length - 1].blockInSpeed) + '/s' : '没数据';
                    // 最新磁盘 output 数据 速率
                    var lastBlockOutSpeed = data.length !== 0 ? convertSize(data[data.length - 1].blockOutSpeed) + '/s' : '没数据';
                    // 新增时间
                    var insertTime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    var option = {
                        title: {
                            text: '磁盘',
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: '容器名：' + containerName + '，写入(总量)：' + lastBlockIn + '，读取(总量)：' + lastBlockOut + '，写入(速率)：' + lastBlockInSpeed + ',读取(速率)：' + lastBlockOutSpeed,
                            subtextStyle: {
                                color: '#BEBEBE'
                            }
                        },
                        // 鼠标移到折线上展示数据
                        tooltip: {
                            trigger: 'axis',
                            formatter: function (params) {
                                var result = '';
                                var axisName = '';
                                var itemValue = '';
                                params.forEach(function (item) {
                                    axisName = item.axisValue;
                                    if (item.seriesName.includes('速率')) {
                                        itemValue = item.marker + item.seriesName + ': ' + convertSize(item.data) + '/s</br>';
                                    } else {
                                        itemValue = item.marker + item.seriesName + ': ' + convertSize(item.data) + '</br>';
                                    }
                                    result += itemValue;
                                });
                                return axisName + '</br>' + result;
                            }
                        },
                        legend: {
                            data: ['写入(总量)', '读取(总量)', '写入(速率)', '读取(速率)'],
                            x: 'center',
                            y: '12%',
                            orient: 'horizontal'
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
                        grid: {
                            left: '5%',
                            right: '8%'
                        },
                        xAxis: {
                            type: 'category',
                            // X轴从零刻度开始
                            boundaryGap: false,
                            data: insertTime,
                            axisLabel: {
                                rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                            }
                        },
                        yAxis: [{
                            type: 'value',
                            name: '总量',
                            min: 0,  //一定要设置最小刻度
                            max: maxBlockIn > maxBlockOut ? maxBlockIn : maxBlockOut,
                            axisLabel: {
                                formatter: function (value, index) {
                                    return convertSize(value);
                                }
                            }
                        }, {
                            type: 'value',
                            name: '速率',
                            min: 0,  //一定要设置最小刻度
                            max: maxBlockInSpeed > maxBlockOutSpeed ? maxBlockInSpeed : maxBlockOutSpeed,
                            axisLabel: {
                                formatter: function (value, index) {
                                    return convertSize(value) + '/s';
                                }
                            }
                        }],
                        // 数据
                        series: [{
                            name: '写入(总量)',
                            data: blockIn,
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 0,
                            // markLine: {
                            //     data: [
                            //         {
                            //             yAxis: lastMenLimitNum,
                            //             itemStyle: {
                            //                 color: '#E13C00'
                            //             }
                            //         }]
                            // },
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
                        }, {
                            name: '读取(总量)',
                            data: blockOut,
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 0,
                            // markLine: {
                            //     data: [
                            //         {
                            //             yAxis: lastMenLimitNum,
                            //             itemStyle: {
                            //                 color: '#E13C00'
                            //             }
                            //         }]
                            // },
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#00E5EE'
                                    }, {
                                        offset: 0.5,
                                        color: '#00F5FF'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#00C5CD'
                                }
                            }
                        }, {
                            name: '写入(速率)',
                            data: blockInSpeed,
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 1,  // 关键：绑定到第二个 Y 轴（速率）
                            // markLine: {
                            //     data: [
                            //         {
                            //             yAxis: lastMenLimitNum,
                            //             itemStyle: {
                            //                 color: '#E13C00'
                            //             }
                            //         }]
                            // },
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#7B68EE'
                                    }, {
                                        offset: 0.5,
                                        color: '#8470FF'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#6A5ACD'
                                }
                            }
                        }, {
                            name: '读取(速率)',
                            data: blockOutSpeed,
                            type: 'line',
                            smooth: true,
                            yAxisIndex: 1,  // 关键：绑定到第二个 Y 轴（速率）
                            // markLine: {
                            //     data: [
                            //         {
                            //             yAxis: lastMenLimitNum,
                            //             itemStyle: {
                            //                 color: '#E13C00'
                            //             }
                            //         }]
                            // },
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#D15FEE'
                                    }, {
                                        offset: 0.5,
                                        color: '#E066FF'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#B452CD'
                                }
                            }
                        }]
                    };
                    getDockerBlockInfoChart.setOption(option);
                },
                error: function () {
                }
            });
        }

        // 发送ajax请求
        function execute() {
            if (autoRefresh) {
                // 发送ajax请求，获取docker CPU使用率图表数据
                getDockerCpuUtilizationRateChartInfo(time);
                // 发送ajax请求，获取docker内存使用率图表数据
                getDockerMenUtilizationRateChartInfo(time);
                // 发送ajax请求，获取docker当前使用的内存和最大可以使用的内存图表数据
                getDockerMenUsageLimitChartInfo(time);
                // 发送ajax请求，获取docker网络图表数据
                getDockerNetChartInfo(time);
                // 发送ajax请求，获取docker磁盘图表数据
                getDockerBlockChartInfo(time);
            }
        }

        // 页面加载后第一次执行
        execute();
        // 每30秒刷新一次
        window.setInterval(function () {
            execute();
        }, 1000 * 30);
    });
    e('dockerStatsDetail', {});
});