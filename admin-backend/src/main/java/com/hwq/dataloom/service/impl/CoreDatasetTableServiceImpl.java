package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.framework.result.BaseResponse;
import com.hwq.dataloom.framework.result.ResultUtils;
import com.hwq.dataloom.model.entity.CoreDatasetTable;
import com.hwq.dataloom.service.CoreDatasetTableService;
import com.hwq.dataloom.mapper.CoreDatasetTableMapper;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

/**
* @author 25020
* @description 针对表【core_dataset_table(table数据集)】的数据库操作Service实现
* @createDate 2024-08-13 14:45:15
*/
@Service
public class CoreDatasetTableServiceImpl extends ServiceImpl<CoreDatasetTableMapper, CoreDatasetTable>
    implements CoreDatasetTableService{

    @Lazy
    @Resource
    private CoreDatasetTableService coreDatasetTableService;

    public Long addDatasetTable(CoreDatasetTable coreDatasetTable) {
        ThrowUtils.throwIf(ObjectUtils.isEmpty(coreDatasetTable), ErrorCode.PARAMS_ERROR, "请求参数为空");
        boolean save = coreDatasetTableService.save(coreDatasetTable);
        ThrowUtils.throwIf(!save,ErrorCode.OPERATION_ERROR,"新增数据表失败！");
        Long id = coreDatasetTable.getId();
        return id;
    }

}




