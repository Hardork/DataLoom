package com.hwq.dataloom.core.workflow.graph_engine.entities.event;

import com.hwq.dataloom.core.workflow.graph_engine.entities.RouteNodeState;
import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 节点事件基类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseNodeEvent extends GraphEngineEvent {

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
