package com.hwq.dataloom.model.json.datasource;
import com.hwq.dataloom.model.dto.datasource.TableFieldInfo;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ExcelSheetData {

    /**
     * 分页名
     */
    private String sheetName;

    /**
     * 行数据
     */
    private List<String[]> data;

    /**
     * 字段信息
     */
    private List<TableFieldInfo> fieldInfos;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 行数据（映射列名）
     */
    private List<Map<String, Object>> jsonArray;
}