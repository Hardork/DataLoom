package com.hwq.dataloom.core.workflow.node.data;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据提取节点数据类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DataExtractionNodeData extends BaseNodeData {
    /**
     * 数据源id
     */
    private Long datasourceId;

    /**
     * 指定表名称
     */
    private String schemaName;

    /**
     * 是否通过sql提取数据
     */
    private boolean queryBySql;

    /**
     * 查询sql
     */
    private String sql;

    /**
     * 通过自然语言查询
     */
    private String query;

    /**
     * 输出结果
     */
    private Output output;
}

