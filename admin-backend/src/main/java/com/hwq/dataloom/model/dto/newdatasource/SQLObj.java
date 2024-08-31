package com.hwq.dataloom.model.dto.newdatasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SQLObj {
    private String tableSchema;
    private String tableName;
    private String tableAlias;

    private String fieldName;
    private String fieldAlias;

    private String groupField;
    private String groupAlias;

    private String orderField;
    private String orderAlias;
    private String orderDirection;

    private String whereField;
    private String whereAlias;
    private String whereTermAndValue;

    private String limitFiled;
}