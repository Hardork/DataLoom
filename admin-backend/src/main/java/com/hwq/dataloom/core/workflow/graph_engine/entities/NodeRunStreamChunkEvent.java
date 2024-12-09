package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 节点运行流事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
class NodeRunStreamChunkEvent extends BaseNodeEvent {

    /**
     * 响应内容块
     */
    private String chunkContent;

    private List<String> fromVariableSelector;

}