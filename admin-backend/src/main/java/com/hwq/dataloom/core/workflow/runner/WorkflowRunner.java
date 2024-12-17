package com.hwq.dataloom.core.workflow.runner;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.core.file.File;
import com.hwq.dataloom.core.workflow.WorkflowEntry;
import com.hwq.dataloom.core.workflow.entitys.SingleIterationRunEntity;
import com.hwq.dataloom.core.workflow.enums.SystemVariableKey;
import com.hwq.dataloom.core.workflow.enums.UserFrom;
import com.hwq.dataloom.core.workflow.graph.GraphRunEntity;
import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
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
    public void run(WorkflowGenerateEntity workflowGenerateEntity, WorkflowQueueManager workflowQueueManager, Workflow workflow, String threadPoolId) {

        SingleIterationRunEntity singleIterationRunEntity = workflowGenerateEntity.getSingleIterationRunEntity();
        GraphRunEntity graphRunEntity = null;
        VariablePool variablePool = null;
        if (singleIterationRunEntity != null) {
            Pair<GraphRunEntity, VariablePool> graphVariablePoolPair = getGraphAndVariablePoolOfSingleIteration(workflow, singleIterationRunEntity.getNodeId(), workflowGenerateEntity.getInputs());
            graphRunEntity = graphVariablePoolPair.getKey();
            variablePool = graphVariablePoolPair.getValue();
        } else {
            Map<String, Object> inputs = workflowGenerateEntity.getInputs();
            List<File> files = workflowGenerateEntity.getFiles();

            // 创建一个变量池
            Map<SystemVariableKey, Object> systemInputs = new HashMap<>();
            systemInputs.put(SystemVariableKey.FILES, files);
            systemInputs.put(SystemVariableKey.USER_ID, workflowGenerateEntity.getUserId());
            systemInputs.put(SystemVariableKey.WORKFLOW_RUN_ID, workflowGenerateEntity.getWorkflowRunId());
            systemInputs.put(SystemVariableKey.WORKFLOW_ID, workflowGenerateEntity.getWorkflowConfig().getWorkflowId());

            variablePool = new VariablePool(
                    systemInputs,
                    inputs,
                    workflow.getEnvVariablesFromJsonStr(),
                    ListUtil.empty()
            );
            // 初始化graph
            graphRunEntity = Graph.init(workflow.getGraph(), null);
        }
        WorkflowEntry workflowEntry = new WorkflowEntry(
                workflow.getWorkflowId(),
                graphRunEntity,
                workflowGenerateEntity.getUserId(),
                workflow.getGraphFromStr(),
                UserFrom.ACCOUNT,
                3,
                variablePool,
                threadPoolId
        );
        // TODO：执行workflowEntry.run方法
    }

    /**
     * 获取单次迭代的图和变量池
     * @param workflow 工作流实体
     * @param nodeId 起始节点
     * @param userInputs 输入参数
     * @return 图和变量池
     */
    private Pair<GraphRunEntity, VariablePool> getGraphAndVariablePoolOfSingleIteration(Workflow workflow, String nodeId, Map<String, Object> userInputs) {
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
        GraphRunEntity graph = Graph.init(matchGraphDict, nodeId);

        // 寻找任务的头节点
        Optional<Node> iterationNode = nodes.stream()
                .filter(node -> nodeId.equals(node.getId()))
                .findFirst();
        ThrowUtils.throwIf(!iterationNode.isPresent(), ErrorCode.OPERATION_ERROR, "任务头节点未找到");

        // 获取节点类型
        Node node = iterationNode.get();
        NodeTypeEnum nodeType = NodeTypeEnum.getEnumByValue(node.getData().get("type").toString());

        // 根据不同类型的Node提取入参
        BaseNodeHandler nodeHandler = NodeHandlerMapping.getNodeClassByType(nodeType);

        // 获取变量映射
        Map<String, List<String>> variableMapping = nodeHandler.extractVariableSelectorToVariableMapping(graph, node);

        // 初始化变量池
        VariablePool variablePool = new VariablePool(new HashMap<>(), new HashMap<>(), workflow.getEnvVariablesFromJsonStr());

        // 节点数据Map转换为节点数据对象
        BaseNodeData nodeData = nodeHandler.parseNodeDataFromMap(node.getData());

        // 将用户输入的参数填入变量池
        WorkflowEntry.mappingUserInputsToVariablePool(variableMapping, userInputs, variablePool, nodeType, nodeData);
        return new Pair<>(graph, variablePool);
    }

}
