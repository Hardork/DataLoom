package com.hwq.dataloom.core.workflow.graph_engine.entities;

import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import lombok.Data;

/**
 * 迭代事件基类
 */
@Data
public class BaseIterationEvent extends GraphEngineEvent {

    private String iterationId;

    private String iterationNodeId;

    private NodeTypeEnum iterationNodeTypeEnum;

    private BaseNodeData iterationNodeData;

    private String parallelId;

    private String parallelStartNodeId;

    private String parentParallelId;

    private String parentParallelStartNodeId;

}
