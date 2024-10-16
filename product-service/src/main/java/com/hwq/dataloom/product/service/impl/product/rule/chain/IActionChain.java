package com.hwq.dataloom.product.service.impl.product.rule.chain;


import com.hwq.dataloom.product.model.entity.Product;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description: 下单规则过滤接口
 **/
public interface IActionChain extends IActionChainArmory {

    boolean action(Product product);

}
