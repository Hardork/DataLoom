package com.hwq.bi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.bi.model.dto.chart.ChartQueryRequest;
import com.hwq.bi.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HWQ
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2023-09-01 23:03:32
*/
public interface ChartService extends IService<Chart> {

    void validChart(Chart chart, boolean add);

    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);
}
