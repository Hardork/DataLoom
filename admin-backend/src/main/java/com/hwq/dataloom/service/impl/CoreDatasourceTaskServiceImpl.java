package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.mapper.CoreDatasourceTaskMapper;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.dto.newdatasource.TaskDTO;
import com.hwq.dataloom.model.entity.CoreDatasourceTask;
import com.hwq.dataloom.service.CoreDatasourceTaskService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class CoreDatasourceTaskServiceImpl extends ServiceImpl<CoreDatasourceTaskMapper, CoreDatasourceTask>
    implements CoreDatasourceTaskService {

    @Resource
    @Lazy
    private CoreDatasourceTaskService coreDatasourceTaskService;

    @Override
    public Long addTask(DatasourceDTO datasourceDTO, Long datasetTableId) {
        CoreDatasourceTask coreDatasourceTask = new CoreDatasourceTask();
        coreDatasourceTask.setDataSourceId(datasourceDTO.getId());
        TaskDTO taskDTO = datasourceDTO.getSyncSetting();
        ThrowUtils.throwIf(taskDTO == null, ErrorCode.PARAMS_ERROR);
        coreDatasourceTask.setName(datasourceDTO.getId() + "同步任务");
        coreDatasourceTask.setDatasetTableId(datasetTableId);
        coreDatasourceTask.setUpdateType(taskDTO.getUpdateType());
        coreDatasourceTask.setStartTime(taskDTO.getStartTime());
        coreDatasourceTask.setSyncRate(taskDTO.getSyncRate());
        coreDatasourceTask.setCron(taskDTO.getCron());
        coreDatasourceTask.setSimpleCronValue(taskDTO.getSimpleCronValue());
        coreDatasourceTask.setSimpleCronType(taskDTO.getSimpleCronType());
        coreDatasourceTask.setEndLimit(taskDTO.getEndLimit());
        coreDatasourceTask.setEndTime(taskDTO.getEndTime());
        coreDatasourceTask.setTaskStatus("执行中");

        boolean save = coreDatasourceTaskService.save(coreDatasourceTask);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        Long id = coreDatasourceTask.getId();
        return id;
    }
}
