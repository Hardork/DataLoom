package com.hwq.dataloom.service.basic.chain.chart_analysis;

import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.model.dto.ai.AnalysisChartByAIRequest;
import com.hwq.dataloom.model.entity.Chart;
import com.hwq.dataloom.model.enums.ChartStatusEnum;
import com.hwq.dataloom.service.ChartService;
import com.hwq.dataloom.service.basic.handler.AITaskAbstractChainHandler;
import com.hwq.dataloom.utils.MoonshotAiClient;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.cms.PasswordRecipientId;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.hwq.dataloom.constant.ChainHandlerMarkConstant.ANALYSIS_CHART;
import static com.hwq.dataloom.constant.PromptConstants.SINGLE_CHART_ANALYSIS_PROMPT;

/**
 * TODO：智能分析 - 获取执行SQL
 */
@Component
@Slf4j
public class analysisChartChainHandler implements AITaskAbstractChainHandler<AnalysisChartByAIRequest> {


    @Resource
    private AiManager aiManager;

    @Resource
    private ChartService chartService;

    @Override
    public void handle(AnalysisChartByAIRequest requestParam) {
        Boolean isSuccess = requestParam.getIsSuccess();
        if (!isSuccess) { // 记录日志，前一个AI编排任务执行失败，记录执行
            return;
        }
        String question = requestParam.getRes();
        String chatRes = aiManager.doChatWithKimi128K(question, prompt());

    }

    @Override
    public void doAfterFailed(AnalysisChartByAIRequest request) {
        log.error("ANALYSIS_CHART：执行分析任务失败");
        Chart updateChartResult = new Chart();
        updateChartResult.setId(request.getChartId());
        updateChartResult.setStatus(ChartStatusEnum.FAILED.getValue());
        updateChartResult.setExecMessage("执行分析任务失败");
        chartService.updateById(updateChartResult);
    }

    @Override
    public void doLog() {

    }

    public void taskFailLog(AnalysisChartByAIRequest requestParam, String failedReason) {
        // 记录失败原因以及更新任务状态

    }

    @Override
    public String prompt() {
        return SINGLE_CHART_ANALYSIS_PROMPT;
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
