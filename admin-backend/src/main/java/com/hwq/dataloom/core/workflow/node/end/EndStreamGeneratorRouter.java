package com.hwq.dataloom.core.workflow.node.end;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.core.workflow.edge.GraphEdge;
import com.hwq.dataloom.core.workflow.node.Node;
import com.hwq.dataloom.core.workflow.variable.VariableSelector;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * End流生成路由
 */
public class EndStreamGeneratorRouter {

    /**
     * 初始化方法
     * @param nodeIdConfigMapping 节点配置映射
     * @param reverseEdgeMapping 边映射
     * @param nodeParallelMapping 节点并行关心映射
     * @return End流参数
     */
    public static EndStreamParam init(
            Map<String, Node> nodeIdConfigMapping,
            Map<String, List<GraphEdge>> reverseEdgeMapping,
            Map<String, String> nodeParallelMapping) {
        // 解析结束节点的流输出节点值选择器
        Map<String, List<List<String>>> endStreamVariableSelectorsMapping = new HashMap<>();
        for (Map.Entry<String, Node> entry : nodeIdConfigMapping.entrySet()) {
            String endNodeId = entry.getKey();
            Node nodeConfig = entry.getValue();

            // 跳过非结束节点和并行节点
            if (!nodeConfig.getData().getOrDefault("type", "").equals(NodeTypeEnum.END.getValue())
                    || nodeParallelMapping.containsKey(endNodeId)) {
                continue;
            }

            // 获取流输出的生成路由
            List<List<String>> streamVariableSelectors = extractStreamVariableSelector(nodeIdConfigMapping, nodeConfig);
            endStreamVariableSelectorsMapping.put(endNodeId, streamVariableSelectors);
        }

        // 获取结束节点的依赖关系
        List<String> endNodeIds = new ArrayList<>(endStreamVariableSelectorsMapping.keySet());
        Map<String, List<String>> endDependencies = fetchEndDependencies(
                endNodeIds,
                reverseEdgeMapping,
                nodeIdConfigMapping);

        return new EndStreamParam(
                endStreamVariableSelectorsMapping,
                endDependencies
        );
    }

    // 从节点数据中提取流变量选择器
    public static List<List<String>> extractStreamVariableSelectorFromNodeData(
            Map<String, Node> nodeIdConfigMapping,
            EndNodeData nodeData) {
        List<VariableSelector> variableSelectors = nodeData.getOutputs();

        List<List<String>> valueSelectors = new ArrayList<>();
        for (VariableSelector variableSelector : variableSelectors) {
            if (variableSelector == null) {
                continue;
            }

            String nodeId = variableSelector.getValueSelector().get(0);
            if (!nodeId.equals("sys") && nodeIdConfigMapping.containsKey(nodeId)) {
                Node node = nodeIdConfigMapping.get(nodeId);
                String nodeType = (String) node.getData().getOrDefault("type", "");
                if (!valueSelectors.contains(variableSelector.getValueSelector())
                        && nodeType.equals(NodeTypeEnum.LLM.getValue())
                        && variableSelector.getValueSelector().get(1).equals("text")) {
                    valueSelectors.add(variableSelector.getValueSelector());
                }
            }
        }
        return valueSelectors;
    }

    // 从节点配置中提取流变量选择器
    public static List<List<String>> extractStreamVariableSelector(
            Map<String, Node> nodeIdConfigMapping,
            Node node) {
        EndNodeData endNodeData = JSONUtil.toBean(JSONUtil.toJsonStr(node.getData()), EndNodeData.class);
        return extractStreamVariableSelectorFromNodeData(nodeIdConfigMapping, endNodeData);
    }

    // 获取结束节点的依赖关系
    public static Map<String, List<String>> fetchEndDependencies(
            List<String> endNodeIds,
            Map<String, List<GraphEdge>> reverseEdgeMapping,
            Map<String, Node> nodeIdConfigMapping) {
        Map<String, List<String>> endDependencies = new HashMap<>();
        for (String endNodeId : endNodeIds) {
            endDependencies.computeIfAbsent(endNodeId, k -> new ArrayList<>());
            recursiveFetchEndDependencies(
                    endNodeId,
                    endNodeId,
                    nodeIdConfigMapping,
                    reverseEdgeMapping,
                    endDependencies);
        }
        return endDependencies;
    }

    // 递归获取结束节点的依赖关系
    public static void recursiveFetchEndDependencies(
            String currentNodeId,
            String endNodeId,
            Map<String, Node> nodeIdConfigMapping,
            Map<String, List<GraphEdge>> reverseEdgeMapping,
            Map<String, List<String>> endDependencies) {
        List<GraphEdge> reverseEdges = reverseEdgeMapping.getOrDefault(currentNodeId, new ArrayList<>());
        for (GraphEdge edge : reverseEdges) {
            String sourceNodeId = edge.getSourceNodeId();
            Node node = nodeIdConfigMapping.get(sourceNodeId);
            String sourceNodeType = node != null ? (String) node.getData().getOrDefault("type", "") : "";
            if (sourceNodeType.equals(NodeTypeEnum.IF_ELSE.getValue())
                    || sourceNodeType.equals(NodeTypeEnum.QUESTION_CLASSIFIER.getValue())) {
                endDependencies.get(endNodeId).add(sourceNodeId);
            } else {
                recursiveFetchEndDependencies(
                        sourceNodeId,
                        endNodeId,
                        nodeIdConfigMapping,
                        reverseEdgeMapping,
                        endDependencies);
            }
        }
    }
}