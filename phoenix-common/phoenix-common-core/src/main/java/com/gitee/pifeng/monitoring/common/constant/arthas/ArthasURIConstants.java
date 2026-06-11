package com.gitee.pifeng.monitoring.common.constant.arthas;

/**
 * <p>
 * URI 常量
 * </p>
 *
 * @author hengyunabc
 * @custom.date 2020-10-22
 */
public class ArthasURIConstants {

    /**
     * @see ArthasMethodConstants
     */
    public static final String METHOD = "method";

    /**
     * 响应
     */
    public static final String RESPONSE = "response";

    /**
     * agent id
     */
    public static final String ID = "id";

    /**
     * server用于区分不同client的内部id
     */
    public static final String CLIENT_CONNECTION_ID = "clientConnectionId";

    /**
     * server向client请求http代理时的目标url
     *
     * @see ArthasMethodConstants#HTTP_PROXY
     */
    public static final String TARGET_URL = "targetUrl";

    /**
     * 标识一次proxy请求，随机生成
     */
    public static final String PROXY_REQUEST_ID = "requestId";

    /**
     * proxy请求的返回值，base64编码
     */
    public static final String PROXY_RESPONSE_DATA = "responseData";

    /**
     * arthas版本
     */
    public static final String ARTHAS_VERSION = "arthasVersion";

    public static final String APP_NAME = "appName";

}
