package com.hwq.dataloom.core.workflow.config;

import com.hwq.dataloom.core.workflow.entitys.WorkflowAdditionalFeatures;
import com.hwq.dataloom.core.workflow.entitys.WorkflowConfig;
import com.hwq.dataloom.model.entity.Workflow;

/**
 * @Author: HWQ
 * @Description: 工作流配置管理类
 * @DateTime: 2024/11/26 16:40
 **/
public class WorkflowAppConfigManager {
    public static WorkflowConfig getWorkflowConfig(Workflow workflow) {
        String features = workflow.getFeatures();
        WorkflowConfig.builder()
                .userId(workflow.getUserId())
                .variables(WorkflowVariablesConfigManager.convert(workflow))
                .workflowId(workflow.getWorkflowId())
                .workflowAdditionalFeatures(WorkflowAdditionalFeatures)
                .build();
    }
}
