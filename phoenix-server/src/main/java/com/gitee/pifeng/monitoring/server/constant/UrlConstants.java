package com.gitee.pifeng.monitoring.server.constant;

/**
 * <p>
 * URL
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月6日 下午3:34:20
 */
@Deprecated
public final class UrlConstants {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2020/10/27 13:26
     */
    private UrlConstants() {
    }

    /**
     * 发送命令包URL地址
     */
    public static final String COMMAND_ISSUING_URL = "/command-issuing/accept-command-package";

}
