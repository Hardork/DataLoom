package com.hwq.dataloom.model.json.workflow;

import com.hwq.dataloom.model.enums.workflow.NodeTypeEnum;
import com.hwq.dataloom.model.json.workflow.edge.Edge;
import com.hwq.dataloom.model.json.workflow.node.Node;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 画布节点配置类
 */
@Data
public class Graph {

    /**
     * 节点集合
     */
    private List<Node> nodes;

    /**
     * 边集合
     */
    private List<Edge> edges;

    /**
     * 视图
     */
    private Viewport viewport;


    /**
     * 寻找起始节点
     * @return start节点列表
     */
    public List<Node> findStartNode() {
        if (nodes.isEmpty()) {
            return new ArrayList<>();
        }
        return nodes.stream()
                .filter(node -> NodeTypeEnum.START.getValue().equals(node.getType()))
                .collect(Collectors.toList());
    }
}

/**
 * 视图类
 */
@Data
class Viewport {
    private double x;
    private double y;
    private double zoom;
}