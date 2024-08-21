package com.hwq.dataloom.utils.strategy;

import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasource;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class DataSourceStrategyManager {

    public void handleConfiguration(CoreDatasource coreDatasource, DatasourceDTO datasourceDTO) {
        String type = datasourceDTO.getType();
        if (StringUtils.isNotEmpty(type) && StringUtils.equals(datasourceDTO.getType(), "API")) {
            DataSourceStrategy dataSourceStrategy = new APIDataSourceStrategy();
            dataSourceStrategy.handleConfiguration(coreDatasource,datasourceDTO);
        } else if (StringUtils.isNotEmpty(type) && StringUtils.equals(datasourceDTO.getType(), "SQL")) {
            DataSourceStrategy dataSourceStrategy = new APIDataSourceStrategy();
            dataSourceStrategy.handleConfiguration(coreDatasource,datasourceDTO);
        } else if (StringUtils.isNotEmpty(type) && StringUtils.equals(datasourceDTO.getType(), "EXCEL")) {
            DataSourceStrategy dataSourceStrategy = new APIDataSourceStrategy();
            dataSourceStrategy.handleConfiguration(coreDatasource,datasourceDTO);
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不存在的数据类型！");
        }
    }
}
