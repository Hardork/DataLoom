package com.hwq.dataloom.core.workflow.queue.event;

/**
 * @author HWQ
 * @date 2024/11/29 23:22
 * @description 工作流执行失败事件
 */
public class QueueWorkflowFailedEvent extends BaseQueueEvent {
    public QueueWorkflowFailedEvent() {
        super(QueueEventEnum.WORKFLOW_FAILED);
    }
}
