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

public class TraceQueueManager {
    private Long userId;

    private Long conversationId;

    private Queue<TraceTask> traceManagerQueue = new ConcurrentLinkedQueue<>();
    /**
     * 最大批量处理数量
     */
    private int batchSize = 100;
    /**
     * 批量任务处理间隔
     */
    private long interval = 5000; // 模拟间隔时间（单位：毫秒），原Python代码中是5秒，这里转换为毫秒
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
        if (timerThread == null ||!timerThread.isAlive()) {
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
            System.out.println("模拟发送任务数据到Celery: " + traceInfo.getClass().getSimpleName() + ", " + traceInfo.getMessageData());
        }
    }
}

@Data
class TraceTask {
    private String traceType;
    private String messageId;
    private WorkflowRuns workflowRun;
    private Long userId;
    private Long conversationId;
    private Object timer;
    private Map<String, Object> kwargs = new HashMap<>();
    private String fileBaseUrl = "http://127.0.0.1:5001";

    private WorkflowTraceInfo workflowTrace(WorkflowRuns workflowRun, Long conversationId, Long userId) {
        // 模拟构建WorkflowTraceInfo并返回，实际需要根据业务逻辑完善数据填充
        return new WorkflowTraceInfo(workflowRun, conversationId, userId);
    }

    public BaseTraceInfo execute() {
        return preprocess();
    }

    private BaseTraceInfo preprocess() {
        switch (traceType) {
            case TraceTaskConstants.WORKFLOW_TRACE:
                return workflowTrace(workflowRun, conversationId, userId);
            default:
                return null;
        }
    }
}