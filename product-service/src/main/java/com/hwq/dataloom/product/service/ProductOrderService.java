package com.hwq.dataloom.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.framework.request.PageRequest;
import com.hwq.dataloom.product.model.dto.ProductDTO;
import com.hwq.dataloom.product.model.dto.ProductOrderDTO;
import com.hwq.dataloom.product.model.entity.Product;
import com.hwq.dataloom.product.model.entity.ProductOrder;
import com.hwq.dataloom.product.model.request.ProductOrderRequest;

import javax.annotation.Resource;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
public interface ProductOrderService extends IService<ProductOrder> {

    void createProductOrder(ProductOrderRequest productOrderRequest);


}
