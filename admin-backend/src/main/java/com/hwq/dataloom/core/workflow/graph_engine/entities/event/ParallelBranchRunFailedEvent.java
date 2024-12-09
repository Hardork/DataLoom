package com.hwq.dataloom.core.workflow.graph_engine.entities.event;

import lombok.Data;

@Data
public class ParallelBranchRunFailedEvent extends BaseParallelBranchEvent {
    private String error;
}