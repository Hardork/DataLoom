package com.hwq.dataloom.core.workflow.graph_engine.entities.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 下一个迭代运行事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IterationRunNextEvent extends BaseIterationEvent {

    private int index;

    private Object preIterationOutput;

}