package com.hwq.dataloom.core.workflow.graph_engine.entities;

import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import lombok.Data;

/**
 * 节点事件基类
 */
@Data
class BaseNodeEvent extends GraphEngineEvent {

    // Getters and setters
    private String id;

    private String nodeId;

    private NodeTypeEnum nodeType;

    private BaseNodeData nodeData;

    private RouteNodeState routeNodeState;

    private String parallelId;

    private String parallelStartNodeId;

    private String parentParallelId;

    private String parentParallelStartNodeId;

    private String inIterationId;
}
