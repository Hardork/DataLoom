package com.hwq.dataloom.model.dto.workflow;

import lombok.Data;

import java.util.Map;

/**
 * @author HWQ
 * @date 2024/11/21 23:24
 * @description 运行工作流请求类
 */
@Data
public class RunWorkflowDraftDTO {
    /**
     * 工作流ID
     */
    private Long workflowId;

    /**
     * 输入参数
     */
    private Map<String, String> inputs;
}
