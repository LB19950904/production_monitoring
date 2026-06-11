/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['admin', 'form'], function () {
        var admin = layui.admin, $ = layui.$;

        // 发送ajax请求，获取网络设备接口信息
        function getNetworkdDeviceIfInfo() {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-network-device-if/get-network-device-if-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    ip: ip // 网络设备IP
                },
                success: function (result) {
                    var data = result.data;
                    var html = '';
                    for (var i = 0; i < data.length; i++) {
                        var obj = data[i];
                        // 索引号
                        var ifIndex = obj.ifIndex;
                        // 描述
                        var ifDescr = obj.ifDescr;
                        // 类型
                        var ifType = obj.ifType;
                        // 最大传输单元(MTU)
                        var ifMtu = obj.ifMtu;
                        // 速率(bps)
                        var ifSpeed = obj.ifSpeed;
                        // 物理地址
                        var ifPhysAddress = obj.ifPhysAddress;
                        // 管理状态
                        var ifAdminStatus = obj.ifAdminStatus;
                        // 操作状态
                        var ifOperStatus = obj.ifOperStatus;
                        // 接收字节数
                        var ifInOctets = obj.ifInOctets;
                        // 发送字节数
                        var ifOutOctets = obj.ifOutOctets;
                        // 网络接口实时接收速率（以比特/秒为单位）
                        var ifInRealTimeSpeed = obj.ifInRealTimeSpeed;
                        // 网络接口实时发送速率（以比特/秒为单位）
                        var ifOutRealTimeSpeed = obj.ifOutRealTimeSpeed;
                        html += '<div class="layui-col-md3"><label class="label-font-weight">索引号：</label>' + ifIndex + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">描述：</label>' + ifDescr + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">类型：</label>' + ifType + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">最大传输单元(MTU)：</label>' + ifMtu + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">速率(bps)：</label>' + ifSpeed + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">物理地址：</label>' + ifPhysAddress + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">管理状态：</label>' + ifAdminStatus + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">操作状态：</label>' + ifOperStatus + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">接收字节数：</label>' + ifInOctets + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">接收速率(bps)：</label>' + ifInRealTimeSpeed + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">发送字节数：</label>' + ifOutOctets + '</div>' + //
                            '    <div class="layui-col-md3"><label class="label-font-weight">发送速率(bps)：</label>' + ifOutRealTimeSpeed + '</div>';
                        if (i !== data.length - 1) {
                            html += '<hr class="layui-bg-gray hr-padding">';
                        }
                    }
                    $('#if').empty().append(html);
                }
            });
        }

        // 执行ajax请求
        function execute() {
            // 发送ajax请求，获取网络设备接口信息
            getNetworkdDeviceIfInfo();
        }

        // 页面加载后第一次执行
        execute();
        // 每30秒刷新一次
        window.setInterval(function () {
            execute();
        }, 1000 * 30);
    });
    e('networkDeviceDetailIf', {});
});