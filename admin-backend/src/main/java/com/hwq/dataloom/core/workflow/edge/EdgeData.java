package com.hwq.dataloom.core.workflow.edge;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/11/23 17:17
 * @description
 */
@Data
public class EdgeData {
    private String sourceNodeId;

    private String targetNodeId;

    private RunCondition runCondition;
}
