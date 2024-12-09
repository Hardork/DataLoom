package com.hwq.dataloom.core.workflow.graph_engine.entities.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 节点运行开始事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NodeRunStartedEvent extends BaseNodeEvent {

    private String predecessorNodeId;

}