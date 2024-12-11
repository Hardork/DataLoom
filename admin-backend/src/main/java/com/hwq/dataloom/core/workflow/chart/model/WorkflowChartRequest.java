package com.hwq.dataloom.core.workflow.chart.model;

import lombok.Data;

/**
 * @Author: HCJ
 * @DateTime: 2024/12/11
 * @Description:
 **/
@Data
public class WorkflowChartRequest {

    /**
     * 图表id
     */
    private Long id;

    /**
     * 查询数据条数
     */
    private Integer queryCount;
}
