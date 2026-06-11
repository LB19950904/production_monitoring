package com.gitee.pifeng.monitoring.common.constant.arthas;

/**
 * <p>
 * client和server之间通过 URI来通迅，在URI里定义了一个 method的参数，定义不同的行为
 * </p>
 *
 * @author hengyunabc
 * @custom.date 2020-10-22
 */
public class ArthasMethodConstants {

    /**
     * <pre>
     * client启动时注册的method
     *
     * ws://192.168.1.10:7777/ws?method=agentRegister
     *
     * server回应：
     *
     * response:/?method=agentRegister&amp;id=bvDOe8XbTM2pQWjF4cfw
     *
     * id不指定，则随机生成
     * </pre>
     */
    public static final String AGENT_REGISTER = "agentRegister";

    /**
     * <pre>
     * server通知client启动一个新的连接
     *
     * response:/?method=startTunnel&amp;id=bvDOe8XbTM2pQWjF4cfw&amp;clientConnectionId=AMku9EFz2gxeL2gedGOC
     * </pre>
     */
    public static final String START_TUNNEL = "startTunnel";

    /**
     * <pre>
     * browser通知server去连接client
     *
     * ws://192.168.1.10:7777/ws?method=connectArthas&amp;id=bvDOe8XbTM2pQWjF4cfw
     * </pre>
     */
    public static final String CONNECT_ARTHAS = "connectArthas";

    /**
     * <pre>
     * client收到startTunnel指令之后，以下面的URI新建一个连接：
     *
     * ws://127.0.0.1:7777/ws/?method=openTunnel&amp;clientConnectionId=AMku9EFz2gxeL2gedGOC&amp;id=bvDOe8XbTM2pQWjF4cfw
     * </pre>
     */
    public static final String OPEN_TUNNEL = "openTunnel";

    /**
     * <pre>
     * server向client请求http中转，比如访问 <a href="http://localhost:3658/arthas-output/xxx.html">http://localhost:3658/arthas-output/xxx.html</a>
     * </pre>
     */
    public static final String HTTP_PROXY = "httpProxy";

}
