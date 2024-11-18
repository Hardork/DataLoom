package com.hwq.dataloom.model.dto.workflow;

import lombok.Data;

/**
 * 更新工作流请求类
 */
@Data
public class UpdateWorkflowDTO {
    /**
     * 主键
     */
    private Long workflowId;

    /**
     * 工作流名称
     */
    private String workflowName;

    /**
     * 工作流图标
     */
    private String workflowIcon;

    /**
     * 工作流作用描述
     */
    private String description;
}
