package com.hwq.dataloom.model.json;
import com.hwq.dataloom.model.dto.datasource.TableFieldInfo;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ExcelSheetData {
    private String sheetName;
    private List<String[]> data;
    private List<TableFieldInfo> fieldInfos;
    private String fileName;
    private List<Map<String, Object>> jsonArray;
}