package com.hwq.dataloom.model.vo.data;

import com.hwq.dataloom.model.dto.datasource.TableFieldInfo;
import com.hwq.dataloom.mongo.entity.ChartData;
import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/6/17 15:18
 * @description 预览文件数据接口
 */
@Data
public class PreviewExcelDataVO {
    private Boolean isValid;
    private String errorMessage;
    private List<ChartData> dataList;
    private List<TableFieldInfo> tableFieldInfosList;
}
