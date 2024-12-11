package com.hwq.dataloom.core.workflow.chart.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.core.workflow.chart.model.WorkflowChartRequest;
import com.hwq.dataloom.core.workflow.chart.model.WorkflowChartVO;
import com.hwq.dataloom.model.entity.WorkflowChartInfo;

/**
 * @Author: HCJ
 * @DateTime: 2024/12/11
 * @Description:
 **/
public interface WorkflowChartInfoService extends IService<WorkflowChartInfo> {

    WorkflowChartVO getWorkflowChartInfoAndData(WorkflowChartRequest workflowChartRequest);
}
