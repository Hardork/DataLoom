package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 * 迭代运行失败事件
 */
@Data
public class IterationRunFailedEvent extends BaseIterationEvent {

    private Date startAt;

    private Map<String, Object> inputs;

    private Map<String, Object> outputs;

    private Map<String, Object> metadata;

    private int steps;

    private String error;
}