package com.hwq.dataloom.core.ops;

import com.hwq.dataloom.core.ops.constants.TraceTaskConstants;
import com.hwq.dataloom.core.ops.entitys.BaseTraceInfo;
import com.hwq.dataloom.core.ops.entitys.WorkflowTraceInfo;
import com.hwq.dataloom.model.entity.WorkflowRuns;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @Author: HWQ
 * @Description:
 * @DateTime: 2024/11/26 9:40
 **/
@Data
public class TraceQueueManager {
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 对话ID
     */
    private Long conversationId;

    /**
     * 跟踪任务存储队列
     */
    private Queue<TraceTask> traceManagerQueue = new ConcurrentLinkedQueue<>();

    /**
     * 最大批量处理数量
     */
    private int batchSize = 100;

    /**
     * 批量任务处理间隔
     */
    private long interval = 5000;

    /**
     * 触发任务线程
     */
    private Thread timerThread;

    public TraceQueueManager(Long userId, Long conversationId) {
        this.userId = userId;
        this.conversationId = conversationId;
        startTimer();
    }

    public void addTraceTask(TraceTask traceTask) {
        traceManagerQueue.add(traceTask);
        startTimer();
    }

    private void startTimer() {
        if (timerThread == null || !timerThread.isAlive()) {
            timerThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(interval);
                        run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            timerThread.start();
        }
    }

    public List<TraceTask> collectTasks() {
        List<TraceTask> tasks = new ArrayList<>();
        while (tasks.size() < batchSize &&!traceManagerQueue.isEmpty()) {
            TraceTask task = traceManagerQueue.poll();
            if (task!= null) {
                tasks.add(task);
            }
        }
        return tasks;
    }

    public void run() {
        List<TraceTask> tasks = collectTasks();
        if (!tasks.isEmpty()) {
            sendToCelery(tasks);
        }
    }

    private void sendToCelery(List<TraceTask> tasks) {
        for (TraceTask task : tasks) {
            BaseTraceInfo traceInfo = task.execute();
            // 这里模拟处理发送到Celery相关的数据，实际需要对接对应Java的任务队列框架等
            // TODO: 将当前的任务数据发送到消息队列
            System.out.println("模拟发送任务数据到Celery: " + traceInfo.getClass().getSimpleName() + ", " + traceInfo.getMessageData());
        }
    }
}

