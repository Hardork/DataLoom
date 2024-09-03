package com.hwq.dataloom.mq.event;

import lombok.Builder;
import lombok.Data;

/**
 * @author HWQ
 * @date 2024/9/2 22:43
 * @description 优惠券立即发放消息体
 */
@Data
@Builder
public class CouponTaskDirectEvent {
    /**
     * 优惠券任务id
     */
    private Long couponTaskId;
}
