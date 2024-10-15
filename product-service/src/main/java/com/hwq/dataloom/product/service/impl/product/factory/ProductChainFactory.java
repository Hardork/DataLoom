package com.hwq.dataloom.product.service.impl.product.factory;

import com.hwq.dataloom.product.service.impl.product.chain.IActionChain;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description:
 **/
@Service
public class ProductChainFactory {

    private final IActionChain actionChain;

    public ProductChainFactory(Map<String, IActionChain> actionChainGroup){
        actionChain = actionChainGroup.get(ActionModel.sku_base_action.code);
        actionChain.appendNext(actionChainGroup.get(ActionModel.sku_stock_action.getCode()));
    }
    public IActionChain openActionChain() {
        return this.actionChain;
    }

    @Getter
    @AllArgsConstructor
    public enum ActionModel {

        sku_base_action("sku_base_action", "活动的库存、时间校验"),
        sku_stock_action("sku_stock_action", "活动sku库存"),
        ;

        private final String code;
        private final String info;

    }
}
