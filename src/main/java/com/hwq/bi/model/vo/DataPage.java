package com.hwq.bi.model.vo;

import com.hwq.bi.model.dto.datasource.TableFieldInfo;
import com.hwq.bi.mongo.entity.ChartData;
import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/4/25 20:09
 * @description
 */
@Data
public class DataPage {
    private Long current;
    private Long size;
    private Long total;
    private List<ChartData> dataList;
    private List<TableFieldInfo> tableFieldInfosList;
}
