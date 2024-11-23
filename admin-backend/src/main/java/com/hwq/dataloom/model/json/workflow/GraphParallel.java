package com.hwq.dataloom.model.json.workflow;

import lombok.Data;

/**
 * 图表并行类
 */
@Data
public class GraphParallel {
    private String id;
    private String startFromNodeId;
    private String parentParallelId;
    private String parentParallelStartNodeId;
    private String endToNodeId;
}