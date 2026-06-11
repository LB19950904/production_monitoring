package com.gitee.pifeng.monitoring.agent.util.docker;

import com.gitee.pifeng.monitoring.common.domain.docker.StatsDomain;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

/**
 * <p>
 * 测试docker统计信息工具类
 * </p>
 *
 * @author 皮锋
 * @custom.date 2022/7/22 22:49
 */
@Slf4j
public class DockerStatsUtilsTest {

    /**
     * <p>
     * 测试获取docker统计信息
     * </p>
     *
     * @author 皮锋
     * @custom.date 2022/7/23 13:58
     */
    @Test
    public void testGetStatsInfo() {
        StatsDomain statsInfo = DockerStatsUtils.getIntervalStatsInfo();
        assertNotNull(statsInfo);
        log.info(statsInfo.toJsonString());
    }

}