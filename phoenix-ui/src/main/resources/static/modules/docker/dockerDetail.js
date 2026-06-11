/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(function (e) {
    layui.use(['admin', 'element', 'layer'], function () {
        var admin = layui.admin, $ = layui.$;

        // 根据条件获取docker信息
        function getMonitorDockerInfo() {
            // 弹出loading框
            // var loadingIndex = layer.load(1, {
            //     shade: [0.1, '#fff'] //0.1透明度的白色背景
            // });
            admin.req({
                type: 'get',
                url: layui.setter.base + 'monitor-docker/get-monitor-docker-info',
                dataType: 'json',
                contentType: 'application/json;charset=utf-8',
                headers: {
                    "X-CSRF-TOKEN": tokenValue
                },
                data: {
                    id: id, // docker主键ID
                },
                success: function (result) {
                    var data = result.data;
                    var serverIp = data.serverIp;
                    var architecture = data.architecture;
                    var containers = data.containers;
                    var containersStopped = data.containersStopped;
                    var containersPaused = data.containersPaused;
                    var containersRunning = data.containersRunning;
                    var isDebug = String(data.isDebug) === '1' ? '是' : '否';
                    var dockerRootDir = data.dockerRootDir;
                    var images = data.images + '（包含中间映像层）';
                    var kernelVersion = data.kernelVersion;
                    var isMemoryLimit = String(data.isMemoryLimit) === '1' ? '是' : '否';
                    var memTotalStr = data.memTotalStr;
                    var serverVersion = data.serverVersion;
                    var cpuNum = data.cpuNum;
                    var eventsListenerNum = data.eventsListenerNum;
                    var rawValues = data.rawValues;
                    var isOnline = String(data.isOnline) === '1' ? '在线' : '离线';
                    var offlineCount = data.offlineCount;
                    var monitorEnv = isEmpty(data.monitorEnv) ? '' : data.monitorEnv;
                    var monitorGroup = isEmpty(data.monitorGroup) ? '' : data.monitorGroup;
                    var dockerSummary = isEmpty(data.dockerSummary) ? '' : data.dockerSummary;
                    var insertTime = data.insertTime;
                    var updateTime = data.updateTime;
                    var serviceHtml = '<div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">服务器IP：</label>' + serverIp +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">内核版本：</label>' + kernelVersion +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">架构：</label>' + architecture +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">CPU核数：</label>' + cpuNum +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">docker版本：</label>' + serverVersion +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">debug模式：</label>' + isDebug +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">docker根目录：</label>' + dockerRootDir +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">监听事件数：</label>' + eventsListenerNum +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">限制内存大小：</label>' + isMemoryLimit +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">内存总大小：</label>' + memTotalStr +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">状态：</label>' + isOnline +
                        '              </div>' +
                        '              <div class="layui-col-md3">' +
                        '                   <label class="label-font-weight">离线次数：</label>' + offlineCount +
                        '              </div>';
                    $('#service').empty().append(serviceHtml);
                    var containerHtml = '<div class="layui-col-md3">' +
                        '                     <label class="label-font-weight">容器数：</label>' + containers +
                        '                </div>' +
                        '                <div class="layui-col-md3">' +
                        '                     <label class="label-font-weight">停止容器数：</label>' + containersStopped +
                        '                </div>' +
                        '                <div class="layui-col-md3">' +
                        '                     <label class="label-font-weight">暂停容器数：</label>' + containersPaused +
                        '                </div>' +
                        '                <div class="layui-col-md3">' +
                        '                     <label class="label-font-weight">运行容器数：</label>' + containersRunning +
                        '                </div>';
                    $('#container').empty().append(containerHtml);
                    var imageHtml = '<div class="layui-col-md3">' +
                        '                 <label class="label-font-weight">镜像数：</label>' + images +
                        '            </div>';
                    $('#image').empty().append(imageHtml);
                    var managerHtml = '<div class="layui-col-md4">' +
                        '                   <label class="label-font-weight">环境：</label>' + monitorEnv +
                        '              </div>' +
                        '              <div class="layui-col-md4">' +
                        '                   <label class="label-font-weight">分组：</label>' + monitorGroup +
                        '              </div>' +
                        '              <div class="layui-col-md12">' +
                        '                   <label class="label-font-weight">描述：</label>' + dockerSummary +
                        '              </div>' +
                        '              <div class="layui-col-md4">' +
                        '                   <label class="label-font-weight">新增时间：</label>' + insertTime +
                        '              </div>' +
                        '              <div class="layui-col-md4">' +
                        '                   <label class="label-font-weight">更新时间：</label>' + updateTime +
                        '              </div>';
                    $('#manager').empty().append(managerHtml);
                    var attributeHtml = '<div class="layui-col-md12">' + rawValues + '</div>';
                    $('#attribute').empty().append(attributeHtml);
                    // 关闭loading框
                    // layer.close(loadingIndex);
                },
                error: function () {
                    // 关闭loading框
                    // layer.close(loadingIndex);
                }
            });
        }

        // 执行ajax请求
        function execute() {
            // 发送ajax请求，获取运行时数据
            getMonitorDockerInfo();
        }

        // 每30秒刷新一次
        window.setInterval(function () {
            execute();
        }, 1000 * 30);
    });
    e('dockerDetail', {});
});