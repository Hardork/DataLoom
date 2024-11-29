package com.hwq.dataloom.core.workflow.queue;

import com.hwq.dataloom.core.workflow.enums.PublishFrom;
import com.hwq.dataloom.core.workflow.queue.event.BaseQueueEvent;
import com.hwq.dataloom.core.workflow.queue.event.QueueErrorEvent;
import com.hwq.dataloom.core.workflow.queue.event.QueuePingEvent;
import com.hwq.dataloom.core.workflow.queue.event.QueueStopEvent;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 工作流监听队列基类
 */
@Data
public abstract class AppQueueManager {
    private final String taskId;
    private final Long userId;
    private final BlockingQueue<Object> queue;

    private final long NAX_EXECUTION_TIMEOUT = 1200;

    private final RedisTemplate<String, String> redisTemplate;

    public AppQueueManager(String taskId, Long userId, RedisTemplate<String, String> redisTemplate) {
        if (userId == null) {
            throw new IllegalArgumentException("User is required");
        }
        redisTemplate.opsForValue().set(generateTaskBelongCacheKey(taskId), "end-user-" + userId, 1800);
        this.taskId = taskId;
        this.userId = userId;
        this.queue = new ArrayBlockingQueue<>(100);
        this.redisTemplate = redisTemplate;
    }

    public List<Object> listen() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long lastPingTime = 0;
        List<Object> resultList = new ArrayList<>();
        while (true) {
            try {
                Object message = queue.poll(1, TimeUnit.SECONDS);
                if (message == null) {
                    break;
                }
                resultList.add(message);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw e;
            } finally {
                long elapsedTime = System.currentTimeMillis() - startTime;
                if (elapsedTime >= NAX_EXECUTION_TIMEOUT || isStopped()) {
                    // 发布两个消息以确保客户端能接收到停止信号，并在停止信号处理后停止监听
                    publish(new QueueStopEvent(QueueStopEvent.StopBy.USER_MANUAL), PublishFrom.TASK_PIPELINE);
                    publish(new QueueStopEvent(QueueStopEvent.StopBy.USER_MANUAL), PublishFrom.TASK_PIPELINE);
                }
                if (elapsedTime / 10000 > lastPingTime) {
                    publish(new QueuePingEvent(), PublishFrom.TASK_PIPELINE);
                    lastPingTime = elapsedTime / 10000;
                }
            }
        }
        return resultList;
    }

    public void stopListen() {
        try {
            queue.put(null);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void publishError(Exception e, PublishFrom pubFrom) throws InterruptedException {
        publish(new QueueErrorEvent(e), pubFrom);
    }

    public void publish(BaseQueueEvent event, PublishFrom pubFrom) throws InterruptedException {
        _publish(event, pubFrom);
    }

    protected abstract void _publish(BaseQueueEvent event, PublishFrom pubFrom) throws InterruptedException;

    public void setStopFlag(String taskId, String userId) {
        String result = redisTemplate.opsForValue().get(generateTaskBelongCacheKey(taskId));
        if (result == null) {
            return;
        }
        String userPrefix = "endUser";
        if (!result.equals(userPrefix + "-" + userId)) {
            return;
        }
        String stoppedCacheKey = generateStoppedCacheKey(taskId);
        redisTemplate.opsForValue().set(stoppedCacheKey,  "1", 600, TimeUnit.SECONDS);
    }

    /**
     * 判断任务是否被停止了
     * @return 结果
     */
    public boolean isStopped() {
        String stoppedCacheKey = generateStoppedCacheKey(taskId);
        String result = redisTemplate.opsForValue().get(stoppedCacheKey);
        return StringUtils.isEmpty(result);
    }

    /**
     * 生成存储任务归属的key标识
     * @param taskId 任务ID
     * @return key标识
     */
    private static String generateTaskBelongCacheKey(String taskId) {
        return "generate_task_belong:" + taskId;
    }

    /**
     * 生成停止任务的key标识
     * @param taskId 任务ID
     * @return key标识
     */
    private static String generateStoppedCacheKey(String taskId) {
        return "generate_task_stopped:" + taskId;
    }

    private void checkForSqlAlchemyModels(Object data) {
        if (data instanceof java.util.Map) {
            java.util.Map<?,?> mapData = (java.util.Map<?,?>) data;
            for (Object value : mapData.values()) {
                checkForSqlAlchemyModels(value);
            }
        } else if (data instanceof java.util.List) {
            java.util.List<?> listData = (java.util.List<?>) data;
            for (Object item : listData) {
                checkForSqlAlchemyModels(item);
            }
        } else {
            // 这里假设不存在类似Python中 SQLAlchemy 模型相关的情况判断（因为Java中没有直接对应概念），可根据实际需求调整
            // 如果有类似的模型判断逻辑，可以在这里进行相应的类型检查和异常抛出
        }
    }
}



