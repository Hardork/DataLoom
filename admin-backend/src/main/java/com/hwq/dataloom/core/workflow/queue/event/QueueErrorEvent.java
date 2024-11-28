package com.hwq.dataloom.core.workflow.queue.event;

/**
 * @author HWQ
 * @date 2024/11/29 00:02
 * @description 队列异常事件
 */
public class QueueErrorEvent extends BaseQueueEvent{
    private String errorMsg;
    public QueueErrorEvent(Exception e) {
        super(QueueEventEnum.ERROR);
        this.errorMsg = e.getMessage();
    }
}
