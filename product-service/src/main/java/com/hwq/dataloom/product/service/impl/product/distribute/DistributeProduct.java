package com.hwq.dataloom.product.service.impl.product.distribute;

import com.hwq.dataloom.product.mq.event.ProductDistributeEvent;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/16
 * @Description:
 **/
public interface DistributeProduct {

    void giveOutProduct(ProductDistributeEvent productDistributeEvent);
}
