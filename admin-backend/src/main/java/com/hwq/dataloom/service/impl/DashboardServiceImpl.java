package com.hwq.dataloom.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.dashboard.AddDashboardChartRequestDTO;
import com.hwq.dataloom.model.dto.dashboard.AddDashboardRequestDTO;
import com.hwq.dataloom.model.dto.dashboard.EditDashboardChartRequestDTO;
import com.hwq.dataloom.model.dto.dashboard.SaveDashboardRequestDTO;
import com.hwq.dataloom.model.entity.ChartOption;
import com.hwq.dataloom.model.entity.Dashboard;
import com.hwq.dataloom.service.ChartOptionService;
import com.hwq.dataloom.service.DashboardService;
import com.hwq.dataloom.mapper.DashboardMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
* @author wqh
* @description 针对表【dashboard(仪表盘表)】的数据库操作Service实现
* @createDate 2024-09-12 18:52:14
*/
@Service
public class DashboardServiceImpl extends ServiceImpl<DashboardMapper, Dashboard>
    implements DashboardService{

    @Resource
    private ChartOptionService chartOptionService;

    @Override
    public void addDashboard(AddDashboardRequestDTO addDashboardRequestDTO, User loginUser) {
        Dashboard dashboard = new Dashboard();
        BeanUtils.copyProperties(addDashboardRequestDTO, dashboard);
        dashboard.setUserId(loginUser.getId());
        ThrowUtils.throwIf(!this.save(dashboard), ErrorCode.SYSTEM_ERROR, "保存数据失败");
    }

    @Override
    public void deleteDashboard(Long dashboardId, User loginUser) {
        Dashboard dashboard = this.getDashboardById(dashboardId, loginUser);
        ThrowUtils.throwIf(dashboard == null, ErrorCode.NOT_FOUND_ERROR);
        LambdaQueryWrapper<Dashboard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Dashboard::getId, dashboard)
                .eq(Dashboard::getUserId, loginUser.getId());
        ThrowUtils.throwIf(!this.remove(queryWrapper), ErrorCode.SYSTEM_ERROR);
    }

    @Override
    public void saveDashboard(SaveDashboardRequestDTO saveDashboardRequestDTO, User loginUser) {
        Dashboard dashboard = new Dashboard();
        BeanUtils.copyProperties(saveDashboardRequestDTO, dashboard);
        ThrowUtils.throwIf(!this.updateById(dashboard), ErrorCode.SYSTEM_ERROR, "保存数据失败");
    }

    @Override
    public Long addChart(AddDashboardChartRequestDTO addDashboardChartRequestDTO, User loginUser) {
        ChartOption chartOption = new ChartOption();
        BeanUtils.copyProperties(addDashboardChartRequestDTO, chartOption);
        ThrowUtils.throwIf(!chartOptionService.save(chartOption), ErrorCode.SYSTEM_ERROR, "保存数据失败");
        return chartOption.getId();
    }

    @Override
    public ChartOption getChartById(Long chartOptionId) {
        return chartOptionService.getById(chartOptionId);
    }

    @Override
    public void editChart(EditDashboardChartRequestDTO editDashboardChartRequestDTO, User loginUser) {
        ChartOption chartOption = new ChartOption();
        BeanUtils.copyProperties(editDashboardChartRequestDTO, chartOption);
        ThrowUtils.throwIf(!chartOptionService.updateById(chartOption), ErrorCode.SYSTEM_ERROR, "保存数据失败");
    }

    @Override
    public List<ChartOption> listAllChart(Long dashboardId, User loginUser) {
        // 鉴权是否有权限查看
        Dashboard dashboard = this.getById(dashboardId);
        ThrowUtils.throwIf(!dashboard.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        LambdaQueryWrapper<ChartOption> qw = new LambdaQueryWrapper<>();
        qw.eq(ChartOption::getDashboardId, dashboardId);
        return chartOptionService.list(qw);
    }

    @Override
    public List<Dashboard> listAllDashboard(User loginUser) {
        LambdaQueryWrapper<Dashboard> qw = new LambdaQueryWrapper<>();
        qw.eq(Dashboard::getUserId, loginUser.getId());
        return this.list(qw);
    }

    @Override
    public Dashboard getDashboardById(Long dashboardId, User loginUser) {
        Dashboard dashboard = this.getById(dashboardId);
        ThrowUtils.throwIf(dashboard == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!dashboard.getUserId().equals(loginUser.getId()), ErrorCode.NOT_FOUND_ERROR);
        return dashboard;
    }

    @Override
    public Boolean deleteChart(Long chartId, User loginUser) {
        ChartOption chartOption = chartOptionService.getById(chartId);
        Long dashboardId = chartOption.getDashboardId();
        Dashboard dashboard = this.getById(dashboardId);
        ThrowUtils.throwIf(dashboard == null, ErrorCode.NOT_FOUND_ERROR);
        // 更新dashboard的snapshot
        // {lg : [{ i: 1, x: 0, y: 0, w: 3, h: 2 }, {{ i: 2, x: 4, y: 4, w: 3, h: 2 }}]}
        String snapshot = dashboard.getSnapshot();
        if (StringUtils.isEmpty(snapshot)) return true;
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(snapshot);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "序列化异常");
        }

        // 获取 "lg" 数组
        ArrayNode lgArray = (ArrayNode) rootNode.get("lg");

        // 遍历数组，删除 i == 删除图表id 的元素
        for (int i = 0; i < lgArray.size(); i++) {
            JsonNode node = lgArray.get(i);
            if (node.get("i").asLong() == chartOption.getId()) {
                lgArray.remove(i);
                break; // 假设只有一个 i == 1 的元素，找到后删除并退出循环
            }
        }

        // 将修改后的 JsonNode 转换为字符串
        String updatedJsonStr = null;
        try {
            updatedJsonStr = mapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "序列化异常");
        }
        System.out.println("Updated JSON: " + updatedJsonStr);
        dashboard.setSnapshot(updatedJsonStr);
        ThrowUtils.throwIf(!this.updateById(dashboard), ErrorCode.SYSTEM_ERROR);
        return true;
    }
}




