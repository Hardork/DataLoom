package com.hwq.dataloom.model.dto.workflow;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 新增工作流请求
 */
@Data
public class AddWorkflowDTO {
    /**
     * 工作流名称
     */
    @TableField(value = "workflowName")
    private String workflowName;

    /**
     * 工作流图标
     */
    @TableField(value = "workflowIcon")
    private String workflowIcon;

    /**
     * 工作流作用描述
     */
    @TableField(value = "description")
    private String description;

    /**
     * 工作流类型
     */
    @TableField(value = "type")
    private String type;
}
