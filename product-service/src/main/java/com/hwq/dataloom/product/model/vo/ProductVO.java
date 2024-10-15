package com.hwq.dataloom.product.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
    private List<ProductInfo> productInfoList;
    private Long total;

    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public class ProductInfo{
        private Long id;
        private Integer stockCountSurplus;
        private BigDecimal productAmount;
        private String productDesc;
    }
}
