/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['admin', 'form'], function () {
        var admin = layui.admin, $ = layui.$;

        // 发送ajax请求，获取网络设备系统信息
        function getNetworkdDeviceSysInfo() {
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-network-device-sys/get-network-device-sys-info',
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
                    // 运行时间
                    var sysUpTime = isEmpty(data.sysUpTime) ? '' : data.sysUpTime;
                    // 技术支持
                    var sysContact = isEmpty(data.sysContact) ? '' : data.sysContact;
                    // 系统名称
                    var sysName = isEmpty(data.sysName) ? '' : data.sysName;
                    // 物理位置
                    var sysLocation = isEmpty(data.sysLocation) ? '' : data.sysLocation;
                    // 系统描述
                    var sysDescr = isEmpty(data.sysDescr) ? '' : data.sysDescr;
                    // 服务类型
                    var sysServices = isEmpty(data.sysServices) ? '' : data.sysServices;
                    var html = '<div class="layui-col-md3"><label class="label-font-weight">运行时间：</label>' + sysUpTime + '</div>' + //
                        '       <div class="layui-col-md3"><label class="label-font-weight">技术支持：</label>' + sysContact + '</div>' + //
                        '       <div class="layui-col-md3"><label class="label-font-weight">系统名称：</label>' + sysName + '</div>' + //
                        '       <div class="layui-col-md3"><label class="label-font-weight">物理位置：</label>' + sysLocation + '</div>' + //
                        '       <div class="layui-col-md12"><label class="label-font-weight">系统描述：</label>' + sysDescr + '</div>' + //
                        '       <div class="layui-col-md12"><label class="label-font-weight">服务类型：</label>' + sysServices + '</div>';
                    $('#sys').empty().append(html);
                }
            });
        }

        // 执行ajax请求
        function execute() {
            // 发送ajax请求，获取网络设备系统信息
            getNetworkdDeviceSysInfo();
        }

        // 页面加载后第一次执行
        execute();
        // 每30秒刷新一次
        window.setInterval(function () {
            execute();
        }, 1000 * 30);
    });
    e('networkDeviceDetailSys', {});
});