package com.hwq.dataloom.framework.mq;

import cn.hutool.json.JSONUtil;
import com.hwq.dataloom.framework.mq.model.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Author: HCJ
 * @DateTime: 2024/11/19
 * @Description:
 **/
@Component
@Slf4j
public class EventPublisher {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void publish(String topic, BaseEvent.EventMessage<?> eventMessage) {
        try {

            String messageJson = JSONUtil.toJsonStr(eventMessage);
            rabbitTemplate.convertAndSend(topic, messageJson);
            log.info("发送MQ消息 topic:{} message:{}", topic, messageJson);
        } catch (Exception e) {
            log.error("发送MQ消息失败 topic:{} message:{}", topic, JSONUtil.toJsonStr(eventMessage), e);
            throw e;
        }
    }

    public void publish(String topic, String eventMessageJSON){
        try {
            rabbitTemplate.convertAndSend(topic, eventMessageJSON);
            log.info("发送MQ消息 topic:{} message:{}", topic, eventMessageJSON);
        } catch (Exception e) {
            log.error("发送MQ消息失败 topic:{} message:{}", topic, eventMessageJSON, e);
            throw e;
        }
    }

    public void publish(String exchangeName, String routingKey,  BaseEvent.EventMessage<?> eventMessage) {
        try {
            String messageJson = JSONUtil.toJsonStr(eventMessage);
            rabbitTemplate.convertAndSend(exchangeName,routingKey, messageJson);
            log.info("发送MQ消息 exchangeName:{} routingKey:{} message:{}", exchangeName,routingKey, messageJson);
        } catch (Exception e) {
            log.info("发送MQ消息失败 exchangeName:{} routingKey:{} message:{}", exchangeName,routingKey, JSONUtil.toJsonStr(eventMessage),e);
            throw e;
        }
    }
}
