package com.gitee.pifeng.monitoring.agent.util.docker;

import cn.hutool.core.io.unit.DataSizeUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gitee.pifeng.monitoring.common.domain.docker.StatsDomain;
import com.google.common.collect.Lists;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

/**
 * <p>
 * docker统计信息工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/22 22:43
 */
@Slf4j
public class DockerStatsUtils {

    /**
     * 全局缓存：上一次采集的 StatsDomain（会被 synchronized 保护）
     */
    private static volatile StatsDomain lastStatsDomain = null;

    /**
     * 全局缓存：上一次采集的时间（纳秒）
     */
    private static final AtomicLong LAST_TIME = new AtomicLong(System.nanoTime());

    /**
     * 保护 lastStatsDomain 和 LAST_TIME 的锁
     */
    private static final Object LOCK = new Object();

    /**
     * <p>
     * 获取docker统计信息
     * </p>
     *
     * @return docker统计信息
     * @author 皮锋
     * @custom.date 2022/7/22 22:46
     */
    private static StatsDomain getStatsInfo() {
        ProcessBuilder processBuilder = new ProcessBuilder("docker", "stats", "--all", "--no-stream", "--no-trunc", "--format", "{{json .}}");
        // 将错误信息输出流合并到标准输出流
        processBuilder.redirectErrorStream(true);
        try {
            // 执行
            @Cleanup(value = "destroy")
            Process process = processBuilder.start();
            try (BufferedReader buf = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                // 返回值
                StatsDomain statsDomain = new StatsDomain();
                List<StatsDomain.StatsInfoDomain> statsInfoDomainList = Lists.newArrayList();
                // 返回值
                String line;
                while ((line = buf.readLine()) != null) {
                    line = line.trim();
                    if (!StringUtils.startsWith(line, "{")) {
                        // 跳过非 JSON 行（如空行、提示信息）
                        continue;
                    }
                    JSONObject node = JSON.parseObject(line);
                    StatsDomain.StatsInfoDomain statsInfoDomain = StatsDomain.StatsInfoDomain.builder()
                            .containerId(node.getString("ID"))
                            .containerName(node.getString("Name"))
                            .cpuUtilizationRate(node.getString("CPUPerc"))
                            .menUsageLimit(node.getString("MemUsage"))
                            .menUtilizationRate(node.getString("MemPerc"))
                            .netIo(node.getString("NetIO"))
                            .blockIo(node.getString("BlockIO"))
                            .pids(node.getString("PIDs"))
                            .build();
                    statsInfoDomainList.add(statsInfoDomain);
                }
                statsDomain.setStatsNum(statsInfoDomainList.size());
                statsDomain.setStatsInfoDomainList(statsInfoDomainList);
                // 设置超时时长
                if (!process.waitFor(25, TimeUnit.SECONDS)) {
                    // 强制终止进程
                    process.destroyForcibly();
                    log.error("Docker stats 命令超时，已强制终止。");
                    return null;
                }
                // 检查退出码是否正常
                int exitCode = process.exitValue();
                if (exitCode != 0) {
                    log.error("Docker stats 命令已退出，退出码为：{}", exitCode);
                    return null;
                }
                // 销毁子线程，通过lombok的@Cleanup销毁
                // process.destroy();
                return statsDomain;
            }
        } catch (Exception e) {
            log.error("获取docker统计信息异常：{}", e.getMessage());
        }
        return null;
    }

    /**
     * <p>
     * 获取按时间间隔计算的docker统计信息
     * </p>
     *
     * @return 按时间间隔计算的docker统计信息
     * @author 皮锋
     * @custom.date 2025/9/28 09:08
     */
    public static StatsDomain getIntervalStatsInfo() {
        StatsDomain currentStatsDomain = getStatsInfo();
        if (currentStatsDomain == null || CollectionUtils.isEmpty(currentStatsDomain.getStatsInfoDomainList())) {
            log.warn("本次采集 Docker stats 数据失败或为空！");
            return null;
        }
        long currentTime = System.nanoTime();

        // 加锁：保证 lastStatsDomain 和 LAST_TIME 的读取与更新是原子的
        synchronized (LOCK) {
            // 1. 计算时间差
            long lastTimeValue = LAST_TIME.get();

            // 2. 第一次调用？只缓存，不计算速率
            if (lastStatsDomain == null) {
                lastStatsDomain = currentStatsDomain;
                LAST_TIME.set(currentTime);
                return null;
            }
            double timeOffset = (currentTime - lastTimeValue) / 1_000_000_000.0;

            // 3. 构建上一次的映射
            Map<String, StatsDomain.StatsInfoDomain> lastStatsMap = lastStatsDomain.getStatsInfoDomainList().stream()
                    .collect(Collectors.toMap(StatsDomain.StatsInfoDomain::getContainerName, identity()));
            // 4. 遍历当前数据，计算速率
            for (StatsDomain.StatsInfoDomain current : currentStatsDomain.getStatsInfoDomainList()) {
                String containerName = current.getContainerName();
                StatsDomain.StatsInfoDomain last = lastStatsMap.get(containerName);
                if (last == null) {
                    log.debug("容器 {} 本次新增，跳过速率计算。", containerName);
                    continue;
                }
                current.setNetIoSpeed(parseIoValues(current.getNetIo(), last.getNetIo(), timeOffset));
                current.setBlockIoSpeed(parseIoValues(current.getBlockIo(), last.getBlockIo(), timeOffset));
            }
            // 5. 更新全局状态（原子性操作）
            lastStatsDomain = currentStatsDomain;
            LAST_TIME.set(currentTime);
            return currentStatsDomain;
        }
    }

