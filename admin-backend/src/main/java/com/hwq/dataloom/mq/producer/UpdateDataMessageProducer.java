package com.hwq.dataloom.mq.producer;

import com.hwq.dataloom.constant.MqConstant;
import com.hwq.dataloom.framework.mq.EventPublisher;
import com.hwq.dataloom.mq.event.UpdateDataMessageEvent;
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
    private EventPublisher eventPublisher;

    @Resource
    private UpdateDataMessageEvent updateDataMessageEvent;

    public void sendMessage(UpdateDataMessageEvent.UpdateDataMessage message) {
        eventPublisher.publish(MqConstant.UPDATE_DATA_QUEUE_NAME,updateDataMessageEvent.buildEventMessage(message));
    }
}
