package com.hwq.dataloom.core.workflow.entitys;

import com.hwq.dataloom.core.file.File;
import com.hwq.dataloom.core.ops.TraceQueueManager;
import com.hwq.dataloom.core.workflow.config.WorkflowConfig;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 工作流生成信息实体
 * @Funtion: 主要用于存储工作流运行时需要的信息
 * @DateTime: 2024/11/27 17:17
 **/
@Data
@Builder
public class WorkflowGenerateEntity {
    /**
     * 任务ID UUID
     */
    private String taskId;

    /**
     * 用户输入
     */
    private Map<String, Object> inputs;

    /**
     * 文件列表
     */
    private List<File> files;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 是否为流返回
     */
    private boolean stream;

    /**
     * 最大调用深度
     */
    private int callDepth;

    /**
     * 跟踪任务队列
     */
    private TraceQueueManager traceQueueManager;

    /**
     * 工作流配置
     */
    private WorkflowConfig workflowConfig;

    /**
     * 工作流运行ID
     */
    private String workflowRunId;
}
