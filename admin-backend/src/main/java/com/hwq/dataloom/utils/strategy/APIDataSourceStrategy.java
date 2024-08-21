package com.hwq.dataloom.utils.strategy;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceTaskService;

import javax.annotation.Resource;
import java.util.List;

public class APIDataSourceStrategy implements DataSourceStrategy {

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Override
    public void handleConfiguration(CoreDatasource coreDatasource,DatasourceDTO datasourceDTO) {
        Long id = coreDatasource.getId();
        List<ApiDefinition> apiDefinitions = JSONUtil.toList(datasourceDTO.getConfiguration(), ApiDefinition.class);
        // 循环新增数据表和数据源同步任务
        for (ApiDefinition apiDefinition : apiDefinitions) {
            CoreDatasetTable coreDatasetTable = new CoreDatasetTable();
            coreDatasetTable.setName(apiDefinition.getName());
            coreDatasetTable.setTableName(apiDefinition.getDeTableName());
            coreDatasetTable.setDatasourceId(id);
            // coreDatasetTable.setDatasetGroupId();
            coreDatasetTable.setType(apiDefinition.getType());
            coreDatasetTable.setInfo(apiDefinition.getDesc());
            coreDatasetTable.setSqlVariableDetails(null);
            Long datasetTableId = coreDatasetTableService.addDatasetTable(coreDatasetTable);
            ThrowUtils.throwIf(datasetTableId < 0, ErrorCode.OPERATION_ERROR, "新增数据表失败！");
            Long datasourceTaskId = coreDatasourceTaskService.addTask(datasourceDTO, datasetTableId);
            ThrowUtils.throwIf(datasourceTaskId < 0, ErrorCode.OPERATION_ERROR, "新增定时任务失败！");
        }
    }

}
