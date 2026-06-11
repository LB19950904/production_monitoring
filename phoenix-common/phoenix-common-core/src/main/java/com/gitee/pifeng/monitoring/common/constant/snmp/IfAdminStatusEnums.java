package com.gitee.pifeng.monitoring.common.constant.snmp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * 网络接口的管理状态枚举
 * </p>
 *
 * <a href="http://www.net-snmp.org/docs/mibs/interfaces.html?spm=5176.28103460.0.0.65225d27kPqrh3">
 * 参考文档：http://www.net-snmp.org/docs/mibs/interfaces.html?spm=5176.28103460.0.0.65225d27kPqrh3
 * </a>
 *
 * @author 皮锋
 * @custom.date 2022/11/10 17:40
 */
@Getter
@ToString
@AllArgsConstructor
public enum IfAdminStatusEnums {

    UP(1, "up", "启用"),

    DOWN(2, "down", "禁用"),

    TESTING(3, "testing", "测试");

    /**
     * 网络接口的管理状态ID
     */
    private final int id;

    /**
     * 网络接口的管理状态名字（英文）
     */
    private final String nameEn;

    /**
     * 网络接口的管理状态名字（中文）
     */
    private final String nameCn;

    /**
     * <p>
     * 网络接口的管理状态ID转中文名字
     * </p>
     *
     * @param id 网络接口的管理状态ID
     * @return 网络接口的管理状态中文名字
     * @author 皮锋
     * @custom.date 2024/11/10 17:53
     */
    public static String getNameCnById(int id) {
        for (IfAdminStatusEnums status : IfAdminStatusEnums.values()) {
            if (status.getId() == id) {
                return status.getNameCn() + "(" + status.getNameEn() + ")";
            }
        }
        return null;
    }

}
