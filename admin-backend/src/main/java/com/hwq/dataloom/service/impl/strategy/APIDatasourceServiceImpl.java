package com.hwq.dataloom.service.impl.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hwq.dataloom.constant.DatasourceConstant;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.ai.AskAIWithDataTablesAndFieldsRequest;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.dto.newdatasource.TableField;
import com.hwq.dataloom.model.dto.newdatasource.TaskDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.enums.DataSourceTypeEnum;

import com.hwq.dataloom.model.json.ai.UserChatForSQLRes;
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
import com.hwq.dataloom.service.CoreDatasetTableFieldService;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.CoreDatasourceTaskService;
import com.hwq.dataloom.service.basic.strategy.DatasourceExecuteStrategy;
import com.hwq.dataloom.utils.ApiUtils;
import com.hwq.dataloom.utils.datasource.CustomPage;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/8/21 09:55
 * @description API数据源策略实现类
 */
@Component
public class APIDatasourceServiceImpl implements DatasourceExecuteStrategy<DatasourceDTO> {

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private CoreDatasetTableFieldService coreDatasetTableFieldService;

    @Resource
    private DatasourceEngine datasourceEngine;

    @Override
    public String mark() {
        return DataSourceTypeEnum.API.getValue();
    }

    @Override
    public CoreDatasource getCoreDatasource() {
        return null;
    }

    @Override
    @Transactional
    public Long addCoreData(DatasourceDTO datasourceDTO, User loginUser) {
        // 新增数据源
        CoreDatasource coreDatasource = new CoreDatasource();
        coreDatasource.setName(datasourceDTO.getName());
        coreDatasource.setDescription(datasourceDTO.getDescription());
        coreDatasource.setType(datasourceDTO.getType());
        coreDatasource.setPid(datasourceDTO.getPid());
        coreDatasource.setEditType(datasourceDTO.getEditType().toString());
        coreDatasource.setConfiguration(datasourceDTO.getConfiguration());
        coreDatasource.setStatus(datasourceDTO.getStatus());
        coreDatasource.setTaskStatus(datasourceDTO.getTaskStatus());
        coreDatasource.setEnableDataFill(coreDatasource.getEnableDataFill());
        coreDatasource.setUserId(loginUser.getId());
        boolean save = coreDatasourceService.save(coreDatasource);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "新增数据源失败！");
        Long id = coreDatasource.getId();
        List<ApiDefinition> apiDefinitions = JSONUtil.toList(datasourceDTO.getConfiguration(), ApiDefinition.class);
        // 循环新增数据表 、 数据源同步任务 、 数据字段 、 XXL JOB定时任务
        for (ApiDefinition apiDefinition : apiDefinitions) {
            CoreDatasetTable coreDatasetTable = new CoreDatasetTable();
            coreDatasetTable.setName(apiDefinition.getName());
            coreDatasetTable.setTableName(String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.API.getValue(), id, apiDefinition.getName()));
            coreDatasetTable.setDatasourceId(id);
            coreDatasetTable.setType(apiDefinition.getType());
            coreDatasetTable.setInfo(apiDefinition.getDesc());
            coreDatasetTable.setSqlVariableDetails(null);
            Long datasetTableId = coreDatasetTableService.addDatasetTable(coreDatasetTable);
            ThrowUtils.throwIf(datasetTableId < 0, ErrorCode.OPERATION_ERROR, "新增数据表失败！");

            Integer xxlJobId = null;
            datasourceDTO.setId(id);
            // 添加XXL JOB定时任务
            TaskDTO taskDTO = datasourceDTO.getSyncSetting();
            if (!taskDTO.getUpdateType().equals("RIGHTNOW")) {
                xxlJobId = coreDatasourceTaskService.addXxlJob(datasourceDTO, apiDefinition);
            }

            Long datasourceTaskId = null;
            if (xxlJobId != null) {
                datasourceTaskId = coreDatasourceTaskService.addTask(datasourceDTO, datasetTableId,xxlJobId);
                ThrowUtils.throwIf(datasourceTaskId < 0, ErrorCode.OPERATION_ERROR, "新增定时任务失败！");
            }

            Long lastExecTime = coreDatasourceTaskService.getById(datasourceTaskId).getLastExecTime();

            List<TableField> fields = apiDefinition.getFields();
            int columnIndex = 0;
            List<CoreDatasetTableField> coreDatasetTableFieldList = new ArrayList<>();
            for (TableField field : fields) {
                columnIndex++;
                CoreDatasetTableField coreDatasetTableField = new CoreDatasetTableField();
                BeanUtil.copyProperties(field,coreDatasetTableField);

                coreDatasetTableField.setDatasourceId(id);
                coreDatasetTableField.setDatasetTableId(datasetTableId);
                coreDatasetTableField.setColumnIndex(columnIndex);
                coreDatasetTableField.setLastSyncTime(lastExecTime);
                coreDatasetTableField.setGroupType("d");
                coreDatasetTableFieldList.add(coreDatasetTableField);
            }
            boolean savedBatch = coreDatasetTableFieldService.saveBatch(coreDatasetTableFieldList);
            ThrowUtils.throwIf(!savedBatch,ErrorCode.OPERATION_ERROR,"新增字段失败！");

