package com.hwq.dataloom.product.mq.event;

import com.hwq.dataloom.product.model.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/16
 * @Description:
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDistributeEvent {

    /** 订单流水号 */
    private String outBusinessNo;
    /** 商品 */
    private Product product;
    /** 用户ID */
    private Long userId;
}
