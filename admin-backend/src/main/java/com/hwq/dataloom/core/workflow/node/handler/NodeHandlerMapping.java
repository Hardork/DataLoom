package com.hwq.dataloom.core.workflow.node.handler;

import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/12/9 23:37
 * @description 节点处理器映射
 */
public class NodeHandlerMapping {
    private static final Map<NodeTypeEnum, BaseNodeHandler> nodeTypeClassesMapping = new HashMap<>();

    static {
        nodeTypeClassesMapping.put(NodeTypeEnum.START, new StartNodeHandler());
        // TODO: 继续添加其他节点处理器
    }

    public static BaseNodeHandler getNodeClassByType(NodeTypeEnum type) {
        return nodeTypeClassesMapping.get(type);
    }
}
