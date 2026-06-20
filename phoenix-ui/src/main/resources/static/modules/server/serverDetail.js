/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['admin', 'element', 'form'], function () {
        var admin = layui.admin, $ = layui.$, form = layui.form, element = layui.element;
        // 渲染进度条
        element.render('progress');
        // 基于准备好的dom，初始化echarts实例
        var getServerCpuInfoChart = echarts.init(document.getElementById('get-server-cpu-info'), 'infographic');
        var getServerMemoryInfoChart = echarts.init(document.getElementById('get-server-memory-info'), 'infographic');
        var getServerProcessChartInfoChart = echarts.init(document.getElementById('get-server-process-info'), 'infographic');
        var getServerNetworkSpeedInfoChart = echarts.init(document.getElementById('get-server-network-speed-info'), 'infographic');
        var getServerPowerSourceInfoChart = echarts.init(document.getElementById('get-server-power-source-info'), 'infographic');
        var getServerSensorsInfoChart = echarts.init(document.getElementById('get-server-sensors-info'), 'infographic');
        var getServerLoadAverageInfoChart = echarts.init(document.getElementById('get-server-load-average-info'), 'infographic');
        // 浏览器窗口大小发生改变时
        window.addEventListener("resize", function () {
            getServerCpuInfoChart.resize();
            getServerMemoryInfoChart.resize();
            getServerProcessChartInfoChart.resize();
            getServerNetworkSpeedInfoChart.resize();
            getServerPowerSourceInfoChart.resize();
            getServerSensorsInfoChart.resize();
            getServerLoadAverageInfoChart.resize();
        });
        // 堆内存图表和非堆内存图表时间
        var time = 'hour';
        var $addressOptionSelected = $('#address option:selected');
        // 服务器网卡地址
        var chartAddress = $addressOptionSelected.val() === undefined ? '' : $addressOptionSelected.val();
        // 是否自动刷新
        var autoRefresh = true;
        // 时间条件发生改变
        form.on('select(time)', function (data) {
            time = data.value;
            // 发送ajax请求，获取CPU图表数据
            getServerCpuChartInfo(time);
            // 发送ajax请求，获取内存图表数据
            getServerMemoryChartInfo(time);
            // 发送ajax请求，获取进程图表数据
            getServerProcessChartInfo(time);
            // 发送ajax请求，获取网速图表数据
            getServerNetworkSpeedChartInfo(time, chartAddress);
            // 发送ajax请求，获取平均负载图表数据
            getServerLoadAverageChartInfo(time);
        });
        // 服务器网卡地址改变
        form.on('select(address)', function (data) {
            chartAddress = data.value;
            // 发送ajax请求，获取网速图表数据
            getServerNetworkSpeedChartInfo(time, chartAddress);
        });
        // 自动刷新条件改变
        form.on('switch(autoRefresh)', function (data) {
            //是否被选中，true或者false
            autoRefresh = data.elem.checked;
        });

        // 发送ajax请求，获取传感器图表数据
        function getServerSensorsChartInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-sensors/get-server-detail-page-server-sensors-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var cpuTemperature = result.data;
                    if (isNaN(cpuTemperature)) {
                        $('#get-server-sensors-info').text(I18nUtils.t('serverDetail.unknown'));
                        // 关闭loading框
                        // layer.close(loadingIndex);
                        return;
                    }
                    var TP_value = cpuTemperature;
                    var kd = [];
                    var Gradient = [];
                    var leftColor = '';
                    var boxPosition = [65, 0];
                    var TP_txt = '';
                    // 刻度使用柱状图模拟，短设置1，长的设置3；构造一个数据
                    for (var i = 0, len = 135; i <= len; i++) {
                        if (i < 10 || i > 130) {
                            kd.push('')
                        } else {
                            if ((i - 10) % 20 === 0) {
                                kd.push('-3');
                            } else if ((i - 10) % 4 === 0) {
                                kd.push('-1');
                            } else {
                                kd.push('');
                            }
                        }
                    }
                    //中间线的渐变色和文本内容
                    if (TP_value > 80) {
                        TP_txt = I18nUtils.t('serverDetail.tempHigh');
                        Gradient.push({
                            offset: 0,
                            color: '#93FE94'
                        }, {
                            offset: 0.5,
                            color: '#E4D225'
                        }, {
                            offset: 1,
                            color: '#E01F28'
                        });
                    } else if (TP_value > 10) {
                        TP_txt = I18nUtils.t('serverDetail.tempNormal');
                        Gradient.push({
                            offset: 0,
                            color: '#93FE94'
                        }, {
                            offset: 1,
                            color: '#E4D225'
                        });
                    } else {
                        TP_txt = I18nUtils.t('serverDetail.tempLow');
                        Gradient.push({
                            offset: 1,
                            color: '#93FE94'
                        });
                    }
                    leftColor = Gradient[Gradient.length - 1].color;
                    // 因为柱状初始化为0，温度存在负值，所以加上负值60和空出距离10
                    var option = {
                        title: {
                            text: I18nUtils.t('serverDetail.chart.cpuTempTitle'),
                            show: false
                        },
                        grid: {
                            left: '40%',
                            top: '1%',
                            bottom: '11%'
                            // containLabel: true
                        },
                        yAxis: [{
                            show: false,
                            data: [],
                            min: 0,
                            max: 135,
                            axisLine: {
                                show: false
                            }
                        }, {
                            show: false,
                            min: 0,
                            max: 50
                        }, {
                            type: 'category',
                            data: ['', '', '', '', '', '', '', '', '', '', ''],
                            position: 'left',
                            offset: -80,
                            axisLabel: {
                                fontSize: 10,
                                color: '#808a87'
                            },
                            axisLine: {
                                show: false
                            },
                            axisTick: {
                                show: false
                            }
                        }],
                        xAxis: [{
                            show: false,
                            min: -20,
                            max: 100,
                            data: []
                        }, {
                            show: false,
                            min: -20,
                            max: 100,
                            data: []
                        }, {
                            show: false,
                            min: -20,
                            max: 100,
                            data: []
                        }, {
                            show: false,
                            min: -5,
                            max: 40
                        }],
                        series: [{
                            name: '条',
                            type: 'bar',
                            // 对应上xAxis的第一个对象配置
                            xAxisIndex: 0,
                            data: [{
                                value: (TP_value + 10), //这个改那个颜色刻度的
                                label: {
                                    normal: {
                                        show: true,
                                        position: boxPosition,
                                        width: 40,
                                        height: 100,
                                        formatter: '{back| ' + TP_value + ' }{unit|°C}\n{downTxt|' + TP_txt + '}',
                                        rich: {
                                            back: {
                                                align: 'center',
                                                lineHeight: 50,
                                                fontSize: 30,
                                                fontFamily: 'digifacewide',
                                                color: leftColor
                                            },
                                            unit: {
                                                fontFamily: '微软雅黑',
                                                fontSize: 15,
                                                lineHeight: 50,
                                                color: leftColor
                                            },
                                            downTxt: {
                                                lineHeight: 50,
                                                fontSize: 18,
                                                align: 'center',
                                                color: '#808a87'
                                            }
                                        }
                                    }
                                }
                            }],
                            barWidth: 18,
                            itemStyle: {
                                normal: {
                                    color: new echarts.graphic.LinearGradient(0, 1, 0, 0, Gradient)
                                }
                            },
                            z: 2
                        }, {
                            name: '白框',
                            type: 'bar',
                            xAxisIndex: 1,
                            barGap: '-100%',
                            data: [134],
                            barWidth: 22,
                            itemStyle: {
                                normal: {
                                    color: '#fff',
                                    barBorderRadius: 50,
                                }
                            },
                            z: 1
                        }, {
                            name: '外框',
                            type: 'bar',
                            xAxisIndex: 2,
                            barGap: '-100%',
                            data: [135],
                            barWidth: 28,
                            itemStyle: {
                                normal: {
                                    color: '#D3D3D3',
                                    barBorderRadius: 50,
                                }
                            },
                            z: 0
                        }, {
                            name: '圆',
                            type: 'scatter',
                            hoverAnimation: false,
                            data: [0],
                            xAxisIndex: 0,
                            symbolSize: 48,
                            itemStyle: {
                                normal: {
                                    color: '#93FE94',
                                    opacity: 1
                                }
                            },
                            z: 2
                        }, {
                            name: '白圆',
                            type: 'scatter',
                            hoverAnimation: false,
                            data: [0],
                            xAxisIndex: 1,
                            symbolSize: 53,
                            itemStyle: {
                                normal: {
                                    color: '#fff',
                                    opacity: 1
                                }
                            },
                            z: 1
                        }, {
                            name: '外圆',
                            type: 'scatter',
                            hoverAnimation: false,
                            data: [0],
                            xAxisIndex: 2,
                            symbolSize: 60,
                            itemStyle: {
                                normal: {
                                    color: '#D3D3D3',
                                    opacity: 1
                                }
                            },
                            z: 0
                        }, {
                            name: '刻度',
                            type: 'bar',
                            yAxisIndex: 0,
                            xAxisIndex: 3,
                            label: {
                                normal: {
                                    show: true,
                                    position: 'left',
                                    distance: 18,
                                    color: '#808a87',
                                    fontSize: 14,
                                    formatter: function (params) {
                                        if (params.dataIndex > 130 || params.dataIndex < 10) {
                                            return '';
                                        } else {
                                            if ((params.dataIndex - 10) % 20 === 0) {
                                                return params.dataIndex - 10; //这个改刻度的，当减70的时候刻度是从-60开始不是从零开始
                                            } else {
                                                return '';
                                            }
                                        }
                                    }
                                }
                            },
                            barGap: '-100%',
                            data: kd,
                            barWidth: 1,
                            itemStyle: {
                                normal: {
                                    color: '#808a87',
                                    barBorderRadius: 120
                                }
                            },
                            z: 0
                        }]
                    };
                    getServerSensorsInfoChart.setOption(option);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取电池图表数据
        function getServerPowerSourcesChartInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-power-sources/get-server-detail-page-server-power-sources-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var rCapacityPercentAvg = result.data;
                    if (isNaN(rCapacityPercentAvg)) {
                        $('#get-server-power-source-info').text(I18nUtils.t('serverDetail.unknown'));
                        // 关闭loading框
                        // layer.close(loadingIndex);
                        return;
                    }
                    var option = {
                        tooltip: {				// 本系列特定的 tooltip 设定。
                            show: true,
                            formatter: '{b}：{c}%',
                            backgroundColor: 'rgba(50,50,50,0.7)',	// 提示框浮层的背景颜色。注意：series.tooltip 仅在 tooltip.trigger 为 'item' 时有效。
                            borderColor: '#333',		// 提示框浮层的边框颜色。...
                            borderWidth: 0,				// 提示框浮层的边框宽。...
                            padding: 5,					// 提示框浮层内边距，单位px，默认各方向内边距为5，接受数组分别设定上右下左边距。...
                            textStyle: {				// 提示框浮层的文本样式。...
                                // color ,fontStyle ,fontWeight ,fontFamily ,fontSize ,lineHeight ,.......
                            }
                        },
                        series: [
                            {
                                name: I18nUtils.t('serverDetail.chart.batteryGaugeName'),		// 系列名称,用于tooltip的显示，legend 的图例筛选，在 setOption 更新数据和配置项时用于指定对应的系列。
                                type: 'gauge',			// 系列类型
                                radius: '95%',			// 参数:number, string。 仪表盘半径,默认 75% ，可以是相对于容器高宽中较小的一项的一半的百分比，也可以是绝对的数值。
                                center: ['50%', '55%'],	// 仪表盘位置(圆心坐标)
                                startAngle: 225,		// 仪表盘起始角度,默认 225。圆心 正右手侧为0度，正上方为90度，正左手侧为180度。
                                endAngle: -45,			// 仪表盘结束角度,默认 -45
                                clockwise: true,		// 仪表盘刻度是否是顺时针增长,默认 true。
                                min: 0,					// 最小的数据值,默认 0 。映射到 minAngle。
                                max: 100,				// 最大的数据值,默认 100 。映射到 maxAngle。
                                splitNumber: 10,		// 仪表盘刻度的分割段数,默认 10。
                                axisLine: {				// 仪表盘轴线(轮廓线)相关配置。
                                    show: true,				// 是否显示仪表盘轴线(轮廓线),默认 true。
                                    lineStyle: {			// 仪表盘轴线样式。
                                        //color: [[0.2, 'rgba(255,0,0,0.8)'], [0.8, 'rgba(0,255,255,0.8)'], [1, 'rgba(0,255,0,0.8)']], 	//仪表盘的轴线可以被分成不同颜色的多段。每段的  结束位置(范围是[0,1]) 和  颜色  可以通过一个数组来表示。默认取值：[[0.2, '#91c7ae'], [0.8, '#63869e'], [1, '#c23531']]
                                        color: [[0.2, '#F56C6C'], [0.4, '#E6A23C'], [0.6, '#5CB87A'], [0.8, '#1989FA'], [1, '#6F7AD3']],
                                        opacity: 1,					//图形透明度。支持从 0 到 1 的数字，为 0 时不绘制该图形。
                                        width: 15,					//轴线宽度,默认 30。
                                        shadowBlur: 20,				//(发光效果)图形阴影的模糊大小。该属性配合 shadowColor,shadowOffsetX, shadowOffsetY 一起设置图形的阴影效果。
                                        shadowColor: '#eee'		//阴影颜色。支持的格式同color。
                                    }
                                },
                                splitLine: {			// 分隔线样式。
                                    show: true,				// 是否显示分隔线,默认 true。
                                    length: 30,				// 分隔线线长。支持相对半径的百分比,默认 30。
                                    lineStyle: {			// 分隔线样式。
                                        color: '#eee',				//线的颜色,默认 #eee。
                                        opacity: 1,					//图形透明度。支持从 0 到 1 的数字，为 0 时不绘制该图形。
                                        width: 2,					//线度,默认 2。
                                        type: 'solid',				//线的类型,默认 solid。 此外还有 dashed,dotted
                                        shadowBlur: 10,				//(发光效果)图形阴影的模糊大小。该属性配合 shadowColor,shadowOffsetX, shadowOffsetY 一起设置图形的阴影效果。
                                        shadowColor: '#eee'		//阴影颜色。支持的格式同color。
                                    }
                                },
                                axisTick: {				// 刻度(线)样式。
                                    show: true,				// 是否显示刻度(线),默认 true。
                                    splitNumber: 5,			// 分隔线之间分割的刻度数,默认 5。
                                    length: 8,				// 刻度线长。支持相对半径的百分比,默认 8。
                                    lineStyle: {			// 刻度线样式。
                                        color: '#eee',				//线的颜色,默认 #eee。
                                        opacity: 1,					//图形透明度。支持从 0 到 1 的数字，为 0 时不绘制该图形。
                                        width: 1,					//线度,默认 1。
                                        type: 'solid',				//线的类型,默认 solid。 此外还有 dashed,dotted
                                        shadowBlur: 10,				//(发光效果)图形阴影的模糊大小。该属性配合 shadowColor,shadowOffsetX, shadowOffsetY 一起设置图形的阴影效果。
                                        shadowColor: '#eee'		//阴影颜色。支持的格式同color。
                                    }
                                },
                                axisLabel: {			// 刻度标签。
                                    show: true,				// 是否显示标签,默认 true。
                                    distance: 5,			// 标签与刻度线的距离,默认 5。
                                    color: '#808a87',			// 文字的颜色,默认 #fff。
                                    fontSize: 12,			// 文字的字体大小,默认 5。
                                    formatter: '{value}'	// 刻度标签的内容格式器，支持字符串模板和回调函数两种形式。 示例:// 使用字符串模板，模板变量为刻度默认标签 {value},如:formatter: '{value} kg'; // 使用函数模板，函数参数分别为刻度数值,如formatter: function (value) {return value + 'km/h';}
                                },
                                pointer: {				// 仪表盘指针。
                                    show: true,				// 是否显示指针,默认 true。
                                    length: '70%',			// 指针长度，可以是绝对数值，也可以是相对于半径的百分比,默认 80%。
                                    width: 5				// 指针宽度,默认 8。
                                },
                                itemStyle: {			// 仪表盘指针样式。
                                    color: 'auto',			// 指针颜色，默认(auto)取数值所在的区间的颜色
                                    opacity: 1,				// 图形透明度。支持从 0 到 1 的数字，为 0 时不绘制该图形。
                                    borderWidth: 0,			// 描边线宽,默认 0。为 0 时无描边。
                                    borderType: 'solid',	// 柱条的描边类型，默认为实线，支持 'solid', 'dashed', 'dotted'。
                                    borderColor: '#000',	// 图形的描边颜色,默认 '#000'。支持的颜色格式同 color，不支持回调函数。
                                    shadowBlur: 10,			// (发光效果)图形阴影的模糊大小。该属性配合 shadowColor,shadowOffsetX, shadowOffsetY 一起设置图形的阴影效果。
                                    shadowColor: '#fff'	// 阴影颜色。支持的格式同color。
                                },
                                emphasis: {				// 高亮的 仪表盘指针样式
                                    itemStyle: {
                                        //高亮 和正常  两者具有同样的配置项,只是在不同状态下配置项的值不同。
                                    }
                                },
                                title: {				// 仪表盘标题。
                                    show: true,				// 是否显示标题,默认 true。
                                    offsetCenter: [0, '20%'],//相对于仪表盘中心的偏移位置，数组第一项是水平方向的偏移，第二项是垂直方向的偏移。可以是绝对的数值，也可以是相对于仪表盘半径的百分比。
                                    color: '#808a87',			// 文字的颜色,默认 #333。
                                    fontSize: 18			// 文字的字体大小,默认 15。
                                },
                                detail: {				// 仪表盘详情，用于显示数据。
                                    show: true,				// 是否显示详情,默认 true。
                                    offsetCenter: [0, '50%'],// 相对于仪表盘中心的偏移位置，数组第一项是水平方向的偏移，第二项是垂直方向的偏移。可以是绝对的数值，也可以是相对于仪表盘半径的百分比。
                                    color: 'auto',			// 文字的颜色,默认 auto。
                                    fontSize: 18,			// 文字的字体大小,默认 15。
                                    formatter: '{value}%'	// 格式化函数或者字符串
                                },
                                data: [{
                                    name: I18nUtils.t('serverDetail.chart.batteryGaugeDetail'),
                                    value: rCapacityPercentAvg
                                }]
                            }
                        ]
                    };
                    getServerPowerSourceInfoChart.setOption(option);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取传感器数据
        function getServerSensorsInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-sensors/get-server-detail-page-server-sensors-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var data = result.data;
                    var cpuTemperature = data.cpuTemperature;
                    var cpuVoltage = data.cpuVoltage;
                    var fanSpeed = data.fanSpeed != null ? data.fanSpeed : I18nUtils.t('serverDetail.unknown');
                    var html = '<div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.sensor.cpuTemperature') + '：</label>' + cpuTemperature +
                        '       </div>' +
                        '       <div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.sensor.cpuVoltage') + '：</label>' + cpuVoltage +
                        '       </div>' +
                        '       <div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.sensor.fanSpeed') + '：</label>' + fanSpeed +
                        '       </div>';
                    $('#sensors').empty().append(html);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取电池数据
        function getServerPowerSourcesInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-power-sources/get-server-detail-page-server-power-sources-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var data = result.data;
                    // 没有电池信息
                    if (data.length === 0) {
                        $('#powerSourcesDiv').hide();
                    } else {
                        $('#powerSourcesDiv').show();
                    }
                    var html = '';
                    for (var i = 0; i < data.length; i++) {
                        var obj = data[i];
                        var name = obj.name;
                        var deviceName = obj.deviceName;
                        var amperage = obj.amperage.startsWith('-') ? I18nUtils.t('serverDetail.discharging') + ' ' + obj.amperage.replace('-', '') : I18nUtils.t('serverDetail.charging') + ' ' + obj.amperage;
                        var chemistry = obj.chemistry;
                        var currentCapacity = obj.currentCapacity;
                        var designCapacity = obj.designCapacity;
                        var manufactureDate = obj.manufactureDate;
                        var manufacturer = obj.manufacturer;
                        var maxCapacity = obj.maxCapacity;
                        var powerUsageRate = obj.powerUsageRate.startsWith('-') ? I18nUtils.t('serverDetail.discharging') + ' ' + obj.powerUsageRate.replace('-', '') : I18nUtils.t('serverDetail.charging') + ' ' + obj.powerUsageRate;
                        var remainingCapacityPercent = (obj.remainingCapacityPercent * 100).toFixed(2) + '%';
                        var serialNumber = obj.serialNumber;
                        var temperature = obj.temperature;
                        var timeRemainingEstimated = obj.timeRemainingEstimated;
                        var timeRemainingInstant = obj.timeRemainingInstant;
                        var isPowerOnLine = obj.isPowerOnLine === '1' ? I18nUtils.t('serverDetail.charging') : I18nUtils.t('serverDetail.discharging');
                        var voltage = obj.voltage;
                        html += '<div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.name') + '：</label>' + name + '(' + deviceName + ')' +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.serialNumber') + '：</label>' + serialNumber +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.type') + '：</label>' + chemistry +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.manufacturer') + '：</label>' + manufacturer +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.manufactureDate') + '：</label>' + manufactureDate +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.designCapacity') + '：</label>' + designCapacity +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.maxCapacity') + '：</label>' + maxCapacity +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.currentCapacity') + '：</label>' + currentCapacity +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.remainingPercent') + '：</label>' + remainingCapacityPercent +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.timeRemainingEstimated') + '：</label>' + timeRemainingEstimated +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + isPowerOnLine + I18nUtils.t('serverDetail.battery.timeRemainingInstant') + '：</label>' + timeRemainingInstant +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.voltage') + '：</label>' + voltage +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.amperage') + '：</label>' + amperage +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.powerUsageRate') + '：</label>' + powerUsageRate +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.battery.temperature') + '：</label>' + temperature +
                            '    </div>';
                        if (i !== data.length - 1) {
                            html += '<hr class="layui-bg-gray hr-padding">';
                        }
                    }
                    $('#powerSources').empty().append(html);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取进程数据
        function getServerProcessInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-process/get-server-detail-page-server-process-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var data = result.data;
                    var html = '';
                    for (var i = 0; i < data.length; i++) {
                        var obj = data[i];
                        // 进程ID
                        var processId = obj.processId;
                        // 进程名
                        var name = obj.name;
                        // 执行进程的完整路径
                        // var path = (!isEmpty(obj.path) && obj.path.length > 200) ? obj.path.substr(0, 200) + ' ......' : obj.path;
                        var path = obj.path == null ? '' : obj.path;
                        // 进程命令行
                        // var commandLine = (!isEmpty(obj.commandLine) && obj.commandLine.length > 200) ? obj.commandLine.substr(0, 200) + ' ......' : obj.commandLine;
                        var commandLine = obj.commandLine == null ? '' : obj.commandLine;
                        // 进程当前的工作目录
                        // var currentWorkingDirectory = (!isEmpty(obj.currentWorkingDirectory) && obj.currentWorkingDirectory.length > 200) ? obj.currentWorkingDirectory.substr(0, 200) + ' ......' : obj.currentWorkingDirectory;
                        var currentWorkingDirectory = obj.currentWorkingDirectory == null ? '' : obj.currentWorkingDirectory;
                        // 用户名
                        var user = obj.user;
                        //进程执行状态
                        var state = obj.state;
                        // 进程已启动的毫秒数
                        var upTime = obj.upTime;
                        // 进程的开始时间
                        var startTime = obj.startTime;
                        // 进程的累积CPU使用率
                        var cpuLoadCumulative = obj.cpuLoadCumulative;
                        // 进程的位数
                        var bitness = obj.bitness;
                        // 占用内存大小
                        var memorySize = obj.memorySizeStr;
                        // 占用端口
                        var ports = obj.ports;
                        html += '<div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.pid') + '：</label>' + processId +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.name') + '：</label>' + name +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.cpuUsage') + '：</label>' + cpuLoadCumulative + '%' +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.memoryRss') + '：</label>' + memorySize +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.state') + '：</label>' + state +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.bitness') + '：</label>' + bitness +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.startTime') + '：</label>' + startTime +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.upTime') + '：</label>' + upTime +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.user') + '：</label>' + user +
                            '    </div>';
                        if (!isEmpty(ports)) {
                            html += '<div class="layui-col-md12">' +
                                '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.ports') + '：</label>' + ports +
                                '    </div>';
                        }
                        if (!isEmpty(commandLine)) {
                            html += '<div class="layui-col-md12">' +
                                '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.commandLine') + '：</label>' + commandLine +
                                '    </div>';
                        }
                        if (!isEmpty(path)) {
                            html += '<div class="layui-col-md12">' +
                                '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.path') + '：</label>' + path +
                                '    </div>';
                        }
                        if (!isEmpty(currentWorkingDirectory)) {
                            html += '<div class="layui-col-md12">' +
                                '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.process.workingDir') + '：</label>' + currentWorkingDirectory +
                                '    </div>';
                        }
                        if (i !== data.length - 1) {
                            html += '<hr class="layui-bg-gray hr-padding">';
                        }
                    }
                    $('#process').empty().append(html);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取CPU数据
        function getServerCpuInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-cpu/get-server-detail-page-server-cpu-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var data = result.data;
                    var html = '';
                    // 设置CPU核数
                    var cpuCores = data.length;
                    $('#cpuHead').empty().append('(' + cpuCores + I18nUtils.t('serverDetail.core') + ')');
                    for (var i = 0; i < data.length; i++) {
                        var obj = data[i];
                        var cpuVendor = obj.cpuVendor;
                        var cpuMhz = obj.cpuMhz;
                        var cpuModel = obj.cpuModel;
                        var cpuNice = obj.cpuNice;
                        var cpuCombined = obj.cpuCombined;
                        var cpuIdle = obj.cpuIdle;
                        var cpuSys = obj.cpuSys;
                        var cpuUser = obj.cpuUser;
                        var cpuWait = obj.cpuWait;
                        html += '<div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.cpu.frequency') + '：</label>' + cpuMhz + 'MHz' +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.cpu.vendor') + '：</label>' + cpuVendor +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.cpu.model') + '：</label>' + cpuModel +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.cpu.totalUsage') + '：</label>' + (cpuCombined * 100).toFixed(2) + '%' +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.cpu.idle') + '：</label>' + (cpuIdle * 100).toFixed(2) + '%' +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.cpu.userUsage') + '：</label>' + (cpuUser * 100).toFixed(2) + '%' +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.cpu.systemUsage') + '：</label>' + (cpuSys * 100).toFixed(2) + '%' +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.cpu.wait') + '：</label>' + (cpuWait * 100).toFixed(2) + '%' +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.cpu.nice') + '：</label>' + (cpuNice * 100).toFixed(2) + '%' +
                            '    </div>';
                        if (i !== data.length - 1) {
                            html += '<hr class="layui-bg-gray hr-padding">';
                        }
                    }
                    $('#cpu').empty().append(html);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取GPU数据
        function getServerGpuInfo() {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-gpu/get-server-detail-page-server-gpu-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var data = result.data;
                    // 没有gpu信息
                    if (data.length === 0) {
                        $('#gpuDiv').hide();
                    } else {
                        $('#gpuDiv').show();
                    }
                    var html = '';
                    for (var i = 0; i < data.length; i++) {
                        var obj = data[i];
                        var gpuDeviceId = obj.gpuDeviceId;
                        var gpuName = obj.gpuName;
                        var gpuVendor = obj.gpuVendor;
                        var gpuVersionInfo = obj.gpuVersionInfo;
                        var gpuVramTotal = obj.gpuVramTotal;
                        html += '<div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.gpu.deviceId') + '：</label>' + gpuDeviceId +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.gpu.name') + '：</label>' + gpuName +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.gpu.vendor') + '：</label>' + gpuVendor +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.gpu.versionInfo') + '：</label>' + gpuVersionInfo +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.gpu.vramTotal') + '：</label>' + gpuVramTotal +
                            '    </div>';
                        if (i !== data.length - 1) {
                            html += '<hr class="layui-bg-gray hr-padding">';
                        }
                    }
                    $('#gpu').empty().append(html);
                },
                error: function () {
                }
            });
        }

        // 发送ajax请求，获取网卡数据
        function getServerNetcardInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-netcard/get-server-detail-page-server-netcard-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var data = result.data;
                    var html = '';
                    for (var i = 0; i < data.length; i++) {
                        var obj = data[i];
                        var address = obj.address;
                        var broadcast = obj.broadcast;
                        var description = obj.description;
                        var hwAddr = obj.hwAddr;
                        var mask = obj.mask;
                        var name = obj.name;
                        var rx = obj.rx;
                        var rxDropped = obj.rxDropped;
                        var rxErrors = obj.rxErrors;
                        var rxPackets = obj.rxPackets;
                        var tx = obj.tx;
                        var txDropped = obj.txDropped;
                        var txErrors = obj.txErrors;
                        var txPackets = obj.txPackets;
                        var downloadSpeed = obj.downloadSpeed;
                        var uploadSpeed = obj.uploadSpeed;
                        var type = obj.type;
                        html += '<div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.name') + '：</label>' + name +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.type') + '：</label>' + type +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.address') + '：</label>' + address +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.mask') + '：</label>' + mask +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.broadcast') + '：</label>' + broadcast +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.mac') + '：</label>' + hwAddr +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.description') + '：</label>' + description +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.rxTotal') + '：</label>' + rx +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.rxPackets') + '：</label>' + rxPackets + ' ' + I18nUtils.t('serverDetail.unit') +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.rxErrors') + '：</label>' + rxErrors + ' ' + I18nUtils.t('serverDetail.unit') +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.rxDropped') + '：</label>' + rxDropped + ' ' + I18nUtils.t('serverDetail.unit') +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.txTotal') + '：</label>' + tx +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.txPackets') + '：</label>' + txPackets + ' ' + I18nUtils.t('serverDetail.unit') +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.txErrors') + '：</label>' + txErrors + ' ' + I18nUtils.t('serverDetail.unit') +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.txDropped') + '：</label>' + txDropped + ' ' + I18nUtils.t('serverDetail.unit') +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.downloadSpeed') + '：</label>' + downloadSpeed +
                            '    </div>' +
                            '    <div class="layui-col-md4">' +
                            '       <label class="label-font-weight">' + I18nUtils.t('serverDetail.netcard.uploadSpeed') + '：</label>' + uploadSpeed +
                            '    </div>';
                        if (i !== data.length - 1) {
                            html += '<hr class="layui-bg-gray hr-padding">';
                        }
                    }
                    $('#netcard').empty().append(html);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取操作系统数据
        function getServerOsInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-os/get-server-os-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var data = result.data;
                    var ip = data.ip;
                    var osName = data.osName;
                    var osArch = data.osArch;
                    var osTimeZone = data.osTimeZone;
                    var osVersion = data.osVersion;
                    var serverName = data.serverName;
                    var userHome = data.userHome;
                    var userName = data.userName;
                    var html = '<div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.os.ipAddress') + '：</label>' + ip +
                        '       </div>' +
                        '       <div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.os.serverName') + '：</label>' + serverName +
                        '       </div>' +
                        '       <div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.os.timeZone') + '：</label>' + osTimeZone +
                        '       </div>' +
                        '       <div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.os.osName') + '：</label>' + osName +
                        '       </div>' +
                        '       <div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.os.osArch') + '：</label>' + osArch +
                        '       </div>' +
                        '       <div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.os.osVersion') + '：</label>' + osVersion +
                        '       </div>' +
                        '       <div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.os.userName') + '：</label>' + userName +
                        '       </div>' +
                        '       <div class="layui-col-md4">' +
                        '           <label class="label-font-weight">' + I18nUtils.t('serverDetail.os.userHome') + '：</label>' + userHome +
                        '       </div>';
                    $('#os').empty().append(html);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取磁盘图表数据
        function getServerDiskChartInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-disk/get-server-detail-page-server-disk-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 服务器IP
                },
                success: function (result) {
                    var data = result.data;
                    var html = '';
                    for (var i = 0; i < data.length; i++) {
                        var obj = data[i];
                        // 分区的盘符名称
                        var devName = obj.devName;
                        // 分区的盘符路径
                        var dirName = obj.dirName;
                        // 磁盘类型
                        var sysTypeName = obj.sysTypeName;
                        // 磁盘总大小
                        var totalStr = obj.totalStr;
                        // 磁盘可用大小
                        var availStr = obj.availStr;
                        // 磁盘资源的利用率
                        var usePercent = obj.usePercent;
                        html += '<div class="layui-col-md4" style="padding-right: 30px;">' +
                            '         <div class="layui-progress layui-progress-big" style="margin: 100px 0 15px;" lay-showPercent="yes">' +
                            '              <h3 style="top: -85px;">' + I18nUtils.t('serverDetail.disk.devName') + '：' + devName + '</h3>' +
                            '              <h3 style="top: -65px;">' + I18nUtils.t('serverDetail.disk.dirName') + '：' + dirName + '</h3>' +
                            '              <h3 style="top: -45px;">' + I18nUtils.t('serverDetail.disk.type') + '：' + sysTypeName + '</h3>' +
                            '              <h3 style="top: -25px;">' + I18nUtils.t('serverDetail.disk.capacity') + '：' + I18nUtils.t('serverDetail.disk.available') + availStr + '/' + I18nUtils.t('serverDetail.disk.total') + totalStr + '</h3>';
                        if (usePercent >= 90) {
                            html += '<div class="layui-progress-bar layui-bg-red" lay-percent="' + usePercent + '%"></div>';
                        } else if (usePercent >= 80 && usePercent < 90) {
                            html += '<div class="layui-progress-bar layui-bg-orange" lay-percent="' + usePercent + '%"></div>';
                        } else {
                            html += '<div class="layui-progress-bar layui-bg-green" lay-percent="' + usePercent + '%"></div>';
                        }
                        html += '</div></div>';
                    }
                    $('#get-server-disk-info').empty().append(html);
                    // 重新渲染进度条
                    element.render('progress');
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取内存图表数据
        function getServerMemoryChartInfo(time) {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-memory-history/get-server-detail-page-server-memory-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip, // 服务器IP
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // 物理内存总量（单位：GB）
                    var memTotal = data.length !== 0 ? data[data.length - 1].memTotal + ' GB' : I18nUtils.t('serverDetail.noData');
                    // 物理内存使用率
                    var menUsedPercent = data.length !== 0 ? data[data.length - 1].menUsedPercent + '%' : I18nUtils.t('serverDetail.noData');
                    // 交换区总量（单位：GB）
                    var swapTotal = data.length !== 0 ? data[data.length - 1].swapTotal + ' GB' : I18nUtils.t('serverDetail.noData');
                    // 交换区使用率
                    var swapUsedPercent = data.length !== 0 ? data[data.length - 1].swapUsedPercent + '%' : I18nUtils.t('serverDetail.noData');
                    var memUsed = data.map(function (item) {
                        return item.memUsed;
                    });
                    // 交换区使用量
                    var swapUsed = data.map(function (item) {
                        return item.swapUsed;
                    });
                    // 新增时间
                    var insertTime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    var option = {
                        title: {
                            text: I18nUtils.t('serverDetail.chart.memSwapTitle'),
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: I18nUtils.t('serverDetail.chart.memSwapSubtitle', {memTotal: memTotal, memUsedPercent: menUsedPercent, swapTotal: swapTotal, swapUsedPercent: swapUsedPercent}),
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
                                    var itemValue = item.marker + item.seriesName + ': ' + item.data + ' GB</br>';
                                    result += itemValue;
                                });
                                return axisName + '</br>' + result;
                            }
                        },
                        legend: {
                            data: [I18nUtils.t('serverDetail.series.memUsed'), I18nUtils.t('serverDetail.series.swapUsed')],
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
                            right: '7%'
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
                            name: I18nUtils.t('serverDetail.series.memUsed'),
                            min: 0,  //一定要设置最小刻度
                            max: Math.ceil(Math.max.apply(null, data.map(function (item) {
                                return item.memTotal;
                            }))),  //一定要设置最大刻度
                            axisLabel: {
                                formatter: '{value} GB'
                            }
                        }, {
                            type: 'value',
                            name: I18nUtils.t('serverDetail.series.swapUsed'),
                            min: 0,  //一定要设置最小刻度
                            max: Math.ceil(Math.max.apply(null, data.map(function (item) {
                                return item.swapTotal;
                            }))),
                            axisLabel: {
                                formatter: '{value} GB'
                            }
                        }],
                        // 数据
                        series: [{
                            name: I18nUtils.t('serverDetail.series.memUsed'),
                            yAxisIndex: 0,
                            data: memUsed,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.avg'),
                                    type: 'average',
                                    itemStyle: {
                                        color: '#9400D3'
                                    },
                                    label: {
                                        position: 'middle',
                                        formatter: function (params) {
                                            return params.value + ' GB' + params.name;
                                        }
                                    }
                                }]
                            },
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#9F79EE'
                                    }, {
                                        offset: 0.5,
                                        color: '#AB82FF'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#8968CD'
                                }
                            }
                        }, {
                            name: I18nUtils.t('serverDetail.series.swapUsed'),
                            yAxisIndex: 1,
                            data: swapUsed,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.avg'),
                                    type: 'average',
                                    itemStyle: {
                                        color: '#228B22'
                                    },
                                    label: {
                                        position: 'middle',
                                        formatter: function (params) {
                                            return params.value + ' GB' + params.name;
                                        }
                                    }
                                }]
                            },
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
                    getServerMemoryInfoChart.setOption(option);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取进程图表数据
        function getServerProcessChartInfo(time) {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-process-history/get-server-detail-page-server-process-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip, // 服务器IP
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // 运行进程数
                    var processNum = data.map(function (item) {
                        return item.processNum;
                    });
                    // 最大进程数
                    var maxProcessNum = data.length !== 0 ? Math.max(...processNum) : I18nUtils.t('serverDetail.noData');
                    // 最小进程数
                    var minProcessNum = data.length !== 0 ? Math.min(...processNum) : I18nUtils.t('serverDetail.noData');
                    // 新增时间
                    var insertTime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    var option = {
                        title: {
                            text: I18nUtils.t('serverDetail.chart.processTitle'),
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: I18nUtils.t('serverDetail.chart.processSubtitle', {max: maxProcessNum, min: minProcessNum}),
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
                                    var itemValue = item.marker + item.seriesName + ': ' + item.data + ' ' + I18nUtils.t('serverDetail.unit') + '</br>';
                                    result += itemValue;
                                });
                                return axisName + '</br>' + result;
                            }
                        },
                        legend: {
                            data: [I18nUtils.t('serverDetail.series.runningProcess')],
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
                            right: '6%'
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
                            name: I18nUtils.t('serverDetail.chart.yAxisCount'),
                            axisLabel: {
                                formatter: '{value}'
                            }
                        }],
                        // 数据
                        series: [{
                            name: I18nUtils.t('serverDetail.series.runningProcess'),
                            yAxisIndex: 0,
                            data: processNum,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.avg'),
                                    type: 'average',
                                    itemStyle: {
                                        color: '#228B22'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return Math.round(params.value) + '\n' + params.name;
                                        }
                                    }
                                }]
                            },
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
                    getServerProcessChartInfoChart.setOption(option);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取CPU图表数据
        function getServerCpuChartInfo(time) {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-cpu-history/get-server-detail-page-server-cpu-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip, // 服务器IP
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // CPU用户使用率
                    var cpuUser = data.map(function (item) {
                        return item.cpuUser;
                    });
                    // CPU系统使用率
                    var cpuSys = data.map(function (item) {
                        return item.cpuSys;
                    });
                    // CPU等待率
                    var cpuWait = data.map(function (item) {
                        return item.cpuWait;
                    });
                    // CPU错误率
                    var cpuNice = data.map(function (item) {
                        return item.cpuNice;
                    });
                    // CPU总利用率
                    var cpuCombined = data.map(function (item) {
                        return item.cpuCombined;
                    });
                    // CPU剩余率
                    var cpuIdle = data.map(function (item) {
                        return item.cpuIdle;
                    });
                    // 最新CPU总利用率
                    var lastCpuCombined = data.length !== 0 ? data[data.length - 1].cpuCombined.toFixed(2) + '%' : I18nUtils.t('serverDetail.noData');
                    // 最新CPU剩余率
                    // var lastCpuIdle = data.length !== 0 ? (100 - data[data.length - 1].cpuCombined).toFixed(2) + '%' : I18nUtils.t('serverDetail.noData');
                    // 最大CPU总使用率
                    var maxCpuCombined = data.length !== 0 ? Math.max(...cpuCombined).toFixed(2) + '%' : I18nUtils.t('serverDetail.noData');
                    // 最小CPU总使用率
                    var minCpuCombined = data.length !== 0 ? Math.min(...cpuCombined).toFixed(2) + '%' : I18nUtils.t('serverDetail.noData');
                    // 新增时间
                    var insertTime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    var option = {
                        title: {
                            text: 'CPU',
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            // subtext: '剩余率：' + lastCpuIdle + '，总使用率：' + lastCpuCombined,
                            subtext: I18nUtils.t('serverDetail.chart.cpuSubtitle', {current: lastCpuCombined, max: maxCpuCombined, min: minCpuCombined}),
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
                            data: [I18nUtils.t('serverDetail.series.idle'), I18nUtils.t('serverDetail.series.totalUsage'), I18nUtils.t('serverDetail.series.userUsage'), I18nUtils.t('serverDetail.series.systemUsage'), I18nUtils.t('serverDetail.series.wait'), I18nUtils.t('serverDetail.series.nice')],
                            // selected: {'用户使用率': false, '系统使用率': false, '等待率': false, '错误率': false},
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
                            name: I18nUtils.t('serverDetail.chart.yAxisUsage'),
                            min: 0,  //一定要设置最小刻度
                            max: 100,  //一定要设置最大刻度
                            minInterval: 20, //这个可自己设置刻度间隔
                            axisLabel: {
                                formatter: '{value}%'
                            }
                        },
                        // 数据
                        series: [{
                            name: I18nUtils.t('serverDetail.series.idle'),
                            data: cpuIdle,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.avgIdle'),
                                    type: 'average',
                                    itemStyle: {
                                        color: '#228B22'
                                    },
                                    label: {
                                        position: 'middle',
                                        formatter: function (params) {
                                            return params.value + '%' + params.name;
                                        }
                                    }
                                }]
                            },
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
                            name: I18nUtils.t('serverDetail.series.totalUsage'),
                            data: cpuCombined,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.avgTotal'),
                                    type: 'average',
                                    itemStyle: {
                                        color: '#B22222'
                                    },
                                    label: {
                                        position: 'middle',
                                        formatter: function (params) {
                                            return params.value + '%' + params.name;
                                        }
                                    }
                                }]
                            },
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#EE9572'
                                    }, {
                                        offset: 0.5,
                                        color: '#FFA07A'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#CD8162'
                                }
                            }
                        }, {
                            name: I18nUtils.t('serverDetail.series.userUsage'),
                            data: cpuUser,
                            type: 'line',
                            smooth: true,
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#EEEE00'
                                    }, {
                                        offset: 0.5,
                                        color: '#FFFF00'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#CDCD00'
                                }
                            }
                        }, {
                            name: I18nUtils.t('serverDetail.series.systemUsage'),
                            data: cpuSys,
                            type: 'line',
                            smooth: true,
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
                        }, {
                            name: I18nUtils.t('serverDetail.series.wait'),
                            data: cpuWait,
                            type: 'line',
                            smooth: true,
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#4F4F4F'
                                    }, {
                                        offset: 0.5,
                                        color: '#696969'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#363636'
                                }
                            }
                        }, {
                            name: I18nUtils.t('serverDetail.series.nice'),
                            data: cpuNice,
                            type: 'line',
                            smooth: true,
                            areaStyle: {
                                type: 'default',
                                // 渐变色实现
                                color: new echarts.graphic.LinearGradient(0, 0, 0, 1,
                                    // 三种由深及浅的颜色
                                    [{
                                        offset: 0,
                                        color: '#EE4000'
                                    }, {
                                        offset: 0.5,
                                        color: '#FF4500'
                                    }, {
                                        offset: 1,
                                        color: '#FFFFFF'
                                    }])
                            },
                            itemStyle: {
                                normal: {
                                    // 设置颜色
                                    color: '#CD3700'
                                }
                            }
                        }]
                    };
                    getServerCpuInfoChart.setOption(option);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取网速图表数据
        function getServerNetworkSpeedChartInfo(time, chartAddress) {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-netcard-history/get-server-detail-page-server-network-speed-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip, // 服务器IP
                    address: chartAddress, // 服务器网卡地址
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // 网卡名字
                    var name = data.length !== 0 ? data[data.length - 1].name : "";
                    // 时间
                    var datetime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    // 下载速率
                    var downloadSpeed = data.map(function (item) {
                        return item.downloadSpeed;
                    });
                    // 上传速率
                    var uploadSpeed = data.map(function (item) {
                        return item.uploadSpeed;
                    });
                    // 最新下载速率
                    var lastDownloadSpeed = data.length !== 0 ? convertSize(data[data.length - 1].downloadSpeed) + '/s' : I18nUtils.t('serverDetail.noData');
                    // 最新上传速率
                    var lastUploadSpeed = data.length !== 0 ? convertSize(data[data.length - 1].uploadSpeed) + '/s' : I18nUtils.t('serverDetail.noData');
                    var option = {
                        title: {
                            text: I18nUtils.t('serverDetail.chart.netSpeedTitle', {address: chartAddress, name: name}),
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: I18nUtils.t('serverDetail.chart.netSpeedSubtitle', {upload: lastUploadSpeed, download: lastDownloadSpeed}),
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
                                    var itemValue = item.marker + item.seriesName + ': ' + convertSize(item.data) + '/s</br>';
                                    result += itemValue;
                                });
                                return axisName + '</br>' + result;
                            }
                        },
                        legend: {
                            data: [I18nUtils.t('serverDetail.series.uploadSpeed'), I18nUtils.t('serverDetail.series.downloadSpeed')],
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
                        grid: {
                            left: '5%',
                            right: '8%'
                        },
                        xAxis: {
                            type: 'category',
                            // X轴从零刻度开始
                            boundaryGap: false,
                            data: datetime,
                            axisLabel: {
                                rotate: 0 //调整数值改变倾斜的幅度（范围-90到90）
                            },
                        },
                        yAxis: {
                            type: 'value',
                            name: I18nUtils.t('serverDetail.chart.yAxisSpeed'),
                            axisLabel: {
                                formatter: function (value, index) {
                                    return convertSize(value) + '/s';
                                }
                            }
                        },
                        // 数据
                        series: [{
                            name: I18nUtils.t('serverDetail.series.downloadSpeed'),
                            data: downloadSpeed,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.avg'),
                                    type: 'average',
                                    itemStyle: {
                                        color: '#228B22'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return convertSize(params.value) + '/s' + '\n' + params.name;
                                        }
                                    }
                                }]
                            },
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
                            name: I18nUtils.t('serverDetail.series.uploadSpeed'),
                            data: uploadSpeed,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.avg'),
                                    type: 'average',
                                    itemStyle: {
                                        color: '#1E90FF'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return convertSize(params.value) + '/s' + '\n' + params.name;
                                        }
                                    }
                                }]
                            },
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
                        }]
                    };
                    getServerNetworkSpeedInfoChart.setOption(option);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 发送ajax请求，获取平均负载图表数据
        function getServerLoadAverageChartInfo(time) {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-server-load-average-history/get-server-detail-page-server-load-average-chart-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip, // 服务器IP
                    time: time // 时间
                },
                success: function (result) {
                    var data = result.data;
                    // 1分钟
                    var one = data.map(function (item) {
                        return item.one;
                    });
                    // 5分钟
                    var five = data.map(function (item) {
                        return item.five;
                    });
                    // 15分钟
                    var fifteen = data.map(function (item) {
                        return item.fifteen;
                    });
                    // 新增时间
                    var insertTime = data.map(function (item) {
                        return item.insertTime.replace(' ', '\n');
                    });
                    // CPU逻辑核数量
                    var logicalProcessorCount = data.length !== 0 ? data[data.length - 1].logicalProcessorCount : 0;
                    // 最新负载情况
                    var lastOne = data.length !== 0 ? data[data.length - 1].one : -1;
                    var lastFive = data.length !== 0 ? data[data.length - 1].five : -1;
                    var lastFifteen = data.length !== 0 ? data[data.length - 1].fifteen : -1;
                    var option = {
                        title: {
                            text: I18nUtils.t('serverDetail.chart.loadAverageTitle'),
                            left: 'center',
                            textStyle: {
                                color: '#696969',
                                fontSize: 14
                            },
                            subtext: I18nUtils.t('serverDetail.chart.loadAverageSubtitle', {one: lastOne, five: lastFive, fifteen: lastFifteen}),
                            subtextStyle: {
                                color: '#BEBEBE'
                            }
                        },
                        legend: {
                            data: [I18nUtils.t('serverDetail.series.oneMin'), I18nUtils.t('serverDetail.series.fiveMin'), I18nUtils.t('serverDetail.series.fifteenMin')],
                            x: 'center',
                            y: '12%',
                            orient: 'horizontal'
                        },
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
                            right: '7%'
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
                            name: I18nUtils.t('serverDetail.chart.yAxisLoad')
                        },
                        // 鼠标移到折线上展示数据
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
                        // 数据
                        series: [{
                            name: I18nUtils.t('serverDetail.series.oneMin'),
                            data: one,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.ideal'),
                                    yAxis: logicalProcessorCount * 0.7,
                                    itemStyle: {
                                        color: '#2E8B57'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }, {
                                    name: I18nUtils.t('serverDetail.series.overload'),
                                    yAxis: logicalProcessorCount,
                                    itemStyle: {
                                        color: '#FFB90F'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }, {
                                    name: I18nUtils.t('serverDetail.series.critical'),
                                    yAxis: logicalProcessorCount * 5,
                                    itemStyle: {
                                        color: '#EE2C2C'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }]
                            },
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
                            name: I18nUtils.t('serverDetail.series.fiveMin'),
                            data: five,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.ideal'),
                                    yAxis: logicalProcessorCount * 0.7,
                                    itemStyle: {
                                        color: '#2E8B57'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }, {
                                    name: I18nUtils.t('serverDetail.series.overload'),
                                    yAxis: logicalProcessorCount,
                                    itemStyle: {
                                        color: '#FFB90F'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }, {
                                    name: I18nUtils.t('serverDetail.series.critical'),
                                    yAxis: logicalProcessorCount * 5,
                                    itemStyle: {
                                        color: '#EE2C2C'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }]
                            },
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
                            name: I18nUtils.t('serverDetail.series.fifteenMin'),
                            data: fifteen,
                            type: 'line',
                            smooth: true,
                            markLine: {
                                data: [{
                                    name: I18nUtils.t('serverDetail.series.alarm'),
                                    yAxis: logicalProcessorCount * serverOverloadThreshold15minutes,
                                    itemStyle: {
                                        color: '#FF7F50'
                                    },
                                    label: {
                                        position: 'middle',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }, {
                                    name: I18nUtils.t('serverDetail.series.ideal'),
                                    yAxis: logicalProcessorCount * 0.7,
                                    itemStyle: {
                                        color: '#2E8B57'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }, {
                                    name: I18nUtils.t('serverDetail.series.overload'),
                                    yAxis: logicalProcessorCount,
                                    itemStyle: {
                                        color: '#FFB90F'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }, {
                                    name: I18nUtils.t('serverDetail.series.critical'),
                                    yAxis: logicalProcessorCount * 5,
                                    itemStyle: {
                                        color: '#EE2C2C'
                                    },
                                    label: {
                                        position: 'end',
                                        formatter: function (params) {
                                            return params.value + params.name;
                                        }
                                    }
                                }]
                            },
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
                        }]
                    };
                    getServerLoadAverageInfoChart.setOption(option);
                },
                error: function () {
                }
            });
        }

        // 发送ajax请求
        function execute() {
            if (autoRefresh) {
                // 发送ajax请求，获取CPU图表数据
                getServerCpuChartInfo(time);
                // 发送ajax请求，获取内存图表数据
                getServerMemoryChartInfo(time);
                // 发送ajax请求，获取进程图表数据
                getServerProcessChartInfo(time);
                // 发送ajax请求，获取网速图表数据
                getServerNetworkSpeedChartInfo(time, chartAddress);
                // 发送ajax请求，获取磁盘图表数据
                getServerDiskChartInfo();
                // 发送ajax请求，获取电池图表数据
                getServerPowerSourcesChartInfo();
                // 发送ajax请求，获取传感器图表数据
                getServerSensorsChartInfo();
                // 发送ajax请求，获取平均负载图表数据
                getServerLoadAverageChartInfo(time);
            }
            // 发送ajax请求，获取操作系统数据
            getServerOsInfo();
            // 发送ajax请求，获取网卡数据
            getServerNetcardInfo();
            // 发送ajax请求，获取CPU数据
            getServerCpuInfo();
            // 发送ajax请求，获取GPU数据
            getServerGpuInfo();
            // 发送ajax请求，获取电池数据
            getServerPowerSourcesInfo();
            // 发送ajax请求，获取传感器数据
            getServerSensorsInfo();
            // 发送ajax请求，获取进程数据
            getServerProcessInfo();
        }

        // 页面加载后第一次执行
        execute();
        // 每30秒刷新一次
        window.setInterval(function () {
            execute();
        }, 1000 * 30);
    });
    e('serverDetail', {});
});