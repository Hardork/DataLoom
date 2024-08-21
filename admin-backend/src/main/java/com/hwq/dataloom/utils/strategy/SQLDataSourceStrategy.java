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

public class SQLDataSourceStrategy implements DataSourceStrategy {

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Override
    public void handleConfiguration(CoreDatasource coreDatasource,DatasourceDTO datasourceDTO) {

    }

}
