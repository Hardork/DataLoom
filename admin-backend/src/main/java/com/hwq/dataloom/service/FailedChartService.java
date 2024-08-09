package com.hwq.dataloom.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.model.dto.failed_chart.FailedChartQueryRequest;
import com.hwq.dataloom.model.entity.FailedChart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HWQ
* @description 针对表【failed_chart(失败分析表)】的数据库操作Service
* @createDate 2023-10-23 16:19:25
*/
public interface FailedChartService extends IService<FailedChart> {

    QueryWrapper<FailedChart> getQueryWrapper(FailedChartQueryRequest failedChartQueryRequest);
}
