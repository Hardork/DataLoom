package com.hwq.dataloom.core.workflow.node.handler;

import com.hwq.dataloom.core.workflow.entitys.NodeRunResult;
import com.hwq.dataloom.core.workflow.graph.Graph;
import com.hwq.dataloom.core.workflow.graph_engine.GraphInitParams;
import com.hwq.dataloom.core.workflow.graph_engine.entities.GraphRuntimeState;
import com.hwq.dataloom.core.workflow.node.Node;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;

import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/12/8 12:11
 * @description 开始节点处理器
 */
public class StartNodeHandler extends BaseNodeHandler {

    public StartNodeHandler() {
    }

    public StartNodeHandler(
            String id, Map<String, Object> config,
            Graph graph,
            GraphInitParams graphInitParams,
            GraphRuntimeState graphRuntimeState,
            String previousNodeId,
            String threadPoolId
    ) {
        super(NodeTypeEnum.START, id, config, graph, graphInitParams, graphRuntimeState, previousNodeId,threadPoolId);
    }

    @Override
    public Map<String, List<String>> extractVariableSelectorToVariableMapping(Graph graph, Node node) {
        return null;
    }

    @Override
    public NodeRunResult nodeRun() {
//        this.
        return null;
    }

}
