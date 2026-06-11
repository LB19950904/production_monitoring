package com.gitee.pifeng.monitoring.agent.core;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.agent.business.server.service.*;
import com.gitee.pifeng.monitoring.common.domain.Docker;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.*;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.common.web.core.Invoker;
import com.gitee.pifeng.monitoring.common.web.core.InvokerHolder;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 方法执行助手
 * </p>
 *
 * @author 皮锋
 * @custom.date 2020年3月5日 上午10:59:29
 */
@Slf4j
public class MethodExecuteHandler {

    /**
     * 代理端包构造器
     */
    private static final AgentPackageConstructor AGENT_PACKAGE_CONSTRUCTOR = AgentPackageConstructor.getInstance();

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2020/10/27 13:26
     */
    private MethodExecuteHandler() {
    }

    /**
     * <p>
     * 向服务端发送心跳包
     * </p>
     *
     * @param heartbeatPackage 心跳包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2020年3月5日 上午11:01:46
     */
    @Deprecated
    public static BaseResponsePackage sendHeartbeatPackage2Server(HeartbeatPackage heartbeatPackage) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(IHeartbeatService.class, "sendHeartbeatPackage");
        // 执行命令，返回执行结果
        return execute(invoker, heartbeatPackage);
    }

    /**
     * <p>
     * 向服务端发送告警包
     * </p>
     *
     * @param alarmPackage 告警包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2020年3月6日 下午3:23:56
     */
    @Deprecated
    public static BaseResponsePackage sendAlarmPackage2Server(AlarmPackage alarmPackage) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(IAlarmService.class, "sendAlarmPackage");
        // 执行命令，返回执行结果
        return execute(invoker, alarmPackage);
    }

    /**
     * <p>
     * 向服务端发送异常包
     * </p>
     *
     * @param exceptionPackage 异常信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2024/2/29 10:23
     */
    @Deprecated
    public static BaseResponsePackage sendExceptionPackage2Server(ExceptionPackage exceptionPackage) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(IExceptionService.class, "sendExceptionPackage");
        // 执行命令，返回执行结果
        return execute(invoker, exceptionPackage);
    }

    /**
     * <p>
     * 向服务端发送服务器信息包
     * </p>
     *
     * @param serverPackage 服务器信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2020年3月7日 下午5:18:32
     */
    @Deprecated
    public static BaseResponsePackage sendServerPackage2Server(ServerPackage serverPackage) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(IServerService.class, "sendServerPackage");
        // 执行命令，返回执行结果
        return execute(invoker, serverPackage);
    }

    /**
     * <p>
     * 向服务端发送网络设备信息包
     * </p>
     *
     * @param networkDevicePackage 网络设备信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2024/11/22 8:46
     */
    @Deprecated
    public static BaseResponsePackage sendNetworkDevicePackage2Server(NetworkDevicePackage networkDevicePackage) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(INetworkDeviceService.class, "sendNetworkDevicePackage");
        // 执行命令，返回执行结果
        return execute(invoker, networkDevicePackage);
    }

    /**
     * <p>
     * 向服务端发送Java虚拟机信息包
     * </p>
     *
     * @param jvmPackage Java虚拟机信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2020/8/15 22:09
     */
    @Deprecated
    public static BaseResponsePackage sendJvmPackage2Server(JvmPackage jvmPackage) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(IJvmService.class, "sendJvmPackage");
        // 执行命令，返回执行结果
        return execute(invoker, jvmPackage);
    }

    /**
     * <p>
     * 向服务端发送docker信息包
     * </p>
     *
     * @param dockerPackage docker信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2022/6/26 21:54
     */
    public static BaseResponsePackage sendDockerPackage2Server(DockerPackage dockerPackage) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(IDockerService.class, "sendDockerPackage");
        // 执行命令，返回执行结果
        return execute(invoker, dockerPackage);
    }

    /**
     * <p>
     * 向服务端发送命令信息包
     * </p>
     *
     * @param commandPackage 命令信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2023/7/23 9:54
     */
    public static BaseResponsePackage sendCommandPackage2Server(CommandPackage commandPackage) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(ICommandIssuingService.class, "sendCommandPackage");
        // 执行命令，返回执行结果
        return execute(invoker, commandPackage);
    }

    /**
     * <p>
     * 向服务端发送下线信息包
     * </p>
     *
     * @param offlinePackage 下线信息包
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2023/7/23 9:54
     */
    @Deprecated
    public static BaseResponsePackage sendOfflinePackage2Server(OfflinePackage offlinePackage) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(IOfflineService.class, "sendOfflinePackage");
        // 执行命令，返回执行结果
        return execute(invoker, offlinePackage);
    }

    /**
     * <p>
     * 向服务端发送基础请求包
     * </p>
     *
     * @param baseRequestPackage 基础请求包
     * @param url                URL路径
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2021/4/5 14:44
     */
    public static BaseResponsePackage sendBaseRequestPackage2Server(BaseRequestPackage baseRequestPackage, String url) {
        // 通过命令执行器管理器，获取指定的命令执行器
        Invoker invoker = InvokerHolder.getInvoker(IBaseRequestPackageService.class, "sendBaseRequestPackage");
        // 执行命令，返回执行结果
        return execute(invoker, baseRequestPackage, url);
    }

    /**
     * <p>
     * 执行方法，获取返回结果
     * </p>
     *
     * @param invoker 命令执行器-{@link Invoker}
     * @param objects 数据
     * @return {@link BaseResponsePackage}
     * @author 皮锋
     * @custom.date 2020/3/11 22:27
     */
    public static BaseResponsePackage execute(Invoker invoker, Object... objects) {
        // 执行命令，返回执行结果
        BaseResponsePackage responsePackage;
        try {
            assert invoker != null;
            Object object = invoker.invoke(objects);
            responsePackage = (BaseResponsePackage) object;
        } catch (Exception e) {
            Result result = Result.builder().isSuccess(false).msg(e.getMessage()).build();
            responsePackage = AGENT_PACKAGE_CONSTRUCTOR.structureBaseResponsePackage(result);
        }
        return responsePackage;
    }

    /**
     * <p>
     * 将指定类型的数据封装成对应的数据包，并发送到服务端
     * </p>
     * 该方法支持泛型调用，要求泛型类型必须实现 {@link ISuperBean} 接口。
     * 根据传入的数据类型 {@code dataType}，选择对应的构建逻辑生成数据包，
     * 并通过网络发送至服务端，同时记录发送耗时和响应结果。
     *
     * @param <T>      数据的具体类型，必须是 {@link ISuperBean} 的子类
     * @param data     要发送的数据对象，不能为空
     * @param dataType 数据的具体类型 Class 对象，用于运行时类型判断
     * @author 皮锋
     * @custom.date 2025/7/18 09:32
     */
    public static <T extends ISuperBean> void send(T data, Class<T> dataType) {
        // 计时器
        TimeInterval timer = DateUtil.timer();
        // 数据类型名
        String dataTypeName = dataType.getSimpleName().toLowerCase();
        try {
            // 基础响应包
            BaseResponsePackage baseResponsePackage;
            // 根据类型构造对应的数据包
            if (dataType == Docker.class && data instanceof Docker) {
                // 构建docker信息包
                DockerPackage dockerPackage = AGENT_PACKAGE_CONSTRUCTOR.structureDockerPackage((Docker) data);
                // 把docker信息包转发到服务端
                baseResponsePackage = sendDockerPackage2Server(dockerPackage);
            } else {
                log.error("不支持的数据类型：{}", dataType.getName());
                return;
            }
            Result result = baseResponsePackage.getResult();
            if (log.isDebugEnabled()) {
                log.debug("{}信息包响应消息：{}", dataTypeName, result.toJsonString());
            }
        } catch (NetException e) {
            log.error("获取网络信息异常！", e);
        } catch (Exception e) {
            log.error("其它异常！", e);
        } finally {
            // 时间差（毫秒）
            String betweenDay = timer.intervalPretty();
            // 临界值
            int criticalValue = 5;
            if (timer.intervalSecond() > criticalValue) {
                log.warn("构建+发送{}信息包耗时：{}", dataTypeName, betweenDay);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("构建+发送{}信息包耗时：{}", dataTypeName, betweenDay);
                }
            }
        }
    }

}
