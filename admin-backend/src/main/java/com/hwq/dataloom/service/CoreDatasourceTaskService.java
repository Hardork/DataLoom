package com.hwq.dataloom.service;

import com.hwq.dataloom.model.dto.newdatasource.DatasourceDTO;
import com.hwq.dataloom.model.dto.newdatasource.TaskDTO;
import com.hwq.dataloom.model.entity.CoreDatasourceTask;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 25020
* @description 针对表【core_datasource_task(数据源定时同步任务)】的数据库操作Service
* @createDate 2024-08-13 14:45:00
*/
public interface CoreDatasourceTaskService extends IService<CoreDatasourceTask> {

    /**
     * 添加数据源同步任务
     * @param datasourceDTO
     * @return
     */
    Long addTask(DatasourceDTO datasourceDTO);

}
