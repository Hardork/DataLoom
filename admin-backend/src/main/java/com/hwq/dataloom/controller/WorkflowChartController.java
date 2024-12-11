package com.hwq.dataloom.controller;

import com.hwq.dataloom.core.workflow.chart.model.WorkflowChartRequest;
import com.hwq.dataloom.core.workflow.chart.model.WorkflowChartVO;
import com.hwq.dataloom.core.workflow.chart.service.WorkflowChartDataService;
import com.hwq.dataloom.core.workflow.chart.service.WorkflowChartInfoService;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.workflow.SaveWorkflowDTO;
import com.hwq.dataloom.model.entity.WorkflowChartData;
import com.hwq.dataloom.model.vo.workflow.SaveWorkflowDraftVO;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: HCJ
 * @DateTime: 2024/12/11
 * @Description:
 **/
@RestController
@RequestMapping("/workflow_chart")
@Slf4j
public class WorkflowChartController {

    @Resource
    private WorkflowChartInfoService workflowChartInfoService;
    @Resource
    private WorkflowChartDataService workflowChartDataService;

    @GetMapping("/query")
    @Operation(summary = "工作流图表数据查询")
    public BaseResponse<WorkflowChartVO> getWorkflowChartInfoAndData(
            @RequestBody WorkflowChartRequest workflowChartRequest,
            HttpServletRequest request) {
        WorkflowChartVO workflowChartInfoAndData = workflowChartInfoService.getWorkflowChartInfoAndData(workflowChartRequest);
        return ResultUtils.success(workflowChartInfoAndData);
    }

    @PostMapping("/save_data")
    @Operation(summary = "工作流图表数据保存")
    public BaseResponse<Boolean> getWorkflowChartInfoAndData(
            @RequestBody WorkflowChartData workflowChartData,
            HttpServletRequest request) {
        boolean save = workflowChartDataService.save(workflowChartData);
        return ResultUtils.success(save);
    }
}
