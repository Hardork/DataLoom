package com.hwq.dataloom.model.vo.workflow;

import lombok.Builder;
import lombok.Data;

/**
 * 工作流列表查询返回类
 */
@Data
@Builder
public class WorkflowVO {
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

    /**
     * 工作流类型
     */
    private String type;

    /**
     * 版本信息
     */
    private String version;
}
