package com.hwq.dataloom.mq.producer;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.hwq.dataloom.constants.CouponMessageConstant;
import com.hwq.dataloom.mq.dto.BasicDTO;
import com.hwq.dataloom.mq.event.CouponRemindEvent;
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
 * @date 2024/9/4 03:24
 * @description 优惠券提示消费者
 */
@Component
public class CouponRemindProducer {
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 构建请求类
     * @param couponRemindEvent 延迟消息
     * @return 请求类
     */
    public BasicDTO buildBaseSendDTO(CouponRemindEvent couponRemindEvent) {
        return BasicDTO.builder()
                .eventName("优惠券提示")
                .key(couponRemindEvent.getUserId() + ":" + couponRemindEvent.getCouponTemplateId())
                .topic(CouponMessageConstant.REMIND_COUPON_TOPIC)
                .timeout(2000L)
                .delayTime(DateUtil.offsetMinute(couponRemindEvent.getStartTime(), -couponRemindEvent.getRemindTime()).getTime())
                .build();
    }

    /**
     * 构建payload
     * @param couponRemindEvent 延迟消息
     * @param basicDTO 请求类
     * @return payload
     */
    public Message<?> buildMessage(CouponRemindEvent couponRemindEvent, BasicDTO basicDTO) {
        return MessageBuilder
                .withPayload(new MessageWrapper<>(basicDTO.getKey(), couponRemindEvent))
                .setHeader(MessageConst.PROPERTY_KEYS, basicDTO.getKey())
                .setHeader(MessageConst.PROPERTY_TAGS, basicDTO.getTag())
                .build();

    }

    /**
     * 发送延迟消息
     * @param couponRemindEvent 延迟消息
     * @return 发送结果
     */
    public SendResult sendMessage(CouponRemindEvent couponRemindEvent) {
        BasicDTO basicDTO = buildBaseSendDTO(couponRemindEvent);
        StringBuilder des = StrUtil.builder().append(basicDTO.getTopic());
        if (StringUtils.isNotEmpty(basicDTO.getTag())) {
            des.append(":").append(basicDTO.getTag());
        }
        return rocketMQTemplate.syncSend(String.valueOf(des), buildMessage(couponRemindEvent, basicDTO));
    }
}
