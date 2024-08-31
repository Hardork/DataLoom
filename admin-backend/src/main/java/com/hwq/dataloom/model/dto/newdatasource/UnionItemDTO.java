package com.hwq.dataloom.model.dto.newdatasource;

import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import lombok.Data;

import java.io.Serializable;

@Data
public class UnionItemDTO implements Serializable {
    private CoreDatasetTableField parentField;
    private CoreDatasetTableField currentField;
}