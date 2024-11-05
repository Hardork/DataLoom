package com.hwq.dataloom.service.basic.chain.chart_analysis;

import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.model.dto.ai.AnalysisChartByAIRequest;
import com.hwq.dataloom.service.basic.handler.AITaskAbstractChainHandler;
import com.hwq.dataloom.utils.MoonshotAiClient;
import org.bouncycastle.cms.PasswordRecipientId;
import org.springframework.stereotype.Component;

import static com.hwq.dataloom.constant.ChainHandlerMarkConstant.ANALYSIS_CHART;

/**
 * TODO：智能分析 - 获取执行SQL
 */
@Component
public class analysisChartChainHandler implements AITaskAbstractChainHandler<AnalysisChartByAIRequest> {


    @Override
    public void handle(AnalysisChartByAIRequest requestParam) {
        Boolean isSuccess = requestParam.getIsSuccess();
        if (!isSuccess) { // 记录日志，前一个AI编排任务执行失败，记录执行
            return;
        }


    }

    @Override
    public void doLog() {

    }

    public void taskFailLog(AnalysisChartByAIRequest requestParam, String failedReason) {
        // 记录失败原因以及更新任务状态

    }

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
