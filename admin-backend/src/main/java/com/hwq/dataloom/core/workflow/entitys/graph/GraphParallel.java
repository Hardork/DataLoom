package com.hwq.dataloom.core.workflow.entitys.graph;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图表并行类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraphParallel {
    /**
     * 并行id
     */
    private String id;

    /**
     * 开始节点id
     */
    private String startFromNodeId;

    /**
     * 父并行id
     */
    private String parentParallelId;

    /**
     * 父并行开始节点id
     */
    private String parentParallelStartNodeId;

    /**
     * 结束节点id
     */
    private String endToNodeId;
}