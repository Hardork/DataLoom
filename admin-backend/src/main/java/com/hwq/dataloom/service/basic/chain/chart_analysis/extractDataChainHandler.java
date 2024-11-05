package com.hwq.dataloom.service.basic.chain.chart_analysis;

import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.model.dto.ai.AnalysisChartByAIRequest;
import com.hwq.dataloom.service.AIService;
import com.hwq.dataloom.service.basic.handler.AITaskAbstractChainHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import static com.hwq.dataloom.constant.ChainHandlerMarkConstant.ANALYSIS_CHART;

/**
 * TODO：智能分析 - 提取用户数据
 * 从数据
 */
@Component
public class extractDataChainHandler implements AITaskAbstractChainHandler<AnalysisChartByAIRequest> {

    @Resource
    private AiManager aiManager;

    @Resource
    private AIService aiService;


    @Override
    public void handle(AnalysisChartByAIRequest requestParam) {
        String question = requestParam.getQuestion();
        //      * 分析需求：%s,
        //     * [
        //     * {表名: %s, 表注释： %s, 字段列表:[{%s}、{%s}]}
        //     * {表名: %s, 表注释： %s, 字段列表:[{%s}、{%s}]}
        //     * ]
        // 根据需求查询SQL
//        String resSql = aiManager.doChatWithKimi32K(question, 200);
        // 执行sql

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
        return 1;
    }
}
