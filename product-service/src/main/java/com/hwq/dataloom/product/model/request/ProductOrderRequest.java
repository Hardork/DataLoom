package com.hwq.dataloom.product.model.request;

import lombok.Data;

/**
 * @Author: HCJ
 * @DateTime: 2024/9/30
 * @Description:
 **/
@Data
public class ProductOrderRequest {
    private Long productId;
    private Long pointId;
    private Long userId;
}
