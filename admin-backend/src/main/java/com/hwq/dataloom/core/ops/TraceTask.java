package com.hwq.dataloom.core.ops;

import com.hwq.dataloom.core.ops.constants.TraceTaskConstants;
import com.hwq.dataloom.core.ops.entitys.BaseTraceInfo;
import com.hwq.dataloom.core.ops.entitys.WorkflowTraceInfo;
import com.hwq.dataloom.model.entity.WorkflowRuns;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 跟踪任务
 */
@Data
public class TraceTask {
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

    /**
     * 执行跟踪任务
     * @return 跟踪实体
     */
    public BaseTraceInfo execute() {
        return preprocess();
    }

    /**
     * 根据标识返回跟踪实体
     * @return 跟踪实体
     */
    private BaseTraceInfo preprocess() {
        if (traceType.equals(TraceTaskConstants.WORKFLOW_TRACE)) {
            return workflowTrace(workflowRun, conversationId, userId);
        }
        return null;
    }
}