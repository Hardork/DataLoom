package com.hwq.dataloom.core.workflow.graph;

import com.hwq.dataloom.core.workflow.edge.GraphEdge;
import com.hwq.dataloom.core.workflow.node.Node;
import com.hwq.dataloom.core.workflow.node.end.EndStreamParam;
import com.hwq.dataloom.core.workflow.runner.AnswerStreamGeneratorRouter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/12/15 23:15
 * @description 画布运行实体
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GraphRunEntity {
    /**
     * 画布跟节点ID
     */
    private String rootNodeId;

    /**
     * 画布节点ID集合
     */
    private List<String> nodeIds;

    /**
     * 节点配置映射
     */
    private Map<String, Node> nodeIdConfigMapping;

    /**
     * 边映射
     */
    private Map<String, List<GraphEdge>> endEdgeMapping;

    /**
     * 并行关系映射
     */
    private Map<String, GraphParallel> parallelMapping;

    /**
     * 节点并行关系映射
     */
    private Map<String, String> nodeParallelMap;

    /**
     * 响应流生成路由
     */
    private AnswerStreamGeneratorRouter answerStreamGeneratorRouter;

    /**
     * end流参数
     */
    private EndStreamParam endStreamParam;
}
