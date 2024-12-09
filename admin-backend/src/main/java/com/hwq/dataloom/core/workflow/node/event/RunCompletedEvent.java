package com.hwq.dataloom.core.workflow.node.event;

import com.hwq.dataloom.core.workflow.entitys.NodeRunResult;
import com.hwq.dataloom.core.workflow.graph_engine.entities.event.BaseNodeEvent;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: HWQ
 * @Description:
 * @DateTime: 2024/12/9 14:37
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class RunCompletedEvent extends BaseNodeEvent {
    private NodeRunResult runResult;

    public RunCompletedEvent(NodeRunResult runResult) {
        this.runResult = runResult;
    }
}
