package com.hwq.dataloom.core.workflow.runner;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.core.workflow.node.answer.AnswerNodeData;
import com.hwq.dataloom.core.workflow.node.answer.route_chunk.TextGenerateRouteChunk;
import com.hwq.dataloom.core.workflow.node.answer.route_chunk.VarGenerateRouteChunk;
import com.hwq.dataloom.core.workflow.prompt.PromptTemplateParser;
import com.hwq.dataloom.core.workflow.utils.VariableTemplateParser;
import com.hwq.dataloom.core.workflow.variable.VariableSelector;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import com.hwq.dataloom.core.workflow.edge.GraphEdge;
import com.hwq.dataloom.core.workflow.node.Node;
import com.hwq.dataloom.core.workflow.node.answer.route_chunk.GenerateRouteChunk;

import java.util.*;

/**
 * @author HWQ
 * @date 2024/11/24 20:23
 * @description 响应流编排
 */
public class AnswerStreamGeneratorRouter {

    /**
     * 提取生成路由选择器
     * @param nodeIdConfigMapping 节点配置
     * @param endEdgeMapping 边映射
     * @return 响应流生成器路由
     */
    public static AnswerStreamGeneratorRouter init(Map<String, Node> nodeIdConfigMapping, Map<String, List<GraphEdge>> endEdgeMapping) {
        Map<String, List<GenerateRouteChunk>> answerGenerateRoute = new HashMap<>();
        for (Map.Entry<String, Node> entry : nodeIdConfigMapping.entrySet()) {
            String answerNodeId = entry.getKey();
            Node nodeConfig = entry.getValue();
            if (nodeConfig.getData().get("type") != NodeTypeEnum.ANSWER.getValue()) {
                continue;
            }
            List<GenerateRouteChunk> generateRouteChunks = extractGenerateRouteSelectors(nodeConfig);
            answerGenerateRoute.put(answerNodeId, generateRouteChunks);
        }

        // 获取响应依赖
        List<String> answerNodeIds = new ArrayList<>(answerGenerateRoute.keySet());
        Map<String, List<String>> answersDependencies = fetchAnswersDependencies(answerNodeIds, endEdgeMapping, nodeIdConfigMapping);

        return null;
    }


    /**
     * 从节点数据中生产
     * @param nodeConfig 节点配置
     * @return 路由块
     */
    private static List<GenerateRouteChunk> extractGenerateRouteSelectors(Node nodeConfig) {
        Map<String, Object> data = nodeConfig.getData();
        AnswerNodeData answerNodeData = JSONUtil.toBean(JSONUtil.toJsonStr(data), AnswerNodeData.class);
        return extractGenerateRouteFromNodeData(answerNodeData);
    }


    /**
     * 从节点数据中提取生成路由
     *
     * @param nodeData 节点数据对象
     * @return 生成路由块的列表
     */
    public static List<GenerateRouteChunk> extractGenerateRouteFromNodeData(AnswerNodeData nodeData) {
        // 创建变量模板解析器，解析节点数据中的答案部分
        VariableTemplateParser variableTemplateParser = new VariableTemplateParser(nodeData.getAnswer());
        // 提取变量选择器
        List<VariableSelector> variableSelectors = variableTemplateParser.extractVariableSelectors();

        // 创建值选择器映射，键为变量，值为变量选择器
        Map<String, List<String>> valueSelectorMapping = new HashMap<>();
        for (VariableSelector variableSelector : variableSelectors) {
            valueSelectorMapping.put(variableSelector.getVariable(), variableSelector.getValueSelector());
        }

        // 获取变量选择器中的变量列表
        List<String> variableKeys = new ArrayList<>(valueSelectorMapping.keySet());

        // 创建提示模板解析器，解析节点数据中的答案部分，并设置是否包含变量模板
        PromptTemplateParser templateParser = new PromptTemplateParser(nodeData.getAnswer(), true);
        // 获取模板中的变量键列表
        List<String> templateVariableKeys = templateParser.getVariableKeys();

        // 取变量键列表和模板变量键列表的交集
        Set<String> intersection = new HashSet<>(variableKeys);
        intersection.retainAll(new HashSet<>(templateVariableKeys));
        variableKeys = new ArrayList<>(intersection);

        // 获取节点数据中的答案模板
        String template = nodeData.getAnswer();
        // 替换模板中的变量为特定格式
        for (String var : variableKeys) {
            template = template.replace("{{" + var + "}}", "Ω{{" + var + "}}Ω");
        }

        // 存储生成的路由
        List<GenerateRouteChunk> generateRoutes = new ArrayList<>();
        // 按特定分隔符分割模板，并处理每个部分
        for (String part : template.split("Ω")) {
            if (!part.isEmpty()) {
                // 判断部分是否为变量
                if (isVariable(part, variableKeys)) {
                    String varKey = part.replace("Ω", "").replace("{{", "").replace("}}", "");
                    List<String> valueSelector = valueSelectorMapping.get(varKey);
                    generateRoutes.add(new VarGenerateRouteChunk(valueSelector));
                } else {
                    generateRoutes.add(new TextGenerateRouteChunk(part));
                }
            }
        }
        return generateRoutes;
    }

