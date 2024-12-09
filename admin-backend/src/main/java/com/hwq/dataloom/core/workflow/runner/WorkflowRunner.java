package com.hwq.dataloom.core.workflow.runner;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.core.workflow.entitys.SingleIterationRunEntity;
import com.hwq.dataloom.core.workflow.node.handler.BaseNodeHandler;
import com.hwq.dataloom.core.workflow.node.handler.NodeHandlerMapping;
import com.hwq.dataloom.core.workflow.variable.VariablePool;
import com.hwq.dataloom.core.workflow.entitys.WorkflowGenerateEntity;
import com.hwq.dataloom.core.workflow.queue.WorkflowQueueManager;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.entity.Workflow;

import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import com.hwq.dataloom.core.workflow.graph.Graph;
import com.hwq.dataloom.core.workflow.edge.Edge;
import com.hwq.dataloom.core.workflow.node.Node;
import org.springframework.stereotype.Component;

/**
 * @author HWQ
 * @date 2024/11/21 23:45
 * @description 工作流运行类
 * @Funtion: 主要负责工作流的任务调度运行
 */
@Component
public class WorkflowRunner {


    // TODO: 初始化Graph，包含寻找Start节点、
    public void run(WorkflowGenerateEntity workflowGenerateEntity, WorkflowQueueManager workflowQueueManager, Workflow workflow) {

        SingleIterationRunEntity singleIterationRunEntity = workflowGenerateEntity.getSingleIterationRunEntity();
        if (singleIterationRunEntity != null) {
            Pair<Graph, VariablePool> graphVariablePoolPair = getGraphAndVariablePoolOfSingleIteration(workflow, singleIterationRunEntity.getNodeId(), workflowGenerateEntity.getInputs());
        } else {
            Graph graph = Graph.init(workflow.getGraph(), null);
        }

        // init graph

        // init end stream param

    }

    /**
     * 获取单次迭代的图和变量池
     * @param workflow 工作流实体
     * @param nodeId 起始节点
     * @param inputs 输入参数
     * @return 图和变量池
     */
    private Pair<Graph, VariablePool> getGraphAndVariablePoolOfSingleIteration(Workflow workflow, String nodeId, Map<String, Object> inputs) {
        // 校验
        Map<String, Object> graphDict = workflow.graphDict();
        ThrowUtils.throwIf(graphDict == null, ErrorCode.OPERATION_ERROR, "工作流配置为空");
        ThrowUtils.throwIf(graphDict.get("nodes") == null || graphDict.get("edges") == null, ErrorCode.OPERATION_ERROR, "工作流配置中缺少节点或边配置信息");
        ThrowUtils.throwIf(!(graphDict.get("nodes") instanceof List), ErrorCode.OPERATION_ERROR, "工作流配置中节点配置必须为列表");
        ThrowUtils.throwIf(!(graphDict.get("edges") instanceof List), ErrorCode.OPERATION_ERROR, "工作流配置中边配置必须为列表");
        // 过滤出符合迭代条件的节点
        Graph originalGraph = workflow.getGraphFromStr();
        List<Node> nodes = originalGraph.getNodes();
        List<Node> matchNodes = nodes.stream()
                .filter(node -> nodeId.equals(node.getId()) || node.getData() != null && node.getData().get("iterationId").equals(nodeId))
                .collect(Collectors.toList());
        graphDict.put("nodes", matchNodes);
        List<String> nodeIds = matchNodes.stream().map(Node::getId).collect(Collectors.toList());

        // 过滤出符合迭代条件的边
        List<Edge> edges = originalGraph.getEdges();
        List<Edge> matchEdges = edges.stream()
                .filter(
                        edge -> (edge.getSource() == null || nodeIds.contains(edge.getSource()))
                    && (edge.getTarget() == null || nodeIds.contains(edge.getTarget()))
                ).collect(Collectors.toList());
        graphDict.put("edges", matchEdges);
        String matchGraphDict = JSONUtil.toJsonStr(graphDict);

        // 初始化graph
        Graph graph = Graph.init(matchGraphDict, nodeId);
        ThrowUtils.throwIf(graph == null, ErrorCode.OPERATION_ERROR, "工作流初始化失败,请检查配置");

        // 寻找任务的头节点
        Optional<Node> iterationNode = nodes.stream()
                .filter(node -> nodeId.equals(node.getId()))
                .findFirst();
        ThrowUtils.throwIf(!iterationNode.isPresent(), ErrorCode.OPERATION_ERROR, "任务头节点未找到");

        // 获取节点类型
        Node node = iterationNode.get();
        NodeTypeEnum type = NodeTypeEnum.getEnumByValue(node.getData().get("type").toString());
        // 根据不同类型的Node提取入参
        BaseNodeHandler nodeHandlerByType = NodeHandlerMapping.getNodeClassByType(type);
        Map<String, List<String>> variableMapping = nodeHandlerByType.extractVariableSelectorToVariableMapping(graph, node);
        // 将用户的输入映射到变量池中
        return null;
    }



}
