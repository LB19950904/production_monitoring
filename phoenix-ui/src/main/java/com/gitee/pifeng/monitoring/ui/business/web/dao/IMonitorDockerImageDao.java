package com.gitee.pifeng.monitoring.ui.business.web.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gitee.pifeng.monitoring.ui.business.web.entity.MonitorDockerImage;
import com.gitee.pifeng.monitoring.ui.business.web.vo.MonitorDockerImageVo;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * <p>
 * docker镜像信息表数据访问对象
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022-06-26
 */
public interface IMonitorDockerImageDao extends BaseMapper<MonitorDockerImage> {

    /**
     * <p>
     * 获取docker镜像列表
     * </p>
     *
     * @param page   分页参数
     * @param params 请求条件
     * @return docker镜像列表
     * @author 皮锋
     * @custom.date 2022/8/21 21:08
     */
    Page<MonitorDockerImageVo> getMonitorDockerImageList(Page<MonitorDockerImageVo> page, @Param("params") Map<String, Object> params);

    /**
     * <p>
     * 获取docker镜像信息
     * </p>
     *
     * @param serverIp 服务器IP
     * @param imageId  镜像ID
     * @return docker镜像信息表现层对象
     * @author 皮锋
     * @custom.date 2022/8/21 16:42
     */
    MonitorDockerImageVo getMonitorDockerImageInfo(@Param("serverIp") String serverIp, @Param("imageId") String imageId);

}
