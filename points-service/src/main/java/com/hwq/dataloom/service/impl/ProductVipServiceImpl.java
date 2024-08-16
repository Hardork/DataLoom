package com.hwq.dataloom.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.ThrowUtils;
import com.hwq.dataloom.model.entity.ProductVip;
import com.hwq.dataloom.mapper.ProductVipMapper;
import com.hwq.dataloom.service.ProductVipService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
* @author HWQ
* @description 针对表【product_vip(产品信息)】的数据库操作Service实现
* @createDate 2023-10-10 08:38:17
*/
@Service
public class ProductVipServiceImpl extends ServiceImpl<ProductVipMapper, ProductVip>
    implements ProductVipService {

    @Override
    public void validProductInfo(ProductVip productVip, boolean b) {
         String name = productVip.getName();
         String description = productVip.getDescription();
         Long total = productVip.getTotal();
         Integer duration = productVip.getDuration();
         Long originalTotal = productVip.getOriginalTotal();
        ThrowUtils.throwIf(StringUtils.isEmpty(name), ErrorCode.PARAMS_ERROR, "产品名称不得为空");
        ThrowUtils.throwIf(StringUtils.isEmpty(description), ErrorCode.PARAMS_ERROR, "产品描述不得为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(total), ErrorCode.PARAMS_ERROR, "产品金额不得为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(originalTotal), ErrorCode.PARAMS_ERROR, "产品原价不得为空");
        ThrowUtils.throwIf(ObjectUtils.isEmpty(duration), ErrorCode.PARAMS_ERROR, "产品有效期不得为空");
    }
}




