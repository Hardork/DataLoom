package com.hwq.dataloom.core.workflow;

import com.hwq.dataloom.core.ops.TraceQueueManager;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.entity.Workflow;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 工作流应用生成器
 * @DateTime: 2024/11/25 15:59
 **/
@Component
public class WorkflowAppGenerator {

    public void generate(Workflow workflow, User user, Map<String, Object> args, boolean stream, int callDepth, String workflowThreadPoolId) throws Exception {
        // TODO: 处理文件相关的参数，暂时不做

        // TODO: 获取工作流的配置

        // TODO: 初始化跟踪队列
        Long userId = user.getId();
        TraceQueueManager traceQueueManager = new TraceQueueManager(userId, null);
        // TODO: 初始化工作流应用实体

        // TODO: 创建线程池
    }
}
