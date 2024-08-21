package com.hwq.dataloom.service.basic;

import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;

import java.util.List;

/**
 * @author HWQ
 * @date 2024/8/20 16:07
 * @description 数据源执行策略接口定义
 */
public interface DatasourceExecuteStrategy<REQ> {
    /**
     * 执行策略标识
     * @return
     */
    String mark();

    /**
     * 获取数据源信息
     */
    CoreDatasource getCoreDatasource();

    /**
     * 添加数据源
     */
    Long addCoreData(REQ req, User loginUser);


    /**
     * 校验数据
     * @param req 数据源
     * @return
     */
    Boolean validDatasource(REQ req);


    /**
     * 获取数据源所有表信息
     * @param coreDatasource 数据源信息
     * @return
     */
    List<CoreDatasetTable> getTables(CoreDatasource coreDatasource);

    /**
     * 获取数据源指定表的所有字段信息
     * @param coreDatasource
     * @param tableName
     * @return
     */
    List<CoreDatasetTableField> getTableFields(CoreDatasource coreDatasource, String tableName);
}
