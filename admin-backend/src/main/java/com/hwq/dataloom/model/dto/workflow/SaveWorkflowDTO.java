package com.hwq.dataloom.model.dto.workflow;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author HWQ
 * @date 2024/11/18 23:18
 * @description
 */
@Data
public class SaveWorkflowDTO {
    /**
     * 工作流id
     */
    private String workflowId;

    /**
     * 工作流画布任务
     */
    private String graph;

    /**
     * 画布草稿对应的哈希值
     */
    private String hashUnique;

    /**
     * 画布特征
     */
    private String features;

    /**
     * 环境变量（JSON格式）
     */
    private String envVariables;

    /**
     * 对话变量（JSON格式）
     */
    private String conversationVariables;
}
