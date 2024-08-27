package com.hwq.dataloom.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.model.entity.User;
import com.hwq.dataloom.model.dto.datasource.GetTableFieldsDTO;
import com.hwq.dataloom.model.dto.newdatasource.ApiDefinition;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.dto.newdatasource.TableField;
import com.hwq.dataloom.model.dto.newdatasource.TaskDTO;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.model.entity.CoreDatasource;
import com.hwq.dataloom.model.entity.CoreDatasourceTask;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.service.CoreDatasourceService;
import com.hwq.dataloom.mapper.CoreDatasourceMapper;
import com.hwq.dataloom.service.basic.DatasourceExecuteStrategy;
import com.hwq.dataloom.service.basic.DatasourceStrategyChoose;
import jodd.bean.BeanUtil;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.hwq.dataloom.utils.ApiUtils.handleStr;

/**
* @author wqh
* @description 针对表【core_datasource(数据源表)】的数据库操作Service实现
* @createDate 2024-08-18 22:41:36
*/
@Service
public class CoreDatasourceServiceImpl extends ServiceImpl<CoreDatasourceMapper, CoreDatasource>
    implements CoreDatasourceService{


    @Resource
    private DatasourceStrategyChoose datasourceStrategyChoose;
    @Autowired
    private CoreDatasourceTaskServiceImpl coreDatasourceTaskServiceImpl;

    @Override
    public Long addDatasource(DatasourceDTO datasourceDTO, User user) {
        // 根据type找到对应的策略实现类，进行对应的数据源添加操作
         DatasourceExecuteStrategy executeStrategy = datasourceStrategyChoose.choose(datasourceDTO.getType());
        return executeStrategy.addCoreData(datasourceDTO, user);
    }

    @Override
    public DatasourceDTO getDataSource(Long datasourceId, User loginUser) {
        CoreDatasource coreDatasource = this.getById(datasourceId);
        ThrowUtils.throwIf(coreDatasource == null, ErrorCode.PARAMS_ERROR);
        // 鉴权
        ThrowUtils.throwIf(!coreDatasource.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        DatasourceDTO datasourceDTO = new DatasourceDTO();
        BeanUtils.copyProperties(coreDatasource, datasourceDTO);
        QueryWrapper<CoreDatasourceTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("datasourceId", datasourceId);
        CoreDatasourceTask datasourceTask = coreDatasourceTaskServiceImpl.getOne(queryWrapper);
        TaskDTO taskDTO = null;
        if (ObjectUtils.isNotEmpty(datasourceTask)) {
            taskDTO = new TaskDTO();
            BeanUtils.copyProperties(datasourceTask, taskDTO);
            datasourceDTO.setLastSyncTime(datasourceTask.getLastExecTime());
        }
        datasourceDTO.setSyncSetting(taskDTO);
        return datasourceDTO;
    }

    @Override
    public Boolean validDatasourceConfiguration(DatasourceDTO datasourceDTO) {
        // 根据type找到对应的策略实现类，进行对应数据源校验
        DatasourceExecuteStrategy executeStrategy = datasourceStrategyChoose.choose(datasourceDTO.getType());
        return executeStrategy.validDatasource(datasourceDTO);
    }

    @Override
    public List<CoreDatasetTable> getTablesByDatasourceId(Long datasourceId, User loginUser) {
        CoreDatasource coreDatasource = this.getById(datasourceId);
        ThrowUtils.throwIf(coreDatasource == null, ErrorCode.PARAMS_ERROR);
        // 鉴权
        ThrowUtils.throwIf(!coreDatasource.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        DatasourceExecuteStrategy executeStrategy = datasourceStrategyChoose.choose(coreDatasource.getType());
        return executeStrategy.getTables(coreDatasource);
    }

    @Override
    public void handleApiResponse(ApiDefinition apiDefinition, String responseBody) {
        List<Map<String,Object>> fields = new ArrayList<>();
        String rootPath = "";
        try {
            handleStr(apiDefinition,responseBody,fields,rootPath);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"调用接口失败！");
        }
        apiDefinition.setJsonFields(fields);
        ArrayList<TableField> tablefield = new ArrayList<>();
        for (Map<String, Object> field : fields) {
            TableField tableField = new TableField();
            tableField.setName(field.get("name").toString());
            tableField.setOriginName(field.get("name").toString());
            tableField.setType(field.get("type").toString());
            tableField.setJsonPath(field.get("jsonPath").toString());
        }
        apiDefinition.setFields(tablefield);
    }

    @Override
    public List<CoreDatasetTableField> getTableFieldsByDatasourceIdAndTableName(GetTableFieldsDTO getTableFieldsDTO, User loginUser) {
        Long datasourceId = getTableFieldsDTO.getDatasourceId();
        String tableName = getTableFieldsDTO.getTableName();
        CoreDatasource coreDatasource = this.getById(datasourceId);
        ThrowUtils.throwIf(coreDatasource == null, ErrorCode.PARAMS_ERROR);
        // 鉴权
        ThrowUtils.throwIf(!coreDatasource.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);
        DatasourceExecuteStrategy executeStrategy = datasourceStrategyChoose.choose(coreDatasource.getType());
        return executeStrategy.getTableFields(coreDatasource, tableName);
    }


}




