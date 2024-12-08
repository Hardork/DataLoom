package com.hwq.dataloom.core.workflow.node;

import com.hwq.dataloom.core.workflow.graph.Position;
import lombok.Data;

import java.util.Map;

/**
 * 工作流节点类
 */
@Data
public class Node {
    /**
     * 节点数据
     */
    private Map<String, Object> data;
    /**
     * 节点高度
     */
    private double height;
    /**
     * 节点id
     */
    private String id;
    /**
     * 位置信息
     */
    private Position position;
    /**
     * 绝对位置信息
     */
    private Position positionAbsolute;
    /**
     * 是否被选中
     */
    private boolean selected;
    /**
     * 源位置新增
     */
    private String sourcePosition;
    /**
     * 目标位置信息
     */
    private String targetPosition;
    /**
     * 节点对应的类型
     */
    private String type;
    /**
     * 节点宽度
     */
    private double width;
}
