package com.hwq.dataloom.core.workflow.graph_engine.entities.event;

import lombok.*;

/**
 * 节点运行失败事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeRunFailedEvent extends BaseNodeEvent {
    private String error;
}
