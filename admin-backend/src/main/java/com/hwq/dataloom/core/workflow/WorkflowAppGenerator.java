package com.hwq.dataloom.core.workflow;

import com.hwq.dataloom.core.ops.TraceQueueManager;
import com.hwq.dataloom.core.workflow.config.WorkflowAppConfigManager;
import com.hwq.dataloom.core.workflow.entitys.WorkflowConfig;
import com.hwq.dataloom.core.workflow.entitys.WorkflowGenerateEntity;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.entity.Workflow;
import org.springframework.stereotype.Component;

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

    public void generate(Workflow workflow, User user, Map<String, Object> args, boolean stream, int callDepth, String workflowThreadPoolId) throws Exception {
        // TODO: 处理文件相关的参数，暂时不做

        // 获取工作流的配置
        WorkflowConfig workflowConfig = WorkflowAppConfigManager.getWorkflowConfig(workflow);
        // 初始化跟踪队列
        Long userId = user.getId();
        TraceQueueManager traceQueueManager = new TraceQueueManager(userId, null);
        // TODO: 初始化工作流应用实体
        // TODO: inputs外层需要校验
        Map<String, Object> inputs = (Map<String, Object>) args.get("input");
        // TODO: 创建线程池
        String workflowRunId = UUID.randomUUID().toString();
        WorkflowGenerateEntity.builder()
                .userId(userId)
                .workflowRunId(workflowRunId)
                .workflowConfig(workflowConfig)
                .inputs(inputs)
                .stream(stream)
                .callDepth(callDepth)
                .files(new ArrayList<>()) // TODO: 等待后续处理文件完善，预计11/30完成
                .build();
    }
}
