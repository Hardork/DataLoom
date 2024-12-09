package com.hwq.dataloom.core.workflow.graph_engine.entities;

import lombok.Data;

@Data
class ParallelBranchRunFailedEvent extends BaseParallelBranchEvent {
    private String error;
}