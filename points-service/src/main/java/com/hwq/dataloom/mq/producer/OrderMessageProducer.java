package com.hwq.dataloom.mq.producer;

import com.hwq.dataloom.constants.MqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/8/13 08:51
 * @description
 */
@Component
public class OrderMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;
    /**
     * 发送订单
     * @param message
     */
    public void sendOrderMessage(String message) {
        rabbitTemplate.convertAndSend(MqConstant.ORDER_DELAYED_EXCHANGE, MqConstant.ORDER_DELAYED_ROUTING_KEY, message);
    }
}
