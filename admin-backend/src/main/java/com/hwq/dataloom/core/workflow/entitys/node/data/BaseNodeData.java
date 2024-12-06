package com.hwq.dataloom.core.workflow.entitys.node.data;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 节点数据基类
 */
@Data
public abstract class BaseNodeData {
    private String desc;
    private String title;
    private Boolean selected;
    private String type;
    private List<Variable> variables;
}

/**
 * 参数类
 */
@Data
class Variable {
    private String label;
    private String value;
    private String maxLength;
    private List<String> options;
    private Boolean required;
    private String type;
}