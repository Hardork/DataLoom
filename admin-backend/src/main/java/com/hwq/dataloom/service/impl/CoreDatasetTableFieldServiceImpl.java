package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.mapper.CoreDatasetTableFieldMapper;
import com.hwq.dataloom.model.entity.CoreDatasetTableField;
import com.hwq.dataloom.service.CoreDatasetTableFieldService;
import org.springframework.stereotype.Service;

/**
 * @author HWQ
 * @date 2024/8/23 01:15
 * @description
 */
@Service
public class CoreDatasetTableFieldServiceImpl extends ServiceImpl<CoreDatasetTableFieldMapper, CoreDatasetTableField>
        implements CoreDatasetTableFieldService {
}
