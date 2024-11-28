package com.hwq.dataloom.core.workflow.queue.event;

/**
 * @author HWQ
 * @date 2024/11/28 20:59
 * @description 工作流队列事件基类
 */
public class BaseQueueEvent {
    private QueueEventEnum event;

    public BaseQueueEvent(QueueEventEnum event) {
        this.event = event;
    }

    public QueueEventEnum getEvent() {
        return event;
    }

    public void setEvent(QueueEventEnum event) {
        this.event = event;
    }
}
