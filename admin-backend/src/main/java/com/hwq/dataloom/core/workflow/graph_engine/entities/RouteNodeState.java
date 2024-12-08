package com.hwq.dataloom.core.workflow.graph_engine.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hwq.dataloom.core.workflow.entitys.NodeRunResult;
import com.hwq.dataloom.core.workflow.enums.NodeRunStatus;

class RouteNodeState {

    enum Status {
        @JsonProperty("running")
        RUNNING,
        @JsonProperty("success")
        SUCCESS,
        @JsonProperty("failed")
        FAILED,
        @JsonProperty("paused")
        PAUSED
    }

    @JsonProperty("id")
    private String id;

    @JsonProperty("node_id")
    private String nodeId;

    @JsonProperty("node_run_result")
    private NodeRunResult nodeRunResult;

    @JsonProperty("status")
    private Status status;

    @JsonProperty("start_at")
    private LocalDateTime startAt;

    @JsonProperty("paused_at")
    private LocalDateTime pausedAt;

    @JsonProperty("finished_at")
    private LocalDateTime finishedAt;

    @JsonProperty("failed_reason")
    private String failedReason;

    @JsonProperty("paused_by")
    private String pausedBy;

    @JsonProperty("index")
    private int index;

    public RouteNodeState() {
        this.id = UUID.randomUUID().toString();
        this.status = Status.RUNNING;
        this.startAt = LocalDateTime.now();
        this.index = 1;
    }

    public void setFinished(NodeRunResult runResult) {
        if (status == Status.SUCCESS || status == Status.FAILED) {
            throw new RuntimeException("Route state " + id + " already finished");
        }

        if (runResult.getStatus() == NodeRunStatus.SUCCESS) {
            status = Status.SUCCESS;
        } else if (runResult.getStatus() == NodeRunStatus.FAILED) {
            status = Status.FAILED;
            failedReason = runResult.getError();
        } else {
            throw new RuntimeException("Invalid route status " + runResult.getStatus());
        }

        nodeRunResult = runResult;
        finishedAt = LocalDateTime.now();
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public NodeRunResult getNodeRunResult() {
        return nodeRunResult;
    }

    public void setNodeRunResult(NodeRunResult nodeRunResult) {
        this.nodeRunResult = nodeRunResult;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartAt() {
        return startAt;
    }

    public void setStartAt(LocalDateTime startAt) {
        this.startAt = startAt;
    }

    public LocalDateTime getPausedAt() {
        return pausedAt;
    }

    public void setPausedAt(LocalDateTime pausedAt) {
        this.pausedAt = pausedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public String getFailedReason() {
        return failedReason;
    }

    public void setFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public String getPausedBy() {
        return pausedBy;
    }

    public void setPausedBy(String pausedBy) {
        this.pausedBy = pausedBy;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}