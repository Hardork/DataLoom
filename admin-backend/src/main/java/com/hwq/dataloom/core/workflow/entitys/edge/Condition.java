package com.hwq.dataloom.core.workflow.entitys.edge;

import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/11/23 17:30
 * @description 运行条件
 */
@Data
public class Condition {
    private List<String> variableSelector;
    /**
     * 比较符号
     */
    private String comparisonOperator;

    private String value;

    private SubVariableCondition subVariableCondition;

}

@Data
class SubVariableCondition {
    private String logicalOperator;

    private SubCondition conditions;
}

@Data
class SubCondition {
    private String key;

    private String comparisonOperator;

    private String value;
}
