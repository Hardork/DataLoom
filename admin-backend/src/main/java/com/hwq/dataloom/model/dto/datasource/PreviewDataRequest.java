package com.hwq.dataloom.model.dto.datasource;

import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/5/24 01:39
 * @description 数据预览请求类
 */
@Data
public class PreviewDataRequest{

    private String datasourceId;

    private String dataName;

    /**
     * 请求预览字段
     */
    private List<CoreDatasetTableField> allFields;

}

@Data
class UnionToParent {
    private List<CoreDatasetTableField> unionFields;
}
