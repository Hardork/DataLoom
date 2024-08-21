package com.hwq.dataloom.service.impl.strategy;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.enums.DataSourceTypeEnum;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.CoreDatasourceTaskService;
import com.hwq.dataloom.service.basic.DatasourceExecuteStrategy;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HWQ
 * @date 2024/8/21 09:55
 * @description API数据源策略实现类
 */
public class APIDatasourceServiceImpl implements DatasourceExecuteStrategy<DatasourceDTO> {

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Override
    public String mark() {
        return DataSourceTypeEnum.API.getValue();
    }

    @Override
    public CoreDatasource getCoreDatasource() {
        return null;
    }

    @Override
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
        ThrowUtils.throwIf(!save,ErrorCode.OPERATION_ERROR,"新增数据源失败！");
        Long id = coreDatasource.getId();
        List<ApiDefinition> apiDefinitions = JSONUtil.toList(datasourceDTO.getConfiguration(), ApiDefinition.class);
        // 循环新增数据表和数据源同步任务
        for (ApiDefinition apiDefinition : apiDefinitions) {
            CoreDatasetTable coreDatasetTable = new CoreDatasetTable();
            coreDatasetTable.setName(apiDefinition.getName());
            coreDatasetTable.setTableName(apiDefinition.getDeTableName());
            coreDatasetTable.setDatasourceId(id);
            coreDatasetTable.setType(apiDefinition.getType());
            coreDatasetTable.setInfo(apiDefinition.getDesc());
            coreDatasetTable.setSqlVariableDetails(null);
            Long datasetTableId = coreDatasetTableService.addDatasetTable(coreDatasetTable);
            ThrowUtils.throwIf(datasetTableId < 0, ErrorCode.OPERATION_ERROR, "新增数据表失败！");
            Long datasourceTaskId = coreDatasourceTaskService.addTask(datasourceDTO, datasetTableId);
            ThrowUtils.throwIf(datasourceTaskId < 0, ErrorCode.OPERATION_ERROR, "新增定时任务失败！");
        }
        return id;
    }

    @Override
    public Boolean validDatasource(DatasourceDTO datasourceDTO) {
        // zzx TODO: 校验API数据
        return null;
    }

    @Override
    public List<CoreDatasetTable> getTables(CoreDatasource coreDatasource) {
        // zzx TODO: 获取数据源表信息
        return null;
    }

    @Override
    public List<CoreDatasetTableField> getTableFields(CoreDatasource coreDatasource, String tableName) {
        return null;
    }
}
