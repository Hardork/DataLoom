package com.hwq.dataloom.core.workflow.graph_engine.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hwq.dataloom.core.workflow.entitys.NodeRunResult;
import com.hwq.dataloom.core.workflow.enums.NodeRunStatus;
import lombok.Data;
import lombok.Getter;

/**
 * 路径节点状态
 */
@Data
public class RouteNodeState {

    enum Status {
        RUNNING,
        SUCCESS,
        FAILED,
        PAUSED
    }

    private String id;

    private String nodeId;

    private NodeRunResult nodeRunResult;

    private Status status;

    private LocalDateTime startAt;

    private LocalDateTime pausedAt;

    private LocalDateTime finishedAt;

    private String failedReason;

    private String pausedBy;

    private int index;

    public RouteNodeState() {
        this.id = UUID.randomUUID().toString();
        this.status = Status.RUNNING;
        this.startAt = LocalDateTime.now();
        this.index = 1;
    }

    public RouteNodeState(String nodeId, LocalDateTime startAt) {
        this.nodeId = nodeId;
        this.startAt = startAt;
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
}