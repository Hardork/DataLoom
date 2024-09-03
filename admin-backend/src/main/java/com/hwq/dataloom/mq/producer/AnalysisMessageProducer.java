package com.hwq.dataloom.mq.producer;

import com.hwq.dataloom.mq.constant.AnalysisMqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AnalysisMessageProducer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     * @param message
     */
    public void sendMessage(String message) {
        rabbitTemplate.convertAndSend(AnalysisMqConstant.BI_EXCHANGE_NAME, AnalysisMqConstant.BI_ROUTING_KEY, message);
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


}
