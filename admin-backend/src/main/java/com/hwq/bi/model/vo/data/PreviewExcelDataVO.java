package com.hwq.bi.model.vo.data;

import com.hwq.bi.model.dto.datasource.TableFieldInfo;
import com.hwq.bi.mongo.entity.ChartData;
import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/6/17 15:18
 * @description
 */
@Data
public class PreviewExcelDataVO {
    private Boolean isValid;
    private String errorMessage;
    private List<ChartData> dataList;
    private List<TableFieldInfo> tableFieldInfosList;
}
