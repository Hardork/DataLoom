package com.hwq.dataloom.service.impl.strategy;
import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.ai.AskAIWithDataTablesAndFieldsRequest;
import com.hwq.dataloom.model.dto.datasource.SchemaStructure;
import com.hwq.dataloom.model.dto.datasource_tree.AddDatasourceDirRequest;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.enums.DataSourceTypeEnum;
import com.hwq.dataloom.model.enums.DirTypeEnum;
import com.hwq.dataloom.model.json.ai.UserChatForSQLRes;
import com.hwq.dataloom.model.json.datasource.StructDatabaseConfiguration;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.DatasourceDirTreeService;
import com.hwq.dataloom.service.basic.strategy.DatasourceExecuteStrategy;
import com.hwq.dataloom.utils.datasource.CustomPage;
import com.hwq.dataloom.utils.datasource.RemoteMySQLEngine;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author HWQ
 * @date 2024/8/20 23:49
 * @description MySQL数据源策略实现类
 * TODO: 缓存连接
 */
@Component
public class MySQLDatasourceServiceImpl implements DatasourceExecuteStrategy<DatasourceDTO> {

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private DatasourceDirTreeService datasourceDirTreeService;

    @Resource
    private RemoteMySQLEngine remoteMySQLEngine;

    @Override
    public String mark() {
        return DataSourceTypeEnum.MYSQL.getValue();
    }

    @Override
    public CoreDatasource getCoreDatasource() {
        return null;
    }

    @Override
    @Transactional
    public Long addCoreData(DatasourceDTO datasourceDTO, User loginUser) {
        String configuration = datasourceDTO.getConfiguration();
        // 将JSON转换为对象
        StructDatabaseConfiguration structDatabaseConfiguration = JSONUtil.toBean(configuration, StructDatabaseConfiguration.class);
        // 重新进行一次数据源校验
        RemoteMySQLEngine.checkConnectValid(structDatabaseConfiguration);
        // 根据pid存储到数据源文件树中
        AddDatasourceDirRequest addDatasourceDirRequest = AddDatasourceDirRequest
                .builder()
                .name(datasourceDTO.getName())
                .pid(datasourceDTO.getPid())
                .type(DirTypeEnum.FILE.getText())
                .build();
        datasourceDirTreeService.addDatasourceDirNode(addDatasourceDirRequest, loginUser);
        // 存储数据源信息
        CoreDatasource coreDatasource = new CoreDatasource();
        coreDatasource.setName(datasourceDTO.getName());
        coreDatasource.setDescription(datasourceDTO.getDescription());
        coreDatasource.setType(datasourceDTO.getType());
        coreDatasource.setConfiguration(configuration);
        coreDatasource.setUserId(loginUser.getId());
        ThrowUtils.throwIf(!coreDatasourceService.save(coreDatasource), ErrorCode.SYSTEM_ERROR);
        return coreDatasource.getId();
    }

    @Override
    public Boolean validDatasource(DatasourceDTO datasourceDTO) {
        String configuration = datasourceDTO.getConfiguration();
        // 将JSON转换为对象
        StructDatabaseConfiguration structDatabaseConfiguration = JSONUtil.toBean(configuration, StructDatabaseConfiguration.class);
        return RemoteMySQLEngine.checkConnectValid(structDatabaseConfiguration);
    }

    @Override
    public List<CoreDatasetTable> getTables(CoreDatasource coreDatasource) {
        String configuration = coreDatasource.getConfiguration();
        // 将JSON转换为对象
        StructDatabaseConfiguration structDatabaseConfiguration = JSONUtil.toBean(configuration, StructDatabaseConfiguration.class);
        // 获取对应数据源所有的表名
        List<String> tableNames = RemoteMySQLEngine.getSchemas(structDatabaseConfiguration);
        // 封装返回类
        List<CoreDatasetTable> coreDatasetTables = new ArrayList<>();
        for (String tableName : tableNames) {
            CoreDatasetTable coreDatasetTable = new CoreDatasetTable();
            coreDatasetTable.setTableName(tableName);
            coreDatasetTable.setDatasourceId(coreDatasource.getId());
            coreDatasetTable.setType(coreDatasource.getType());
            coreDatasetTables.add(coreDatasetTable);
        }
        return coreDatasetTables;
    }

    @Override
    public List<CoreDatasetTableField> getTableFields(CoreDatasource coreDatasource, String tableName) {
        StructDatabaseConfiguration structDatabaseConfiguration = getStructDatabaseConfiguration(coreDatasource);
        List<SchemaStructure> structure = RemoteMySQLEngine.structure(structDatabaseConfiguration, tableName);
        List<CoreDatasetTableField> tableFieldList = new ArrayList<>();
        for (SchemaStructure schemaStructure : structure) {
            CoreDatasetTableField field = new CoreDatasetTableField();
            field.setName(schemaStructure.getComment());
            field.setType(schemaStructure.getType());
            field.setOriginName(schemaStructure.getColumnName());
            field.setDatasourceId(coreDatasource.getId());
            tableFieldList.add(field);
        }
        return tableFieldList;
    }

    @Override
    public CustomPage<Map<String, Object>> getDataFromDatasourceBySql(CoreDatasource datasource, String sql, Integer pageNo,Integer pageSize) throws SQLException {
        StructDatabaseConfiguration structDatabaseConfiguration = getStructDatabaseConfiguration(datasource);
        // 获取对应数据源所有的表名
        return remoteMySQLEngine.execSelectSqlToQueryAICustomSQLVO(structDatabaseConfiguration, sql, pageNo,pageSize);
    }

    @Override
    public List<AskAIWithDataTablesAndFieldsRequest> getAskAIWithDataTablesAndFieldsRequests(CoreDatasource coreDatasource, User loginUser) throws SQLException {
        StructDatabaseConfiguration structDatabaseConfiguration = getStructDatabaseConfiguration(coreDatasource);
        return RemoteMySQLEngine.getAskAIWithDataTablesAndFieldsRequests(structDatabaseConfiguration);
    }

    public StructDatabaseConfiguration getStructDatabaseConfiguration(CoreDatasource datasource) {
        // 从第三方数据中获取数据
        String configuration = datasource.getConfiguration();
        // 将JSON转换为对象
        return JSONUtil.toBean(configuration, StructDatabaseConfiguration.class);
    }
}
