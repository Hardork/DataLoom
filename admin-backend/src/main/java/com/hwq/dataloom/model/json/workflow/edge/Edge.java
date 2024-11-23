package com.hwq.dataloom.model.json.workflow.edge;

import lombok.Data;

/**
 * 工作流节点连接边
 */
@Data
public class Edge {
    private String id;
    private boolean selected;
    private String source;
    private String sourceHandle;
    private String target;
    private String targetHandle;
    private String type;
    private int zIndex;
    private EdgeData data;
}
