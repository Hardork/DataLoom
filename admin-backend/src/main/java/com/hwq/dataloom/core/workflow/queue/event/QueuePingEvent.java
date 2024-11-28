package com.hwq.dataloom.core.workflow.queue.event;

/**
 * @author HWQ
 * @date 2024/11/28 23:55
 * @description
 */
public class QueuePingEvent extends BaseQueueEvent {
    public QueuePingEvent() {
        super(QueueEventEnum.PING);
    }
}
