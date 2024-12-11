package com.hwq.dataloom.core.workflow.node.handler;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.core.workflow.entitys.NodeRunResult;
import com.hwq.dataloom.core.workflow.graph.Graph;
import com.hwq.dataloom.core.workflow.graph_engine.GraphInitParams;
import com.hwq.dataloom.core.workflow.graph_engine.entities.GraphRuntimeState;
import com.hwq.dataloom.core.workflow.node.Node;
import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
import com.hwq.dataloom.core.workflow.node.data.StartNodeData;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.WorkflowException;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;

import java.util.HashMap;
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
        return new HashMap<String, List<String>>();
    }

    @Override
    public BaseNodeData parseNodeDataFromMap(Map<String, Object> data) {
        Object dataObj = data.get("data");
        StartNodeData nodeData = null;
        try {
            nodeData = JSONUtil.toBean(JSONUtil.toJsonStr(dataObj), StartNodeData.class);
        } catch (Exception e) {
            throw new WorkflowException(ErrorCode.JSON_TO_BEAN_ERROR, "转化start节点数据失败");
        }
        return nodeData;
    }

    @Override
    public NodeRunResult nodeRun() {

        return null;
    }

}
