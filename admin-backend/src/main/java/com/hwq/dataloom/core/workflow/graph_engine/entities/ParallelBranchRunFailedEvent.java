package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;

@Data
public class ParallelBranchRunFailedEvent extends BaseParallelBranchEvent {
    private String error;
}