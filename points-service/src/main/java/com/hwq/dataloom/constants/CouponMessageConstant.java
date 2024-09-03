package com.hwq.dataloom.constants;

/**
 * @author HWQ
 * @date 2024/8/30 14:58
 * @description 优惠券消息常量类
 */
public interface CouponMessageConstant {
    /**
     * 优惠券模版发放（延迟发放） Topic 第一阶段
     */
    String DELAY_MESSAGE_TOPIC = "coupon_delay_send_topic";
    String DELAY_MESSAGE_CONSUMER_GROUP = "delay_message_consumer_group";

    /**
     * 优惠券模版发放（立即发放） Topic 第一阶段
     */
    String EXCEL_ANALYSIS_TOPIC = "excel_analysis_topic";

    String EXCEL_ANALYSIS_CONSUMER_GROUP = "excel_analysis_consumer_group";

    String SCHEDULE_COUPON_DISTRIBUTION_TOPIC = "schedule_coupon_distribution_topic";

    String SCHEDULE_COUPON_DISTRIBUTION_CONSUMER_GROUP = "schedule_coupon_distribution_consumer_group";

    String REMIND_COUPON_TOPIC = "remind_coupon_topic";
    String REMIND_COUPON_CONSUMER_GROUP = "remind_coupon_consumer_group";


    String COUPON_DISTRIBUTION_TOPIC = "coupon_distribution_topic";
    String COUPON_DISTRIBUTION_CONSUMER_GROUP = "coupon_distribution_consumer_group";

}
