package com.hwq.dataloom.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * table数据集表字段
 * @TableName core_dataset_table_field
 */
@TableName(value ="core_dataset_table_field")
@Data
public class CoreDatasetTableField implements Serializable {
    /**
     * ID
     */
    @TableId
    private Long id;

    /**
     * 数据源ID
     */
    private Long datasourceId;

    /**
     * 数据表ID
     */
    private Long datasetTableId;

    /**
     * 数据集ID
     */
    private Long datasetGroupId;

    /**
     * 图表ID
     */
    private Long chartId;

    /**
     * 原始字段名
     */
    private String originName;

    /**
     * 字段名用于展示
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 维度/指标标识 d:维度，q:指标
     */
    private String groupType;

    /**
     * 原始字段类型
     */
    private String type;

    /**
     * 是否选中
     */
    private Integer checked;

    /**
     * 列位置
     */
    private Integer columnIndex;

    /**
     * 同步时间
     */
    private Long lastSyncTime;

    /**
     * 是否为唯一标识
     */
    private Integer isUnique;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}