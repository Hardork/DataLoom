package com.hwq.dataloom.service;

import com.hwq.dataloom.model.entity.ProductVip;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HWQ
* @description 针对表【product_vip(产品信息)】的数据库操作Service
* @createDate 2023-10-10 08:38:17
*/
public interface ProductVipService extends IService<ProductVip> {

    void validProductInfo(ProductVip productVip, boolean b);
}
