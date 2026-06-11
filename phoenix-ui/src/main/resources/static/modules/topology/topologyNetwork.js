/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['index', 'admin', 'layer', 'fullscreen'], function () {
            // 初始化基本对象
            var admin = layui.admin, l = layui.layer;
            // 设置全屏按钮的 CSS 样式
            layui.fullscreen.css({top: '0px'});

            if (monitorNetVos.length > 200) {
                l.msg('节点太多，放弃渲染！', {icon: 5, shift: 6});
                return;
            }
            var stage = new jtopo.Stage('topology');
            var layer = new jtopo.Layer('layer-1');
            stage.addChild(layer);
            //页面加载完，先第一次绘制拓扑图
            drawing(monitorNetVos);

            // 获取网络拓扑图信息
            function getNetworkTopologyInfo() {
                admin.req({
                    type: 'post',
                    url: layui.setter.base + 'monitor-topology/get-network-topology-info',
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
                    monitorNetVos = data;
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
                        // 服务端网络信息根据‘ipSource’去重
                        var formNets = uniqueArr(data, 'ipSource');
                        formNets.forEach((fromNet, index) => {
                            // 中心点
                            var x = (stage.width / formNets.length) * (index + 0.5), y = stage.height * 0.5;
                            // IP地址（来源）
                            var fromIpSource = fromNet.ipSource;
                            // 添加根节点对象
                            var fromNode = window.commonJtopo.addNode(layer, fromIpSource, x, y, 128, 128, {
                                imageSrc: layui.setter.base + 'images/topology/cloud-blue.png',
                                styles: {
                                    font: 'bold 14px arial'
                                }
                            }, {});
                            fromNodes.set(fromIpSource, fromNode);
                        });
                        return fromNodes;
                    }

                    // 封装to节点
                    function wrapToNodes(fromNodes) {
                        // 添加子节点
                        var toNodes = new Map();
                        fromNodes.forEach((fromNode, fromIpSource) => {
                            var x = fromNode.x, y = fromNode.y;
                            // 装载用于此次布局的目标节点
                            var tempNodes = [];
                            // from源IP去重后的数组
                            var fromIpSources = objectArr2AttributeArr(data, 'ipSource', true);
                            data.forEach((toNet) => {
                                // IP地址（来源）
                                var toIpSource = toNet.ipSource;
                                // IP地址（目的地）
                                var toIpTarget = toNet.ipTarget;
                                // 状态
                                var toStatus = toNet.status;
                                // 匹配上：to节点属于此from节点
                                if (toIpSource === fromIpSource) {
                                    // 源和目标相同，直接忽略
                                    if (toIpTarget === toIpSource) {
                                        // 结束本次循环
                                    }
                                    // 目标在源中存在，直接忽略
                                    else if (fromIpSources.includes(toIpTarget)) {
                                        // 结束本次循环
                                    }
                                    // 创建目标节点
                                    else {
                                        // 先拿，有就不再创建，没有才创建
                                        var toNode = toNodes.get(toIpTarget);
                                        if (isEmpty(toNode)) {
                                            // 添加节点对象
                                            var point = randCirclePoint(x, y, 200);
                                            toNode = window.commonJtopo.addNode(layer, toIpTarget, point[0], point[1], 128, 128, {
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
                                                net: toNet
                                            });
                                            // 鼠标进入
                                            toNode.on('mouseenter', function (event) {
                                                // 描述
                                                var desc = this.userData.net.ipDesc;
                                                // 平均响应时间
                                                var avgTime = this.userData.net.avgTime;
                                                // 状态
                                                var thatStatus = this.userData.net.status;
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
                                                var offlineCount = this.userData.net.offlineCount;
                                                // 监控环境
                                                var monitorEnv = this.userData.net.monitorEnv;
                                                // 监控分组
                                                var monitorGroup = this.userData.net.monitorGroup;
                                                var html = '<div class="layui-row layui-col-space10" style="word-wrap:break-word;">';
                                                if (isNotEmpty(desc)) {
                                                    html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">描述：</label>' + desc + '</div>';
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
                                            toNodes.set(toIpTarget, toNode);
                                        } else {
                                            // 添加用户自定义属性
                                            var nets = toNode.userData.nets;
                                            nets.push(toNet);
                                            toNode.userData.nets = nets;
                                        }
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
                                    // 节点间隔
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
                        data.forEach(net => {
                            // 主键ID
                            var id = net.id;
                            // IP地址（来源）
                            var ipSource = net.ipSource;
                            // IP地址（目的地）
                            var ipTarget = net.ipTarget;
                            // 状态
                            var status = net.status;
                            // 平均响应时间（毫秒）
                            var avgTime = isNotEmpty(net.avgTime) ? formatMillisecond(net.avgTime) : '';
                            // 源节点
                            var fromNode = fromNodes.get(ipSource);
                            // 目标节点
                            var toNode = toNodes.get(ipTarget);
                            if (isEmpty(toNode)) {
                                toNode = fromNodes.get(ipTarget);
                            }
                            if (isNotEmpty(fromNode) && isNotEmpty(toNode)) {
                                // 连线key
                                var myLinkKey = ipSource + '-->' + ipTarget;
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
                    title: 'Ping耗时',
                    content: ctxPath + 'monitor-network/avg-time?id=' + id,
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
                drawing(monitorNetVos);
            });

            // 每15秒刷新一次
            window.setInterval(function () {
                getNetworkTopologyInfo();
            }, 1000 * 15);

        }
    );
    e('topologyNetwork', {});
});