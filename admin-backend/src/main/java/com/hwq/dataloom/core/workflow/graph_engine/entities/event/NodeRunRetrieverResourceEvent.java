package com.hwq.dataloom.core.workflow.graph_engine.entities.event;

import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 节点运行检索器资源事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NodeRunRetrieverResourceEvent extends BaseNodeEvent {

    private List<Map<String, Object>> retrieverResources;

    private String context;

}