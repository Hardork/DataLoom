package com.hwq.dataloom.service.impl.strategy;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.spring.util.BeanUtils;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.dto.newdatasource.TableField;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.enums.DataSourceTypeEnum;

import com.hwq.dataloom.service.CoreDatasetTableFieldService;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.service.CoreDatasourceTaskService;
import com.hwq.dataloom.service.basic.DatasourceExecuteStrategy;
import com.hwq.dataloom.utils.ApiUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author HWQ
 * @date 2024/8/21 09:55
 * @description API数据源策略实现类
 */
@Component
public class APIDatasourceServiceImpl implements DatasourceExecuteStrategy<DatasourceDTO> {

    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    @Resource
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Resource
    private CoreDatasourceService coreDatasourceService;

    @Resource
    private CoreDatasetTableFieldService coreDatasetTableFieldService;

    @Override
    public String mark() {
        return DataSourceTypeEnum.API.getValue();
    }

    @Override
    public CoreDatasource getCoreDatasource() {
        return null;
    }

    @Override
    public Long addCoreData(DatasourceDTO datasourceDTO, User loginUser) {
        // 新增数据源
        CoreDatasource coreDatasource = new CoreDatasource();
        coreDatasource.setName(datasourceDTO.getName());
        coreDatasource.setDescription(datasourceDTO.getDescription());
        coreDatasource.setType(datasourceDTO.getType());
        coreDatasource.setPid(datasourceDTO.getPid());
        coreDatasource.setEditType(datasourceDTO.getEditType().toString());
        coreDatasource.setConfiguration(datasourceDTO.getConfiguration());
        coreDatasource.setStatus(datasourceDTO.getStatus());
        coreDatasource.setTaskStatus(datasourceDTO.getTaskStatus());
        coreDatasource.setEnableDataFill(coreDatasource.getEnableDataFill());
        coreDatasource.setUserId(loginUser.getId());
        boolean save = coreDatasourceService.save(coreDatasource);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR, "新增数据源失败！");
        Long id = coreDatasource.getId();
        List<ApiDefinition> apiDefinitions = JSONUtil.toList(datasourceDTO.getConfiguration(), ApiDefinition.class);
        // 循环新增数据表 、 数据源同步任务 、 数据字段
        for (ApiDefinition apiDefinition : apiDefinitions) {
            CoreDatasetTable coreDatasetTable = new CoreDatasetTable();
            coreDatasetTable.setName(apiDefinition.getName());
            coreDatasetTable.setTableName(apiDefinition.getDeTableName());
            coreDatasetTable.setDatasourceId(id);
            coreDatasetTable.setType(apiDefinition.getType());
            coreDatasetTable.setInfo(apiDefinition.getDesc());
            coreDatasetTable.setSqlVariableDetails(null);
            Long datasetTableId = coreDatasetTableService.addDatasetTable(coreDatasetTable);
            ThrowUtils.throwIf(datasetTableId < 0, ErrorCode.OPERATION_ERROR, "新增数据表失败！");

            datasourceDTO.setId(id);
            Long datasourceTaskId = coreDatasourceTaskService.addTask(datasourceDTO, datasetTableId);
            ThrowUtils.throwIf(datasourceTaskId < 0, ErrorCode.OPERATION_ERROR, "新增定时任务失败！");

            Long lastExecTime = coreDatasourceTaskService.getById(datasourceTaskId).getLastExecTime();

            List<TableField> fields = apiDefinition.getFields();
            int columnIndex = 0;
            ArrayList<CoreDatasetTableField> coreDatasetTableFieldList = new ArrayList<>();
            for (TableField field : fields) {
                columnIndex++;
                CoreDatasetTableField coreDatasetTableField = new CoreDatasetTableField();
                BeanUtil.copyProperties(field,coreDatasetTableField);

                coreDatasetTableField.setDatasourceId(id);
                coreDatasetTableField.setDatasetTableId(datasetTableId);
                coreDatasetTableField.setColumnIndex(columnIndex);
                coreDatasetTableField.setLastSyncTime(lastExecTime);
                coreDatasetTableField.setGroupType("d");
                coreDatasetTableFieldList.add(coreDatasetTableField);
            }
            boolean savedBatch = coreDatasetTableFieldService.saveBatch(coreDatasetTableFieldList);
            ThrowUtils.throwIf(!savedBatch,ErrorCode.OPERATION_ERROR,"新增字段失败！");
        }
        return id;
    }

    @Override
    public Boolean validDatasource(DatasourceDTO datasourceDTO) {
        // 校验API数据
        String configuration = datasourceDTO.getConfiguration();
        ApiDefinition apiDefinition = JSONUtil.toBean(configuration, ApiDefinition.class);
        String responseBody = null;
        try {
            // 向API发送请求
            CloseableHttpResponse response = ApiUtils.getApiResponse(apiDefinition);
            int code = response.getCode();
            if (code != 200) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "调用接口失败！错误码为：" + code);
            }
            responseBody = EntityUtils.toString(response.getEntity());
            if (StringUtils.isEmpty(responseBody)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "接口调用失败！接口请求结果为空！");
            }
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Override
    public List<CoreDatasetTable> getTables(CoreDatasource coreDatasource) {
        // zzx TODO: 获取数据源表信息
        return null;
    }

    @Override
    public List<CoreDatasetTableField> getTableFields(CoreDatasource coreDatasource, String tableName) {
        return null;
    }
}
