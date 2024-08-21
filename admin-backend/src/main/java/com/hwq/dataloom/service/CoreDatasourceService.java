package com.hwq.dataloom.service;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.datasource.GetTableFieldsDTO;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author wqh
* @description 针对表【core_datasource(数据源表)】的数据库操作Service
* @createDate 2024-08-18 22:41:36
*/
public interface CoreDatasourceService extends IService<CoreDatasource> {

    /**
     * 添加数据源
     * @param datasourceDTO
     * @param user
     * @return
     */
    Long addDatasource(DatasourceDTO datasourceDTO, User user);

    /**
     * 校验数据源配置
     * @param datasourceDTO
     * @return
     */
    Boolean validDatasourceConfiguration(DatasourceDTO datasourceDTO);

    /**
     * 获取数据源所有表信息
     * @param datasourceId
     * @param loginUser
     * @return
     */
    List<CoreDatasetTable> getTablesByDatasourceId(Long datasourceId, User loginUser);

    /**
     * 获取数据源指定表的字段信息
     * @param getTableFieldsDTO
     * @param loginUser
     * @return
     */
    List<CoreDatasetTableField> getTableFieldsByDatasourceIdAndTableName(GetTableFieldsDTO getTableFieldsDTO, User loginUser);
}
