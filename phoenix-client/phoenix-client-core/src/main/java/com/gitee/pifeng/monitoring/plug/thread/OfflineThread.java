package com.gitee.pifeng.monitoring.plug.thread;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import com.gitee.pifeng.monitoring.common.constant.ResultMsgConstants;
import com.gitee.pifeng.monitoring.common.domain.Result;
import com.gitee.pifeng.monitoring.common.dto.OfflinePackage;
import com.gitee.pifeng.monitoring.common.dto.WebSocketPackage;
import com.gitee.pifeng.monitoring.common.exception.NetException;
import com.gitee.pifeng.monitoring.plug.core.ClientPackageConstructor;
import com.gitee.pifeng.monitoring.plug.core.DataExchanger;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;

/**
 * <p>
 * 下线线程
 * </p>
 *
 * @author 皮锋
 * @custom.date 2023/5/30 13:41
 */
@Slf4j
public class OfflineThread implements Callable<Result> {

    /**
     * 客户端包构造器
     */
    private final ClientPackageConstructor clientPackageConstructor = ClientPackageConstructor.getInstance();

    /**
     * <p>
     * 构建+发送下线数据包
     * </p>
     *
     * @return 线程执行结果
     * @author 皮锋
     * @custom.date 2023/5/30 13:44
     */
    @Override
    public Result call() {
        if (!DataExchanger.isReady()) {
            return Result.builder().isSuccess(false).msg("数据交换器未准备好，请稍后再试！").build();
        }
        // 返回结果
        Result result;
        // 计时器
        TimeInterval timer = DateUtil.timer();
        try {
            // 构建下线数据包
            OfflinePackage offlinePackage = this.clientPackageConstructor.structureOfflinePackage();
            // 发送请求
            WebSocketPackage requestPackage = new WebSocketPackage();
            requestPackage.setClassName(OfflinePackage.class.getName());
            requestPackage.setPayload(offlinePackage);
            DataExchanger.sendMessage(requestPackage);
            result = Result.builder().isSuccess(true).msg(ResultMsgConstants.SUCCESS).build();
            // 改成用 WebSocket，弃用 HTTP
            // String resultStr = Sender.send(UrlConstants.OFFLINE_URL, offlinePackage.toJsonString());
            // if (log.isDebugEnabled()) {
            // log.debug("下线数据包响应消息：{}", resultStr);
            // }
            // BaseResponsePackage baseResponsePackage = JSON.parseObject(resultStr, BaseResponsePackage.class);
            // result = baseResponsePackage.getResult();
            // } catch (IOException e) {
            // String msg = "IO异常！";
            // log.error(msg, e);
            // result = Result.builder().isSuccess(false).msg(msg).build();
        } catch (NetException e) {
            String msg = "获取网络信息异常！";
            log.error(msg, e);
            result = Result.builder().isSuccess(false).msg(msg).build();
        } catch (Exception e) {
            String msg = "其它异常！";
            log.error(msg, e);
            result = Result.builder().isSuccess(false).msg(msg).build();
        } finally {
            // 时间差（毫秒）
            String betweenDay = timer.intervalPretty();
            // 临界值
            int criticalValue = 5;
            if (timer.intervalSecond() > criticalValue) {
                log.warn("构建+发送下线数据包耗时：{}", betweenDay);
            } else {
                if (log.isDebugEnabled()) {
                    log.debug("构建+发送下线数据包耗时：{}", betweenDay);
                }
            }
        }
        return result;
    }

}