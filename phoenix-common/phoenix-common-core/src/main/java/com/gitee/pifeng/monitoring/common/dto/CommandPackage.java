package com.gitee.pifeng.monitoring.common.dto;

import com.gitee.pifeng.monitoring.common.domain.Command;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 命令信息包
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/9/20 21:29
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class CommandPackage extends BaseRequestPackage {

    /**
     * 命令信息
     */
    private Command command;

}
