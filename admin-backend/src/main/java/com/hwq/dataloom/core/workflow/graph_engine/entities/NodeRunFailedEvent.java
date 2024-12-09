package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;

/**
 * 节点运行失败事件
 */
@Data
class NodeRunFailedEvent extends BaseNodeEvent {
    private String error;
}
