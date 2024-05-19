package com.hwq.bi.service.impl;
import java.sql.Driver;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.bi.bizmq.BiMessageProducer;
import com.hwq.bi.common.ErrorCode;
import com.hwq.bi.constant.CommonConstant;
import com.hwq.bi.exception.BusinessException;
import com.hwq.bi.exception.ThrowUtils;
import com.hwq.bi.mapper.ChartMapper;
import com.hwq.bi.model.dto.chart.ChartQueryRequest;
import com.hwq.bi.model.entity.Chart;
import com.hwq.bi.model.entity.Post;
import com.hwq.bi.model.entity.User;
import com.hwq.bi.model.entity.UserData;
import com.hwq.bi.service.ChartService;
import com.hwq.bi.service.UserDataService;
import com.hwq.bi.service.impl.role_info.RoleService;
import com.hwq.bi.service.impl.role_info.RoleStrategyFactory;
import com.hwq.bi.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author HWQ
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2023-09-01 23:03:32
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

    @Resource
    private UserDataService userDataService;

    @Resource
    private BiMessageProducer biMessageProducer;

    @Resource
    private RoleStrategyFactory roleStrategyFactory;

    @Override
    public void validChart(Chart chart, boolean add) {
        if (chart == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String goal = chart.getGoal();
        String name = chart.getName();
        String chartData = chart.getChartData();
        String chartType = chart.getChartType();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(goal, name, chartData, chartType), ErrorCode.PARAMS_ERROR);
        }
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Long genChartByAiWithDataAsyncMq(String name, String goal, String chartType, Long dataId, User loginUser) {
        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");
        ThrowUtils.throwIf(dataId == null, ErrorCode.PARAMS_ERROR, "数据集id不得为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 查询对应的dataId
        UserData userData = userDataService.getById(dataId);
        ThrowUtils.throwIf(userData == null, ErrorCode.PARAMS_ERROR, "请求数据集不存在");

        // 校验token数


        // 鉴权data
        ThrowUtils.throwIf(!loginUser.getId().equals(userData.getUserId()), ErrorCode.NO_AUTH_ERROR);
        // 初始化chart，设置状态为wait（等待中）
        long newChartId = initChart(name, goal, chartType, loginUser, userData);
        // 发送任务到队列中
        RoleService roleStrategy = roleStrategyFactory.getRoleStrategy(loginUser.getUserRole());
        ThrowUtils.throwIf(roleStrategy == null, ErrorCode.PARAMS_ERROR);
        roleStrategy.sendMessageToMQ(String.valueOf(newChartId));
        return newChartId;
    }

    private long initChart(String name, String goal, String chartType, User loginUser, UserData userData) {
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setUserId(loginUser.getId());
        chart.setUserDataId(userData.getId());
        chart.setChartType(chartType);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        boolean saveResult = save(chart);
        long newChartId = chart.getId();
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        return newChartId;
    }

}




