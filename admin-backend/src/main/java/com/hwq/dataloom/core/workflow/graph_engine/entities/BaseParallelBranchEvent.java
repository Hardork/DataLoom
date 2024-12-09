package com.hwq.dataloom.core.workflow.graph_engine.entities;

/**
 * 并行分支事件基类
 */
public class BaseParallelBranchEvent extends GraphEngineEvent {

    private String parallelId;

    private String parallelStartNodeId;

    private String parentParallelId;

    private String parentParallelStartNodeId;

    private String inIterationId;

    // Getters and setters
    public String getParallelId() {
        return parallelId;
    }

    public void setParallelId(String parallelId) {
        this.parallelId = parallelId;
    }

    public String getParallelStartNodeId() {
        return parallelStartNodeId;
    }

    public void setParallelStartNodeId(String parallelStartNodeId) {
        this.parallelStartNodeId = parallelStartNodeId;
    }

    public String getParentParallelId() {
        return parentParallelId;
    }

    public void setParentParallelId(String parentParallelId) {
        this.parentParallelId = parentParallelId;
    }

    public String getParentParallelStartNodeId() {
        return parentParallelStartNodeId;
    }

    public void setParentParallelStartNodeId(String parentParallelStartNodeId) {
        this.parentParallelStartNodeId = parentParallelStartNodeId;
    }

    public String getInIterationId() {
        return inIterationId;
    }

    public void setInIterationId(String inIterationId) {
        this.inIterationId = inIterationId;
    }
}
