package com.hwq.dataloom.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.model.dto.chart.ChartQueryRequest;
import com.hwq.dataloom.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.framework.model.entity.User;

/**
* @author HWQ
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-09-01 23:03:32
*/
public interface ChartService extends IService<Chart> {

    void validChart(Chart chart, boolean add);

    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);

    /**
     * 生成分析图表
     * @param name
     * @param goal
     * @param chartType
     * @param dataId
     * @param loginUser
     * @return
     */
    @Deprecated
    Long genChartByAiWithDataAsyncMq(String name, String goal, String chartType, Long dataId, User loginUser);

}
