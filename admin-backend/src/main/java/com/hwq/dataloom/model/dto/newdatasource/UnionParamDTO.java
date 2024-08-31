package com.hwq.dataloom.model.dto.newdatasource;

import com.hwq.dataloom.model.entity.CoreDatasetTable;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class UnionParamDTO implements Serializable {
    private String unionType;
    private List<UnionItemDTO> unionFields;
    private CoreDatasetTable parentDs;
    private CoreDatasetTable currentDs;
    private SQLObj parentSQLObj;
    private SQLObj currentSQLObj;
}