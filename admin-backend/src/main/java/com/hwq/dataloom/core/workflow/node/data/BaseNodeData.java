package com.hwq.dataloom.core.workflow.node.data;

import lombok.Data;

import java.util.List;

/**
 * 节点数据基类
 */
@Data
public class BaseNodeData {
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