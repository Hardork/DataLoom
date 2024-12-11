package com.hwq.dataloom.core.workflow.chart.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.core.workflow.chart.model.WorkflowChartRequest;
import com.hwq.dataloom.core.workflow.chart.model.WorkflowChartVO;
import com.hwq.dataloom.core.workflow.chart.service.WorkflowChartDataService;
import com.hwq.dataloom.core.workflow.chart.service.WorkflowChartInfoService;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.mapper.WorkflowChartInfoMapper;
import com.hwq.dataloom.model.entity.WorkflowChartData;
import com.hwq.dataloom.model.entity.WorkflowChartInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author: HCJ
 * @DateTime: 2024/12/11
 * @Description:
 **/
@Service
public class WorkflowChartInfoServiceImpl extends ServiceImpl<WorkflowChartInfoMapper, WorkflowChartInfo>
        implements WorkflowChartInfoService {

    @Resource
    private WorkflowChartDataService workflowChartDataService;


    @Override
    public WorkflowChartVO getWorkflowChartInfoAndData(WorkflowChartRequest workflowChartRequest) {
        Long id = workflowChartRequest.getId();
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        Integer queryCount = workflowChartRequest.getQueryCount();
        ThrowUtils.throwIf(queryCount == null || queryCount < 0 || queryCount > 100, ErrorCode.PARAMS_ERROR);
        // 查询图表信息
        WorkflowChartInfo workflowChartInfo = getById(id);
        ThrowUtils.throwIf(workflowChartInfo == null, new BusinessException(ErrorCode.PARAMS_ERROR, "查询id 的图表不存在"));
        // 查询数据
        Page<WorkflowChartData> workflowChartDataPage = workflowChartDataService.page(new Page<>(1, queryCount),
                new LambdaQueryWrapper<WorkflowChartData>()
                        .eq(WorkflowChartData::getWorkflowChartId, id)
                        .orderByDesc(WorkflowChartData::getCollectionTime)
        );
        // 组合
        return WorkflowChartVO.builder()
                .id(id)
                .chartName(workflowChartInfo.getChartName())
                .chartDesc(workflowChartInfo.getChartDesc())
                .workflowChartDataList(workflowChartDataPage.getRecords())
                .build();
    }
}
