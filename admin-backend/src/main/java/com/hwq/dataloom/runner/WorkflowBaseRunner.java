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
            // 判断是否属于正常分支
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

        Map<String, Node> runNodeListMap = new HashMap<>();
        runNodeList.forEach(nodeId -> runNodeListMap.put(nodeId, allNodeMap.get(nodeId)));

        // init parallel mapping (初始化并行节点)
        Map<String, GraphParallel> parallelMapping = new HashMap<>();
        Map<String, String> nodeParallelMap = new HashMap<>();
        addParallelsRecursively(sourceEdgeMapping, endEdgeMapping, rootNodeId, parallelMapping, nodeParallelMap, null);

        // Check if it exceeds N layers of parallel
        for (GraphParallel parallel : parallelMapping.values()) {
            if (parallel.getParentParallelId() != null) {
                checkExceedParallelLimit(parallelMapping, 3, parallel.getParentParallelId(), 1);
            }
        }

        // init answer stream generate routes (初始化响应流生成节点)
        AnswerStreamGeneratorRouter answerStreamGeneratorRouter = AnswerStreamGeneratorRouter.init(runNodeListMap, endEdgeMapping);

        // init end stream param

    }

    /**
     * 校验当前并行层数是否超过了N层
     * @param parallelMapping 并行映射
     * @param levelLimit 层数限制
     * @param parentParallelId 父层Id
     */
    private void checkExceedParallelLimit(Map<String, GraphParallel> parallelMapping, int levelLimit, String parentParallelId, int currentLevel) {
        GraphParallel parentParallel = parallelMapping.get(parentParallelId);
        if (parentParallel == null) return;

        currentLevel += 1;
        if (currentLevel > levelLimit) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "并行运行的层数超过了" + levelLimit + "层");
        }

        if (parentParallel.getParentParallelId() != null) {
            checkExceedParallelLimit(parallelMapping, levelLimit, parentParallel.getParentParallelId(), currentLevel);
        }
    }


    /**
     * 校验运行路径中是否包含环
     * @param route 运行路径
     * @param sourceEdgeMapping 边映射
     */
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

    private void addParallelsRecursively(
            Map<String, List<GraphEdge>> sourceEdgeMapping,
            Map<String, List<GraphEdge>> endEdgeMapping,
            String rootNodeId,
            Map<String, GraphParallel> parallelMapping,
            Map<String, String> nodeParallelMapping,
            GraphParallel parentParallel
    ) {
        List<GraphEdge> targetNodeEdges = sourceEdgeMapping.get(rootNodeId);
        GraphParallel parallel = null;
        if (targetNodeEdges.size() > 1) {
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
            // 遍历无运行条件
            for (Map.Entry<String, List<GraphEdge>> entry : conditionEdgeMappings.entrySet()) {
                List<GraphEdge> graphEdges = entry.getValue();
                String conditionHash = entry.getKey();
                if (graphEdges.size() > 1) { // 如果边集合的大小大于1，说明下个节点有并行节点
                    if (!parallelBranchNodeIds.containsKey(conditionHash)) {
                        parallelBranchNodeIds.put(conditionHash, new ArrayList<>());
                    }
                    for (GraphEdge graphEdge : graphEdges) { // 添加并行节点
                        parallelBranchNodeIds.get(conditionHash).add(graphEdge.getTargetNodeId());
                    }
                }
            }

            // 用于存储不同运行条件哈希值对应的并行结构对象的映射
            Map<String, GraphParallel> conditionParallels = new HashMap<>();
            // 遍历parallelBranchNodeIds，处理每个运行条件哈希值对应的并行分支节点ID列表
            for (Map.Entry<String, List<String>> entry : parallelBranchNodeIds.entrySet()) {
                String conditionHash = entry.getKey();
                List<String> conditionParallelBranchNodeIds = entry.getValue();
                // 如果当前运行条件对应的并行分支节点ID列表不为空，说明有实际的并行分支情况需要处理
                if (!conditionParallelBranchNodeIds.isEmpty()) {
                    // 获取父并行结构的ID，如果父并行结构不存在则为null
                    String parentParallelId = parentParallel != null ? parentParallel.getId() : null;
                    // 创建一个新的GraphParallel对象，用于表示当前并行结构
                    parallel = GraphParallel.builder()
                            .startFromNodeId(rootNodeId)
                            .parentParallelId(parentParallel != null ? parentParallel.getId() : null)
                            .parentParallelStartNodeId(parentParallel != null ? parentParallel.getStartFromNodeId() : null)
                            .build();
                    // 将新创建的并行结构对象添加到parallelMapping中，键为其自身的ID
                    parallelMapping.put(parallel.getId(), parallel);
                    // 将当前并行结构对象添加到conditionParallels中，键为对应的运行条件哈希值
                    conditionParallels.put(conditionHash, parallel);

                    // 获取当前并行分支内所有节点的ID，通过调用辅助方法实现（需根据实际情况完善该方法）
                    Map<String, List<String>> inBranchNodeIds = fetchAllNodeIdsInParallels(
                            sourceEdgeMapping,
                            endEdgeMapping,
                            conditionParallelBranchNodeIds
                    );

                    // 用于收集最终确定属于当前并行分支的节点ID列表
                    List<String> parallelNodeIds = new ArrayList<>();
                    // 遍历获取到的并行分支内节点ID的映射，将每个节点ID添加到parallelNodeIds中（需要进行一些判断逻辑）
                    for (List<String> nodeIds : inBranchNodeIds.values()) {
                        for (String nodeId : nodeIds) {
                            boolean inParentParallel = true;
                            // 如果存在父并行结构，则需要进一步判断当前节点是否在父并行结构内
                            if (parentParallelId!= null) {
                                inParentParallel = false;
                                // 遍历节点与并行结构的映射，检查当前节点是否属于父并行结构
                                for (Map.Entry<String, String> mappingEntry : nodeParallelMapping.entrySet()) {
                                    if (mappingEntry.getValue().equals(parentParallelId) && mappingEntry.getKey().equals(nodeId)) {
                                        inParentParallel = true;
                                        break;
                                    }
                                }
                            }
                            // 如果确定当前节点在父并行结构内，则将其添加到parallelNodeIds列表中，并更新节点与并行结构的映射
                            if (inParentParallel) {
                                parallelNodeIds.add(nodeId);
                                nodeParallelMapping.put(nodeId, parallel.getId());
                            }
                        }
                    }

                    // 用于收集在当前并行分支外部但又与之相关的目标节点ID的集合
                    Set<String> outsideParallelTargetNodeIds = new HashSet<>();
                    // 遍历当前并行分支内确定的节点ID列表，查找外部相关的目标节点ID
                    for (String nodeId : parallelNodeIds) {
                        // 如果节点ID等于当前并行结构的起始节点ID，则跳过，不进行后续处理
                        if (nodeId.equals(parallel.getStartFromNodeId())) {
                            continue;
                        }
                        // 获取当前节点的出边列表
                        List<GraphEdge> nodeEdges = sourceEdgeMapping.get(nodeId);
                        // 如果出边列表为空，则跳过当前节点的处理
                        if (nodeEdges == null) {
                            continue;
                        }
                        // 如果当前节点的出边数量大于1，跳过当前节点的处理（这里主要关注单出边情况来确定外部相关节点）
                        if (nodeEdges.size() > 1) {
                            continue;
                        }
                        // 获取当前节点唯一出边的目标节点ID
                        String targetNodeId = nodeEdges.get(0).getTargetNodeId();
                        // 如果目标节点ID已经在当前并行分支的节点ID列表中，则跳过当前节点的处理
                        if (parallelNodeIds.contains(targetNodeId)) {
                            continue;
                        }
                        // 如果存在父并行结构，从parallelMapping中获取对应的父并行结构对象，如果获取不到则跳过当前节点的处理
                        if (parentParallelId!= null) {
                            GraphParallel parent = parallelMapping.get(parentParallelId);
                            if (parent == null) {
                                continue;
                            }
                        }
                        // 判断目标节点ID是否满足以下几种与父并行结构或当前并行结构相关的条件之一
                        // 如果满足，则将目标节点ID添加到outsideParallelTargetNodeIds集合中
                        if ((nodeParallelMapping.containsKey(targetNodeId) && nodeParallelMapping.get(targetNodeId).equals(parentParallelId))
                                || (parentParallel != null && parentParallel.getEndToNodeId()!= null && targetNodeId.equals(parentParallel.getEndToNodeId()))
                                || (!nodeParallelMapping.containsKey(targetNodeId) && parentParallel == null)) {
                            outsideParallelTargetNodeIds.add(targetNodeId);
                        }
                    }

                    // 如果outsideParallelTargetNodeIds集合中只有一个元素，说明只有一个外部相关的目标节点ID
                    if (outsideParallelTargetNodeIds.size() == 1) {
                        // 如果存在父并行结构，且父并行结构有结束节点ID，并且当前并行结构的结束节点ID与父并行结构的结束节点ID相等
                        if (parentParallel != null && parentParallel.getEndToNodeId()!= null && parallel.getEndToNodeId()!= null && parallel.getEndToNodeId().equals(parentParallel.getEndToNodeId())) {
                            // 将当前并行结构的结束节点ID设置为null
                            parallel.setEndToNodeId(null);
                        } else {
                            // 否则，将这个唯一的外部相关目标节点ID设置为当前并行结构的结束节点ID
                            parallel.setEndToNodeId(outsideParallelTargetNodeIds.iterator().next());
                        }
                    }

                    // 如果存在带有运行条件的边映射（即conditionEdgeMappings不为空）
                    if (!conditionEdgeMappings.isEmpty()) {
                        // 遍历每个运行条件哈希值及其对应的边列表
                        for (Map.Entry<String, List<GraphEdge>> conditionEntry : conditionEdgeMappings.entrySet()) {
                            String conditionHashInner = conditionEntry.getKey();
                            List<GraphEdge> graphEdgesInner = conditionEntry.getValue();
                            // 遍历每条边，获取当前边对应的并行结构，并进行递归调用，继续处理后续节点的并行结构
                            for (GraphEdge graphEdge : graphEdgesInner) {
                                GraphParallel currentParallel = getCurrentParallel(
                                        parallelMapping,
                                        graphEdge,
                                        conditionParallels.get(conditionHashInner),
                                        parentParallel
                                );
                                addParallelsRecursively(
                                        sourceEdgeMapping,
                                        endEdgeMapping,
                                        graphEdge.getTargetNodeId(),
                                        parallelMapping,
                                        nodeParallelMapping,
                                        currentParallel
                                );
                            }
                        }
                    } else {
                        // 如果不存在带有运行条件的边映射（即所有边都属于默认情况），直接遍历起始节点的出边
                        // 获取每条边对应的并行结构，并进行递归调用，继续处理后续节点的并行结构
                        for (GraphEdge graphEdge : targetNodeEdges) {
                            GraphParallel currentParallel = getCurrentParallel(
                                    parallelMapping,
                                    graphEdge,
                                    parallel,
                                    parentParallel
                            );
                            addParallelsRecursively(
                                    sourceEdgeMapping,
                                    endEdgeMapping,
                                    graphEdge.getTargetNodeId(),
                                    parallelMapping,
                                    nodeParallelMapping,
                                    currentParallel
                            );
                        }
                    }
                }
            }
        } else {
            // 如果起始节点的出边数量不大于1（即0条或1条边），直接遍历出边
            // 获取每条边对应的并行结构，并进行递归调用，继续处理后续节点的并行结构
            for (GraphEdge graphEdge : targetNodeEdges) {
                GraphParallel currentParallel = getCurrentParallel(
                        parallelMapping,
                        graphEdge,
                        parallel,
                        parentParallel
                );
                addParallelsRecursively(
                        sourceEdgeMapping,
                        endEdgeMapping,
                        graphEdge.getTargetNodeId(),
                        parallelMapping,
                        nodeParallelMapping,
                        currentParallel
                );
            }
        }
        }

    private GraphParallel getCurrentParallel(
            Map<String, GraphParallel> parallelMapping,
            GraphEdge graphEdge,
            GraphParallel parallel,
            GraphParallel parentParallel)
    {
        GraphParallel currentParallel = null;
        if (parallel != null) {
            currentParallel = parentParallel;
        } else if (parentParallel != null) {
            if (parentParallel.getEndToNodeId() == null || !graphEdge.getTargetNodeId().equals(parentParallel.getEndToNodeId())) {
                currentParallel = parentParallel;
            } else {

            }
        }
        return null;
    }

    /**
     * 用于获取并行分支中所有节点的ID信息
     * @param edgeMapping 边映射
     * @param reverseEdgeMapping 逆序边映射
     * @param parallelBranchNodeIds 并行分支节点Id集合
     * @return 并行分支中所有节点的ID信息
     */
    public  Map<String, List<String>> fetchAllNodeIdsInParallels(
            Map<String, List<GraphEdge>> edgeMapping,
            Map<String, List<GraphEdge>> reverseEdgeMapping,
            List<String> parallelBranchNodeIds
    ) {
        // 用于存储每个并行分支节点及其可达节点ID列表的映射，初始每个并行分支节点的可达节点列表只包含自身
        Map<String, List<String>> routesNodeIds = new HashMap<>();
        // 遍历并行分支节点ID列表，初始化routesNodeIds
        for (String parallelBranchNodeId : parallelBranchNodeIds) {
            List<String> nodeIds = new ArrayList<>();
            nodeIds.add(parallelBranchNodeId);
            routesNodeIds.put(parallelBranchNodeId, nodeIds);

            // 递归地获取从当前并行分支节点出发的可达节点ID，添加到对应的列表中（需完善_recursively_fetch_routes方法）
            recursivelyFetchRoutes(edgeMapping, parallelBranchNodeId, nodeIds);
        }

        // 用于存储叶子节点（没有出边的节点）的ID信息，键为对应的并行分支节点ID，值为叶子节点ID列表
        Map<String, List<String>> leafNodeIds = new HashMap<>();
        // 用于存储需要合并的分支节点相关信息，键为节点ID，值为与之相关的其他分支节点ID列表
        Map<String, List<String>> mergeBranchNodeIds = new HashMap<>();

        // 遍历每个并行分支及其对应的可达节点ID列表
        for (Map.Entry<String, List<String>> entry : routesNodeIds.entrySet()) {
            String branchNodeId = entry.getKey();
            List<String> nodeIds = entry.getValue();
            // 遍历当前并行分支的可达节点ID列表
            for (String nodeId : nodeIds) {
                // 如果当前节点在边映射中不存在（即没有出边）或者出边数量为0，则认为是叶子节点
                if (!edgeMapping.containsKey(nodeId) || edgeMapping.get(nodeId).isEmpty()) {
                    if (!leafNodeIds.containsKey(branchNodeId)) {
                        leafNodeIds.put(branchNodeId, new ArrayList<>());
                    }
                    leafNodeIds.get(branchNodeId).add(nodeId);
                }

                // 遍历所有并行分支及其可达节点ID列表，检查当前节点是否在其他分支的可达节点列表中，并且满足一些合并条件
                for (Map.Entry<String, List<String>> entry2 : routesNodeIds.entrySet()) {
                    String branchNodeId2 = entry2.getKey();
                    List<String> innerRoute2 = entry2.getValue();
                    if (!branchNodeId.equals(branchNodeId2) && innerRoute2.contains(nodeId)
                            && reverseEdgeMapping.getOrDefault(nodeId, new ArrayList<>()).size() > 1
                            && isNodeInRoutes(reverseEdgeMapping, nodeId, routesNodeIds)) {
                        if (!mergeBranchNodeIds.containsKey(nodeId)) {
                            mergeBranchNodeIds.put(nodeId, new ArrayList<>());
                        }
                        if (!mergeBranchNodeIds.get(nodeId).contains(branchNodeId2)) {
                            mergeBranchNodeIds.get(nodeId).add(branchNodeId2);
                        }
                    }
                }
            }
        }

        // 对mergeBranchNodeIds按照其值（相关分支节点ID列表的长度）进行降序排序，返回一个有序的LinkedHashMap
        mergeBranchNodeIds = sortMergeBranchNodeIdsByLengthDesc(mergeBranchNodeIds);

        // 用于存储重复的结束节点相关信息，键为两个重复节点ID组成的元组（这里用长度为2的数组模拟元组），值为相关分支节点ID列表
        Map<String[], List<String>> duplicateEndNodeIds = new HashMap<>();
        // 遍历mergeBranchNodeIds，查找具有相同相关分支节点ID集合的节点对，作为重复的结束节点记录下来
        for (Map.Entry<String, List<String>> entry : mergeBranchNodeIds.entrySet()) {
            String nodeId = entry.getKey();
            List<String> branchNodeIds = entry.getValue();
            for (Map.Entry<String, List<String>> entry2 : mergeBranchNodeIds.entrySet()) {
                String nodeId2 = entry2.getKey();
                List<String> branchNodeIds2 = entry2.getValue();
                if (!nodeId.equals(nodeId2) && branchNodeIds.equals(branchNodeIds2)) {
                    String[] key = new String[]{nodeId, nodeId2};
                    if (!duplicateEndNodeIds.containsKey(key) &&!duplicateEndNodeIds.containsKey(new String[]{nodeId2, nodeId})) {
                        duplicateEndNodeIds.put(key, branchNodeIds);
                    }
                }
            }
        }

        // 根据节点顺序关系，删除重复的结束节点中较后的节点记录，只保留较前的节点记录
        for (Map.Entry<String[], List<String>> entry : duplicateEndNodeIds.entrySet()) {
            String[] nodeIds = entry.getKey();
            String nodeId = nodeIds[0];
            String nodeId2 = nodeIds[1];
            if (isNode2AfterNode1(nodeId, nodeId2, edgeMapping)) {
                if (mergeBranchNodeIds.containsKey(nodeId2)) {
                    mergeBranchNodeIds.remove(nodeId2);
                }
            } else if (isNode2AfterNode1(nodeId2, nodeId, edgeMapping)) {
                if (mergeBranchNodeIds.containsKey(nodeId)) {
                    mergeBranchNodeIds.remove(nodeId);
                }
            }
        }

        // 用于存储需要合并的分支节点对应的合并节点ID，键为分支节点ID，值为合并到的节点ID
        Map<String, String> branchesMergeNodeIds = new HashMap<>();
        // 遍历经过处理后的mergeBranchNodeIds，确定最终的合并节点关系
        for (Map.Entry<String, List<String>> entry : mergeBranchNodeIds.entrySet()) {
            String nodeId = entry.getKey();
            List<String> branchNodeIds = entry.getValue();
            if (branchNodeIds.size() <= 1) {
                continue;
            }
            for (String branchNodeId : branchNodeIds) {
                if (branchesMergeNodeIds.containsKey(branchNodeId)) {
                    continue;
                }
                branchesMergeNodeIds.put(branchNodeId, nodeId);
            }
        }

        // 用于存储最终确定的在并行分支内的节点ID信息，键为并行分支节点ID，值为对应的在分支内的节点ID列表
        Map<String, List<String>> inBranchNodeIds = new HashMap<>();
        // 遍历每个并行分支及其可达节点ID列表，根据合并节点关系等确定最终的分支内节点ID列表
        for (Map.Entry<String, List<String>> entry : routesNodeIds.entrySet()) {
            String branchNodeId = entry.getKey();
            List<String> nodeIds = entry.getValue();
            inBranchNodeIds.put(branchNodeId, new ArrayList<>());
            if (!branchesMergeNodeIds.containsKey(branchNodeId)) {
                // 如果当前并行分支节点不需要合并，将其自身及可达节点ID列表都添加到最终的分支内节点ID列表中
                inBranchNodeIds.get(branchNodeId).add(branchNodeId);
                inBranchNodeIds.get(branchNodeId).addAll(nodeIds);
            } else {
                String mergeNodeId = branchesMergeNodeIds.get(branchNodeId);
                if (!mergeNodeId.equals(branchNodeId)) {
                    inBranchNodeIds.get(branchNodeId).add(branchNodeId);
                }
                // 递归地添加从当前并行分支节点到合并节点之间的所有节点ID到最终的分支内节点ID列表中（需完善_recursively_add_parallel_node_ids方法）
                recursivelyAddParallelNodeIds(inBranchNodeIds.get(branchNodeId), edgeMapping, mergeNodeId, branchNodeId);
            }
        }

        return inBranchNodeIds;
    }

    private boolean isNode2AfterNode1(String nodeId, String nodeId2, Map<String, List<GraphEdge>> edgeMapping) {
        // TODO:
        return false;
    }

    private void recursivelyAddParallelNodeIds(
            List<String> branchNodeIds,
            Map<String, List<GraphEdge>> edgeMapping,
            String mergeNodeId,
            String startNodeId)
    {
        for (GraphEdge graphEdge : edgeMapping.getOrDefault(startNodeId, new ArrayList<>())) {
            if (!graphEdge.getTargetNodeId().equals(mergeNodeId) && !branchNodeIds.contains(graphEdge.getTargetNodeId())) {
                branchNodeIds.add(graphEdge.getTargetNodeId());
                // 递归
                recursivelyAddParallelNodeIds(branchNodeIds, edgeMapping, mergeNodeId, graphEdge.getTargetNodeId());
            }
        }
    }

    private Map<String, List<String>> sortMergeBranchNodeIdsByLengthDesc(Map<String, List<String>> mergeBranchNodeIds) {
        // TODO:
        return null;
    }

    /**
     * 判断节点是否存在路径中
     * @param reverseEdgeMapping 逆序边映射
     * @param startNodeId 开始节点Id
     * @param routesNodeIds 路径节点Id集合
     * @return 是否存在路径节点中
     */
    private boolean isNodeInRoutes(
            Map<String, List<GraphEdge>> reverseEdgeMapping,
            String startNodeId,
            Map<String, List<String>> routesNodeIds
    ) {
        // TODO: 判断node是否在routes中
        if (!reverseEdgeMapping.containsKey(startNodeId)) return false;

        Set<String> allRoutesNodeIds = new HashSet<>();

        List<GraphEdge> graphEdges = reverseEdgeMapping.get(startNodeId);
        graphEdges.forEach(graphEdge -> {
            graphEdge.getTargetNodeId();
        });

        return false;
    }


    /**
     * 递归寻找与startNodeId有关联的节点
     * @param edgeMapping 边映射
     * @param startNodeId 开始节点
     * @param routesNodeIds 存储途径节点Id集合
     */
    private void recursivelyFetchRoutes(
            Map<String, List<GraphEdge>> edgeMapping,
            String startNodeId,
            List<String> routesNodeIds
    ) {
        if (!edgeMapping.containsKey(startNodeId)) return;

        List<GraphEdge> graphEdges = edgeMapping.get(startNodeId);
        graphEdges.forEach(graphEdge -> {
            if (!routesNodeIds.contains(graphEdge.getTargetNodeId())) {
                routesNodeIds.add(graphEdge.getTargetNodeId());
                recursivelyFetchRoutes(edgeMapping, graphEdge.getTargetNodeId(), routesNodeIds);
            }
        });
    }


}
