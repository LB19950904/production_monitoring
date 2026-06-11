package com.gitee.pifeng.monitoring.plug.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import com.gitee.pifeng.monitoring.common.dto.JavaThreadPoolPackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.common.util.threadpool.JavaThreadPoolUtils;
import com.gitee.pifeng.monitoring.plug.core.ClientPackageConstructor;
import com.gitee.pifeng.monitoring.plug.core.DataExchanger;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 发送Java线程池信息线程
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/13 16:16
 */
@Slf4j
public class JavaThreadPoolThread implements Runnable {

    /**
     * 客户端包构造器
     */
    private final ClientPackageConstructor clientPackageConstructor = ClientPackageConstructor.getInstance();

    /**
     * <p>
     * 构建+发送Java线程池信息包
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/3/13 17:16
     */
    @Override
    public void run() {
        if (!DataExchanger.isReady()) {
            return;
        }
        // 计时器
        TimeInterval timer = DateUtil.timer();
        try {
            // 获取Java线程池信息
            JavaThreadPool javaThreadPoolInfo = JavaThreadPoolUtils.getJavaThreadPoolInfo();
            // 构建Java线程池信息包
            JavaThreadPoolPackage javaThreadPoolPackage = this.clientPackageConstructor.structureJavaThreadPoolPackage(javaThreadPoolInfo);
            // 发送请求
            WebSocketPackage requestPackage = new WebSocketPackage();
            requestPackage.setClassName(JavaThreadPoolPackage.class.getName());
            requestPackage.setPayload(javaThreadPoolPackage);
            DataExchanger.sendMessage(requestPackage);
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
                log.warn("构建+发送Java线程池信息包耗时：{}", betweenDay);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("构建+发送Java线程池信息包耗时：{}", betweenDay);
                }
            }
        }
    }

}