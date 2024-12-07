package com.hwq.dataloom.service.basic.strategy;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.ai.AskAIWithDataTablesAndFieldsRequest;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.json.ai.UserChatForSQLRes;
import com.hwq.dataloom.model.vo.data.QueryAICustomSQLVO;
import com.hwq.dataloom.utils.datasource.CustomPage;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/8/20 16:07
 * @description 数据源执行策略接口定义
 */
public interface DatasourceExecuteStrategy<REQ> {
    /**
     * 执行策略标识
     *
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
     *
     * @param req 数据源
     * @return 是否通过校验
     */
    Boolean validDatasource(REQ req);


    /**
     * 获取数据源所有表信息
     *
     * @param coreDatasource 数据源信息
     * @return 表信息
     */
    List<CoreDatasetTable> getTables(CoreDatasource coreDatasource);

    /**
     * 获取数据源指定表的所有字段信息
     *
     * @param coreDatasource 数据源
     * @param tableName      表名
     * @return 所有字段信息
     */
    List<CoreDatasetTableField> getTableFields(CoreDatasource coreDatasource, String tableName);

    /**
     * 根据sql从数据源中获取数据
     *
     * @param datasourceId 数据源ID
     * @return 数据封装类
     */
    CustomPage<Map<String, Object>> getDataFromDatasourceBySql(CoreDatasource datasourceId, String sql, Integer pageNo, Integer pageSize) throws SQLException;

    /**
     * 获取表和字段信息
     *
     * @param coreDatasource 数据源
     * @return 表和字段信息
     */
    List<AskAIWithDataTablesAndFieldsRequest> getAskAIWithDataTablesAndFieldsRequests(CoreDatasource coreDatasource, User loginUser) throws SQLException;
}