    /**
     * <p>
     * 获取docker瞬时统计信息
     * </p>
     *
     * @return docker瞬时统计信息
     * @author 皮锋
     * @custom.date 2025/9/24 15:38
     */
    @Deprecated
    public static StatsDomain getInstantaneousStatsInfo() {
        StatsDomain startStatsDomain = getStatsInfo();
        if (startStatsDomain == null || CollectionUtils.isEmpty(startStatsDomain.getStatsInfoDomainList())) {
            log.warn("首次采集 Docker stats 数据失败或为空！");
            return null;
        }
        // 开始时间
        long startTime = System.nanoTime();

        // 休眠1秒
        ThreadUtil.sleep(1, TimeUnit.SECONDS);

        StatsDomain endStatsInfo = getStatsInfo();
        if (endStatsInfo == null || CollectionUtils.isEmpty(endStatsInfo.getStatsInfoDomainList())) {
            log.warn("第二次采集 Docker stats 数据失败或为空！");
            return null;
        }
        // 结束时间
        long endTime = System.nanoTime();
        // 时间差
        double timeOffset = (endTime - startTime) / 1_000_000_000.0;

        // 构建容器ID到Stats的映射，便于快速查找
        Map<String, StatsDomain.StatsInfoDomain> endStatsMap = endStatsInfo.getStatsInfoDomainList().stream()
                .collect(Collectors.toMap(StatsDomain.StatsInfoDomain::getContainerName, identity()));
        // 遍历起始数据，匹配结束数据计算差值
        for (StatsDomain.StatsInfoDomain start : startStatsDomain.getStatsInfoDomainList()) {
            String containerName = start.getContainerName();
            StatsDomain.StatsInfoDomain end = endStatsMap.get(containerName);
            // 容器可能在这1秒内被删除或重启，跳过
            if (end == null) {
                if (log.isDebugEnabled()) {
                    log.debug("容器 {} 在采样期间不可见，跳过！", containerName);
                }
                continue;
            }
            end.setNetIoSpeed(parseIoValues(end.getNetIo(), start.getNetIo(), timeOffset));
            end.setBlockIoSpeed(parseIoValues(end.getBlockIo(), start.getBlockIo(), timeOffset));
        }
        return endStatsInfo;
    }

    /**
     * <p>
     * 计算两个 IO 状态之间的增量（差值），通常用于计算网络、磁盘等的累计 IO 增量（结束值 - 开始值）。
     * </p>
     *
     * @param endIoStr   结束时刻的 IO 值，格式为 "输入 / 输出"，例如 "200MB / 100KB"
     * @param startIoStr 开始时刻的 IO 值，格式同上
     * @param timeOffset 时间差
     * @return 字符串，格式为 "输入增量字节数 / 输出增量字节数"，例如 "104857600 / 51200"，单位都是 B/s
     * @throws IllegalArgumentException 如果任一输入字符串无效（null、空、格式错误）
     * @author 皮锋
     * @custom.date 2025/9/24 15:04
     * @see #convert2byte(String) 用于解析单个 IO 字符串
     */
    private static String parseIoValues(String endIoStr, String startIoStr, double timeOffset) {
        long[] endIo = convert2byte(endIoStr);
        long[] startIo = convert2byte(startIoStr);
        long inputByte = endIo[0] >= startIo[0] ? endIo[0] - startIo[0] : 0;
        long outputByte = endIo[1] >= startIo[1] ? endIo[1] - startIo[1] : 0;
        double inputSpeed = inputByte / timeOffset;
        double outputSpeed = outputByte / timeOffset;
        return inputSpeed + " / " + outputSpeed;
    }

    /**
     * <p>
     * 将格式为 "值 / 值" 的字符串（如 "100MB / 50KB"）解析为字节数组。
     * 支持常见单位（MB, KB, GB, MiB, KiB, GiB 等），解析时会自动忽略大小写和 'i' 字符（如 MiB → MB）。
     * </p>
     *
     * @param ioStr 输入字符串，格式应为 "输入量 / 输出量"，例如 "100MiB / 50KiB"
     * @return long[] 数组，长度为2：索引0为输入量的字节数，索引1为输出量的字节数
     * @throws IllegalArgumentException 如果 ioStr 为 null、空字符串，或不包含两个由 ' / ' 分隔的有效部分
     * @author 皮锋
     * @custom.date 2025/9/24 15:01
     */
    private static long[] convert2byte(String ioStr) {
        if (StringUtils.isEmpty(ioStr)) {
            throw new IllegalArgumentException("ioStr不能为空！");
        }
        String[] ioArray = Arrays.stream(ioStr.split("/"))
                .map(String::trim).map(String::toUpperCase)
                .map(s -> s.replace("I", ""))
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);
        if (ioArray.length < 2) {
            throw new IllegalArgumentException("ioStr必须包含两个由“/”分隔的部分！");
        }
        String input = ioArray[0];
        String output = ioArray[1];
        long inputByte = DataSizeUtil.parse(input);
        long outputByte = DataSizeUtil.parse(output);
        return new long[]{inputByte, outputByte};
    }

}
