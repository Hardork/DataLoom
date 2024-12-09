package com.hwq.dataloom.core.workflow.graph_engine;

import com.hwq.dataloom.core.workflow.enums.UserFrom;
import com.hwq.dataloom.model.enums.workflow.WorkflowTypeEnum;
import lombok.Data;

import java.util.Map;

/**
 * @author HWQ
 * @date 2024/12/8 14:21
 * @description graph初始参数
 */
@Data
public class GraphInitParams {
    private WorkflowTypeEnum workflowType;
    private String workflowId;
    private Map<String, Object> graphConfig;
    private String userId;
    private UserFrom userFrom;
    private int callDepth;
}
