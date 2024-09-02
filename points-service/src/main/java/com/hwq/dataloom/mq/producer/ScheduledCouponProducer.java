package com.hwq.dataloom.mq.producer;

import cn.hutool.core.util.StrUtil;
import com.hwq.dataloom.constants.CouponMessageConstant;
import com.hwq.dataloom.mq.dto.BasicDTO;
import com.hwq.dataloom.mq.event.CouponTaskDirectEvent;
import com.hwq.dataloom.mq.event.ScheduledCouponEvent;
import com.hwq.dataloom.mq.wrapper.MessageWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author HWQ
 * @date 2024/9/3 01:35
 * @description 定时优惠券生产者
 */
@Component
public class ScheduledCouponProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 构建请求类
     * @param scheduledCouponEvent 延迟消息
     * @return 请求类
     */
    public BasicDTO buildBaseSendDTO(ScheduledCouponEvent scheduledCouponEvent) {
        Long couponTaskId = scheduledCouponEvent.getCouponTaskId();
        return BasicDTO.builder()
                .eventName("优惠券延迟推送")
                .key(String.valueOf(couponTaskId))
                .topic(CouponMessageConstant.SCHEDULE_COUPON_DISTRIBUTION_TOPIC)
                .timeout(2000L)
                .build();
    }

    /**
     * 构建payload
     * @param scheduledCouponEvent 延迟消息
     * @param basicDTO 请求类
     * @return payload
     */
    public Message<?> buildMessage(ScheduledCouponEvent scheduledCouponEvent, BasicDTO basicDTO) {
        return MessageBuilder
                .withPayload(new MessageWrapper<>(basicDTO.getKey(), scheduledCouponEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, basicDTO.getKey())
                .setHeader(MessageConst.PROPERTY_TAGS, basicDTO.getTag())
                .build();

    }

    /**
     * 发送延迟消息
     * @param scheduledCouponEvent 延迟消息
     * @return 发送结果
     */
    public SendResult sendMessage(ScheduledCouponEvent scheduledCouponEvent) {
        BasicDTO basicDTO = buildBaseSendDTO(scheduledCouponEvent);
        StringBuilder des = StrUtil.builder().append(basicDTO.getTopic());
        if (StringUtils.isNotEmpty(basicDTO.getTag())) {
            des.append(":").append(basicDTO.getTag());
        }
        return rocketMQTemplate.syncSend(String.valueOf(des), buildMessage(scheduledCouponEvent, basicDTO));
    }
}
