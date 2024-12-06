package com.hwq.dataloom.model.dto.workflow;

import com.hwq.dataloom.core.workflow.entitys.graph.Graph;
import lombok.Data;

import java.util.List;
import java.util.Map;

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
    private Long workflowId;

    /**
     * 工作流画布任务
     */
    private Graph graph;

    /**
     * 画布草稿对应的哈希值
     */
    private String hashUnique;

    /**
     * 画布特征
     */
    private Map<String, Object> features;

    /**
     * 环境变量（JSON格式）
     */
    private List<String> envVariables;

    /**
     * 对话变量（JSON格式）
     */
    private List<String> conversationVariables;
}
