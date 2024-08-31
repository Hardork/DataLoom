package com.hwq.dataloom.constants;

/**
 * @author HWQ
 * @date 2024/8/30 14:58
 * @description 优惠券生产者常量类
 */
public interface CouponMessageProducerConstant {
    String DELAY_MESSAGE_TOPIC = "coupon_delay_send_topic";
    String DIRECT_MESSAGE_TOPIC = "coupon_direct_send_topic";
}
