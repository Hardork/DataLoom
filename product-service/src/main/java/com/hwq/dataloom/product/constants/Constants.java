package com.hwq.dataloom.product.constants;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/15
 * @Description:
 **/
public interface Constants {

    interface RedisKey{
        String SKU_STOCK = "sku:stock:";

        String SKU_STOCK_OFFSET_KEY = "sku:stock:offset";

        String ACTIVITY_SKU_COUNT_QUERY = "activity:sku:count:query";
    }

    interface MQKey{
        String PRODUCT_DISTRIBUTE_TOPIC = "PRODUCT_DISTRIBUTE_TOPIC";
        String PRODUCT_DISTRIBUTE_GROUP = "PRODUCT_DISTRIBUTE_GROUP";
    }
}
