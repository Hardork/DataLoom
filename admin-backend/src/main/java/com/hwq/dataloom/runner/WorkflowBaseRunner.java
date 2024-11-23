package com.hwq.dataloom.runner;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.entity.Workflow;
import com.hwq.dataloom.model.enums.workflow.RunConditionTypeEnum;
import com.hwq.dataloom.model.json.workflow.edge.Edge;
import com.hwq.dataloom.model.json.workflow.GraphParallel;
import com.hwq.dataloom.model.json.workflow.edge.GraphEdge;
import com.hwq.dataloom.model.json.workflow.edge.RunCondition;
import com.hwq.dataloom.model.json.workflow.node.Node;

import com.hwq.dataloom.model.json.workflow.Graph;
import org.apache.commons.lang3.StringUtils;

/**
 * @author HWQ
 * @date 2024/11/21 23:45
 * @description 工作流运行基类
 */
public class WorkflowBaseRunner {
    public Graph initGraph() {
        return null;
    }

    // TODO: 初始化Graph，包含寻找Start节点、
    public void init(Workflow workflow, String rootNodeId) {
        // 1. parse str to bean
        Graph graph = JSONUtil.toBean(workflow.getGraph(), Graph.class);
        // 2.init edges config
        List<Edge> edges = graph.getEdges();
        Map<String, List<GraphEdge>> sourceEdgeMapping = new HashMap<>();
        Map<String, List<GraphEdge>> endEdgeMapping = new HashMap<>();
        Set<String> targetEdgeIds = new HashSet<>();
        for (Edge edge : edges) {
            String sourceNodeId = edge.getSource();
            if (StringUtils.isEmpty(sourceNodeId)) continue;
            if (!sourceEdgeMapping.containsKey(sourceNodeId)) {
                sourceEdgeMapping.put(sourceNodeId, new ArrayList<>());
            }
            String targetNodeId = edge.getTarget();
            if (StringUtils.isEmpty(targetNodeId)) continue;
            if (!endEdgeMapping.containsKey(targetNodeId)) {
                endEdgeMapping.put(targetNodeId, new ArrayList<>());
            }
            targetEdgeIds.add(targetNodeId);

            String sourceHandle = edge.getSourceHandle();
            RunCondition runCondition = null;
            // TODO: 考证这个source的sourceHandler和targetHandler的作用
            if (!StringUtils.isEmpty(sourceHandle) && !sourceHandle.equals("source")) { // 存在分支判断
                runCondition = RunCondition.builder()
                        .type(RunConditionTypeEnum.BRANCH_IDENTIFY.getValue())
                        .branchIdentify(edge.getSourceHandle())
                        .build();
            }
            GraphEdge graphEdge = GraphEdge.builder()
                    .sourceNodeId(sourceNodeId)
                    .targetNodeId(targetNodeId)
                    .runCondition(runCondition)
                    .build();
            // add edge to map
            sourceEdgeMapping.get(sourceNodeId).add(graphEdge);
            endEdgeMapping.get(targetNodeId).add(graphEdge);
        }

        List<Node> nodes = graph.getNodes();
        List<Node> rootNodes = new ArrayList<>();
        Map<String, Node> allNodeMap = new HashMap<>();
        for (Node node : nodes) {
            String id = node.getId();
            if (StringUtils.isEmpty(id)) continue;
            if (!targetEdgeIds.contains(id)) { // root node
                rootNodes.add(node);
            }
            allNodeMap.put(id, node);
        }
        List<String> rootNodeIds = rootNodes.stream()
                .map(Node::getId)
                .collect(Collectors.toList());

        if (StringUtils.isEmpty(rootNodeId)) { // args no rootNodeId
            // find start node as rootNode
            // 2. find start node
            List<Node> startNode = graph.findStartNode();
            ThrowUtils.throwIf(startNode.isEmpty(), ErrorCode.OPERATION_ERROR, "任务缺少start节点");
            ThrowUtils.throwIf(startNode.size() >= 2, ErrorCode.OPERATION_ERROR, "start节点仅可有1个");
            rootNodeId = startNode.get(0).getId();
        }

        if (!rootNodeIds.contains(rootNodeId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "起始节点id" + rootNodeId + "在任务中未找到");
        }

        // Check whether it is connected to the previous node
        List<String> route = new ArrayList<>();
        route.add(rootNodeId);
        checkCircle(route, sourceEdgeMapping);
        // fetch all node ids from root node
        List<String> runNodeList = new ArrayList<>();
        addRunNode2List(runNodeList, sourceEdgeMapping, rootNodeId);

        HashMap<String, Node> runNodeListMap = new HashMap<>();
        runNodeList.forEach(nodeId -> runNodeListMap.put(nodeId, allNodeMap.get(nodeId)));

        // init parallel mapping (初始化并行节点)
        Map<String, GraphParallel> parallelMap = new HashMap<>();
        Map<String, String> nodeParallelMap = new HashMap<>();
        addParallelsRecursively(sourceEdgeMapping, endEdgeMapping, rootNodeId, parallelMap, nodeParallelMap);
    }



