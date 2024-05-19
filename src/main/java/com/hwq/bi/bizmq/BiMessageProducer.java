package com.hwq.bi.bizmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class BiMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.BI_ROUTING_KEY, message);
    }

    /**
     * 发送消息
     * @param message
     * @param exchangeName
     * @param routingKey
     */
    public void sendMessage(String message, String exchangeName, String routingKey) {
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
    }

    /**
     * 发送订单
     * @param message
     */
    public void sendOrderMessage(String message) {
        rabbitTemplate.convertAndSend(BiMqConstant.ORDER_DELAYED_EXCHANGE, BiMqConstant.ORDER_DELAYED_ROUTING_KEY, message);
    }

}
