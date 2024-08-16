package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.entity.ProductPoint;
import com.hwq.dataloom.mapper.ProductPointMapper;
import com.hwq.dataloom.service.ProductPointService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author HWQ
* @description 针对表【product_point(产品信息)】的数据库操作Service实现
* @createDate 2023-10-10 08:38:13
*/
@Service
public class ProductPointServiceImpl extends ServiceImpl<ProductPointMapper, ProductPoint>
    implements ProductPointService {

    @Override
    public void validProductInfo(ProductPoint productPoint, boolean b) {
         String name = productPoint.getName();
         String description = productPoint.getDescription();
         Long total = productPoint.getTotal();
         Long addPoints = productPoint.getAddPoints();
         Long originalTotal = productPoint.getOriginalTotal();
        ThrowUtils.throwIf(StringUtils.isEmpty(name), ErrorCode.PARAMS_ERROR, "产品名称不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(description), ErrorCode.PARAMS_ERROR, "产品描述不得为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(total), ErrorCode.PARAMS_ERROR, "产品金额不得为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(originalTotal), ErrorCode.PARAMS_ERROR, "产品原价不得为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(addPoints), ErrorCode.PARAMS_ERROR, "添加积分不得为空");

    }
}




