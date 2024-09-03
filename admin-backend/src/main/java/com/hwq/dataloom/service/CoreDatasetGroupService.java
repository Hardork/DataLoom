package com.hwq.dataloom.service;

import com.hwq.dataloom.model.dto.newdatasource.CoreDatasetGroupDTO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.model.entity.CoreDatasetGroup;

import java.util.List;
import java.util.Map;

/**
* @author 25020
* @description 针对表【core_dataset_group(数据集分组表)】的数据库操作Service
* @createDate 2024-08-30 10:37:37
*/
public interface CoreDatasetGroupService extends IService<CoreDatasetGroup> {

    /**
     * 获取数据集SQL
     * @param coreDatasetGroupDTO
     * @return
     */
    String getDarasetGroupSql(CoreDatasetGroupDTO coreDatasetGroupDTO);

    /**
     * 处理并执行sql
     * @param datasourceId
     * @param sql
     * @return
     */
    Map<String, List> executePreviewSQL(Long datasourceId,String sql);

}
