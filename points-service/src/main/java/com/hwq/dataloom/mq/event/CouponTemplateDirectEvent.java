package com.hwq.dataloom.mq.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HWQ
 * @date 2024/8/30 15:32
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponTemplateDirectEvent {
    /**
     * 优惠券模板id
     */
    private Long couponTemplateId;
}
