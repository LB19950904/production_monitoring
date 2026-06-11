/**
 * layui 全屏控制插件（带 UI 按钮 + 可选回调钩子）
 *
 * 功能：
 * - 自动在页面右上角创建一个全屏/退出全屏按钮（默认样式）
 * - 用户点击可切换整个页面（<html>）的全屏状态
 * - 提供独立的样式设置方法 `css()` 和回调注册方法 `on()`
 *
 * 使用方式：
 *
 * ────────────────────────────────────────
 * 方式一：仅启用默认 UI 按钮（最简单）
 * ────────────────────────────────────────
 * 在主入口或当前页面引入模块即可：
 *
 *   layui.use(['fullscreen']);
 *
 * 效果：页面右上角出现默认样式的全屏按钮，点击即可全屏/退出。
 *
 * ────────────────────────────────────────
 * 方式二：自定义按钮样式 + 监听状态变化
 * ────────────────────────────────────────
 * layui.use(['fullscreen'], function(mods) {
 *     // 1. 自定义按钮样式（可选）
 *     mods.fullscreen.css({
 *         top: '20px',
 *         right: '30px',
 *         color: '#333'
 *     });
 *
 *     // 2. 注册回调（可选）
 *     mods.fullscreen.on({
 *         onEnter: function() { console.log('进入全屏'); },
 *         onExit:  function() { console.log('退出全屏'); }
 *     });
 * });
 *
 * ────────────────────────────────────────
 * 方式三：通过 layui.fullscreen 调用（需确保模块已加载）
 * ────────────────────────────────────────
 * layui.fullscreen.css({ top: '10px' });
 * layui.fullscreen.on({
 *     onEnter: function() { },
 *     onExit:  function() { }
 * });
 *
 * 注意：
 * - 外部无法主动触发全屏/退出，只能监听结果
 * - 全屏目标为整个 <html>，如需指定容器，请修改 `target` 变量
 * - 模块加载时会自动创建默认样式按钮，无需手动调用 css()
 *
 * @license LPPL License By 皮锋
 * @version layuiAdmin.std-v2020.4.1
 */
