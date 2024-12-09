package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 画布运行失败事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
class GraphRunFailedEvent extends BaseGraphEvent {
    private String error;
}