package com.hwq.dataloom.core.workflow.queue.event;

/**
 * @author HWQ
 * @date 2024/11/29 23:21
 * @description 工作流成功执行完毕事件
 */
public class QueueWorkflowSucceededEvent extends BaseQueueEvent {
    public QueueWorkflowSucceededEvent() {
        super(QueueEventEnum.WORKFLOW_SUCCEEDED);
    }
}
