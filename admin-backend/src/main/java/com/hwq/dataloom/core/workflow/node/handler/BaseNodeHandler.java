package com.hwq.dataloom.core.workflow.node.handler;

import com.hwq.dataloom.core.workflow.entitys.NodeRunResult;
import com.hwq.dataloom.core.workflow.graph.Graph;
import com.hwq.dataloom.core.workflow.graph_engine.GraphInitParams;
import com.hwq.dataloom.core.workflow.node.Node;
import com.hwq.dataloom.core.workflow.node.data.BaseNodeData;
import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: HWQ
 * @Description: 节点处理器基类
 * @DateTime: 2024/12/6 18:00
 **/
@Data
public abstract class BaseNodeHandler {


    private NodeTypeEnum nodeTypeEnum;

    private String id;


    public BaseNodeHandler(NodeTypeEnum nodeTypeEnum, String id, Map<String, Object> config, GraphInitParams graphInitParams, String graphRuntimeState, String threadPoolId) {
        this.nodeTypeEnum = nodeTypeEnum;

    }



    /**
     * 提取变量到Map中
     * @param graph 图
     * @param node 当前节点
     * @return Map
     */
    public abstract Map<String, List<String>> extractVariableSelectorToVariableMapping(Graph graph, Node node);


    public abstract NodeRunResult nodeRun();
}
