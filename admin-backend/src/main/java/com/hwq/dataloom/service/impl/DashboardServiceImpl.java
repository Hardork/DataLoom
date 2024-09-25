package com.hwq.dataloom.service.impl;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
import com.hwq.dataloom.manager.AiManager;
import com.hwq.dataloom.model.dto.dashboard.*;
import com.hwq.dataloom.model.entity.ChartOption;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.entity.Dashboard;
import com.hwq.dataloom.model.enums.SeriesArrayRollUpEnum;
import com.hwq.dataloom.model.enums.SeriesArrayTypeEnum;
import com.hwq.dataloom.model.json.GroupField;
import com.hwq.dataloom.model.json.Series;
import com.hwq.dataloom.model.vo.dashboard.GetChartAnalysisVO;
import com.hwq.dataloom.model.vo.dashboard.GetChartDataVO;
import com.hwq.dataloom.service.ChartOptionService;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.DashboardService;
import com.hwq.dataloom.mapper.DashboardMapper;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.util.List;
import java.util.Objects;

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

    @Resource
    private DatasourceEngine datasourceEngine;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private AiManager aiManager;

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
        // 1.删除图表
        ThrowUtils.throwIf(!chartOptionService.removeById(chartId), ErrorCode.SYSTEM_ERROR);
        // 2.更新dashboard的snapshot
        // snapshot格式 ：
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
        dashboard.setSnapshot(updatedJsonStr);
        ThrowUtils.throwIf(!this.updateById(dashboard), ErrorCode.SYSTEM_ERROR);
        return true;
    }

    /**
     * dataOption示例
     * const dataOption = {
     *       dashboardId: selectedDashboard?.id,
     *       dataCondition: {
     *       datasourceId: addChartDefaultDatasource.id,
     *       dataTableName: addChartDefaultTable.tableName,
     *       seriesArrayType: 0, // 纵轴类型 0 - 统计记录总数 1 - 统计字段数值
     *         // 数值列字段
     *       seriesArray: [
     *           { fieldName: addChartDefaultField.originName, rollup: "COUNT" }
     *       ],
     *         // 分组字段
     *       group: [
     *         { fieldName: addChartDefaultField.originName, mode: "integrated" }
     *       ],
     *       source: { type: "ALL", filterInfo: null },
     *       includeRecordIds: false,
     *       includeArchiveTable: false
     *     }
     *   };
     */
    @Override
    public GetChartDataVO getChartData(GetChartDataRequestDTO getChartDataRequestDTO, User loginUser) {
        String dataOption = getChartDataRequestDTO.getDataOption();
        // 1.根据dataOption加载用户需要的数据
        try {
            JSONObject jsonObject = JSON.parseObject(dataOption);
            Integer seriesArrayType = jsonObject.getInteger("seriesArrayType");
            Long datasourceId = jsonObject.getLong("datasourceId");
            String tableName = jsonObject.getString("dataTableName");
            ThrowUtils.throwIf(StringUtils.isEmpty(tableName), ErrorCode.PARAMS_ERROR, "数据表不得为空");
            // 1.1 鉴权
            CoreDatasource coreDatasource = coreDatasourceService.getById(datasourceId);
            ThrowUtils.throwIf(coreDatasource == null, ErrorCode.NOT_FOUND_ERROR);
            ThrowUtils.throwIf(!Objects.equals(coreDatasource.getUserId(), loginUser.getId()), ErrorCode.NOT_FOUND_ERROR);
            ThrowUtils.throwIf(!coreDatasetTableService.hasPermission(datasourceId, tableName, loginUser), ErrorCode.NO_AUTH_ERROR);
            // 1.2 判断纵轴类型
            SeriesArrayTypeEnum seriesArrayTypeEnum = SeriesArrayTypeEnum.getEnumByValue(seriesArrayType);
            ThrowUtils.throwIf(seriesArrayTypeEnum == null, ErrorCode.PARAMS_ERROR, "对应类型不存在");
            JSONArray seriesArray = jsonObject.getJSONArray("seriesArray");
            List<Series> seriesList = seriesArray.toJavaList(Series.class);
            JSONArray group = jsonObject.getJSONArray("group");
            List<GroupField> groupList = group.toJavaList(GroupField.class);
            // 1.3 根据不同的纵轴类型进行数据抽取
            if (seriesArrayTypeEnum == SeriesArrayTypeEnum.RECORD_COUNT) { // 查询记录总数
                return handleRecordCount(datasourceId, tableName, groupList);
            } else { // TODO: 查询字段值
                return handleFieldGroup(datasourceId, tableName, groupList, seriesList);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数格式错误");
        }
    }

    @Override
    public GetChartDataVO getChartDataById(Long chartId, User loginUser) {
        ChartOption chartOption = chartOptionService.getById(chartId);
        ThrowUtils.throwIf(chartOption == null, ErrorCode.NOT_FOUND_ERROR);
        GetChartDataRequestDTO getChartDataRequestDTO = new GetChartDataRequestDTO();
        getChartDataRequestDTO.setDataOption(chartOption.getDataOption());
        return this.getChartData(getChartDataRequestDTO, loginUser);
    }

    @Override
    public GetChartAnalysisVO getChartAnalysis(Long chartId, User loginUser) {
        ChartOption chartOption = chartOptionService.getById(chartId);
        ThrowUtils.throwIf(chartOption == null, ErrorCode.NOT_FOUND_ERROR);
        if (chartOption.getAnalysisLastFlag()) { // 图表分析结果已经是最新的，直接返回结果
            return GetChartAnalysisVO.builder()
                    .analysisRes(chartOption.getAnalysisRes())
                    .build();
        }
        String dataOption = chartOption.getDataOption();
        GetChartDataVO chartData = getChartDataById(chartId, loginUser);
        String seriesDataListJsonStr = JSONUtil.toJsonStr(chartData.getSeriesDataList());
        String xArrayDataJsonStr = JSONUtil.toJsonStr(chartData.getXArrayData());
        // TODO: 流式返回结果
        String res = aiManager.doAskChartAnalysis(chartOption.getChartName(), dataOption, seriesDataListJsonStr, xArrayDataJsonStr, false, null);
        chartOption.setAnalysisLastFlag(Boolean.TRUE);
        chartOption.setAnalysisRes(res);
        chartOptionService.updateById(chartOption);
        return GetChartAnalysisVO.builder()
                .analysisRes(res)
                .build();
    }

    @Override
    public Boolean getChartAnalysisFlux(Long chartId, User loginUser) {
        ChartOption chartOption = chartOptionService.getById(chartId);
        ThrowUtils.throwIf(chartOption == null, ErrorCode.NOT_FOUND_ERROR);
        if (chartOption.getAnalysisLastFlag()) { // 图表分析结果已经是最新的，直接返回结果
            return Boolean.TRUE;
        }
        String dataOption = chartOption.getDataOption();
        GetChartDataVO chartData = getChartDataById(chartId, loginUser);
        String seriesDataListJsonStr = JSONUtil.toJsonStr(chartData.getSeriesDataList());
        String xArrayDataJsonStr = JSONUtil.toJsonStr(chartData.getXArrayData());
        // TODO: 流式返回结果
        String res = aiManager.doAskChartAnalysis(chartOption.getChartName(), dataOption, seriesDataListJsonStr, xArrayDataJsonStr, true, loginUser);
        chartOption.setAnalysisLastFlag(Boolean.TRUE);
        chartOption.setAnalysisRes(res);
        chartOptionService.updateById(chartOption);
        return Boolean.TRUE;
    }

    private GetChartDataVO handleFieldGroup(Long datasourceId, String tableName, List<GroupField> groupList, List<Series> seriesList) {
        ThrowUtils.throwIf(groupList.isEmpty(), ErrorCode.PARAMS_ERROR, "分组字段不得为空");
        ThrowUtils.throwIf(seriesList.isEmpty(), ErrorCode.PARAMS_ERROR, "数值字段不得为空");
        // 构造SQL
        StringBuilder sql = new StringBuilder();
        String groupFieldName = groupList.get(0).getFieldName();
        sql
                .append("SELECT %s FROM ")
                .append(tableName)
                .append(" group by ")
                .append(groupFieldName)
                .append(" limit ")
                .append(100); // 限制查询上限为100行记录
        // 构造检索字段
        StringBuilder selectFieldSql = new StringBuilder();
        selectFieldSql
                .append(groupFieldName)
                .append(",")
        ;
        seriesList.forEach(field -> {
            // 1. 校验查询类型是否存在
            String rollup = field.getRollup();
            SeriesArrayRollUpEnum seriesArrayRollUpEnum = SeriesArrayRollUpEnum.getEnumByValue(rollup);
            ThrowUtils.throwIf(seriesArrayRollUpEnum == null, ErrorCode.PARAMS_ERROR, "查询类型不存在");
            // 2. 构造字段
            selectFieldSql
                    .append(String.format(seriesArrayRollUpEnum.getSelectTemplate(), field.getFieldName(), field.getFieldName()))
                    .append(",");
        });
        // 去除最后一个,
        String fieldSql = selectFieldSql.substring(0, selectFieldSql.length() - 1);
        String selectSql = String.format(sql.toString(), fieldSql);
        return datasourceEngine.execSelectSqlForGetChartDataVO(datasourceId, selectSql);
    }

    private GetChartDataVO handleRecordCount(Long datasourceId, String tableName, List<GroupField> groupList) {
        ThrowUtils.throwIf(groupList.isEmpty(), ErrorCode.PARAMS_ERROR, "分组字段不得为空");
        // 构造SQL
        StringBuilder sql = new StringBuilder();
        String groupFieldName = groupList.get(0).getFieldName();
        sql
                .append("SELECT %s FROM ")
                .append(tableName)
                .append(" group by ")
                .append(groupFieldName)
                .append(" limit ")
                .append(100); // 限制查询上限为100行记录
        // 构造检索字段
        StringBuilder selectFieldSql = new StringBuilder();
        selectFieldSql
                .append(groupFieldName)
                .append(",")
                .append(String.format(SeriesArrayRollUpEnum.COUNT.getSelectTemplate(), groupFieldName, "记录总数"))
                .append(",")
        ;
        // 去除最后一个,
        String fieldSql = selectFieldSql.substring(0, selectFieldSql.length() - 1);
        String selectSql = String.format(sql.toString(), fieldSql);
        return datasourceEngine.execSelectSqlForGetChartDataVO(datasourceId, selectSql);
    }


}




