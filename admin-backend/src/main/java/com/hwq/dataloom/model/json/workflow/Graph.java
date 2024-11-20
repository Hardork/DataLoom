package com.hwq.dataloom.model.json.workflow;

import com.hwq.dataloom.model.json.workflow.node.Node;
import lombok.Data;

import java.util.List;

/**
 * 画布节点配置类
 */
@Data
public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;
    private Viewport viewport;
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