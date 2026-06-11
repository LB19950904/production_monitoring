package com.gitee.pifeng.monitoring.ui.business.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.gitee.pifeng.monitoring.common.inf.ISuperBean;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * <p>
 * docker事件信息表现层对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/8/21 20:37
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "docker事件信息表现层对象")
public class MonitorDockerEventVo implements ISuperBean {

    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "服务器IP")
    private String serverIp;

    @Schema(description = "事件状态")
    private String eventStatus;

    @Schema(description = "事件ID")
    private String eventId;

    @Schema(description = "事件来源")
    private String eventFrom;

    @Schema(description = "事件类型")
    private String eventType;

    @Schema(description = "事件动作")
    private String eventAction;

    @Schema(description = "事件属性")
    private String eventAttribute;

    @Schema(description = "事件发生时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date happenTime;

    @Schema(description = "新增时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date insertTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+5")
    private Date updateTime;

    @Schema(description = "监控环境")
    private String monitorEnv;

    @Schema(description = "监控分组")
    private String monitorGroup;

    @Schema(description = "docker摘要")
    private String dockerSummary;

    @Schema(description = "docker服务状态（0：离线，1：在线）")
    private String isOnline;

    /**
     * <p>
     * MonitorDockerEventVo转MonitorDockerEvent
     * </p>
     *
     * @return {@link MonitorDockerEvent}
     * @author 皮锋
     * @custom.date 2020/9/3 9:20
     */
    public MonitorDockerEvent convertTo() {
        MonitorDockerEvent monitorDockerEvent = MonitorDockerEvent.builder().build();
        BeanUtils.copyProperties(this, monitorDockerEvent);
        return monitorDockerEvent;
    }

    /**
     * <p>
     * MonitorDockerEvent转MonitorDockerEventVo
     * </p>
     *
     * @param monitorDockerEvent {@link MonitorDockerEvent}
     * @return {@link MonitorDockerEventVo}
     * @author 皮锋
     * @custom.date 2020/9/3 9:22
     */
    public MonitorDockerEventVo convertFor(MonitorDockerEvent monitorDockerEvent) {
        if (null != monitorDockerEvent) {
            BeanUtils.copyProperties(monitorDockerEvent, this);
        }
        return this;
    }

}
