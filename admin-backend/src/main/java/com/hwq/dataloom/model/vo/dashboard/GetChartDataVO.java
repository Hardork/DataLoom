package com.hwq.dataloom.model.vo.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/9/15 20:42
 * @description 获取图表数据
 * X X轴数据类型
 */
@Data
@AllArgsConstructor
@Builder
public class GetChartDataVO {
    private XArrayData xArrayData;

    private List<SeriesData> seriesDataList;
}
