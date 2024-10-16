package com.hwq.dataloom.product.mq.consumer;

import com.hwq.dataloom.product.constants.Constants;
import com.hwq.dataloom.product.model.entity.Product;
import com.hwq.dataloom.product.mq.event.ProductDistributeEvent;
import com.hwq.dataloom.product.mq.wrapper.MessageWrapper;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;

/**
 * @Author: HCJ
 * @DateTime: 2024/10/16
 * @Description:
 **/
@RocketMQMessageListener(
        topic = Constants.MQKey.PRODUCT_DISTRIBUTE_TOPIC,
        consumerGroup = Constants.MQKey.PRODUCT_DISTRIBUTE_GROUP
)
public class ProductDistributeConsumer implements RocketMQListener<MessageWrapper<ProductDistributeEvent>> {
    @Override
    public void onMessage(MessageWrapper<ProductDistributeEvent> messageWrapper) {
        ProductDistributeEvent message = messageWrapper.getMessage();
        Product product = message.getProduct();

    }
}
