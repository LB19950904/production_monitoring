/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['index', 'admin', 'layer', 'fullscreen'], function () {
            // 初始化基本对象
            var admin = layui.admin, l = layui.layer;
            // 设置全屏按钮的 CSS 样式
            layui.fullscreen.css({top: '0px'});

            if (monitorTcpVos.length > 200) {
                l.msg('节点太多，放弃渲染！', {icon: 5, shift: 6});
                return;
            }
            var stage = new jtopo.Stage('topology');
            var layer = new jtopo.Layer('layer-1');
            stage.addChild(layer);
            //页面加载完，先第一次绘制拓扑图
            drawing(monitorTcpVos);

            // 获取Tcp端口拓扑图信息
            function getTcpTopologyInfo() {
                admin.req({
                    type: 'post',
                    url: layui.setter.base + 'monitor-topology/get-tcp-topology-info',
                    dataType: 'json',
                    contentType: 'application/json;charset=utf-8',
                    headers: {
                        "X-CSRF-TOKEN": tokenValue
                    },
                    success: function (result) {
                        var data = result.data;
                        // 绘制拓扑图
                        drawing(data);
                    },
                    error: function () {
                    }
                });
            }

            // 绘制拓扑图
            function drawing(data) {
                if (isNotEmpty(data) && data.length !== 0) {
                    // 重新赋值
                    monitorTcpVos = data;
                    // 移除重绘
                    layer.removeAllChild();
                    // 封装from节点
                    var fromNodes = wrapFromNodes();
                    // 封装to节点
                    var toNodes = wrapToNodes(fromNodes);
                    // 封装连线对象
                    wrapLinks(fromNodes, toNodes);
                    //stage.translateToCenter();
                    // 显示
                    stage.show();

                    // 封装from节点
                    function wrapFromNodes() {
                        var fromNodes = new Map();
                        // 服务端Tcp端口信息根据‘hostnameSource’去重
                        var formTcps = uniqueArr(data, 'hostnameSource');
                        formTcps.forEach((fromTcp, index) => {
                            // 中心点
                            var x = (stage.width / formTcps.length) * (index + 0.5), y = stage.height * 0.5;
                            // 主机名（来源）
                            var fromHostnameSource = fromTcp.hostnameSource;
                            // 添加根节点对象
                            var fromNode = window.commonJtopo.addNode(layer, fromHostnameSource, x, y, 128, 128, {
                                imageSrc: layui.setter.base + 'images/topology/cloud-blue.png',
                                styles: {
                                    font: 'bold 14px arial'
                                }
                            }, {});
                            fromNodes.set(fromHostnameSource, fromNode);
                        });
                        return fromNodes;
                    }

                    // 封装to节点
                    function wrapToNodes(fromNodes) {
                        // 添加子节点
                        var toNodes = new Map();
                        fromNodes.forEach((fromNode, fromHostnameSource) => {
                            var x = fromNode.x, y = fromNode.y;
                            // 装载用于此次布局的目标节点
                            var tempNodes = [];
                            data.forEach((toTcp) => {
                                // 主机名（来源）
                                var toHostnameSource = toTcp.hostnameSource;
                                // 主机名（目的地）
                                var toHostnameTarget = toTcp.hostnameTarget;
                                // 端口号
                                var portTarget = toTcp.portTarget;
                                // 状态
                                var toStatus = toTcp.status;
                                // 匹配上：to节点属于此from节点
                                if (toHostnameSource === fromHostnameSource) {
                                    var toNodeKey = toHostnameTarget + ':' + portTarget;
                                    // 先拿，有就不再创建，没有才创建
                                    var toNode = toNodes.get(toNodeKey);
                                    if (isEmpty(toNode)) {
                                        // 添加节点对象
                                        var point = randCirclePoint(x, y, 200);
                                        toNode = window.commonJtopo.addNode(layer, toNodeKey, point[0], point[1], 128, 128, {
                                            imageSrc: (function () {
                                                if (toStatus === '1') {
                                                    return layui.setter.base + 'images/topology/server-blue.png';
                                                } else if (toStatus === '0') {
                                                    return layui.setter.base + 'images/topology/server-red.png';
                                                } else if (isEmpty(toStatus)) {
                                                    return layui.setter.base + 'images/topology/server-orange.png';
                                                }
                                            })(),
                                            styles: {
                                                fontColor: (function () {
                                                    if (toStatus === '1') {
                                                        return 'black';
                                                    } else if (toStatus === '0') {
                                                        return 'red';
                                                    } else if (isEmpty(toStatus)) {
                                                        return 'orange';
                                                    }
                                                })(),
                                                font: 'bold 14px arial'
                                            }
                                        }, {
                                            tcp: toTcp
                                        });
                                        // 鼠标进入
                                        toNode.on('mouseenter', function (event) {
                                            // 主机名（目的地）
                                            var hostnameTarget = this.userData.tcp.hostnameTarget;
                                            // 端口号
                                            var portTarget = this.userData.tcp.portTarget;
                                            // 描述
                                            var descr = this.userData.tcp.descr;
                                            // 平均响应时间
                                            var avgTime = this.userData.tcp.avgTime;
                                            // 状态
                                            var thatStatus = this.userData.tcp.status;
                                            var status = (function () {
                                                if (thatStatus === '1') {
                                                    return '在线';
                                                } else if (thatStatus === '0') {
                                                    return '离线'
                                                } else if (isEmpty(thatStatus)) {
                                                    return '未知';
                                                }
                                            })();
                                            // 离线次数
                                            var offlineCount = this.userData.tcp.offlineCount;
                                            // 监控环境
                                            var monitorEnv = this.userData.tcp.monitorEnv;
                                            // 监控分组
                                            var monitorGroup = this.userData.tcp.monitorGroup;
                                            var html = '<div class="layui-row layui-col-space10" style="word-wrap:break-word;">' +
                                                '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">主机名：</label>' + hostnameTarget + '</div>' +
                                                '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">端口号：</label>' + portTarget + '</div>';
                                            if (isNotEmpty(descr)) {
                                                html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">描述：</label>' + descr + '</div>';
                                            }
                                            if (isNotEmpty(avgTime)) {
                                                html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">平均响应时间：</label>' + formatMillisecond(avgTime, 'cn') + '</div>';
                                            }
                                            if (isNotEmpty(status)) {
                                                html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">状态：</label>' + status + '</div>';
                                            }
                                            if (isNotEmpty(offlineCount)) {
                                                html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">离线次数：</label>' + offlineCount + '</div>';
                                            }
                                            if (isNotEmpty(monitorEnv)) {
                                                html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">监控环境：</label>' + monitorEnv + '</div>';
                                            }
                                            if (isNotEmpty(monitorGroup)) {
                                                html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">监控分组：</label>' + monitorGroup + '</div>';
                                            }
                                            html += '</div>';
                                            // 鼠标指向小提示
                                            var tooltip = new jtopo.Tooltip(stage);
                                            tooltip.setHtml(html);
                                            var eventDetails = event.details;
                                            tooltip.showAt(eventDetails.x, eventDetails.y);
                                        });
                                        tempNodes.push(toNode);
                                        toNodes.set(toNodeKey, toNode);
                                    }
                                }
                            });
                            if (tempNodes.length > 0) {
                                var layout = new jtopo.CircleLayout();
                                // 动画时间, 毫秒, 不设置,就没有动画效果.
                                // layout.setTime(1000);
                                // 布局的每一步回调（这里是刷新画面）
                                // layout.onLayout(() => layer.update());
                                // 中心点
                                layout.setCenter(x, y);
                                // 圆最小半径
                                var tempMinWidth = ((stage.width / fromNodes.size) / 2) * 0.98;
                                var minWidth = tempMinWidth > 150 ? tempMinWidth - 150 : tempMinWidth;
                                var tempMinHeight = (stage.height / 2) * 0.98;
                                var minHeight = tempMinHeight > 150 ? tempMinHeight - 150 : tempMinHeight;
                                // 如果节点比较少，可以让节点全部显示在屏幕上，不超出屏幕
                                var minRadius = minWidth > minHeight ? minHeight : minWidth;
                                // 如果节点太多，为了保证不重叠，必须延长半径
                                var maxRadius = tempNodes.length * 90 / Math.PI;
                                if (maxRadius > minRadius) {
                                    layout.setMinRadius(maxRadius);
                                } else {
                                    layout.setMinRadius(minRadius);
                                }
                                if (tempNodes.length <= 1) {
                                    layout.setMargin(100, 100, 100, 100);
                                }
                                // 设置角度
                                layout.setAngle(0, 2 * Math.PI);
                                layout.doLayout(tempNodes);
                            }
                        });
                        return toNodes;
                    }

                    // 封装连线对象
                    function wrapLinks(fromNodes, toNodes) {
                        var links = new Map();
                        data.forEach(tcp => {
                            // 主键ID
                            var id = tcp.id;
                            // 主机名（来源）
                            var hostnameSource = tcp.hostnameSource;
                            // 主机名（目的地）
                            var hostnameTarget = tcp.hostnameTarget;
                            // 端口号
                            var portTarget = tcp.portTarget;
                            // 状态
                            var status = tcp.status;
                            // 平均响应时间（毫秒）
                            var avgTime = isNotEmpty(tcp.avgTime) ? formatMillisecond(tcp.avgTime) : '';
                            // 源节点
                            var fromNode = fromNodes.get(hostnameSource);
                            // 目标节点
                            var toNode = toNodes.get(hostnameTarget + ':' + portTarget);
                            if (isNotEmpty(fromNode) && isNotEmpty(toNode)) {
                                // 连线key
                                var myLinkKey = hostnameTarget + ':' + portTarget;
                                // 添加连线对象，先拿，有就不再创建，没有才创建
                                var link = links.get(myLinkKey);
                                if (isEmpty(link)) {
                                    link = window.commonJtopo.addLink(layer, avgTime, fromNode, toNode, 'nearest', 'nearest', {
                                        styles: {
                                            lineWidth: 1,
                                            strokeStyle: (function () {
                                                if (status === '1') {
                                                    return 'black';
                                                } else if (status === '0') {
                                                    return 'red';
                                                } else if (isEmpty(status)) {
                                                    return 'orange';
                                                }
                                            })(),
                                            font: 'bold 15px arial',
                                            labelStyles: {
                                                fontColor: (function () {
                                                    if (status === '1') {
                                                        return 'black';
                                                    } else if (status === '0') {
                                                        return 'red';
                                                    } else if (isEmpty(status)) {
                                                        return 'orange';
                                                    }
                                                })(),
                                                textOffsetY: 10 // 文本偏移量（向下10个像素）
                                            }
                                        }
                                    }, {
                                        id: id,
                                        name: myLinkKey
                                    });
                                    // 鼠标点击
                                    link.on('click', function () {
                                        // 访问平均时间页面
                                        toAvgTimePage(this.userData.id);
                                    });
                                    // 鼠标进入
                                    link.on('mouseenter', function (event) {
                                        //var eventDetails = event.details;
                                        // 鼠标指向小提示
                                        //var tooltip = new jtopo.Tooltip(stage);
                                        //tooltip.setHtml(this.userData.name);
                                        //tooltip.showAt(eventDetails.x, eventDetails.y);
                                        // 变成鼠标手势
                                        stage.setCursor('pointer')
                                    });
                                    // 鼠标离开
                                    link.on('mouseout', function (event) {
                                        // 去掉鼠标手势
                                        stage.setCursor('default')
                                    });
                                    links.set(myLinkKey, link);
                                }
                            }
                        });
                        return links;
                    }
                }
            }

            // 访问平均时间页面
            function toAvgTimePage(id) {
                var avgTimeHistoryLayerIndex = l.open({
                    type: 2,
                    title: 'Telnet耗时',
                    content: ctxPath + 'monitor-tcp/avg-time?id=' + id,
                    maxmin: true,
                    shade: 0,
                    moveOut: true,
                    area: [stage.width, stage.height],
                    success: function (layero, index) {
                        // 在回调方法中的第2个参数“index”表示的是当前弹窗的索引
                        // l.full方法将窗口放大
                        // l.full(index);
                    }
                });
                l.full(avgTimeHistoryLayerIndex);
            }

            // 浏览器窗口大小发生改变时
            window.addEventListener('resize', function () {
                // 重绘
                drawing(monitorTcpVos);
            });

            // 每15秒刷新一次
            window.setInterval(function () {
                getTcpTopologyInfo();
            }, 1000 * 15);

        }
    );
    e('topologyTcp', {});
});