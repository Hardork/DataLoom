package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Map;

/**
 * 迭代运行开始事件
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class IterationRunStartedEvent extends BaseIterationEvent {

    private Date startAt;

    private Map<String, Object> inputs;

    private Map<String, Object> metadata;

    private String predecessorNodeId;
}