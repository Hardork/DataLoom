package com.hwq.dataloom.model.vo.workflow;

import lombok.Builder;
import lombok.Data;

/**
 * 保存工作流草稿返回类
 */
@Data
@Builder
public class SaveWorkflowDraftVO {
    private String status;
    private String uniqueHash;
}
