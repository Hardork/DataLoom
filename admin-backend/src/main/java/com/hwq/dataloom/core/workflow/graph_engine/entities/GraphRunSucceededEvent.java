package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;

import java.util.Map;

/**
 * 画布运行成功事件
 */
@Data
class GraphRunSucceededEvent extends BaseGraphEvent {

    private Map<String, Object> outputs;

}