            // 将请求获得的数据添加到数据仓库
            datasourceEngine.exeCreateTable(id, String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.API.getValue(), id, apiDefinition.getName()), coreDatasetTableFieldList);
            List<String[]> dataList = ApiUtils.toDataList(JSONUtil.toJsonStr(apiDefinition));
            int pageNumber = 1000; //一次插入 1000条
            int totalPage;
            if (dataList.size() % pageNumber > 0) {
                totalPage = dataList.size() / pageNumber + 1;
            } else {
                totalPage = dataList.size() / pageNumber;
            }
            for (int page = 1; page <= totalPage; page++) {
                datasourceEngine.execInsert(id, String.format(DatasourceConstant.TABLE_NAME_TEMPLATE, DataSourceTypeEnum.API.getValue(), id, apiDefinition.getName()), dataList, page, pageNumber);
            }
        }
        return id;
    }

    @Override
    public Boolean validDatasource(DatasourceDTO datasourceDTO) {
        // 校验API数据
        String configuration = datasourceDTO.getConfiguration();
        List<ApiDefinition> apiDefinitions = JSONUtil.toList(configuration, ApiDefinition.class);
        String responseBody = null;
        try {
            // 向API发送请求
            for (ApiDefinition apiDefinition : apiDefinitions) {
                CloseableHttpResponse response = ApiUtils.getApiResponse(apiDefinition);
                int code = response.getCode();
                if (code != 200) {
                    apiDefinition.setStatus("failed");
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "调用接口失败！错误码为：" + code);
                }
                responseBody = EntityUtils.toString(response.getEntity());
                if (StringUtils.isEmpty(responseBody)) {
                    apiDefinition.setStatus("failed");
                    throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口调用失败！接口请求结果为空！");
                }
                apiDefinition.setStatus("success");
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        } finally {
            String newConfiguration = JSONUtil.toJsonStr(apiDefinitions);
            CoreDatasource coreDatasource = new CoreDatasource();
            coreDatasource.setId(datasourceDTO.getId());
            coreDatasource.setConfiguration(newConfiguration);
            coreDatasourceService.updateById(coreDatasource);
        }
        return true;
    }

    @Override
    public List<CoreDatasetTable> getTables(CoreDatasource coreDatasource) {
        // 获取数据源表信息
        ThrowUtils.throwIf(ObjectUtils.isEmpty(coreDatasource),ErrorCode.PARAMS_ERROR);
        List<CoreDatasetTable> coreDatasetTables = new ArrayList<>();
        if (coreDatasource.getId() != null) {
            QueryWrapper<CoreDatasetTable> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("datasourceId",coreDatasource.getId());
            coreDatasetTables = coreDatasetTableService.list(queryWrapper);
        }
        ThrowUtils.throwIf(ObjectUtils.isEmpty(coreDatasetTables),ErrorCode.NOT_FOUND_ERROR);
        return coreDatasetTables;
    }

    @Override
    public List<CoreDatasetTableField> getTableFields(CoreDatasource coreDatasource, String tableName) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(coreDatasource),ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(StringUtils.isEmpty(tableName),ErrorCode.PARAMS_ERROR);
        QueryWrapper<CoreDatasetTable> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("datasourceId",coreDatasource.getId());
        queryWrapper.eq("tableName",tableName);
        CoreDatasetTable coreDatasetTable = coreDatasetTableService.getOne(queryWrapper);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(coreDatasetTable),ErrorCode.NOT_FOUND_ERROR);
        Long coreDatasetTableId = coreDatasetTable.getId();
        QueryWrapper<CoreDatasetTableField> coreDatasetTableFieldQueryWrapper = new QueryWrapper<>();
        coreDatasetTableFieldQueryWrapper.eq("datasetTableId",coreDatasetTableId);
        List<CoreDatasetTableField> coreDatasetTableFields = coreDatasetTableFieldService.list(coreDatasetTableFieldQueryWrapper);
        ThrowUtils.throwIf(ObjectUtils.isEmpty(coreDatasetTableFields),ErrorCode.NOT_FOUND_ERROR);
        return coreDatasetTableFields;
    }

    @Override
     public CustomPage<Map<String, Object>> getDataFromDatasourceBySql(CoreDatasource datasource, String sql, Integer pageNo, Integer pageSize) throws SQLException {
        return datasourceEngine.execSelectSqlToQueryAICustomSQLVO(datasource.getId(), sql, pageNo,pageSize);
    }

    @Override
    public List<AskAIWithDataTablesAndFieldsRequest> getAskAIWithDataTablesAndFieldsRequests(CoreDatasource coreDatasource, User loginUser) {
        // 获取对应数据源所有表信息
        List<CoreDatasetTable> tables = coreDatasourceService.getTablesByDatasourceId(coreDatasource.getId(), loginUser);
        ThrowUtils.throwIf(tables.isEmpty(), ErrorCode.PARAMS_ERROR, "数据源暂无数据");
        List<AskAIWithDataTablesAndFieldsRequest> dataTablesAndFieldsRequests = new ArrayList<>();
        tables.forEach(table -> {
            // 查询所有字段
            LambdaQueryWrapper<CoreDatasetTableField> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(CoreDatasetTableField::getDatasetTableId, table.getId());
            List<CoreDatasetTableField> tableFields = coreDatasetTableFieldService.list(wrapper);
            AskAIWithDataTablesAndFieldsRequest askAIWithDataTablesAndFieldsRequest = AskAIWithDataTablesAndFieldsRequest.builder()
                    .tableId(table.getId())
                    .tableComment(table.getName())
                    .tableName(table.getTableName())
                    .coreDatasetTableFieldList(tableFields)
                    .build();
            dataTablesAndFieldsRequests.add(askAIWithDataTablesAndFieldsRequest);
        });
        return dataTablesAndFieldsRequests;
    }
}
