package com.hwq.dataloom.model.dto.workflow;

import com.hwq.dataloom.framework.request.PageRequest;
import lombok.Data;

/**
 * 查询工作流列表请求类
 */
@Data
public class QueryWorkflowDTO extends PageRequest {
    private String workflowName;
}
