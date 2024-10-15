package com.hwq.dataloom.product.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hwq.dataloom.framework.request.PageRequest;
import com.hwq.dataloom.product.mapper.ProductMapper;
import com.hwq.dataloom.product.model.vo.ProductVO;
import com.hwq.dataloom.product.model.entity.Product;
import com.hwq.dataloom.product.service.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product>
        implements ProductService {
    @Override
    public ProductVO queryProductList(PageRequest pageRequest) {
        // 1. 获取商品列表
        Page<Product> page = this.page(new Page<>(pageRequest.getCurrent(), pageRequest.getPageSize()));
        // 2. todo 获取优惠券列表
        List<Product> records = page.getRecords();
        BigDecimal coupon = new BigDecimal("2.2");

        // 3. 计算价格
        List<ProductVO.ProductInfo> productInfos = records.stream().map(product -> ProductVO.ProductInfo.builder()
                .id(product.getId())
                .productDesc(product.getProductDesc())
                .stockCountSurplus(product.getStockCountSurplus())
                .productAmount(product.getProductAmount().subtract(coupon))
                .build()).collect(Collectors.toList());
        ProductVO productVO = ProductVO.builder().productInfoList(productInfos).total(page.getTotal()).build();
        return productVO;
    }



}
