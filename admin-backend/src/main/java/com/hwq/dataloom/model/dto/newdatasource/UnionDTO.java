package com.hwq.dataloom.model.dto.newdatasource;

import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UnionDTO implements Serializable {
    private CoreDatasetTable currentDs;
    private List<Long> currentDsField;
    private List<CoreDatasetTableField> currentDsFields;
    private List<UnionDTO> childrenDs;
    private UnionParamDTO unionToParent;
    private int allChildCount;
}