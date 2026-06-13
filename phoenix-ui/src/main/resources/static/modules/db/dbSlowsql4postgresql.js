/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(['index', 'admin', 'form', 'table', 'layer', 'laydate'], function (e) {
    // 是否第一次打开此tab容器
    var isFirstOpenThisTab = true;
    // layui对象
    var admin = layui.admin, form = layui.form,
        layer = layui.layer, laydate = layui.laydate, table = layui.table,
        device = layui.device();
    // table对象
    var tableObject = {
        elem: '#list-table-slowsql',
        url: ctxPath + 'db-slowsql4postgresql/get-slowsql-list?dbId=' + id,
        toolbar: '#list-table-slowsql-toolbar',
        request: {
            pageName: 'current',//页码的参数名称，默认：page
            limitName: 'size' //每页数据量的参数名，默认：limit
        },
        response: {
            statusName: 'code', //规定数据状态的字段名称，默认：code
            statusCode: 200,//规定成功的状态码，默认：0
            msgName: 'msg',//规定状态信息的字段名称，默认：msg
            countName: 'count', //规定数据总数的字段名称，默认：count
            dataName: 'data' //规定数据列表的字段名称，默认：data
        },
        parseData: function (res) { //res 即为原始返回的数据
            return {
                'code': res.code, //解析接口状态
                'msg': res.msg, //解析提示文本
                'count': res.data.total, //解析数据长度
                'data': res.data.records //解析数据列表
            };
        },
        cols: [
            [{
                type: 'checkbox',
                hide: !authority,
                fixed: (device.ios || device.android) ? false : 'left',
            }, {
                field: 'id',
                width: 100,
                title: '主键ID',
                sort: !0,
                hide: true
            }, {
                field: 'dbId',
                width: 100,
                title: '数据库表ID',
                sort: !0,
                hide: true
            }, {
                field: 'normalizeSqlText',
                title: 'SQL',
                minWidth: 800,
                sort: !0
            }, {
                field: 'dbName',
                title: '库名',
                minWidth: 80,
                sort: !0
            }, {
                field: 'command',
                title: '命令',
                minWidth: 80,
                sort: !0
            }, {
                field: 'state',
                title: '状态',
                minWidth: 80,
                sort: !0
            }, {
                field: 'executionTimeStr',
                title: '耗时',
                minWidth: 120,
                sort: !0
            }, {
                field: 'thresholdTimeStr',
                title: '慢SQL阈值',
                minWidth: 120,
                sort: !0
            }, {
                field: 'detectTime',
                title: '检测时间',
                minWidth: 170,
                sort: !0
            }, {
                title: '操作',
                width: authority ? 140 : 100,
                align: 'center',
                fixed: (device.ios || device.android) ? false : 'right',
                toolbar: '#list-table-slowsql-toolbar-detail'
            }]
        ],
        page: !0,
        limit: 15,
        limits: [10, 15, 20, 30, 40, 50, 60, 70, 80, 90, 100],
        height: (device.ios || device.android) ? 'full' : 'full-250'
    };
    // 渲染这个table
    table.render(tableObject);
    //监听搜索
    form.on('submit(list-table-slowsql-search)', function (data) {
        var field = data.field;
        //执行重载
        table.reload('list-table-slowsql', {
            where: field
        });
    });
    // 监听重置
    form.on('submit(list-table-slowsql-reset)', function (data) {
        var field = data.field;
        // 清空所有字段的值
        field = clearFields(field);
        //执行重载
        table.reload('list-table-slowsql', {
            where: field
        });
    });
    // 点击表头排序
    table.on('sort(list-table-slowsql)', function (obj) {
        //table.reload('list-table-slowsql', {
        //  initSort: obj
        //});
    });
    //头工具栏事件
    table.on('toolbar(list-table-slowsql)', function (obj) {
        // 批量删除
        if (obj.event === 'slowsqlBatchdel') {
            var checkStatus = table.checkStatus('list-table-slowsql'), checkData = checkStatus.data; //得到选中的数据
            if (checkData.length === 0) {
                return layer.msg('请选择数据');
            }
            layer.confirm('确定删除吗？', function (index) {
                // 弹出loading框
                var loadingIndex = layer.load(1, {
                    shade: [0.1, '#fff'] //0.1透明度的白色背景
                });
                admin.req({
                    type: 'delete',
                    url: ctxPath + 'db-slowsql4postgresql/delete-slowsql',
                    data: JSON.stringify(checkData.map(data => data.id)),
                    dataType: 'json',
                    contentType: 'application/json;charset=utf-8',
                    headers: {
                        "X-CSRF-TOKEN": tokenValue
                    },
                    success: function (result) {
                        var data = result.data;
                        if (data === webConst.SUCCESS) {
                            table.reload('list-table-slowsql'); //数据刷新
                            layer.msg('删除成功！', {icon: 6});
                        } else {
                            layer.msg('删除失败！', {icon: 5, shift: 6});
                        }
                        // 关闭loading框
                        layer.close(loadingIndex);
                    },
                    error: function () {
                        layer.msg('系统错误！', {icon: 5, shift: 6});
                        // 关闭loading框
                        layer.close(loadingIndex);
                    }
                });
            });
        }
        // 刷新
        if (obj.event === 'slowsqlBatchRefresh') {
            //数据刷新
            table.reload('list-table-slowsql');
        }
        // 清空
        if (obj.event === 'slowsqlCleanup') {
            layer.confirm('确定清空吗？', function (index) {
                admin.req({
                    type: 'delete',
                    url: ctxPath + 'db-slowsql4postgresql/cleanup-slowsql',
                    dataType: 'json',
                    contentType: 'application/json;charset=utf-8',
                    headers: {
                        "X-CSRF-TOKEN": tokenValue
                    },
                    success: function (result) {
                        var data = result.data;
                        if (data === webConst.SUCCESS) {
                            table.reload('list-table-slowsql'); //数据刷新
                            layer.msg('清空成功！', {icon: 6});
                        } else {
                            layer.msg('清空失败！', {icon: 5, shift: 6});
                        }
                    },
                    error: function () {
                        layer.msg('系统错误！', {icon: 5, shift: 6});
                    }
                });
            });
        }
    });
    //监听工具条
    table.on('tool(list-table-slowsql)', function (obj) {
        var data = obj.data;
        if (obj.event === 'slowsqlDel') {
            layer.confirm('确定删除吗？', function (index) {
                // 弹出loading框
                var loadingIndex = layer.load(1, {
                    shade: [0.1, '#fff'] //0.1透明度的白色背景
                });
                admin.req({
                    type: 'delete',
                    url: ctxPath + 'db-slowsql4postgresql/delete-slowsql',
                    data: JSON.stringify([data.id]),
                    dataType: 'json',
                    contentType: 'application/json;charset=utf-8',
                    headers: {
                        "X-CSRF-TOKEN": tokenValue
                    },
                    success: function (result) {
                        var data = result.data;
                        if (data === webConst.SUCCESS) {
                            obj.del();
                            table.reload('list-table-slowsql'); //数据刷新
                            layer.msg('删除成功！', {icon: 6});
                        } else {
                            layer.msg('删除失败！', {icon: 5, shift: 6});
                        }
                        // 关闭loading框
                        layer.close(loadingIndex);
                    },
                    error: function () {
                        layer.msg('系统错误！', {icon: 5, shift: 6});
                        // 关闭loading框
                        layer.close(loadingIndex);
                    }
                });
            });
        }
        if (obj.event === 'slowsqlInfo') {
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
                width = document.body.clientWidth * 0.8 + 'px';
                // 高度
                height = document.body.clientHeight * 0.8 + 'px';
            }
            var infoLayerIndex = layer.open({
                type: 2,
                title: '慢SQL详情',
                content: ctxPath + 'db-slowsql4postgresql/slowsql-detail?id=' + data.id,
                maxmin: true,
                shade: 0,
                moveOut: true,
                area: [width, height],
                success: function (layero, index) {
                    // 在回调方法中的第2个参数"index"表示的是当前弹窗的索引
                    // 通过layer.full方法将窗口放大
                    // layer.full(index);
                }
            });
            layer.full(infoLayerIndex);
        }
    });

    e('dbSlowsql4postgresql', {
        // tab页面切换调用方法
        tabSwitch: function () {
            // 慢SQL所在tab容器（第一次打开这个tab页面）
            if (isFirstOpenThisTab === true) {
                // 渲染日期选择插件
                laydate.render({
                    elem: '#detectTime',
                    type: 'date',
                    range: '~'
                });
                // 根据表格ID重置表格尺寸
                table.resize('list-table-slowsql');
                isFirstOpenThisTab = false;
            }
        }
    });
});