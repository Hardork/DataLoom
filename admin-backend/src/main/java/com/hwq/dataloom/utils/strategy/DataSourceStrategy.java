package com.hwq.dataloom.utils.strategy;

import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasource;

public interface DataSourceStrategy {

    void handleConfiguration(CoreDatasource coreDatasource,DatasourceDTO datasourceDTO);

}
