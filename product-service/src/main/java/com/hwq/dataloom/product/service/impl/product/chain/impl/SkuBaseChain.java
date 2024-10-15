package com.hwq.dataloom.product.service.impl.product.chain.impl;

import com.hwq.dataloom.framework.errorcode.ErrorCode;
import com.hwq.dataloom.framework.exception.BusinessException;
import com.hwq.dataloom.product.model.entity.Product;
import com.hwq.dataloom.product.service.impl.product.chain.AbstractActionChain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description:
 **/
@Slf4j
@Component("sku_base_action")
public class SkuBaseChain extends AbstractActionChain {
    @Override
    public boolean action(Product product) {
        log.info("活动责任链-商品库存处理【有效期、状态、库存】开始。productId:{} ", product.getId());
        // 校验；活动日期「开始时间 <- 当前时间 -> 结束时间」
        Date currentDate = new Date();
        if(product.getBeginDateTime().after(currentDate) || product.getEndDateTime().before(currentDate)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验；活动sku库存 「剩余库存从缓存获取的」
        if (product.getStockCount() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }


        return next().action(product);
    }
}
