package com.hwq.dataloom.core.workflow.graph_engine.entities.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 节点运行流事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeRunStreamChunkEvent extends BaseNodeEvent {

    /**
     * 响应内容块
     */
    private String chunkContent;

    private List<String> fromVariableSelector;

}