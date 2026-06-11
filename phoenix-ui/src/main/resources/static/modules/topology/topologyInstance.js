/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['index', 'admin', 'layer', 'fullscreen'], function () {
            // 初始化基本对象
            let admin = layui.admin, l = layui.layer;
            // 设置全屏按钮的 CSS 样式
            layui.fullscreen.css({top: '0px'});

            let stage = new jtopo.Stage('topology');
            let layer = new jtopo.Layer('layer-1');
            stage.addChild(layer);

            //页面加载完，先第一次绘制拓扑图
            drawing(monitorLinkVos);

            // 获取应用程序拓扑图信息
            function getInstanceTopologyInfo() {
                admin.req({
                    type: 'post',
                    url: layui.setter.base + 'monitor-topology/get-instance-topology-info',
                    dataType: 'json',
                    contentType: 'application/json;charset=utf-8',
                    headers: {
                        "X-CSRF-TOKEN": tokenValue
                    },
                    success: function (result) {
                        let data = result.data;
                        // 绘制拓扑图
                        drawing(data);
                    },
                    error: function () {
                    }
                });
            }

            // 获取应用程序信息(Map形式)
            function getMonitorInstance2Map(completeCallback) {
                admin.req({
                    type: 'post',
                    url: layui.setter.base + 'monitor-instance/get-monitor-instance-to-map',
                    dataType: 'json',
                    contentType: 'application/json;charset=utf-8',
                    headers: {
                        "X-CSRF-TOKEN": tokenValue
                    },
                    success: function (result) {
                        // 赋值
                        monitorInstanceVoMap = result.data;
                    },
                    error: function () {
                    },
                    complete: function () {
                        completeCallback();
                    }
                });
            }

            // 绘制拓扑图
            function drawing(data) {
                if (isNotEmpty(data) && data.length !== 0) {
                    // 重新赋值
                    monitorLinkVos = data;
                    // 移除重绘
                    layer.removeAllChild();
                    // 封装根节点
                    let rootNodes = wrapRootNodes();
                    // 封装子节点
                    wrapChildNodes(rootNodes);
                    //stage.translateToCenter();
                    // 显示
                    stage.show();

                    // 封装根节点
                    function wrapRootNodes() {
                        let rootNodes = new Map();
                        // 应用程序链路信息根据‘rootNode’去重
                        let links = uniqueArr(data, 'rootNode');
                        links.forEach((obj, index) => {
                            // 根节点
                            let root = obj.rootNode;
                            let instance = monitorInstanceVoMap[root];
                            if (isEmpty(instance)) {
                                // 跳出当前循环
                                return;
                            }
                            // 中心点
                            let x = (stage.width / links.length) * (index + 0.5), y = stage.height * 0.5;
                            // 根节点名字
                            let rootName = obj.rootNodeName;
                            // 根节点IP地址
                            let rootIp = obj.rootNodeIp;
                            // 添加根节点对象
                            let rootNode = rootNodes.get(root);
                            if (isEmpty(rootNode)) {
                                // 在线状态
                                let isOnline = instance.isOnline;
                                let text = rootName + '\n' + rootIp + '\n' + root;
                                let attribute = {
                                    imageSrc: (function () {
                                        if (isOnline === '1') {
                                            return layui.setter.base + 'images/topology/cloud-blue.png';
                                        } else if (isOnline === '0') {
                                            return layui.setter.base + 'images/topology/cloud-red.png';
                                        } else if (isEmpty(isOnline)) {
                                            return layui.setter.base + 'images/topology/cloud-orange.png';
                                        }
                                    })(),
                                    styles: {
                                        fontColor: (function () {
                                            if (isOnline === '1') {
                                                return 'black';
                                            } else if (isOnline === '0') {
                                                return 'red';
                                            } else if (isEmpty(isOnline)) {
                                                return 'orange';
                                            }
                                        })(),
                                        font: 'bold 14px arial'
                                    }
                                };
                                let userData = {
                                    attribute: root
                                };
                                rootNode = window.commonJtopo.addNode(layer, text, x, y, 128, 128, attribute, userData);
                                // 鼠标点击
                                rootNode.on('click', function () {
                                    if (isNotEmpty(this.userData.attribute)) {
                                        // 访问应用程序详情页面
                                        instanceDetail(this.userData.attribute);
                                    }
                                });
                                // 鼠标进入
                                rootNode.on('mouseenter', function (event) {
                                    if (isNotEmpty(this.userData.attribute)) {
                                        let eventDetails = event.details;
                                        // 鼠标指向小提示
                                        let tooltip = new jtopo.Tooltip(stage);
                                        tooltip.setHtml(wrapTips(this.userData.attribute));
                                        tooltip.showAt(eventDetails.x, eventDetails.y);
                                        // 变成鼠标手势
                                        stage.setCursor('pointer');
                                    }
                                });
                                // 鼠标离开
                                rootNode.on('mouseout', function () {
                                    // 去掉鼠标手势
                                    stage.setCursor('default')
                                });
                                rootNodes.set(root, rootNode);
                            }
                        });
                        return rootNodes;
                    }

                    // 封装子节点
                    function wrapChildNodes(rootNodes) {
                        // 子节点
                        let childNodes = new Map();
                        // 链路
                        let links = new Map();
                        rootNodes.forEach((rootNode, root) => {
                            if (isEmpty(monitorInstanceVoMap[root])) {
                                // 跳出当前循环
                                return;
                            }
                            let x = rootNode.x, y = rootNode.y;
                            // Node和Link组成的集合
                            let objects = [];
                            objects.push(rootNode);
                            data.forEach((obj) => {
                                // 根节点
                                let tempRoot = obj.rootNode;
                                if (root === tempRoot) {
                                    // 子节点
                                    let childes = obj.link.split(',');
                                    // 子节点名字
                                    let childLinkNames = obj.linkName.split(',');
                                    // 子节点IP地址
                                    let childLinkIps = obj.linkIp.split(',');
                                    // 根节点时间
                                    let rootNodeTime = obj.rootNodeTime;
                                    // 时间链
                                    let times = obj.linkTime.split(',');
                                    childes.forEach((child, index) => {
                                        let instance = monitorInstanceVoMap[child];
                                        if (isEmpty(instance)) {
                                            // 跳出当前循环
                                            return;
                                        }
                                        // 先拿，有就不再创建，没有才创建
                                        let childNode = rootNodes.get(child);
                                        if (isEmpty(childNode)) {
                                            childNode = childNodes.get(child);
                                        }
                                        if (isEmpty(childNode)) {
                                            // 在线状态
                                            let isOnline = instance.isOnline;
                                            // 添加节点对象
                                            let point = randCirclePoint(x, y, 200);
                                            let text = childLinkNames[index] + '\n' + childLinkIps[index] + '\n' + child;
                                            let attribute = {
                                                imageSrc: (function () {
                                                    if (isOnline === '1') {
                                                        return layui.setter.base + 'images/topology/server-blue.png';
                                                    } else if (isOnline === '0') {
                                                        return layui.setter.base + 'images/topology/server-red.png';
                                                    } else if (isEmpty(isOnline)) {
                                                        return layui.setter.base + 'images/topology/server-orange.png';
                                                    }
                                                })(),
                                                styles: {
                                                    fontColor: (function () {
                                                        if (isOnline === '1') {
                                                            return 'black';
                                                        } else if (isOnline === '0') {
                                                            return 'red';
                                                        } else if (isEmpty(isOnline)) {
                                                            return 'orange';
                                                        }
                                                    })(),
                                                    font: 'bold 14px arial'
                                                }
                                            };
                                            let userData = {
                                                attribute: child
                                            };
                                            childNode = window.commonJtopo.addNode(layer, text, point[0], point[1], 128, 128, attribute, userData);
                                            // 鼠标点击
                                            childNode.on('click', function () {
                                                if (isNotEmpty(this.userData.attribute)) {
                                                    // 访问应用程序详情页面
                                                    instanceDetail(this.userData.attribute);
                                                }
                                            });
                                            // 鼠标进入
                                            childNode.on('mouseenter', function (event) {
                                                if (isNotEmpty(this.userData.attribute)) {
                                                    let eventDetails = event.details;
                                                    // 鼠标指向小提示
                                                    let tooltip = new jtopo.Tooltip(stage);
                                                    tooltip.setHtml(wrapTips(this.userData.attribute));
                                                    tooltip.showAt(eventDetails.x, eventDetails.y);
                                                    // 变成鼠标手势
                                                    stage.setCursor('pointer');
                                                }
                                            });
                                            // 鼠标离开
                                            childNode.on('mouseout', function () {
                                                // 去掉鼠标手势
                                                stage.setCursor('default')
                                            });
                                            childNodes.set(child, childNode);
                                            objects.push(childNode);
                                        }
                                        // 连线key
                                        let myLinkKey;
                                        // 节点
                                        let myRootNode;
                                        // 文本
                                        let text;
                                        let millisecond;
                                        let isOnline1;
                                        let isOnline2;
                                        let hasAnimation = true;
                                        let color = 'black';
                                        if (index === 0) {
                                            myLinkKey = child + '-->' + root;
                                            myRootNode = rootNode;
                                            millisecond = Math.abs(parseFloat(rootNodeTime) - parseFloat(times[0]));
                                            if (millisecond < 1000) {
                                                color = 'black';
                                            } else if (millisecond >= 1000 && millisecond <= 30000) {
                                                color = '#DE9121';
                                            } else if (millisecond > 30000) {
                                                color = 'red';
                                                hasAnimation = false;
                                            }
                                            isOnline1 = monitorInstanceVoMap[root].isOnline;
                                            isOnline2 = instance.isOnline;
                                            if (isOnline1 === '0' || isOnline2 === '0') {
                                                color = 'red';
                                                hasAnimation = false;
                                            } else if (isEmpty(isOnline1) || isEmpty(isOnline2)) {
                                                color = 'orange';
                                                hasAnimation = false;
                                            }
                                            text = formatMillisecond(millisecond, 'en');
                                        } else if (index > 0) {
                                            myLinkKey = child + '-->' + childes[index - 1];
                                            myRootNode = childNodes.get(childes[index - 1]);
                                            millisecond = Math.abs(parseFloat(times[index - 1]) - parseFloat(times[index]));
                                            if (millisecond < 1000) {
                                                color = 'black';
                                            } else if (millisecond >= 1000 && millisecond <= 30000) {
                                                color = '#DE9121';
                                            } else if (millisecond > 30000) {
                                                color = 'red';
                                                hasAnimation = false;
                                            }
                                            isOnline1 = instance.isOnline;
                                            if (isEmpty(monitorInstanceVoMap[childes[index - 1]])) {
                                                // 跳出当前循环
                                                return;
                                            }
                                            isOnline2 = monitorInstanceVoMap[childes[index - 1]].isOnline;
                                            if (isOnline1 === '0' || isOnline2 === '0') {
                                                color = 'red';
                                                hasAnimation = false;
                                            } else if (isEmpty(isOnline1) || isEmpty(isOnline2)) {
                                                color = 'orange';
                                                hasAnimation = false;
                                            }
                                            text = formatMillisecond(millisecond, 'en');
                                        }
                                        // 添加连线对象，先拿，有就不再创建，没有才创建
                                        let link = links.get(myLinkKey);
                                        if (isEmpty(link)) {
                                            let attribute = {
                                                styles: {
                                                    lineWidth: 1,
                                                    lineDash: [6, 2],
                                                    strokeStyle: color,
                                                    font: 'bold 15px arial',
                                                    labelStyles: {
                                                        fontColor: color,
                                                        textOffsetY: 10 // 文本偏移量（向下10个像素）
                                                    }
                                                }
                                            };
                                            link = window.commonJtopo.addCurveLink(layer, text, myRootNode, childNode, 'nearest', 'nearest', attribute, hasAnimation, {});
                                            // 箭头
                                            link.setBeginArrow(new jtopo.ArrowNode());
                                            link.beginArrow.setStyles(link.style);
                                            links.set(myLinkKey, link);
                                            objects.push(link);
                                        }
                                    });
                                }
                            });
                            if (objects.length > 0) {
                                let layout = new jtopo.TreeLayout('down');
                                layout.setMargin(128, 128, 250, 128);
                                // 动画时间, 毫秒, 不设置,就没有动画效果.
                                // layout.setTime(1200);
                                // 布局的每一步回调（这里是刷新画面）
                                // layout.onLayout(() => layer.update());
                                // 布局后的中心
                                layout.setCenter(x, y);
                                // 生成虚拟树，取第一棵
                                let vTrees = new jtopo.Graph(objects).toTrees();
                                let vTree = vTrees[0];
                                // 执行布局
                                layout.doLayout(vTree).then(() => {
                                    // 结束
                                });
                            }
                        });
                    }

                    // 小提示
                    function wrapTips(instanceId) {
                        let instance = monitorInstanceVoMap[instanceId];
                        // 端点
                        let endpoint = instance.endpoint;
                        if (endpoint === 'client') {
                            endpoint = '客户端';
                        }
                        if (endpoint === 'agent') {
                            endpoint = '代理端';
                        }
                        if (endpoint === 'server') {
                            endpoint = '服务端';
                        }
                        if (endpoint === 'ui') {
                            endpoint = 'UI端';
                        }
                        // 应用名称
                        let instanceName = instance.instanceName;
                        // 应用描述
                        let instanceDesc = instance.instanceDesc;
                        // IP地址
                        let ip = instance.ip;
                        // 新增时间
                        let insertTime = instance.insertTime;
                        // 更新时间
                        let updateTime = instance.updateTime;
                        // 应用状态
                        let isOnline = (function () {
                            if (instance.isOnline === '1') {
                                return '在线';
                            } else if (instance.isOnline === '0') {
                                return '离线';
                            } else if (isEmpty(instance.isOnline)) {
                                return '未知';
                            }
                        })();
                        // 离线次数
                        let offlineCount = instance.offlineCount;
                        // 连接频率
                        let connFrequency = instance.connFrequency + '秒/次';
                        // 程序语言
                        let language = instance.language;
                        // 应用服务器
                        let appServerType = instance.appServerType;
                        // 监控环境
                        let monitorEnv = instance.monitorEnv;
                        // 监控分组
                        let monitorGroup = instance.monitorGroup;
                        let html = '<div class="layui-row layui-col-space10" style="word-wrap:break-word;">' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">应用ID：</label>' + instanceId + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">端点：</label>' + endpoint + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">应用名称：</label>' + instanceName + '</div>';
                        if (isNotEmpty(instanceDesc)) {
                            html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">描述：</label>' + instanceDesc + '</div>';
                        }
                        html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">IP：</label>' + ip + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">状态：</label>' + isOnline + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">离线次数：</label>' + offlineCount + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">连接频率：</label>' + connFrequency + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">程序语言：</label>' + language + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">应用服务器：</label>' + appServerType + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">新增时间：</label>' + insertTime + '</div>';
                        if (isNotEmpty(updateTime)) {
                            html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">更新时间：</label>' + updateTime + '</div>';
                        }
                        if (isNotEmpty(monitorEnv)) {
                            html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">监控环境：</label>' + monitorEnv + '</div>';
                        }
                        if (isNotEmpty(monitorGroup)) {
                            html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">监控分组：</label>' + monitorGroup + '</div>';
                        }
                        html += '</div>';
                        return html;
                    }
                }
            }

            // 访问应用程序详情页面
            function instanceDetail(instanceId) {
                let infoLayerIndex = l.open({
                    type: 2,
                    title: '应用详情（ID：' + instanceId + '）',
                    content: ctxPath + 'monitor-instance/instance-detail?instanceId=' + instanceId,
                    maxmin: true,
                    shade: 0,
                    moveOut: true,
                    area: [stage.width, stage.height],
                    success: function (layero, index) {
                        // 在回调方法中的第2个参数“index”表示的是当前弹窗的索引
                        // 通过layer.full方法将窗口放大
                        // layer.full(index);
                    }
                });
                l.full(infoLayerIndex);
            }

            // 浏览器窗口大小发生改变时
            window.addEventListener('resize', function () {
                // 重绘
                drawing(monitorLinkVos);
            });

            // 每15秒刷新一次
            window.setInterval(function () {
                // 先获取应用程序信息，然后再获取拓扑图信息
                getMonitorInstance2Map(() => {
                    getInstanceTopologyInfo();
                });
            }, 1000 * 15);

        }
    );
    e('topologyInstance', {});
});