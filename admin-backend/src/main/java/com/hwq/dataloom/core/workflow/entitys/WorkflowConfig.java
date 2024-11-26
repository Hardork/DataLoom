package com.hwq.dataloom.core.workflow.entitys;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Author: HWQ
 * @Description: 工作流规则配置类
 * @DateTime: 2024/11/26 16:48
 **/
@Data
@Builder
public class WorkflowConfig {

    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 变量规则集合
     */
    private List<VariableEntity> variables;

    /**
     * 额外的规则
     */
    private WorkflowAdditionalFeatures workflowAdditionalFeatures;
}
