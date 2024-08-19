package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.mapper.CoreDatasourceTaskMapper;
import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.dto.newdatasource.TaskDTO;
import com.hwq.dataloom.model.entity.CoreDatasourceTask;
import com.hwq.dataloom.service.CoreDatasourceService;
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
    public Long addTask(DatasourceDTO datasourceDTO) {
        CoreDatasourceTask coreDatasourceTask = new CoreDatasourceTask();
        coreDatasourceTask.setDs_id(datasourceDTO.getId());
        TaskDTO taskDTO = datasourceDTO.getSyncSetting();
        ThrowUtils.throwIf(taskDTO == null, ErrorCode.PARAMS_ERROR);
        coreDatasourceTask.setName(datasourceDTO.getId() + "同步任务");
        coreDatasourceTask.setUpdate_type(taskDTO.getUpdateType());
        coreDatasourceTask.setStart_time(taskDTO.getStartTime());
        coreDatasourceTask.setSync_rate(taskDTO.getSyncRate());
        coreDatasourceTask.setCron(taskDTO.getCron());
        coreDatasourceTask.setSimple_cron_value(taskDTO.getSimpleCronValue());
        coreDatasourceTask.setSimple_cron_type(taskDTO.getSimpleCronType());
        coreDatasourceTask.setEnd_limit(taskDTO.getEndLimit());
        coreDatasourceTask.setEnd_time(taskDTO.getEndTime());
        coreDatasourceTask.setTask_status("执行中");

        boolean save = coreDatasourceTaskService.save(coreDatasourceTask);
        ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
        Long id = coreDatasourceTask.getId();
        return id;
    }
}
