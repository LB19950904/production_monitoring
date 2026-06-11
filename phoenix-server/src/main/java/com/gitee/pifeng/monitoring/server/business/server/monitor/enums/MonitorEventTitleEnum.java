package com.gitee.pifeng.monitoring.server.business.server.monitor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 各种监控事件中英文对照枚举
 */
@Getter
@AllArgsConstructor
public enum MonitorEventTitleEnum {

    // ==================== 静默告警 ====================
    /** 静默告警提醒 */
    SILENT_ALARM_REMINDER("静默告警提醒", "Silent Alarm Reminder"),

    // ==================== 应用实例监控 ====================
    /** 发现新应用程序 */
    DISCOVER_NEW_APPLICATION("发现新应用程序", "Discover New Application"),
    /** 应用程序上线 */
    APPLICATION_ONLINE("应用程序上线", "Application Online"),
    /** 应用程序离线 */
    APPLICATION_OFFLINE("应用程序离线", "Application Offline"),

    // ==================== 数据库监控 ====================
    /** 数据库连接异常 */
    DATABASE_CONNECTION_ABNORMAL("数据库连接异常", "Database Connection Abnormal"),
    /** 数据库连接恢复 */
    DATABASE_CONNECTION_RECOVERED("数据库连接恢复", "Database Connection Recovered"),
    /** 数据库表空间不够 */
    DATABASE_TABLESPACE_INSUFFICIENT("数据库表空间不够", "Database Tablespace Insufficient"),
    /** 数据库表空间恢复 */
    DATABASE_TABLESPACE_RECOVERED("数据库表空间恢复", "Database Tablespace Recovered"),
    /** 数据库慢SQL */
    DATABASE_SLOW_SQL("数据库慢SQL", "Database Slow SQL"),

    // ==================== Docker监控 ====================
    /** 发现新docker服务 */
    DISCOVER_NEW_DOCKER_SERVICE("发现新docker服务", "Discover New Docker Service"),
    /** docker服务上线 */
    DOCKER_SERVICE_ONLINE("docker服务上线", "Docker Service Online"),
    /** docker服务离线 */
    DOCKER_SERVICE_OFFLINE("docker服务离线", "Docker Service Offline"),

    // ==================== HTTP监控 ====================
    /** HTTP服务中断 */
    HTTP_SERVICE_INTERRUPTED("HTTP服务中断", "HTTP Service Interrupted"),
    /** HTTP服务恢复 */
    HTTP_SERVICE_RECOVERED("HTTP服务恢复", "HTTP Service Recovered"),

    // ==================== 网络监控 ====================
    /** 网络中断 */
    NETWORK_INTERRUPTED("网络中断", "Network Interrupted"),
    /** 网络恢复 */
    NETWORK_RECOVERED("网络恢复", "Network Recovered"),

    // ==================== 网络设备监控 ====================
    /** 发现新网络设备 */
    DISCOVER_NEW_NETWORK_DEVICE("发现新网络设备", "Discover New Network Device"),
    /** 网络设备上线 */
    NETWORK_DEVICE_ONLINE("网络设备上线", "Network Device Online"),
    /** 网络设备离线 */
    NETWORK_DEVICE_OFFLINE("网络设备离线", "Network Device Offline"),

    // ==================== 服务器监控 ====================
    /** 发现新服务器 */
    DISCOVER_NEW_SERVER("发现新服务器", "Discover New Server"),
    /** 服务器上线 */
    SERVER_ONLINE("服务器上线", "Server Online"),
    /** 服务器离线 */
    SERVER_OFFLINE("服务器离线", "Server Offline"),
    /** 服务器CPU过载 */
    SERVER_CPU_OVERLOAD("服务器CPU过载", "Server CPU Overload"),
    /** 服务器CPU恢复正常 */
    SERVER_CPU_RECOVERED("服务器CPU恢复正常", "Server CPU Recovered"),
    /** 服务器内存过载 */
    SERVER_MEMORY_OVERLOAD("服务器内存过载", "Server Memory Overload"),
    /** 服务器内存恢复正常 */
    SERVER_MEMORY_RECOVERED("服务器内存恢复正常", "Server Memory Recovered"),
    /** 服务器15分钟负载过载 */
    SERVER_15MIN_LOAD_OVERLOAD("服务器15分钟负载过载", "Server 15-Minute Load Overload"),
    /** 服务器15分钟负载恢复正常 */
    SERVER_15MIN_LOAD_RECOVERED("服务器15分钟负载恢复正常", "Server 15-Minute Load Recovered"),
    /** 服务器磁盘空间不足 */
    SERVER_DISK_INSUFFICIENT("服务器磁盘空间不足", "Server Disk Space Insufficient"),
    /** 服务器磁盘空间恢复正常 */
    SERVER_DISK_RECOVERED("服务器磁盘空间恢复正常", "Server Disk Space Recovered"),

    // ==================== TCP监控 ====================
    /** TCP服务中断 */
    TCP_SERVICE_INTERRUPTED("TCP服务中断", "TCP Service Interrupted"),
    /** TCP服务恢复 */
    TCP_SERVICE_RECOVERED("TCP服务恢复", "TCP Service Recovered");

    private final String zh;
    private final String en;

    /**
     * <p>
     * 根据中文标题获取英文标题
     * </p>
     *
     * @param chineseTitle 中文标题
     * @return 英文标题，如果没有找到则返回null
     * @author 皮锋
     * @custom.date 2025/6/10 10:00
     */
    public static String getEnglishTitle(String chineseTitle) {
        if (chineseTitle == null || chineseTitle.trim().isEmpty()) {
            return null;
        }
        for (MonitorEventTitleEnum titleEnum : MonitorEventTitleEnum.values()) {
            if (chineseTitle.equals(titleEnum.zh)) {
                return titleEnum.en;
            }
        }
        return null;
    }
}
