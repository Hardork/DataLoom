package com.hwq.dataloom.constants;

/**
 * @author HWQ
 * @date 2024/8/27 16:21
 * @description
 */
public interface CouponConstant {

    /**
     * 创建优惠券模版责任链组标识
     */
    String CREATE_COUPON_TEMPLATE_MASK = "create_coupon_template";

    /**
     * 优惠券模版信息缓存key
     */
    String COUPON_TEMPLATE_INFO_KEY = "couponTemplate_%s";

    String COUPON_BLOOM_FILTER_KEY = "coupon_bloom_filter_key";
}