;layui.define(['admin', 'element', 'form', 'layer'], function (e) {
    var $ = layui.$, layer = layui.layer;
    // 全屏目标元素：为整个 HTML 文档根节点
    var target = $(document.documentElement);
    // 全屏按钮ID
    var btnId = 'layui-fullscreen-btn-my';
    // 回调钩子
    var onEnterCallback = null;   // 进入全屏后的回调
    var onExitCallback = null;    // 退出全屏后的回调

    // 判断当前页面是否处于浏览器全屏状态
    var isFullscreen = function () {
        // !! 的作用是将结果强制转为布尔值（true / false）
        return !!(
            // 标准全屏 API（Chrome 71+、Edge 79+、Firefox 64+、Safari 13+）
            document.fullscreenElement ||
            // WebKit 内核（旧版 Safari、旧版 Chrome）
            document.webkitFullscreenElement ||
            // Firefox（旧版）
            document.mozFullScreenElement ||
            // IE11 / 旧版 Edge
            document.msFullscreenElement
        );
    };

    // 请求将指定 DOM 元素进入浏览器原生全屏模式
    var requestFullscreen = function (el) {
        // 标准 Fullscreen API（Chrome 71+、Firefox 64+、Edge 79+、Safari 13+）
        if (el.requestFullscreen) {
            return el.requestFullscreen();
        }
        // WebKit 内核（Safari 和旧版 Chrome），Element.ALLOW_KEYBOARD_INPUT 是 Safari 特有参数，允许在全屏时接收键盘输入（如终端操作）
        else if (el.webkitRequestFullscreen) {
            return el.webkitRequestFullscreen(Element.ALLOW_KEYBOARD_INPUT);
        }
        // Firefox（旧版本，注意方法名是 requestFullScreen，大写 S）
        else if (el.mozRequestFullScreen) {
            return el.mozRequestFullScreen();
        }
        // IE11 和旧版 Edge（已基本淘汰，但保留以兼容遗留环境）
        else if (el.msRequestFullscreen) {
            return el.msRequestFullscreen();
        }
        // 如果所有 API 都不存在，说明当前浏览器不支持全屏功能
        return Promise.reject(new Error('不支持全屏'));
    };

    // 退出当前浏览器的全屏模式
    var exitFullscreen = function () {
        // 标准 Fullscreen API（Chrome 71+、Firefox 64+、Edge 79+、Safari 13+）
        if (document.exitFullscreen) {
            document.exitFullscreen();
        }
        // WebKit 内核（Safari 和旧版 Chrome）
        else if (document.webkitExitFullscreen) {
            document.webkitExitFullscreen();
        }
        // Firefox（旧版本，注意方法名是 cancelFullScreen，大写 S）
        else if (document.mozCancelFullScreen) {
            document.mozCancelFullScreen();
        }
        // IE11 和旧版 Edge（已基本淘汰，但保留以兼容遗留环境）
        else if (document.msExitFullscreen) {
            document.msExitFullscreen();
        }
    };

    // 更新按钮文字
    var updateBtnText = function () {
        var text = isFullscreen() ? '🔙 退出全屏' : '🖥️ 全屏';
        $('#' + btnId).html(text);
    };

    // 全屏状态变更事件处理器
    var fullscreenHandler = function () {
        var fullscreen = isFullscreen();
        // 更新按钮文字
        updateBtnText();
        // 触发对应回调
        if (fullscreen && typeof onEnterCallback === 'function') {
            onEnterCallback();
        } else if (!fullscreen && typeof onExitCallback === 'function') {
            onExitCallback();
        }
    };

    // 定义全屏事件名称列表
    var fullscreenEvents = ['fullscreenchange', 'webkitfullscreenchange', 'mozfullscreenchange', 'MSFullscreenChange'];
    // 先移除旧监听（避免重复）
    fullscreenEvents.forEach(ev => document.removeEventListener(ev, fullscreenHandler));
    // 监听全屏状态变化
    fullscreenEvents.forEach(ev => document.addEventListener(ev, fullscreenHandler));

    // 初始化全屏按钮
    var initFullscreenBtn = function (style) {
        style = style || {};
        // 合并默认样式与用户样式
        var defaultStyle = {
            position: 'fixed',
            top: '16px',
            right: '25px',
            zIndex: 9999,
            padding: '8px 12px',
            cursor: 'pointer',
            color: '#666666',
            transition: 'all 0.3s ease'
        };
        var finalStyle = $.extend({}, defaultStyle, style);
        // 创建按钮并绑定事件
        var fullscreenButton = $('#' + btnId);
        if (fullscreenButton.length === 0) {
            // 如果不存在，就创建一个新的 div 元素
            var newButton = $('<div id="' + btnId + '">🖥️ 全屏</div>');
            // 设置内联样式（直接写 style 属性）
            newButton.css(finalStyle)
                // 鼠标指针首次进入
                .on('mouseenter', function () {
                    this.style.backgroundColor = '#f0f0f0';
                })
                // 鼠标指针完全离开
                .on('mouseleave', function () {
                    this.style.backgroundColor = '';
                })
                // 点击事件
                .on('click', function () {
                    if (isFullscreen()) {
                        exitFullscreen();
                    } else {
                        // 请求将 Arthas 面板（target 元素）进入全屏
                        // target[0] 获取原生 DOM 元素
                        var promise = requestFullscreen(target[0]);
                        // 检查返回值是否为 Promise（现代浏览器支持）
                        // 并确保其具有 .catch 方法，避免在旧浏览器中报错
                        if (promise && typeof promise.catch === 'function') {
                            // 捕获全屏请求被拒绝或失败的情况（例如：缺少 allow="fullscreen"）
                            promise.catch(function (err) {
                                layer.msg('全屏被拒绝，请确保页面通过 allow="fullscreen" 加载', {icon: 5, shift: 6});
                            });
                        }
                    }
                });
            // 把这个按钮添加到 body 的末尾
            $('body').append(newButton);
            // 更新按钮文字
            updateBtnText();
        } else {
            // 按钮已存在：仅更新样式
            fullscreenButton.css(finalStyle);
        }
    }

    // 自动初始化默认按钮
    initFullscreenBtn();

    // ===== 对外只暴露「注册回调」的接口 =====
    var fullscreenHooks = {
        /**
         * 设置全屏按钮的 CSS 样式
         *
         * @param {Object} style - 要应用的 CSS 样式对象，例如 { top: '10px', color: '#fff' }
         */
        css: function (style) {
            style = style || {};
            // 如果传了 style，就更新按钮样式
            initFullscreenBtn(style);
        },
        /**
         * 注册全屏状态变更的回调函数
         *
         * @param {Object} options - 配置选项
         * @param {Function} [options.onEnter] - 进入全屏后触发
         * @param {Function} [options.onExit]  - 退出全屏后触发
         */
        on: function (options) {
            options = options || {};
            if (typeof options.onEnter === 'function') {
                onEnterCallback = options.onEnter;
            }
            if (typeof options.onExit === 'function') {
                onExitCallback = options.onExit;
            }
        }
    };

    // 挂载到 layui，方便外部注册（非强制）
    layui.fullscreen = fullscreenHooks;

    e('fullscreen', fullscreenHooks);
});