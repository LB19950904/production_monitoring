/*!
 * 描述：封装jtopo公共业务
 * 版本：v 1.0.0
 * 作者：皮锋
 * 修改时间：2022/11/27 14:25
 */
;
(function ($, Jtopo, w, d) {

    // 严格模式
    "use strict";

    // 定义一些api，这里确定了插件的名称
    window.commonJtopo = {
        /**
         * 添加节点对象
         * @param layer 只用作展示的层，每个层对应一个Canvas
         * @param text 文本
         * @param x 坐标x
         * @param y 坐标y
         * @param width 宽度
         * @param height 高度
         * @param attribute 属性
         * @param userData 自定义属性
         * @returns {n|gd|op}
         */
        addNode: function (layer, text, x, y, width, height, attribute, userData) {
            var node = new Jtopo.Node(text, x, y, width, height);
            if (isNotEmpty(attribute) && isJson(attribute)) {
                if (isNotEmpty(attribute.imageSrc)) {
                    node.setImage(attribute.imageSrc, true);
                }
                if (isNotEmpty(attribute.styles)) {
                    node.setStyles(attribute.styles);
                }
            }
            if (isNotEmpty(userData) && isJson(userData)) {
                node.userData = userData;
            }
            layer.addChild(node);
            return node;
        },

        /**
         * 更新节点对象
         * @param node 节点对象
         * @param text 文本
         * @param x 坐标x
         * @param y 坐标y
         * @param width 宽度
         * @param height 高度
         * @param attribute 属性
         * @param userData 自定义属性
         * @returns {*}
         */
        updateNode: function (node, text, x, y, width, height, attribute, userData) {
            if (isNotEmpty(text)) {
                node.text = text;
            }
            if (isNotEmpty(x)) {
                node.x = x;
            }
            if (isNotEmpty(y)) {
                node.y = y;
            }
            if (isNotEmpty(width)) {
                node.width = width;
            }
            if (isNotEmpty(height)) {
                node.height = height;
            }
            if (isNotEmpty(attribute) && isJson(attribute)) {
                if (isNotEmpty(attribute.imageSrc)) {
                    node.setImage(attribute.imageSrc, true);
                }
                if (isNotEmpty(attribute.styles)) {
                    node.setStyles(attribute.styles);
                }
            }
            if (isNotEmpty(userData) && isJson(userData)) {
                node.userData = userData;
            }
            return node;
        },

        /**
         * 添加连线对象
         * @param layer 只用作展示的层，每个层对应一个Canvas
         * @param text 文本
         * @param start 开始节点对象
         * @param end 结束节点对象
         * @param beginPosition 开始节点对象的‘定位点’
         * @param endPosition 结束节点对象的‘定位点’
         * @param attribute 属性
         * @param userData 自定义属性
         * @returns {lG}
         */
        addLink: function (layer, text, start, end, beginPosition, endPosition, attribute, userData) {
            var link = new Jtopo.Link(text, start, end, beginPosition, endPosition);
            var textNode = new Jtopo.TextNode(text);
            link.setLabel(textNode);
            if (isNotEmpty(attribute) && isJson(attribute)) {
                if (isNotEmpty(attribute.styles)) {
                    // link样式
                    link.setStyles(attribute.styles);
                }
                if (isNotEmpty(attribute.styles) && isNotEmpty(attribute.styles.labelStyles)) {
                    // label样式
                    link.label.setStyles(attribute.styles.labelStyles);
                    if (isNotEmpty(attribute.styles.labelStyles.textOffsetY)) {
                        // label文本垂直偏移量
                        textNode.textOffsetY = attribute.styles.labelStyles.textOffsetY;
                    }
                }
            }
            if (isNotEmpty(userData) && isJson(userData)) {
                link.userData = userData;
            }
            layer.addChild(link);
            return link;
        },

        /**
         * 添加弧线连线对象
         * @param layer 只用作展示的层，每个层对应一个Canvas
         * @param text 文本
         * @param start 开始节点对象
         * @param end 结束节点对象
         * @param beginPosition 开始节点对象的‘定位点’
         * @param endPosition 结束节点对象的‘定位点’
         * @param attribute 属性
         * @param hasAnimation 是否为连线添加动画
         * @param userData 自定义属性
         * @returns {lG}
         */
        addCurveLink: function (layer, text, start, end, beginPosition, endPosition, attribute, hasAnimation, userData) {
            var link = new Jtopo.CurveLink(text, start, end, beginPosition, endPosition);
            var textNode = new Jtopo.TextNode(text);
            link.setLabel(textNode);
            if (isNotEmpty(attribute) && isJson(attribute)) {
                if (isNotEmpty(attribute.styles)) {
                    // link样式
                    link.setStyles(attribute.styles);
                }
                if (isNotEmpty(attribute.styles) && isNotEmpty(attribute.styles.labelStyles)) {
                    // label样式
                    link.label.setStyles(attribute.styles.labelStyles);
                    if (isNotEmpty(attribute.styles.labelStyles.textOffsetY)) {
                        // label文本垂直偏移量
                        textNode.textOffsetY = attribute.styles.labelStyles.textOffsetY;
                    }
                }
            }
            if (isNotEmpty(userData) && isJson(userData)) {
                link.userData = userData;
            }
            layer.addChild(link);
            if (hasAnimation) {
                commonJtopo.animationForLink(link, layer);
            }
            return link;
        },

        /**
         * 为连线添加动画
         * @param link 连线对象
         * @param layer 只用作展示的层，每个层对应一个Canvas
         */
        animationForLink: function (link, layer) {
            // 获取样式
            var style = link.style;
            var offset = 0;
            animation();

            function animation() {
                if (++offset > 16) {
                    offset = 0;
                }
                // 添加样式
                style.lineDashOffset = offset;
                link.setStyles(style);
                layer.update();
                requestAnimationFrame(animation);
            }
        }

    };
}(jQuery, jtopo, window, document));