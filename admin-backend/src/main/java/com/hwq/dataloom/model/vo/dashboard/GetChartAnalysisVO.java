package com.hwq.dataloom.model.vo.dashboard;

import lombok.Builder;
import lombok.Data;

/**
 * @author HWQ
 * @date 2024/9/20 00:41
 * @description 获取图表智能分析结果
 */
@Data
@Builder
public class GetChartAnalysisVO {
    private String analysisRes;
}
