package com.hwq.dataloom.core.workflow.queue.event;

/**
 * @author HWQ
 * @date 2024/11/29 23:20
 * @description 消息终止事件
 */
public class QueueMessageEndEvent extends BaseQueueEvent{
    public QueueMessageEndEvent() {
        super(QueueEventEnum.MESSAGE_END);
    }
}
