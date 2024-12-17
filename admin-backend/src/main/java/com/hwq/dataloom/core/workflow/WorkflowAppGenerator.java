package com.hwq.dataloom.core.workflow;

import com.hwq.dataloom.core.ops.TraceQueueManager;
import com.hwq.dataloom.core.workflow.config.WorkflowAppConfigManager;
import com.hwq.dataloom.core.workflow.config.WorkflowConfig;
import com.hwq.dataloom.core.workflow.entitys.WorkflowGenerateEntity;
import com.hwq.dataloom.core.workflow.enums.UserFrom;
import com.hwq.dataloom.core.workflow.queue.WorkflowQueueManager;
import com.hwq.dataloom.core.workflow.runner.WorkflowRunner;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.entity.Workflow;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * @Author: HWQ
 * @Description: 工作流生成核心处理类
 * @DateTime: 2024/11/25 15:59
 **/
@Component
public class WorkflowAppGenerator {

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Resource
    private WorkflowRunner workflowBaseRunner;

    public void generate(Workflow workflow, User user, Map<String, Object> args, boolean stream, int callDepth, String workflowThreadPoolId) throws Exception {
        // TODO: 处理文件相关的参数，暂时不做
        // 获取工作流的配置
        WorkflowConfig workflowConfig = WorkflowAppConfigManager.getWorkflowConfig(workflow);
        // 初始化跟踪队列
        Long userId = user.getId();
        TraceQueueManager traceQueueManager = new TraceQueueManager(userId, null);
        // TODO: inputs外层需要校验
        Map<String, Object> inputs = (Map<String, Object>) args.get("input");
        // TODO: 创建线程池
        String workflowRunId = UUID.randomUUID().toString();
        WorkflowGenerateEntity workflowGenerateEntity = WorkflowGenerateEntity.builder()
                .userId(userId)
                .workflowRunId(workflowRunId)
                .workflowConfig(workflowConfig)
                .inputs(inputs)
                .stream(stream)
                .traceQueueManager(traceQueueManager)
                .callDepth(callDepth)
                .files(new ArrayList<>())
                .taskId(UUID.randomUUID().toString())
                .traceQueueManager(traceQueueManager)
                .userFrom(UserFrom.ACCOUNT) // 先写死，后续通过标识判断用户来源
                .build();
        runByGenerateEntity(workflow, user, workflowGenerateEntity, stream, workflowThreadPoolId);
    }

    /**
     * 根据生成响应实体运行任务
     * @param workflow 工作流
     * @param user 用户
     * @param workflowGenerateEntity 工作流生成响应实体
     * @param stream 是否是流式返回
     * @param threadPoolId 线程池ID
     * @throws Exception 异常
     */
    public void runByGenerateEntity(Workflow workflow, User user, WorkflowGenerateEntity workflowGenerateEntity, boolean stream, String threadPoolId) throws Exception {
        // get workflowQueueManager (管理工作流队列任务)
        WorkflowQueueManager workflowQueueManager = new WorkflowQueueManager(
                workflowGenerateEntity.getTaskId(),
                user.getId(),
                redisTemplate
        );
        // TODO: 改为线程池管理
        new Thread(() -> {
            workflowThreadWorker(workflowGenerateEntity, workflowQueueManager, workflow);
        });

    }

    /**
     * 工作流线程运行任务
     * @param workflowGenerateEntity 工作流生成实体
     * @param queueManager 工作流任务队列
     * @param workflow 工作流
     */
    public void workflowThreadWorker(WorkflowGenerateEntity workflowGenerateEntity, WorkflowQueueManager queueManager, Workflow workflow) {
        // TODO：完善工作流工作线程
        workflowBaseRunner.run(workflowGenerateEntity, queueManager, workflow);
    }
}
