package com.hwq.dataloom.core.workflow.queue;

import com.hwq.dataloom.core.workflow.enums.PublishFrom;
import com.hwq.dataloom.core.workflow.queue.event.*;
import com.hwq.dataloom.core.workflow.queue.message.WorkflowQueueMessage;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.BlockingQueue;

/**
 * @author HWQ
 * @date 2024/11/29 23:00
 * @description 工作流队列管理类
 */
public class WorkflowQueueManager extends AppQueueManager{


    public WorkflowQueueManager(String taskId, Long userId, RedisTemplate<String, String> redisTemplate) {
        super(taskId, userId, redisTemplate);
    }

    /**
     * 推送事件到队列
     * @param event 事件
     * @param pubFrom 事件来源
     */
    @Override
    protected void _publish(BaseQueueEvent event, PublishFrom pubFrom) throws InterruptedException {
        WorkflowQueueMessage workflowQueueMessage = WorkflowQueueMessage.builder()
                .taskId(getTaskId())
                .event(event)
                .build();
        BlockingQueue<Object> queue = getQueue();
        queue.put(workflowQueueMessage);
        if (isStopListenEvent(event)) { // 判断是否需要停止监听队列
            stopListen();
        }
        if (pubFrom == PublishFrom.APPLICATION_MANAGER && isStopped()) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "生成任务被停止");
        }

    }

    /**
     * 判断是否是停止监听队列事件
     * @param baseQueueEvent 队列事件
     * @return 是否是停止监听队列事件
     */
    private boolean isStopListenEvent(BaseQueueEvent baseQueueEvent) {
        return (
                baseQueueEvent instanceof QueueStopEvent ||
                baseQueueEvent instanceof QueueErrorEvent ||
                baseQueueEvent instanceof QueueWorkflowFailedEvent ||
                baseQueueEvent instanceof QueueWorkflowSucceededEvent ||
                baseQueueEvent instanceof  QueueMessageEndEvent
        );
    }
}
