package com.hwq.dataloom.controller;

/**
 * @Author:HWQ
 * @DateTime:2023/9/1 23:42
 * @Description:
 **/

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hwq.dataloom.annotation.*;
import com.hwq.dataloom.mq.producer.AnalysisMessageProducer;

import com.hwq.dataloom.constant.ChartConstant;
import com.hwq.dataloom.framework.constants.UserConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.request.DeleteRequest;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.dto.chart.*;
import com.hwq.dataloom.model.entity.Chart;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.enums.ChartStatusEnum;
import com.hwq.dataloom.model.vo.ChartResponse;
import com.hwq.dataloom.service.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * @author <a href="https://github.com/Hardork">老山羊</a>
 * 
 */
@RestController
@RequestMapping("/admin/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    @Resource
    private AnalysisMessageProducer analysisMessageProducer;

    @Resource
    private RedisTemplate<String, String> redisTemplate;


    /**
     * 基于数据集分析
     * @param request
     * @return
     */
    @PostMapping("/gen/async/mq/data")
    @RateLimiter(ratePerSecond = 2, key = "genChartByAi_")
    @ReduceRewardPoint(reducePoint = 2)
    @BiService
    @CheckPoint(needPoint = 2)
    @Deprecated
    public BaseResponse<ChartResponse> genChartByAiWithDataAsyncMq(@RequestBody GenChartByAiWithDataRequest genChartByAiWithDataRequest, HttpServletRequest request) {
        String name = genChartByAiWithDataRequest.getName();
        String goal = genChartByAiWithDataRequest.getGoal();
        String chartType = genChartByAiWithDataRequest.getChartType();
        Long dataId = genChartByAiWithDataRequest.getDataId();
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        ThrowUtils.throwIf(dataId == null, ErrorCode.PARAMS_ERROR, "数据集id不得为空");
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Long chartId = chartService.genChartByAiWithDataAsyncMq(name, goal, chartType, dataId, loginUser);
        // 响应
        ChartResponse biResponse = new ChartResponse();
        biResponse.setChartId(chartId);
        return ResultUtils.success(biResponse);
    }

    @PostMapping("/gen/retry")
    @ReduceRewardPoint(reducePoint = 2) 
    @CheckPoint(needPoint = 2)
    @Deprecated
    public BaseResponse<ChartResponse> ReGenChartByAiAsync(@RequestBody ReGenChartRequest reGenChartRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(reGenChartRequest == null , ErrorCode.PARAMS_ERROR);
        Long chartId = reGenChartRequest.getChartId();
        ThrowUtils.throwIf( chartId == null , ErrorCode.PARAMS_ERROR);
        // 获取用户信息
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 获取原来图表的信息 并且只有失败图表才能重试
        Chart chartInfo = chartService.getById(chartId);
        ThrowUtils.throwIf(chartInfo == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!chartInfo.getStatus().equals(ChartStatusEnum.FAILED.getValue()), ErrorCode.PARAMS_ERROR, "仅失败图表可重试");
        // 发送信息给消息队列
        analysisMessageProducer.sendMessage(String.valueOf(chartId));
        // 分析成功
        ChartResponse biResponse = new ChartResponse();
        biResponse.setChartId(chartId);
        return ResultUtils.success(biResponse);
    }



    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        chartService.validChart(chart, true);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        // 同时创建一个
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newchartId = chart.getId();
        return ResultUtils.success(newchartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        // 参数校验
        chartService.validChart(chart, false);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 查询redis中对应的缓存
        String chartJson = redisTemplate.opsForValue().get(ChartConstant.CHART_PREFIX + id);
        Chart chart = null;

        if (StringUtils.isNotEmpty(chartJson)) { //缓存命中
            chart = JSONUtil.toBean(chartJson, Chart.class);
        } else {
            chart = chartService.getById(id);
        }

        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // TODO: 从MySQL中获取图表数据
//        String chartData = mongoEngineUtils.mongoToCSV(chart.getUserDataId());
        chart.setChartData(null);

        return ResultUtils.success(chart);
    }


    /**
     * 分页获取列表（封装类）
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<Chart>> listChartVOByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest,
                                                       HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size),
                chartService.getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }


    /**
     * 编辑（表）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        // 参数校验
        chartService.validChart(chart, false);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }


}
