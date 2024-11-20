package com.hwq.dataloom.job.cycle;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.mq.producer.AnalysisMessageProducer;
import com.hwq.dataloom.model.entity.FailedChart;

import java.util.List;
import javax.annotation.Resource;

import com.hwq.dataloom.model.enums.ChartStatusEnum;
import com.hwq.dataloom.service.FailedChartService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 智能分析失败重试
 */
//@Component
@Slf4j
public class RetryTimeoutChart {
    @Resource
    private FailedChartService failedChartService;

    @Resource
    private AnalysisMessageProducer analysisMessageProducer;


    /**
     * 1h执行一次
     */
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void retryTimeoutChart() {
        // 查询Timeout的任务,并且重试次数 < 3
        QueryWrapper<FailedChart> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("status", ChartStatusEnum.TIMEOUT.getValue())
                .lt("retryNum", 3);
        List<FailedChart> list = failedChartService.list(queryWrapper);
        if (CollectionUtils.isEmpty(list)) {
            log.info("no timeout chart");
            return;
        }
        // 开始重试
        for (FailedChart failedChart : list) {
            // 更新超时表
            failedChart.setStatus(ChartStatusEnum.WAIT.getValue());
            failedChart.setRetryNum(failedChart.getRetryNum() + 1);
            failedChartService.updateById(failedChart);
            // 提交任务
            analysisMessageProducer.sendMessage(String.valueOf(failedChart.getChartId()));
        }
        int total = list.size();
        log.info("retry start, total {}", total);
    }
}
