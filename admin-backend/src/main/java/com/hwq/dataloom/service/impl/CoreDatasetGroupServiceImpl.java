package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.dto.newdatasource.CoreDatasetGroupDTO;
import com.hwq.dataloom.model.dto.newdatasource.UnionDTO;
import com.hwq.dataloom.model.dto.newdatasource.UnionParamDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.service.CoreDatasetGroupService;
import com.hwq.dataloom.mapper.CoreDatasetGroupMapper;
import com.hwq.dataloom.service.CoreDatasetTableService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;
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

    @Override
    public String getDarasetGroupSql(CoreDatasetGroupDTO coreDatasetGroupDTO) {
        // TODO 自定义sql
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

    private void buildJoinSQL(List<UnionDTO> childrenDs, String baseJoin) {
        for (UnionDTO unionDTO : childrenDs) {
            int currentDsIndex;
            int parentDsIndex;
            CoreDatasetTable currentDs = unionDTO.getCurrentDs();
            if (ObjectUtils.isEmpty(currentDs)) {
                continue;
            }
            currentDsIndex = (int) (currentDs.getDatasourceId() % dataSourceMap.size());
            String currentDsTableName = currentDs.getTableName();
            UnionParamDTO unionToParent = unionDTO.getUnionToParent();
            String unionType = unionToParent.getUnionType();
            CoreDatasetTableField currentField = unionToParent.getUnionFields().get(0).getCurrentField();
            CoreDatasetTableField parentField = unionToParent.getUnionFields().get(0).getParentField();
            parentDsIndex = (int) (parentField.getDatasourceId() % dataSourceMap.size());
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
            baseJoin = joinBuilder.toString();
            if (ObjectUtils.isNotEmpty(unionDTO.getChildrenDs())) {
                buildJoinSQL(unionDTO.getChildrenDs(), baseJoin);
            }
        }
    }

}




