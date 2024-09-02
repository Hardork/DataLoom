package com.hwq.dataloom.constants;

/**
 * @author HWQ
 * @date 2024/8/30 14:58
 * @description 优惠券消息常量类
 */
public interface CouponMessageConstant {
    /**
     * 优惠券模版发放（立即发放） Topic 第一阶段
     */
    String DELAY_MESSAGE_TOPIC = "coupon_delay_send_topic";
    String DELAY_MESSAGE_CONSUMER_GROUP = "delay_message_consumer_group";

    /**
     * 优惠券模版发放（延迟发放） Topic 第一阶段
     */
    String DIRECT_MESSAGE_TOPIC = "coupon_direct_send_topic";

    String DIRECT_MESSAGE_CONSUMER_GROUP = "direct_message_consumer_group";

    String COUPON_DISTRIBUTION_TOPIC = "coupon_distribution_topic";
    String COUPON_DISTRIBUTION_CONSUMER_GROUP = "coupon_distribution_consumer_group";

}
