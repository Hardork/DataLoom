package com.hwq.dataloom.core.workflow.entitys.node.data;

import lombok.Data;

import java.util.List;

/**
 * 异常值处理节点
 */
@Data
public class OutlierProcessNodeData extends BaseNodeData {
    /**
     * 数据集Id
     */
    private String dataSetId;

    /**
     * 处理规则
     */
    private List<FilterRule> filterRules;
}

@Data
class FilterRule {
    /**
     * 字段名
     */
    private String fieldName;
    /**
     * 需要的类型
     */
    private String needType;

    /**
     * 校验判断符, 包含但是不限于 >  <  !=  ==
     */
    private String operator;

    /**
     * 处理策略 customFill(自定义填充)、delete(删除异常值所在行)、avg(取平均值)、max(取最大值)、min(取最小值)
     */
    private String handleStrategy;
}
