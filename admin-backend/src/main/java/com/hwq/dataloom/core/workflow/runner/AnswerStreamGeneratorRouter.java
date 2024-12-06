package com.hwq.dataloom.core.workflow.runner;

import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import com.hwq.dataloom.core.workflow.entitys.edge.GraphEdge;
import com.hwq.dataloom.core.workflow.entitys.node.Node;
import com.hwq.dataloom.core.workflow.entitys.node.answer.GenerateRouteChunk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/11/24 20:23
 * @description 响应流编排
 */
public class AnswerStreamGeneratorRouter {

    public static AnswerStreamGeneratorRouter init(Map<String, Node> nodeIdConfigMapping, Map<String, List<GraphEdge>> endEdgeMapping) {
        Map<String, List<GenerateRouteChunk>> answerGenerateRoute = new HashMap<>();
        for (Map.Entry<String, Node> entry : nodeIdConfigMapping.entrySet()) {
            String answerNodeId = entry.getKey();
            Node nodeConfig = entry.getValue();
            if (nodeConfig.getData().get("type") != NodeTypeEnum.ANSWER.getValue()) {
                continue;
            }
            extractGenerateRouteSelectors(nodeConfig);
        }
        return null;
    }

    private static void extractGenerateRouteSelectors(Node nodeConfig) {
        // TODO：搞懂 node_data = AnswerNodeData(**config.get("data", {}))的作用
    }
}
