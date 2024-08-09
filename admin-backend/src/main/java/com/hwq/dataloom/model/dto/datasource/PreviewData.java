package com.hwq.dataloom.model.dto.datasource;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/5/24 01:05
 * @description
 */
@Data
public class PreviewData {
    private List<SchemaStructure> field;
    private List<Map<String, String>> data;
}
