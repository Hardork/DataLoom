package com.hwq.dataloom.model.dto.newdatasource;

import lombok.Data;

import java.util.List;


@Data
public class TableField {
    /**
     * 字段名
     */
    private String name;

    /**
     * 原始名称
     */
    private String originName;

    /**
     * 字段类型
     */
    private String type;               //SQL type from java.sql.Types

    private String description;

    private String groupType;

    /**
     * 是否被选中
     */
    private boolean checked = false;

    private String jsonPath;

}
