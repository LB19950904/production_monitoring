package com.gitee.pifeng.monitoring.server.business.server.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * <p>
 * 分布式锁表
 * </p>
 *
 * @author 皮锋
 * @custom.date 2025/11/6 16:30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("MONITOR_DISTRIBUTED_LOCK")
public class MonitorDistributedLock {

    /**
     * 锁名称
     */
    @TableId(value = "LOCK_KEY", type = IdType.INPUT)
    private String lockKey;

    /**
     * 锁持有者
     */
    @TableField("OWNER")
    private String owner;

    /**
     * 过期时间（防死锁）
     */
    @TableField("EXPIRE_TIME")
    private LocalDateTime expireTime;

}