/** layuiAdmin.std-v2020.4.1 LPPL License By https://www.layui.com/admin/ */
;layui.define(['echarts'], function (exports) {
    // echarts-gl.min.js已经在HTML中直接引入，或者echarts-gl已经挂载到window对象
    // 这里我们直接引用全局的echarts-gl对象
    exports('echartsGl', window['echarts-gl']);
});