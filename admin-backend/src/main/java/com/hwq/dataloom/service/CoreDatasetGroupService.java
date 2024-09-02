package com.hwq.dataloom.service;

import com.hwq.dataloom.model.dto.newdatasource.CoreDatasetGroupDTO;
import com.hwq.dataloom.model.entity.CoreDatasetGroup;
import com.baomidou.mybatisplus.extension.service.IService;

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

}
