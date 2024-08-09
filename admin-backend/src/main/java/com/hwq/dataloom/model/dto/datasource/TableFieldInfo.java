package com.hwq.dataloom.model.dto.datasource;

import lombok.Data;

/**
 * @author HWQ
 * @date 2024/5/26 15:16
 * @description
 * tableFiled.setFieldType(null);
 *             tableFiled.setName(s);
 *             tableFiled.setOriginName(s);
 */
@Data
public class TableFieldInfo {
    /**
     * 列名
     */
    private String name;

    /**
     * 源列名
     */
    private String originName;

    /**
     * 字段类型
     */
    private String FieldType;
}
