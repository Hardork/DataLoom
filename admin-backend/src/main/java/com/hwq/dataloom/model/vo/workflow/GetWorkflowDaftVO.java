package com.hwq.dataloom.model.vo.workflow;

import com.hwq.dataloom.model.json.workflow.Graph;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 工作流草稿返回类
 */
@Data
public class GetWorkflowDaftVO {
    /**
     * 画布配置（JSON格式）
     */
    private Graph graph;

    /**
     * 功能特性相关数据（JSON格式）
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

    /**
     * 画布哈希值（用于判断是否变更）
     */
    private String uniqueHash;
}
