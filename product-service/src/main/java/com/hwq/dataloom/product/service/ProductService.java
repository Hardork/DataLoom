package com.hwq.dataloom.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hwq.dataloom.framework.request.PageRequest;
import com.hwq.dataloom.product.model.dto.ProductDTO;
import com.hwq.dataloom.product.model.entity.Product;
import com.hwq.dataloom.product.model.vo.ProductVO;

import java.util.List;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
public interface ProductService extends IService<Product> {

    ProductVO queryProductList(PageRequest pageRequest);


}
