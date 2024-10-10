package com.hwq.dataloom.model.json;

import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/10/9 22:11
 * @description AI一键生成仪表盘生成的DataOptions
 * 示例：
 * [
 *   {
 *     "chartType": "line",
 *     "chartName": "北京空气质量变化趋势",
 *     "dataTableName": "excel_108_北京空气质量",
 *     "seriesArray": [
 *       {"fieldName": "AQI", "rollup": "SUM"},
 *       {"fieldName": "PM2_5", "rollup": "SUM"},
 *       {"fieldName": "PM10", "rollup": "SUM"},
 *       {"fieldName": "S02", "rollup": "SUM"},
 *       {"fieldName": "NO2", "rollup": "SUM"},
 *       {"fieldName": "CO", "rollup": "SUM"}
 *     ],
 *     "group": [
 *       {"fieldName": "日期"}
 *     ]
 *   }
 * ]
 */
@Data
public class AiGenChartDataOptions {
    private String chartType;
    private String chartName;
    private String dataTableName;
    private List<Series> seriesArray;
    private List<GenChartGroup> group;
}
