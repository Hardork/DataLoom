package com.hwq.dataloom.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优惠券模版定时执行时间
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateDelayEvent {

    /**
     * 优惠券模板id
     */
    private Long couponTemplateId;

    /**
     * 具体延迟时间
     */
    private Long delayTime;
}