    /**
     * 判断字符串是否为变量
     * @param part        待判断的字符串
     * @param variableKeys 变量键列表
     * @return 是否为变量
     */
    public static boolean isVariable(String part, List<String> variableKeys) {
        String cleanedPart = part.replace("{{", "").replace("}}", "");
        return part.startsWith("{{") && variableKeys.contains(cleanedPart);
    }

    // 用于获取答案依赖关系
    public static Map<String, List<String>> fetchAnswersDependencies(List<String> answerNodeIds,
                                                                        Map<String, List<GraphEdge>> reverseEdgeMapping,
                                                                        Map<String, Node> nodeIdConfigMapping) {
        // 用于存储答案节点ID及其依赖节点ID列表的映射
        Map<String, List<String>> answerDependencies = new HashMap<>();
        // 遍历答案节点ID列表
        for (String answerNodeId : answerNodeIds) {
            // 如果当前答案节点ID对应的依赖关系还未在映射中存在，则初始化一个空列表
            if (!answerDependencies.containsKey(answerNodeId)) {
                answerDependencies.put(answerNodeId, new ArrayList<>());
            }
            // 递归获取当前答案节点的依赖关系
            recursiveFetchAnswerDependencies(answerNodeId, answerNodeId, nodeIdConfigMapping, reverseEdgeMapping, answerDependencies);
        }
        return answerDependencies;
    }


    /**
     * 递归获取答案依赖关系的静态方法
     * @param currentNodeId 当前NodeId
     * @param answerNodeId 响应节点Id
     * @param nodeIdConfigMapping 节点配置
     * @param reverseEdgeMapping 边映射
     * @param answerDependencies 响应依赖
     */
    private static void recursiveFetchAnswerDependencies(String currentNodeId,
                                                             String answerNodeId,
                                                             Map<String, Node> nodeIdConfigMapping,
                                                             Map<String, List<GraphEdge>> reverseEdgeMapping,
                                                             Map<String, List<String>> answerDependencies) {
        // 获取当前节点的反向边列表，如果不存在则返回空列表
        List<GraphEdge> reverseEdges = reverseEdgeMapping.getOrDefault(currentNodeId, new ArrayList<>());
        for (GraphEdge edge : reverseEdges) {
            String sourceNodeId = edge.getSourceNodeId();
            // 获取源节点配置中"data"下的"type"字段对应的节点类型，这里的获取逻辑根据实际节点配置结构来定
            String sourceNodeType = (String) nodeIdConfigMapping.get(sourceNodeId).getData().get("type");
            // 判断源节点类型是否在指定的几种类型中
            if ("ANSWER".equals(sourceNodeType) || "IF_ELSE".equals(sourceNodeType)
                    || "QUESTION_CLASSIFIER".equals(sourceNodeType) || "ITERATION".equals(sourceNodeType)
                    || "CONVERSATION_VARIABLE_ASSIGNER".equals(sourceNodeType)) {
                // 如果是，则将源节点ID添加到答案节点的依赖列表中
                answerDependencies.get(answerNodeId).add(sourceNodeId);
            } else {
                // 否则继续递归获取源节点的依赖关系
                recursiveFetchAnswerDependencies(sourceNodeId, answerNodeId, nodeIdConfigMapping, reverseEdgeMapping, answerDependencies);
            }
        }
    }
}
