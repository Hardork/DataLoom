package com.hwq.dataloom.mq.producer;

import com.hwq.dataloom.constant.MqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/15
 * @Description:
 **/
@Component
public class UpdateDataMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(MqConstant.UPDATE_DATA_EXCHANGE_NAME,MqConstant.UPDATE_DATA_ROUTING_KEY,message);
    }
}