    public void checkCircle(List<String> route, Map<String, List<GraphEdge>> sourceEdgeMapping) {
        if (route == null || route.isEmpty()) {
            return;
        }

        String lastNodeId = route.get(route.size() - 1);
        List<GraphEdge> outEdges = sourceEdgeMapping.getOrDefault(lastNodeId, new ArrayList<>());

        for (GraphEdge graphEdge : outEdges) {
            if (graphEdge == null || graphEdge.getTargetNodeId() == null) {
                continue;
            }

            if (route.contains(graphEdge.getTargetNodeId())) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR,String.format("Node %s is connected to the previous node, please check the graph.", graphEdge.getSourceNodeId()));
            }

            List<String> newRoute = new ArrayList<>(route);
            newRoute.add(graphEdge.getTargetNodeId());
            checkCircle(newRoute, sourceEdgeMapping);
        }

    }

    public void addRunNode2List(List<String> runNodeList, Map<String, List<GraphEdge>> sourceNodeMap, String nodeId) {
        for (GraphEdge edge : sourceNodeMap.getOrDefault(nodeId, new ArrayList<>())) {
            String nextNodeId = edge.getTargetNodeId();
            if (runNodeList.contains(nextNodeId)) { // has circle
                continue;
            }
            runNodeList.add(nextNodeId);
            addRunNode2List(runNodeList, sourceNodeMap, nextNodeId);
        }
    }

    private void addParallelsRecursively(Map<String, List<GraphEdge>> sourceEdgeMapping, Map<String, List<GraphEdge>> endEdgeMapping, String rootNodeId, Map<String, GraphParallel> parallelMap, Map<String, String> nodeParallelMap) {
        List<GraphEdge> targetNodeEdges = sourceEdgeMapping.get(rootNodeId);
        if (targetNodeEdges.size() > 1) { // exist parallel node
            // 用于存储不同条件下并行分支节点的ID映射
            Map<String, List<String>> parallelBranchNodeIds = new HashMap<>();
            // 用于存储带有运行条件的边的映射，键为条件哈希值
            Map<String, List<GraphEdge>> conditionEdgeMappings = new HashMap<>();
            // 遍历起始节点的出边，进行分类存储
            for (GraphEdge graphEdge : targetNodeEdges) {
                if (graphEdge.getRunCondition() == null) { // 有运行条件
                    if (!parallelBranchNodeIds.containsKey("default")) {
                        parallelBranchNodeIds.put("default", new ArrayList<>());
                    }
                    // 添加到并行分支
                    parallelBranchNodeIds.get("default").add(graphEdge.getTargetNodeId());
                } else { // 无运行条件
                    String conditionHash = graphEdge.getRunCondition().getHash();
                    if (!conditionEdgeMappings.containsKey(conditionHash)) {
                        conditionEdgeMappings.put(conditionHash, new ArrayList<>());
                    }
                    conditionEdgeMappings.get(conditionHash).add(graphEdge);
                }
            }
            // TODO:
        }
    }
}
