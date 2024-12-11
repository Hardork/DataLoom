package com.hwq.dataloom.core.workflow.runner;

import com.hwq.dataloom.core.workflow.node.answer.AnswerNodeData;
import com.hwq.dataloom.core.workflow.utils.VariableTemplateParser;
import com.hwq.dataloom.core.workflow.variable.VariableSelector;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import com.hwq.dataloom.core.workflow.edge.GraphEdge;
import com.hwq.dataloom.core.workflow.node.Node;
import com.hwq.dataloom.core.workflow.node.answer.GenerateRouteChunk;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            extractGenerateRouteSelectors(nodeConfig);
        }
        return null;
    }


    private static void extractGenerateRouteSelectors(Node nodeConfig) {
        // TODO：搞懂 node_data = AnswerNodeData(**config.get("data", {}))的作用
        Map<String, Object> data = nodeConfig.getData();

    }


    /**
     * 从节点数据中提取生成路由
     *
     * @param nodeData 节点数据对象
     * @return 生成路由块的列表
     */
    public List<GenerateRouteChunk> extractGenerateRouteFromNodeData(AnswerNodeData nodeData) {
        // 创建变量模板解析器，解析节点数据中的答案部分
        VariableTemplateParser variableTemplateParser = new VariableTemplateParser(nodeData.getAnswer());
        // 提取变量选择器
        List<VariableSelector> variableSelectors = variableTemplateParser.extractVariableSelectors();

//        // 创建值选择器映射，键为变量，值为变量选择器
//        Map<String, String> valueSelectorMapping = new HashMap<>();
//        for (VariableSelector variableSelector : variableSelectors) {
//            valueSelectorMapping.put(variableSelector.getVariable(), variableSelector.getValueSelector());
//        }
//
//        // 获取变量选择器中的变量列表
//        List<String> variableKeys = new ArrayList<>(valueSelectorMapping.keySet());
//
//        // 创建提示模板解析器，解析节点数据中的答案部分，并设置是否包含变量模板
//        PromptTemplateParser templateParser = new PromptTemplateParser(nodeData.getAnswer(), true);
//        // 获取模板中的变量键列表
//        List<String> templateVariableKeys = templateParser.getVariableKeys();
//
//        // 取变量键列表和模板变量键列表的交集
//        Set<String> intersection = new HashSet<>(variableKeys);
//        intersection.retainAll(new HashSet<>(templateVariableKeys));
//        variableKeys = new ArrayList<>(intersection);
//
//        // 获取节点数据中的答案模板
//        String template = nodeData.getAnswer();
//        // 替换模板中的变量为特定格式
//        for (String var : variableKeys) {
//            template = template.replace("{{" + var + "}}", "Ω{{" + var + "}}Ω");
//        }
//
//        // 存储生成的路由
//        List<GenerateRouteChunk> generateRoutes = new ArrayList<>();
//        // 按特定分隔符分割模板，并处理每个部分
//        for (String part : template.split("Ω")) {
//            if (!part.isEmpty()) {
//                // 判断部分是否为变量
//                if (isVariable(part, variableKeys)) {
//                    String varKey = part.replace("Ω", "").replace("{{", "").replace("}}", "");
//                    String valueSelector = valueSelectorMapping.get(varKey);
//                    generateRoutes.add(new VarGenerateRouteChunk(valueSelector));
//                } else {
//                    generateRoutes.add(new TextGenerateRouteChunk(part));
//                }
//            }
//        }
        return null;
    }

    /**
     * 判断字符串是否为变量
     *
     * @param part        待判断的字符串
     * @param variableKeys 变量键列表
     * @return 是否为变量
     */
    private boolean isVariable(String part, List<String> variableKeys) {
        for (String variableKey : variableKeys) {
            if (part.contains("{{" + variableKey + "}}")) {
                return true;
            }
        }
        return false;
    }
}
