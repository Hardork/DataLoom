package com.hwq.dataloom.core.workflow.graph_engine.entities.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 画布运行成功事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GraphRunSucceededEvent extends BaseGraphEvent {

    private Map<String, Object> outputs;

}