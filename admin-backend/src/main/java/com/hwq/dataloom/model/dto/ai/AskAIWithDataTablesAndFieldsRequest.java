package com.hwq.dataloom.model.dto.ai;

import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/9/7 19:36
 * @description 数据表以及字段
 */
@Data
@Builder
public class AskAIWithDataTablesAndFieldsRequest {
    /**
     * 表id
     */
    private Long tableId;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 表注释
     */
    private String tableComment;

    /**
     * 字段信息
     */
    private List<CoreDatasetTableField> coreDatasetTableFieldList;
}
