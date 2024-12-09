package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;

/**
 * 节点运行开始事件
 */
@Data
public class NodeRunStartedEvent extends BaseNodeEvent {

    private String predecessorNodeId;

}