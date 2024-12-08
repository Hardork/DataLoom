package com.hwq.dataloom.core.workflow.graph;

import lombok.Data;

/**
 * 节点位置信息类
 */
@Data
public class Position {
    /**
     * 节点所在x轴位置
     */
    private double x;

    /**
     * 节点所在y轴位置
     */
    private double y;
}
