package com.hwq.bi.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.bi.annotation.AuthCheck;
import com.hwq.bi.common.BaseResponse;
import com.hwq.bi.common.ResultUtils;
import com.hwq.bi.model.dto.failed_chart.FailedChartQueryRequest;
import com.hwq.bi.model.dto.order.OrderQueryRequest;
import com.hwq.bi.model.entity.FailedChart;
import com.hwq.bi.model.entity.ProductOrder;
import com.hwq.bi.service.FailedChartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author:HWQ
 * @DateTime:2023/10/23 16:26
 * @Description:
 **/
@RestController
@RequestMapping("/failed_chart")
@Slf4j
public class FailedChartController {
    @Resource
    private FailedChartService failedChartService;

    @PostMapping("/list/page")
    @AuthCheck(mustRole = "Admin")
    public BaseResponse<Page<FailedChart>> listFailedChartsByPage(@RequestBody FailedChartQueryRequest failedChartQueryRequest, HttpServletRequest request) {
        long current = failedChartQueryRequest.getCurrent();
        long size = failedChartQueryRequest.getPageSize();
        QueryWrapper<FailedChart> queryWrapper = failedChartService.getQueryWrapper(failedChartQueryRequest);
        Page<FailedChart> orderPage = failedChartService.page(new Page<>(current, size),queryWrapper);
        return ResultUtils.success(orderPage);
    }
}
