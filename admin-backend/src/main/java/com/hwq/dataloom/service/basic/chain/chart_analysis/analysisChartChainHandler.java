package com.hwq.dataloom.service.basic.chain.chart_analysis;

import com.hwq.dataloom.model.dto.ai.AnalysisChartByAIRequest;
import com.hwq.dataloom.service.basic.handler.AITaskAbstractChainHandler;
import org.springframework.stereotype.Component;

import static com.hwq.dataloom.constant.ChainHandlerMarkConstant.ANALYSIS_CHART;

/**
 * TODO：智能分析 - 提取用户数据
 */
@Component
public class analysisChartChainHandler implements AITaskAbstractChainHandler<AnalysisChartByAIRequest> {

    @Override
    public void handle(AnalysisChartByAIRequest requestParam) {

    }

    @Override
    public void doLog() {}

    @Override
    public String prompt() {
        return null;
    }

    @Override
    public String mark() {
        return ANALYSIS_CHART;
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
