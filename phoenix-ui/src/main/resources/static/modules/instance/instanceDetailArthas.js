/** layuiAdmin.std-v2020.4.1 LPPL License By 皮锋 */
;layui.define(['admin', 'element', 'form', 'layer'], function (e) {
    var $ = layui.$, layer = layui.layer;
    // 协议
    var protocol = window.location.protocol;
    // 端口号
    var port = window.location.port;
    // 主机地址
    var hostname = window.location.hostname;
    // http协议对应ws协议
    if (protocol.includes("http:")) {
        protocol = 'ws:';
        if (isEmpty(port)) {
            port = 80;
        }
    }
    // https协议对应wss协议
    else if (protocol.includes("https:")) {
        protocol = 'wss:';
        if (isEmpty(port)) {
            port = 443;
        }
    }
    // 设置到页面表单
    $('#websocketIp').val(hostname);
    $('#websocketPort').val(port);
    // websocket对象
    var websocket;
    // 间隔读取键
    let intervalReadKey = -1;
    // Terminal对象是否可用
    var terminalAvailable = false;
    // 创建 Terminal 对象
    var term = new Terminal({
        // cols: parseInt($('#terminal').width() / 9), //列数
        // rows: parseInt(($(document).height() - 400) / 12), // 行数
        cursorStyle: 'underline', //光标样式
        cursorBlink: true, // 光标闪烁
        // convertEol: true, //启用时，光标将设置为下一行的开头
        disableStdin: true, //是否应禁用输入
        // scrollback: 800, // 回滚
        tabStopWidth: 8, // 制表宽度
        screenKeys: true,
        theme: {
            foreground: '#ffffff', //字体
            background: '#060101', //背景色
            cursor: 'help', //设置光标
            lineHeight: 16
        }
    });
    const fitAddon = new FitAddon.FitAddon();
    fitAddon.activate(term);

    // 监听输入的内容，并把内容发送给arthas
    term.on('data', function (data) {
        if (isNotEmpty(websocket)) {
            websocket.send(JSON.stringify({action: 'read', data: data}));
        }
    });

    // 点击连接
    $('#connect').click(function () {
        if (isNotEmpty(websocket)) {
            layer.msg(I18nUtils.t('instanceDetail.arthas.connected'), {icon: 6});
            return;
        }
        var websocketIp = $('#websocketIp').val();
        var websocketPort = $('#websocketPort').val();
        if (isEmpty(websocketIp)) {
            layer.msg(I18nUtils.t('instanceDetail.arthas.errorIpEmpty'), {icon: 5, shift: 6});
            return;
        }
        if (isEmpty(websocketPort)) {
            layer.msg(I18nUtils.t('instanceDetail.arthas.errorPortEmpty'), {icon: 5, shift: 6});
            return;
        }
        var wsUrl = protocol + '//' + websocketIp
            + ':' + websocketPort
            + '/' + location.pathname.split('/')[1]
            + '/websocket/relay/arthas?method=connectArthas&id=arthas_' + instanceId;
        //创建一个websocket实例
        websocket = new WebSocket(wsUrl);
        // 错误
        websocket.onerror = function () {
            websocket.close();
            websocket = undefined;
            layer.msg(I18nUtils.t('instanceDetail.arthas.errorConnect'), {icon: 5, shift: 6});
        };
        //打开连接websocket
        websocket.onopen = function (evt) {
            // 不禁用输入
            term.setOption('disableStdin', false);
            // 每30秒读一次
            intervalReadKey = window.setInterval(function () {
                if (isNotEmpty(websocket) && websocket.readyState === 1) {
                    websocket.send(JSON.stringify({action: 'read', data: ''}));
                }
            }, 30000);
        };

        //接受到数据
        websocket.onmessage = function (evt) {
            // 把接收的数据写到这个插件的屏幕上
            if (evt.type === 'message') {
                var data = evt.data;
                term.write(data);
                // 返回初始化数据
                if (data.indexOf('arthas_' + instanceId) > 0) {
                    // 授权登录
                    websocket.send(JSON.stringify({action: 'read', data: 'auth ' + instanceId + '\r'}));
                    // 让arthas返回的数据宽高匹配xterm面板的大小
                    websocket.send(JSON.stringify({action: 'resize', cols: term.cols, rows: term.rows}));
                }
            }
        };
        // 关闭
        websocket.onclose = function (message) {
            // 清除事件处理器
            websocket.onmessage = null;
            websocket.onerror = null;
            websocket.onopen = null;
            websocket.onclose = null;
            // 解除引用
            websocket = null;
            // 清理定时器
            if (intervalReadKey !== -1) {
                window.clearInterval(intervalReadKey);
                intervalReadKey = -1;
            }
            if (isNotEmpty(message.reason)) {
                layer.msg(message.reason, {icon: 5, shift: 6});
            }
        };
    });

    // 点击断开连接
    $('#disconnect').click(function () {
        try {
            if (isNotEmpty(websocket)) {
                websocket.close();
                // 清除事件处理器
                websocket.onmessage = null;
                websocket.onerror = null;
                websocket.onopen = null;
                websocket.onclose = null;
                // 解除引用
                websocket = null;
                // 清理定时器
                if (intervalReadKey !== -1) {
                    window.clearInterval(intervalReadKey);
                    intervalReadKey = -1;
                }
                // 重置
                term.reset();
                layer.msg(I18nUtils.t('instanceDetail.arthas.disconnectSuccess'), {icon: 6});
            } else {
                layer.msg(I18nUtils.t('instanceDetail.arthas.notConnected'), {icon: 5, shift: 6});
            }
        } catch {
            layer.msg(I18nUtils.t('instanceDetail.arthas.notConnected'), {icon: 5, shift: 6});
        }
    });

    // 改变窗口大小
    window.addEventListener('resize', function () {
        // 全屏显示
        fitAddon.fit();
        if (isNotEmpty(websocket) && websocket.readyState === 1) {
            // 让arthas返回的数据宽高匹配xterm面板的大小
            websocket.send(JSON.stringify({action: 'resize', cols: term.cols, rows: term.rows}));
        }
    }, {capture: true});

    e('instanceDetailArthas', {
        // 初始化
        init: function () {
            // arthas所在tab容器，Terminal对象不可用（第一次打开这个tab页面）
            if (terminalAvailable === false) {
                // 创建实例
                term.open(document.getElementById('terminal'));
                // 重新设置 cols和rows
                // term.resize(parseInt($('#terminal').width() / 9), parseInt(($(document).height() - 400) / 12) + 1);
                // Terminal对象可用
                terminalAvailable = true;
                term._initialized = true;
                // 全屏显示
                fitAddon.fit();
            }
        },
        // tab页面切换调用方法
        tabSwitch: function () {
            this.init();
        }
    });
});