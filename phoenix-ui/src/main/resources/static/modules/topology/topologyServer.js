/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['index', 'admin', 'layer', 'fullscreen'], function () {
            // 根节点
            let globalRootNodeMap = new Map();
            // 子节点
            let globalChildNodeMap = new Map();
            // 要布局的元素(节点+链路)
            let globalElementsMap = new Map();
            // 初始化基本对象
            let admin = layui.admin, l = layui.layer;
            // 设置全屏按钮的 CSS 样式
            layui.fullscreen.css({top: '0px'});

            let stage = new jtopo.Stage('topology');
            let layer = new jtopo.Layer('layer-1');
            stage.addChild(layer);

            //页面加载完，先第一次绘制拓扑图
            drawing(monitorLinkVos);

            // 获取服务器拓扑图信息
            function getServerTopologyInfo() {
                admin.req({
                    type: 'post',
                    url: layui.setter.base + 'monitor-topology/get-server-topology-info',
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

            // 获取服务器信息(Map形式)
            function getMonitorServer2Map(completeCallback) {
                admin.req({
                    type: 'post',
                    url: layui.setter.base + 'monitor-server/get-monitor-server-to-map',
                    dataType: 'json',
                    contentType: 'application/json;charset=utf-8',
                    headers: {
                        "X-CSRF-TOKEN": tokenValue
                    },
                    success: function (result) {
                        // 赋值
                        monitorServerVoMap = result.data;
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
                    // layer.removeAllChild();
                    // 封装根节点
                    let rootNodes = wrapRootNodes();
                    // 移除所以连线link
                    layer.removeChilds(layer.getAllLinks());
                    // 封装子节点
                    wrapChildNodes(rootNodes);
                    // 显示
                    stage.show();
                    // console.log(layer.children.length);

                    // 封装根节点
                    function wrapRootNodes() {
                        let rootNodes = new Map();
                        // 服务器链路信息根据‘rootNode’去重
                        let links = uniqueArr(data, 'rootNode');
                        links.forEach((obj, index) => {
                            // 根节点
                            let root = obj.rootNode;
                            let server = monitorServerVoMap[root];
                            if (isEmpty(server)) {
                                // 跳出当前循环
                                return;
                            }
                            // 中心点
                            let x = (stage.width / links.length) * (index + 0.5), y = stage.height * 0.5;
                            // 在线状态
                            let isOnline = server.isOnline;
                            // 服务器摘要
                            // let serverSummary = server.serverSummary;
                            let txt = root;
                            // if (isNotEmpty(serverSummary)) {
                            //     txt = txt + '\n' + '(' + serverSummary + ')';
                            // }
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
                            // 先从全局变量中获取根节点
                            let rootNode = globalRootNodeMap.get(root);
                            if (isEmpty(rootNode)) {
                                // 没有再从局部变量中获取根节点
                                rootNode = rootNodes.get(root);
                            }
                            if (isEmpty(rootNode)) {
                                rootNode = window.commonJtopo.addNode(layer, txt, x, y, 128, 128, attribute, userData);
                                // 鼠标点击
                                rootNode.on('click', function () {
                                    // 访问服务器详情页面
                                    if (isNotEmpty(monitorServerVoMap[this.userData.attribute])) {
                                        serverDetail(this.userData.attribute);
                                    }
                                });
                                // 鼠标进入
                                rootNode.on('mouseenter', function (event) {
                                    if (isNotEmpty(monitorServerVoMap[this.userData.attribute])) {
                                        let eventDetails = event.details;
                                        // 鼠标指向小提示
                                        let tooltip = new jtopo.Tooltip(stage);
                                        tooltip.setHtml(wrapTips(this.userData.attribute));
                                        tooltip.showAt(eventDetails.x, eventDetails.y);
                                        // 变成鼠标手势
                                        stage.setCursor('pointer')
                                    }
                                });
                                // 鼠标离开
                                rootNode.on('mouseout', function () {
                                    // 去掉鼠标手势
                                    stage.setCursor('default')
                                });
                            }
                            // 更新节点
                            else {
                                rootNode = window.commonJtopo.updateNode(rootNode, txt, undefined, undefined, undefined, undefined, attribute, userData);
                            }
                            // 最后添加到map
                            rootNodes.set(root, rootNode);
                        });
                        // 求两个map对象的差集
                        const rootMapDifference = mapDifference(globalRootNodeMap, rootNodes);
                        // 移除已经不存在的子节点
                        rootMapDifference.forEach((node) => {
                            layer.removeChild(node);
                        });
                        // 赋值
                        globalRootNodeMap = rootNodes;
                        return rootNodes;
                    }

                    // 封装子节点
                    function wrapChildNodes(rootNodes) {
                        // 子节点
                        let childNodes = new Map();
                        // 链路
                        let links = new Map();
                        rootNodes.forEach((rootNode, root) => {
                            if (isEmpty(monitorServerVoMap[root])) {
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
                                    // 根节点时间
                                    let rootNodeTime = obj.rootNodeTime;
                                    // 时间链
                                    let times = obj.linkTime.split(',');
                                    childes.forEach((child, index) => {
                                        let server = monitorServerVoMap[child];
                                        if (isEmpty(server)) {
                                            // 跳出当前循环
                                            return;
                                        }
                                        // 在线状态
                                        let isOnline = server.isOnline;
                                        // 服务器摘要
                                        // let serverSummary = server.serverSummary;
                                        let nodeText = child;
                                        // if (isNotEmpty(serverSummary)) {
                                        //     nodeText = nodeText + '\n' + '(' + serverSummary + ')';
                                        // }
                                        let nodeAttribute = {
                                            imageSrc: (function () {
                                                if (isOnline === '1') {
                                                    return layui.setter.base + 'images/topology/server-blue.png';
                                                } else if (isOnline === '0') {
                                                    return layui.setter.base + 'images/topology/server-red.png';
                                                } else if (isEmpty(isOnline)) {
                                                    return layui.setter.base + 'images/topology/server-orange.png'
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
                                        let nodeUserData = {
                                            attribute: child
                                        };
                                        // 先拿，有就不再创建，没有才创建
                                        let childNode = globalRootNodeMap.get(child);
                                        if (isEmpty(childNode)) {
                                            childNode = globalChildNodeMap.get(child);
                                        }
                                        if (isEmpty(childNode)) {
                                            childNode = rootNodes.get(child);
                                        }
                                        if (isEmpty(childNode)) {
                                            childNode = childNodes.get(child);
                                        }
                                        if (isEmpty(childNode)) {
                                            // 添加节点对象
                                            let point = randCirclePoint(x, y, 200);
                                            childNode = window.commonJtopo.addNode(layer, nodeText, point[0], point[1], 128, 128, nodeAttribute, nodeUserData);
                                            // 鼠标点击
                                            childNode.on('click', function () {
                                                // 访问服务器详情页面
                                                if (isNotEmpty(monitorServerVoMap[this.userData.attribute])) {
                                                    serverDetail(this.userData.attribute);
                                                }
                                            });
                                            // 鼠标进入
                                            childNode.on('mouseenter', function (event) {
                                                if (isNotEmpty(monitorServerVoMap[this.userData.attribute])) {
                                                    let eventDetails = event.details;
                                                    // 鼠标指向小提示
                                                    let tooltip = new jtopo.Tooltip(stage);
                                                    tooltip.setHtml(wrapTips(this.userData.attribute));
                                                    tooltip.showAt(eventDetails.x, eventDetails.y);
                                                    // 变成鼠标手势
                                                    stage.setCursor('pointer')
                                                }
                                            });
                                            // 鼠标离开
                                            childNode.on('mouseout', function () {
                                                // 去掉鼠标手势
                                                stage.setCursor('default')
                                            });
                                        }
                                        // 更新节点
                                        else {
                                            childNode = window.commonJtopo.updateNode(childNode, nodeText, undefined, undefined, undefined, undefined, nodeAttribute, nodeUserData);
                                        }
                                        if (!arrExistingObj(objects, childNode, 'id')) {
                                            objects.push(childNode);
                                        }
                                        // 最后把节点添加到子节点map和数组
                                        childNodes.set(child, childNode);

                                        // 连线key
                                        let myLinkKey;
                                        // 节点
                                        let myRootNode;
                                        // 文本
                                        let linkText;
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
                                            isOnline1 = monitorServerVoMap[root].isOnline;
                                            isOnline2 = server.isOnline;
                                            if (isOnline1 === '0' || isOnline2 === '0') {
                                                color = 'red';
                                                hasAnimation = false;
                                            } else if (isEmpty(isOnline1) || isEmpty(isOnline2)) {
                                                color = 'orange';
                                                hasAnimation = false;
                                            }
                                            linkText = formatMillisecond(millisecond, 'en');
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
                                            isOnline1 = server.isOnline;
                                            if (isEmpty(monitorServerVoMap[childes[index - 1]])) {
                                                // 跳出当前循环
                                                return;
                                            }
                                            isOnline2 = monitorServerVoMap[childes[index - 1]].isOnline;
                                            if (isOnline1 === '0' || isOnline2 === '0') {
                                                color = 'red';
                                                hasAnimation = false;
                                            } else if (isEmpty(isOnline1) || isEmpty(isOnline2)) {
                                                color = 'orange';
                                                hasAnimation = false;
                                            }
                                            linkText = formatMillisecond(millisecond, 'en');
                                        }
                                        let linkAttribute = {
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
                                        // 添加连线对象，先拿，有就不再创建，没有才创建
                                        let link = links.get(myLinkKey);
                                        if (isEmpty(link)) {
                                            link = window.commonJtopo.addCurveLink(layer, linkText, myRootNode, childNode, 'nearest', 'nearest', linkAttribute, hasAnimation, {});
                                            // 箭头
                                            link.setBeginArrow(new jtopo.ArrowNode());
                                            link.beginArrow.setStyles(link.style);
                                        }
                                        if (!arrExistingObj(objects, link, 'id')) {
                                            objects.push(link);
                                        }
                                        // 最后把链路添加到map和数组
                                        links.set(myLinkKey, link);
                                    });
                                }
                            });
                            // 从全局变量中获取要布局的元素，比较是否相等，如果不相等就要重新布局
                            let globalElements = globalElementsMap.get(root);
                            // 默认认为相等
                            let isEqual = true;
                            if (isEmpty(globalElements)) {
                                isEqual = false;
                            } else {
                                for (let i = 0; i < globalElements.length; i++) {
                                    // 只比较节点，不比较连线
                                    if (isEmpty(objects[i]) || (globalElements[i].className.toLowerCase().includes('node') && globalElements[i].id !== objects[i].id)) {
                                        isEqual = false;
                                        break;
                                    }
                                }
                                for (let j = 0; j < objects.length; j++) {
                                    // 只比较节点，不比较连线
                                    if (isEmpty(globalElements[j]) || (objects[j].className.toLowerCase().includes('node') && objects[j].id !== globalElements[j].id)) {
                                        isEqual = false;
                                        break;
                                    }
                                }
                            }
                            if (objects.length > 0 && !isEqual) {
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
                            // 把要布局的元素放入全局变量中
                            globalElementsMap.set(root, objects);
                        });
                        // 求两个map对象的差集
                        const childMapDifference = mapDifference(globalChildNodeMap, childNodes);
                        // 移除已经不存在的子节点
                        childMapDifference.forEach((node) => {
                            layer.removeChild(node);
                        });
                        // 赋值
                        globalChildNodeMap = childNodes;
                    }

                    // 小提示
                    function wrapTips(ip) {
                        let server = monitorServerVoMap[ip];
                        // 服务器名
                        let serverName = server.serverName;
                        // 服务器摘要
                        let serverSummary = server.serverSummary;
                        // 新增时间
                        let insertTime = server.insertTime;
                        // 更新时间
                        let updateTime = server.updateTime;
                        // 状态
                        let isOnline = (function () {
                            if (server.isOnline === '1') {
                                return '在线';
                            } else if (server.isOnline === '0') {
                                return '离线';
                            } else if (isEmpty(server.isOnline)) {
                                return '未知';
                            }
                        })();
                        // 离线次数
                        let offlineCount = server.offlineCount;
                        // 连接频率
                        let connFrequency = server.connFrequency + '秒/次';
                        // 操作系统名称
                        let osName = server.osName;
                        // CPU使用率
                        let cpuUserPercent = server.cpuUserPercent + '%';
                        // 内存使用率
                        let menUsedPercent = server.menUsedPercent + '%';
                        // 服务器负载
                        let loadAverage = server.loadAverage;
                        // 下行速率
                        let downloadBps = server.downloadBps;
                        // 上行速率
                        let uploadBps = server.uploadBps;
                        // 监控环境
                        let monitorEnv = server.monitorEnv;
                        // 监控分组
                        let monitorGroup = server.monitorGroup;
                        let html = '<div class="layui-row layui-col-space10" style="word-wrap:break-word;">' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">服务器名：</label>' + serverName + '</div>';
                        if (isNotEmpty(serverSummary)) {
                            html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">描述：</label>' + serverSummary + '</div>';
                        }
                        html += '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">IP：</label>' + ip + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">状态：</label>' + isOnline + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">离线次数：</label>' + offlineCount + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">连接频率：</label>' + connFrequency + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">操作系统：</label>' + osName + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">CPU使用率：</label>' + cpuUserPercent + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">内存使用率：</label>' + menUsedPercent + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">服务器负载：</label>' + loadAverage + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">下行速率：</label>' + downloadBps + '</div>' +
                            '<div class="layui-col-md12" style="float: none;"><label class="label-font-weight">上行速率：</label>' + uploadBps + '</div>' +
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

            // 访问服务器详情页面
            function serverDetail(ip) {
                let infoLayerIndex = l.open({
                    type: 2,
                    title: '服务器详情（IP：' + ip + '）',
                    content: ctxPath + 'monitor-server/server-detail?ip=' + ip,
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
                // 先获取服务器信息，然后再获取拓扑图信息
                getMonitorServer2Map(() => {
                    getServerTopologyInfo();
                });
            }, 1000 * 15);

        }
    );
    e('topologyServer', {});
});