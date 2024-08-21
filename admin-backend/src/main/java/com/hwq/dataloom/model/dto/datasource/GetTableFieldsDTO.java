package com.hwq.dataloom.model.dto.datasource;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author HWQ
 * @date 2024/8/21 12:43
 * @description 获取数据源指定表字段请求类
 */
@Data
public class GetTableFieldsDTO {
    // 数据源id
    @NotNull(message = "datasourceId不得为空")
    private Long datasourceId;
    // 表名
    @NotEmpty(message = "tableName不得为空")
    private String tableName;
}
