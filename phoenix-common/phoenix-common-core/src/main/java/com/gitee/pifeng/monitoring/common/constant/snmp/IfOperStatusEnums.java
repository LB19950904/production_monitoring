package com.gitee.pifeng.monitoring.common.constant.snmp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * 网络接口的操作状态枚举
 * </p>
 *
 * <a href="http://www.net-snmp.org/docs/mibs/interfaces.html?spm=5176.28103460.0.0.65225d27kPqrh3">
 * 参考文档：http://www.net-snmp.org/docs/mibs/interfaces.html?spm=5176.28103460.0.0.65225d27kPqrh3
 * </a>
 *
 * @author 皮锋
 * @custom.date 2022/11/10 16:01
 */
@Getter
@ToString
@AllArgsConstructor
public enum IfOperStatusEnums {

    UP(1, "up", "启用"),

    DOWN(2, "down", "禁用"),

    TESTING(3, "testing", "测试"),

    UNKNOWN(4, "unknown", "未知"),

    DORMANT(5, "dormant", "休眠"),

    NOT_PRESENT(6, "notPresent", "不存在"),

    LOWER_LAYER_DOWN(7, "lowerLayerDown", "下层故障");

    /**
     * 网络接口的操作状态ID
     */
    private final int id;

    /**
     * 网络接口的操作状态名字（英文）
     */
    private final String nameEn;

    /**
     * 网络接口的操作状态名字（中文）
     */
    private final String nameCn;

    /**
     * <p>
     * 网络接口的操作状态ID转中文名字
     * </p>
     *
     * @param id 网络接口的操作状态ID
     * @return 网络接口的操作状态中文名字
     * @author 皮锋
     * @custom.date 2024/11/10 18:01
     */
    public static String getNameCnById(int id) {
        for (IfOperStatusEnums status : IfOperStatusEnums.values()) {
            if (status.getId() == id) {
                return status.getNameCn() + "(" + status.getNameEn() + ")";
            }
        }
        return null;
    }

}
