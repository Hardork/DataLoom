package com.hwq.dataloom.core.workflow.queue.message;

import com.hwq.dataloom.core.workflow.queue.event.BaseQueueEvent;
import lombok.Builder;
import lombok.Data;

/**
 * @author HWQ
 * @date 2024/11/29 23:04
 * @description 队列消息基类
 */
@Builder
@Data
public class WorkflowQueueMessage {

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 队列推送事件
     */
    private BaseQueueEvent event;
}
