package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * 节点运行检索器资源事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
class NodeRunRetrieverResourceEvent extends BaseNodeEvent {

    private List<Map<String, Object>> retrieverResources;

    private String context;

}