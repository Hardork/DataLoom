package com.hwq.dataloom.core.workflow.variable;

import lombok.Data;

import java.util.List;

/**
 * 变量选择器类，用于表示变量及其选择器
 */
@Data
public class VariableSelector {

    private String variable;  // 变量
    private List<String> valueSelector;  // 值选择器

    /**
     * 构造函数
     *
     * @param variable      变量
     * @param valueSelector 值选择器
     */
    public VariableSelector(String variable, List<String> valueSelector) {
        this.variable = variable;
        this.valueSelector = valueSelector;
    }
}