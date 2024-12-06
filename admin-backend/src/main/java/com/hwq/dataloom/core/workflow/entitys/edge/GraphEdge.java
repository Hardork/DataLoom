package com.hwq.dataloom.core.workflow.entitys.edge;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HWQ
 * @date 2024/11/23 17:26
 * @description 运行图表边
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphEdge {

    private String sourceNodeId;

    private String targetNodeId;

    private RunCondition runCondition;
}
