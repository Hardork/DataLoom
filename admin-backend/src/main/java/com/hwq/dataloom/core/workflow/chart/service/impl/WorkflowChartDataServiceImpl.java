package com.hwq.dataloom.core.workflow.chart.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.core.workflow.chart.service.WorkflowChartDataService;
import com.hwq.dataloom.mapper.WorkflowChartDataMapper;
import com.hwq.dataloom.model.entity.WorkflowChartData;
import org.springframework.stereotype.Service;

/**
 * @Author: HCJ
 * @DateTime: 2024/12/11
 * @Description:
 **/
@Service
public class WorkflowChartDataServiceImpl extends ServiceImpl<WorkflowChartDataMapper, WorkflowChartData>
        implements WorkflowChartDataService {
}
