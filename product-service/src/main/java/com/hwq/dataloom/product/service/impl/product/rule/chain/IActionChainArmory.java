package com.hwq.dataloom.product.service.impl.product.rule.chain;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description: 活动商品充值实体对象
 **/
public interface IActionChainArmory {

    IActionChain next();

    IActionChain appendNext(IActionChain next);

}
