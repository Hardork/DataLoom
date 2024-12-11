package com.hwq.dataloom.core.workflow.chart.model;

import com.hwq.dataloom.model.entity.WorkflowChartData;
import lombok.Builder;

import java.util.List;

/**
 * @Author: HCJ
 * @DateTime: 2024/12/11
 * @Description:
 **/
@Builder

public class WorkflowChartVO {
    /**
     * 图表id
     */
    private Long id;
    /**
     * 工作流图表名称
     */
    private String chartName;
    /**
     * 图表描述
     */
    private String chartDesc;

    /**
     * 图表数据
     */
    private List<WorkflowChartData> workflowChartDataList;
}
