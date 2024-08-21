package com.hwq.dataloom.utils.strategy;

import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceTaskService;

import javax.annotation.Resource;

public class EXCELDataSourceStrategy implements DataSourceStrategy {

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Override
    public void handleConfiguration(CoreDatasource coreDatasource,DatasourceDTO datasourceDTO) {

    }

}
