package com.hwq.dataloom.model.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HWQ
 * @date 2024/11/3 22:38
 * @description 智能分析图表请求类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnalysisChartByAIRequest {
    private String userQuestion;
    private String curRes;
}
