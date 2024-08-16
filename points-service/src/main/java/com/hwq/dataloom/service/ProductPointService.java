package com.hwq.dataloom.service;

import com.hwq.dataloom.model.entity.ProductPoint;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author HWQ
* @description 针对表【product_point(产品信息)】的数据库操作Service
* @createDate 2023-10-10 08:38:13
*/
public interface ProductPointService extends IService<ProductPoint> {

    void validProductInfo(ProductPoint productPoint, boolean b);
}
