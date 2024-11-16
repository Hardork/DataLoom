package com.hwq.dataloom.service.basic.chain.chart_analysis;

import com.hwq.dataloom.constant.PromptConstants;
import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.model.dto.ai.AnalysisChartByAIRequest;
import com.hwq.dataloom.model.entity.Chart;
import com.hwq.dataloom.model.enums.ChartStatusEnum;
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
import com.hwq.dataloom.service.AIService;
import com.hwq.dataloom.service.ChartService;
import com.hwq.dataloom.service.ChatService;
import com.hwq.dataloom.service.basic.handler.AITaskAbstractChainHandler;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;

import static com.hwq.dataloom.constant.ChainHandlerMarkConstant.ANALYSIS_CHART;

/**
 * TODO：智能分析 - 提取用户数据
 * 从数据
 */
@Component
@Slf4j
public class extractDataChainHandler implements AITaskAbstractChainHandler<AnalysisChartByAIRequest> {

    @Resource
    private AiManager aiManager;

    @Resource
    private DatasourceEngine datasourceEngine;

    @Resource
    private ChartService chartService;


    @Override
    public void handle(AnalysisChartByAIRequest requestParam) {
        // 根据需求查询SQL
        String resSql = aiManager.doChatWithKimi32K(requestParam.getQuestion(), String.format(prompt(), 200));
        try {
            // 执行sql，获取到对应的数据
            QueryAICustomSQLVO queryAICustomSQLVO = datasourceEngine.execSelectSqlToQueryAICustomSQLVO(requestParam.getDatasourceId(), resSql);
            List<String> columns = queryAICustomSQLVO.getColumns();
            List<Map<String, Object>> res = queryAICustomSQLVO.getRes();

        } catch (Exception e) {
        }
    }

    @Override
    public void doAfterFailed(AnalysisChartByAIRequest analysisChartByAIRequest) {
        log.error("ANALYSIS_CHART：执行SQL失败");
        Chart updateChartResult = new Chart();
        updateChartResult.setId(analysisChartByAIRequest.getChartId());
        updateChartResult.setStatus(ChartStatusEnum.FAILED.getValue());
        updateChartResult.setExecMessage("数据提取失败");
        chartService.updateById(updateChartResult);
    }

    @Override
    public void doLog() {
        log.info("ANALYSIS_CHART：执行SQL成功");
    }

    @Override
    public String prompt() {
        return PromptConstants.SQL_ANALYSIS_PROMPT;
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
