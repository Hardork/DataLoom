package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.newdatasource.CoreDatasetGroupDTO;
import com.hwq.dataloom.model.dto.newdatasource.TableField;
import com.hwq.dataloom.model.dto.newdatasource.UnionDTO;
import com.hwq.dataloom.model.dto.newdatasource.UnionParamDTO;
import com.hwq.dataloom.model.entity.CoreDatasetGroup;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.service.CoreDatasetGroupService;
import com.hwq.dataloom.mapper.CoreDatasetGroupMapper;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.utils.datasource.DatasourceEngine;
import org.apache.calcite.sql.SqlNode;
import org.apache.calcite.sql.SqlSelect;
import org.apache.calcite.sql.parser.SqlParseException;
import org.apache.calcite.sql.parser.SqlParser;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 25020
 * @description 针对表【core_dataset_group(数据集分组表)】的数据库操作Service实现
 * @createDate 2024-08-30 10:37:37
 */
@Service
public class CoreDatasetGroupServiceImpl extends ServiceImpl<CoreDatasetGroupMapper, CoreDatasetGroup>
        implements CoreDatasetGroupService {

    @Resource
    private Map<Integer, DataSource> dataSourceMap;

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private DatasourceEngine datasourceEngine;

    @Override
    public String getDarasetGroupSql(CoreDatasetGroupDTO coreDatasetGroupDTO) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(coreDatasetGroupDTO), ErrorCode.PARAMS_ERROR);
        String sql = "SELECT ";
        StringBuilder stringBuilder = new StringBuilder(sql);
        Map<String, String> TableFieldMap = null;
        int dsIndex;
        // 获取所有字段
        List<CoreDatasetTableField> allFields = coreDatasetGroupDTO.getAllFields();
        TableFieldMap = allFields.stream().collect(Collectors.toMap(
                field ->
                {
                    QueryWrapper<CoreDatasetTable> coreDatasetTableQueryWrapper = new QueryWrapper<>();
                    coreDatasetTableQueryWrapper.eq("id", field.getDatasetTableId());
                    CoreDatasetTable coreDatasetTable = coreDatasetTableService.getOne(coreDatasetTableQueryWrapper);
                    String tableName = "data_warehouse_" + coreDatasetTable.getDatasourceId() % dataSourceMap.size() + "." + coreDatasetTable.getTableName();
                    return tableName;
                    },
                CoreDatasetTableField::getName)
        );
        // 获取初始表
        List<UnionDTO> union = coreDatasetGroupDTO.getUnion();
        ThrowUtils.throwIf(ObjectUtils.isEmpty(union), ErrorCode.PARAMS_ERROR);
        UnionDTO unionDTO = union.get(0);
        CoreDatasetTable currentDs = unionDTO.getCurrentDs();
        dsIndex = (int) (currentDs.getDatasourceId() % dataSourceMap.size());
        String currentDsTableName = currentDs.getTableName();

        // 获取连接表和字段
        String join = "";
        if (ObjectUtils.isNotEmpty(unionDTO.getChildrenDs())) {
            buildJoinSQL(unionDTO.getChildrenDs(), join);
        }
        stringBuilder.append(sql);
        for (String key : TableFieldMap.keySet()) {
            stringBuilder.append(key).append(".").append(TableFieldMap.get(key)).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append(" FROM ").append("data_warehouse_").append(dsIndex)
                .append(".").append(currentDsTableName).append(join);
        return stringBuilder.toString();
    }

    @Override
    public Map<String, List> executePreviewSQL(Long datasourceId, String sql) {
        sql += "LIMIT 100";
        // 创建 SQL 解析器配置
        SqlParser.Config parserConfig = SqlParser.config().withCaseSensitive(false);
        // 创建解析器对象
        SqlParser parser = SqlParser.create(sql, parserConfig);
        try {
            SqlNode sqlNode = parser.parseQuery();
            if (!(sqlNode instanceof SqlSelect)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入内容不为SELECT语句");
            }
        } catch (SqlParseException e) {
            throw new RuntimeException(e);
        }
        ResultSet resultSet = datasourceEngine.execSelectSql(datasourceId, sql);
        List<Map<String, Object>> datas;
        try {
            datas = datasourceEngine.resultSetToMapList(resultSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<TableField> fields = null;
        // 处理fields
        if (ObjectUtils.isNotEmpty(datas)) {
            Map<String, Object> map = datas.get(0);
            for (String key : map.keySet()) {
                TableField tableField = new TableField();
                tableField.setName(key);
                tableField.setOriginName(key);
                tableField.setType("VARCHAR");
                tableField.setDescription(key);
                tableField.setGroupType("d");
                fields.add(tableField);
            }
        }
        Map<String, List> data = new HashMap<>();
        data.put("fields", fields);
        data.put("data", datas);
        return data;
    }

    private void buildJoinSQL(List<UnionDTO> childrenDs, String baseJoin) {
        for (UnionDTO unionDTO : childrenDs) {
            CoreDatasetTable currentDs = unionDTO.getCurrentDs();
            if (ObjectUtils.isEmpty(currentDs)) {
                continue;
            }
            if (currentDs.getType().equals("sql")) {
                // 自定义sql连接
                // info中保存自定义sql语句
                String info = currentDs.getInfo();
                // 创建 SQL 解析器配置
                SqlParser.Config parserConfig = SqlParser.config().withCaseSensitive(false);
                // 创建解析器对象
                SqlParser parser = SqlParser.create(info, parserConfig);
                // 解析 SQL 并生成 AST
                try {
                    SqlNode sqlNode = parser.parseQuery();
                    String currentDsTableName = null;
                    // 判断是否为 SELECT 语句
                    if (sqlNode instanceof SqlSelect) {
                        SqlSelect select = (SqlSelect) sqlNode;
                        // 获取 FROM 子句
                        SqlNode fromClause = select.getFrom();
                        // 获取表名
                        if (fromClause != null) {
                            currentDsTableName = fromClause.toString();
                        }
                        ThrowUtils.throwIf(currentDsTableName == null, ErrorCode.PARAMS_ERROR, "未获取FROM子句");
                        StringBuilder joinBuilder = buildJoinSQL(unionDTO, currentDs , currentDsTableName, baseJoin);
                        // 分别拼接剩下的条件
                        SqlNode whereClause = select.getWhere();
                        if (whereClause != null) {
                            joinBuilder.append("WHERE ").append(whereClause).append(" ");
                        }
                        SqlNode groupByClause = select.getGroup();
                        if (groupByClause != null) {
                            joinBuilder.append("GROUP BY ").append(groupByClause).append(" ");
                        }
                        SqlNode havingClause = select.getHaving();
                        if (havingClause != null) {
                            joinBuilder.append("HAVING ").append(havingClause).append(" ");
                        }
                        baseJoin = joinBuilder.toString();
                    } else {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入内容不为SELECT语句");
                    }
                } catch (SqlParseException e) {
                    throw new RuntimeException(e);
                }


            } else {
                // 表连接
                String currentDsTableName = currentDs.getTableName();
                StringBuilder joinBuilder = buildJoinSQL(unionDTO, currentDs , currentDsTableName, baseJoin);
                baseJoin = joinBuilder.toString();
            }
            if (ObjectUtils.isNotEmpty(unionDTO.getChildrenDs())) {
                buildJoinSQL(unionDTO.getChildrenDs(), baseJoin);
            }
        }
    }

    /**
     * 生成sql的JOIN主体部分
     * @param unionDTO
     * @param currentDs 当前数据表
     * @param currentDsTableName 当前数据表表名
     * @param baseJoin Join部分主题
     * @return
     */
    private StringBuilder buildJoinSQL(UnionDTO unionDTO, CoreDatasetTable currentDs, String currentDsTableName, String baseJoin) {
        UnionParamDTO unionToParent = unionDTO.getUnionToParent();
        String unionType = unionToParent.getUnionType();
        CoreDatasetTableField currentField = unionToParent.getUnionFields().get(0).getCurrentField();
        CoreDatasetTableField parentField = unionToParent.getUnionFields().get(0).getParentField();
        int parentDsIndex = (int) (parentField.getDatasourceId() % dataSourceMap.size());
        int currentDsIndex = (int) (currentDs.getDatasourceId() % dataSourceMap.size());
        StringBuilder joinBuilder = new StringBuilder(baseJoin)
                .append(unionType).append(" JOIN ").append("data_warehouse_").append(currentDsIndex).append(".")
                .append(currentDsTableName)
                .append(" ON ")
                .append("data_warehouse_").append(parentDsIndex).append(".")
                .append(coreDatasetTableService.getById(parentField.getDatasetTableId()).getTableName()).append(".")
                .append(parentField.getName())
                .append(" = ")
                .append("data_warehouse_").append(currentDsIndex).append(".")
                .append(currentDsTableName).append(".").append(currentField.getName()).append(" ");
        return joinBuilder;
    }

}




