/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['admin', 'element', 'flow', 'util'], function () {
        var admin = layui.admin, element = layui.element, $ = layui.$, util = layui.util, flow = layui.flow;
        flow.load({
            elem: '#docker-event-detail-flow-auto',//流加载容器
            done: function (page, next) { //执行下一页的回调
                admin.req({
                    type: 'get',
                    url: layui.setter.base + 'monitor-docker-event/get-monitor-docker-event-list',
                    dataType: 'json',
                    contentType: 'application/json;charset=utf-8',
                    headers: {
                        "X-CSRF-TOKEN": tokenValue
                    },
                    data: {
                        size: 10, //每页显示条数
                        current: page, //当前页
                        serverIp: serverIp, // docker主键ID
                        eventId: eventId //事件ID
                    },
                    success: function (result) {
                        var data = result.data;
                        var pages = data.pages;
                        var records = data.records;
                        var lis = [];
                        for (var i = 0; i < records.length; i++) {
                            var obj = records[i];
                            var _id = obj.id;
                            var happenTime = util.toDateString(new Date(obj.happenTime.replace(/-/g, '/')), 'yyyy年MM月dd日HH时mm分ss');
                            var serverIp = obj.serverIp;
                            var eventId = isNotEmpty(obj.eventId) ? obj.eventId : '';
                            var eventStatus = isNotEmpty(obj.eventStatus) ? obj.eventStatus : '';
                            var eventFrom = isNotEmpty(obj.eventFrom) ? obj.eventFrom : '';
                            var eventType = isNotEmpty(obj.eventType) ? obj.eventType : '';
                            var eventAction = isNotEmpty(obj.eventAction) ? obj.eventAction : '';
                            var eventAttribute = obj.eventAttribute;
                            // 解析属性
                            var eventAttributeObjName;
                            var h5;
                            if (isNotEmpty(eventAttribute)) {
                                var eventAttributeObj = $.parseJSON(eventAttribute);
                                eventAttributeObjName = eventAttributeObj.name;
                            }
                            if (isNotEmpty(eventAttributeObjName)) {
                                h5 = '<h5 class="layui-colla-title layui-colla-title-my">' + eventAction + '&nbsp;<em>"' + eventAttributeObjName + '"</em>&nbsp;' + eventType + '</h5>';
                            } else {
                                h5 = '<h5 class=" layui-colla-title layui-colla-title-my">' + eventAction + '&nbsp;' + eventType + '</h5>';
                            }
                            var html = '<li class="layui-timeline-item">' +
                                '           <i class="layui-icon layui-timeline-axis" ' + (_id === id ? 'style="color:#FF5722"' : '') + '></i>' +
                                '              <div class="layui-timeline-content layui-text">' +
                                '                   <h3 class="layui-timeline-title">' + happenTime + '</h3>' +
                                '                   <div class="layui-collapse">' +
                                '                       <div class="layui-colla-item">' +
                                '                            ' + h5 +
                                '                            <div class="layui-colla-content">' +
                                '                                 <ul style="word-break: break-all;">' +
                                '                                     <li>服务器IP：' + serverIp + '</li>' +
                                '                                     <li>事件ID：' + eventId + '</li>' +
                                '                                     <li>事件状态：' + eventStatus + '</li>' +
                                '                                     <li>事件来源：' + eventFrom + '</li>' +
                                '                                     <li>事件类型：' + eventType + '</li>' +
                                '                                     <li>事件动作：' + eventAction + '</li>' +
                                '                                     <li>事件属性：' + eventAttribute + '</li>' +
                                '                                 </ul>' +
                                '                            </div>' +
                                '                        </div>' +
                                '                   </div>' +
                                '               </div>' +
                                '         </li>';
                            lis.push(html);
                        }
                        //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
                        //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
                        next(lis.join(''), page < pages); //假设总页数为 10
                        // 重新渲染折叠面板
                        element.render('collapse');
                    },
                    error: function () {
                    }
                });
            }
        });
    });
    e('dockerEventDetail', {});
});