package com.gitee.pifeng.monitoring.common.util.threadpool;

import com.gitee.pifeng.monitoring.common.domain.JavaThreadPool;
import com.gitee.pifeng.monitoring.common.threadpool.ThreadPoolManager;

/**
 * <p>
 * Java线程池工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2026/3/13 11:55
 */
public class JavaThreadPoolUtils {

    /**
     * <p>
     * 私有化构造方法
     * </p>
     *
     * @author 皮锋
     * @custom.date 2026/3/13 11:55
     */
    private JavaThreadPoolUtils() {
    }

    /**
     * <p>
     * 获取Java线程池信息
     * </p>
     *
     * @return Java线程池信息
     * @author 皮锋
     * @custom.date 2026/3/13 12:02
     */
    public static JavaThreadPool getJavaThreadPoolInfo() {
        return ThreadPoolManager.getAllThreadPoolInfo();
    }

}