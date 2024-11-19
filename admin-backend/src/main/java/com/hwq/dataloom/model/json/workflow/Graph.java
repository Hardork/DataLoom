package com.hwq.dataloom.model.json.workflow;

import com.hwq.dataloom.model.json.workflow.node.Node;
import lombok.Data;

import java.util.List;

@Data
public class Graph {
    private List<Node> nodes;
    private List<Edge> edges;
    private Viewport viewport;
}

@Data
class Viewport {
    private double x;
    private double y;
    private double zoom;
}